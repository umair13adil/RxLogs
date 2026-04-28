package com.blackbox.plog.pLogs.backpressure

import com.blackbox.plog.pLogs.models.LogLevel
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Thread-safe gate evaluated on every log call before it is posted to the Handler queue.
 *
 * Two independent mechanisms:
 *  1. Queue backpressure — compares the caller-supplied [pendingCount] (number of
 *     Runnables enqueued but not yet processed) against the configured thresholds.
 *  2. Per-level quota — limits how many logs of a given level are accepted within
 *     a rolling [BackpressureConfig.quotaWindowMillis] window using a CAS loop so
 *     no locks are needed even under heavy concurrent load.
 *
 * [onDropped] is called after every drop (on the calling thread) and receives the
 * level, reason, and the cumulative dropped count for that (level, reason) pair.
 */
internal object BackpressureGuard {

    @Volatile private var config: BackpressureConfig? = null

    var onDropped: ((level: LogLevel, reason: DroppedReason, droppedTotal: Long) -> Unit)? = null

    private val quotaCounters: Map<LogLevel, AtomicInteger> =
        LogLevel.values().associateWith { AtomicInteger(0) }
    private val windowStart = AtomicLong(System.currentTimeMillis())

    private val droppedByBackpressure: Map<LogLevel, AtomicLong> =
        LogLevel.values().associateWith { AtomicLong(0L) }
    private val droppedByQuota: Map<LogLevel, AtomicLong> =
        LogLevel.values().associateWith { AtomicLong(0L) }

    fun configure(cfg: BackpressureConfig?) {
        config = cfg
        quotaCounters.values.forEach { it.set(0) }
        windowStart.set(System.currentTimeMillis())
    }

    /**
     * Returns the reason this log should be dropped, or null if it should proceed.
     *
     * Quota is checked before backpressure so that a subsystem bumping its quota
     * does not claim a pending-count slot.
     */
    fun shouldDrop(level: LogLevel, pendingCount: Int): DroppedReason? {
        val cfg = config ?: return null

        // 1. Per-level quota — CAS loop, no lock required
        val quota = cfg.perLevelQuotas[level]
        if (quota != null && quota > 0) {
            rotateWindowIfNeeded(cfg)
            val counter = quotaCounters[level]!!
            while (true) {
                val current = counter.get()
                if (current >= quota) return DroppedReason.QUOTA
                if (counter.compareAndSet(current, current + 1)) break
            }
        }

        // 2. Backpressure tiers — ordinal reflects severity (INFO=0 … SEVERE=3)
        if (pendingCount >= cfg.warnQueueCapacity && level.ordinal < LogLevel.ERROR.ordinal) {
            return DroppedReason.BACKPRESSURE // drop INFO + WARNING
        }
        if (pendingCount >= cfg.queueCapacity && level.ordinal < LogLevel.WARNING.ordinal) {
            return DroppedReason.BACKPRESSURE // drop INFO only
        }

        return null
    }

    fun recordDropped(level: LogLevel, reason: DroppedReason) {
        val total = when (reason) {
            DroppedReason.BACKPRESSURE -> droppedByBackpressure[level]!!.incrementAndGet()
            DroppedReason.QUOTA -> droppedByQuota[level]!!.incrementAndGet()
        }
        onDropped?.invoke(level, reason, total)
    }

    fun droppedCount(level: LogLevel, reason: DroppedReason): Long = when (reason) {
        DroppedReason.BACKPRESSURE -> droppedByBackpressure[level]?.get() ?: 0L
        DroppedReason.QUOTA -> droppedByQuota[level]?.get() ?: 0L
    }

    fun resetDroppedCounts() {
        droppedByBackpressure.values.forEach { it.set(0L) }
        droppedByQuota.values.forEach { it.set(0L) }
    }

    // Rotate quota window atomically; only one thread wins the CAS and resets counters.
    private fun rotateWindowIfNeeded(cfg: BackpressureConfig) {
        val now = System.currentTimeMillis()
        val start = windowStart.get()
        if (now - start >= cfg.quotaWindowMillis) {
            if (windowStart.compareAndSet(start, now)) {
                quotaCounters.values.forEach { it.set(0) }
            }
        }
    }
}