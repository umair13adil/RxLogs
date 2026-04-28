package com.blackbox.plog.pLogs.backpressure

import androidx.annotation.Keep
import com.blackbox.plog.pLogs.models.LogLevel

/**
 * Controls how PLog sheds load when the internal write queue backs up.
 *
 * Drop tiers (backpressure):
 *   pendingCount >= [queueCapacity]      → drop INFO
 *   pendingCount >= [warnQueueCapacity]  → drop INFO + WARNING
 *   ERROR and SEVERE are never dropped by backpressure.
 *
 * Per-level quotas (chatty-subsystem protection):
 *   Each [perLevelQuotas] entry limits how many logs of that level are
 *   accepted within a single [quotaWindowMillis] window. 0 = unlimited.
 *   This prevents a high-volume INFO caller from consuming all capacity
 *   and starving ERROR/SEVERE (audit) events.
 *
 * Example:
 *   BackpressureConfig(
 *       queueCapacity     = 300,
 *       warnQueueCapacity = 500,
 *       perLevelQuotas    = mapOf(LogLevel.INFO to 200, LogLevel.WARNING to 100),
 *       quotaWindowMillis = 60_000L
 *   )
 */
@Keep
data class BackpressureConfig(
    val queueCapacity: Int = 500,
    val warnQueueCapacity: Int = 750,
    val perLevelQuotas: Map<LogLevel, Int> = emptyMap(),
    val quotaWindowMillis: Long = 60_000L
)

@Keep
enum class DroppedReason { BACKPRESSURE, QUOTA }