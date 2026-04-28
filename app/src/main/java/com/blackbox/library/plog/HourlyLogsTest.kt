package com.blackbox.library.plog

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.blackbox.plog.elk.PLogMetaInfoProvider
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.backpressure.DroppedReason
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.redaction.MaskType
import com.blackbox.plog.pLogs.redaction.Sensitive
import com.blackbox.plog.tests.PLogTestHelper
import com.blackbox.plog.utils.DateTimeUtils
import com.mooveit.library.Fakeit
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*


class HourlyLogsTest : AppCompatActivity() {

    private val TAG = "HourlyLogsTest"
    private val c: Calendar = Calendar.getInstance()

    private var logsPrinted = 0
    private var currentTime = ""

    private val mIntervalTime  = 15_000
    private val mIntervalLog   = 100
    private val mIntervalStats = 200

    private var mHandlerTime:  Handler? = null
    private var mHandlerLog:   Handler? = null
    private var mHandlerStats: Handler? = null

    // ── Views ──
    private var eventsTV:      AppCompatTextView? = null
    private var statsTV:       AppCompatTextView? = null
    private var redactOutputTV: AppCompatTextView? = null
    private var redactInputET: EditText?          = null
    private var burstBtn:      AppCompatButton?   = null
    private var redactTextBtn: AppCompatButton?   = null
    private var redactObjBtn:  AppCompatButton?   = null

    // ── Redaction demo fixture ──
    private val demoCustomer = DemoCustomer(
        id         = "C-10042",
        name       = "Jane Doe",
        phone      = "+1 555-867-5309",
        email      = "jane.doe@acme-corp.com",
        cardNumber = "4111 1111 1111 1111"
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hourly_logs_test)

        eventsTV       = findViewById(R.id.events)
        statsTV        = findViewById(R.id.status)
        redactOutputTV = findViewById(R.id.redactOutput)
        redactInputET  = findViewById(R.id.redactInputET)
        burstBtn       = findViewById(R.id.burstBtn)
        redactTextBtn  = findViewById(R.id.redactTextBtn)
        redactObjBtn   = findViewById(R.id.redactObjectBtn)

        PLogTestHelper.isTestingHourlyLogs = true
        PLogMetaInfoProvider.elkStackSupported = false

        Fakeit.initWithLocale(Locale.ENGLISH)

        mHandlerTime  = Handler()
        mHandlerLog   = Handler()
        mHandlerStats = Handler()

        PLog.setBackpressureDropCallback { _, _, _ ->
            runOnUiThread { refreshStats() }
        }

        burstBtn?.setOnClickListener {
            PLog.resetDroppedCounts()
            fireBurst()
            refreshStats()
        }

        redactTextBtn?.setOnClickListener { runTextRedactionDemo() }
        redactObjBtn?.setOnClickListener  { runObjectRedactionDemo() }

        runTimers()

        MainApplication.logsConfig?.getLogEventsListener()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeBy(
                onNext = {
                    when (it.event) {
                        EventTypes.NEW_EVENT_LOG_FILE_CREATED -> {
                            PLog.logThis(TAG, "getLogEventsListener", "New log file created: " + it.data, LogLevel.INFO)
                            eventsTV?.text = "New log file created: " + it.data
                        }
                        EventTypes.NEW_EVENT_DIRECTORY_CREATED ->
                            PLog.logThis(TAG, "getLogEventsListener", "New directory created: " + it.data, LogLevel.INFO)
                        else -> {}
                    }
                },
                onError = { it.printStackTrace() }
            )
    }

    // ── Backpressure burst ────────────────────────────────────────────────────

    /**
     * INFO hits QUOTA (~log 21), WARNING climbs until BACKPRESSURE (~log 51).
     * Both DroppedReason values appear after a single tap.
     */
    private fun fireBurst() {
        repeat(500) { i ->
            PLog.logThis(TAG, "burst", "INFO burst #$i",    LogLevel.INFO)
            PLog.logThis(TAG, "burst", "WARNING burst #$i", LogLevel.WARNING)
        }
        Log.i(TAG, "fireBurst complete — pending=${PLog.getPendingLogCount()}")
    }

    // ── Redaction demos ───────────────────────────────────────────────────────

    /**
     * Takes the EditText input (or a default PII string) and shows the regex-redacted
     * version alongside the raw text. Uses ERROR level so backpressure never drops it.
     *
     * The logger writes the redacted form to disk automatically; the raw text never reaches
     * the log file.
     */
    private fun runTextRedactionDemo() {
        val raw = redactInputET?.text?.toString()?.takeIf { it.isNotBlank() }
            ?: "Call Jane at +1 555-867-5309 or email jane.doe@acme-corp.com. Card: 4111 1111 1111 1111"

        val redacted = PLog.redact(raw)          // same transformation the logger applies

        PLog.logThis(TAG, "textRedactionDemo", raw, LogLevel.ERROR)

        showRedactOutput(
            title   = "── Regex Redaction (built-in patterns) ──",
            before  = raw,
            after   = redacted,
            note    = "Logged at ERROR. Raw text never written to disk."
        )
    }

    /**
     * Serializes [demoCustomer] twice — once raw (toString) and once via PLog.redact — and
     * displays the difference.  Also fires the object-accepting logThis overload so the
     * redacted form lands in the log file.
     *
     * Fields:
     *   phone      → FULL_MASK  (***  )
     *   email      → HASH       ([sha256:xxxxxxxx])
     *   cardNumber → PARTIAL    (41*********111)
     */
    private fun runObjectRedactionDemo() {
        val raw      = demoCustomer.toString()
        val redacted = PLog.redact(demoCustomer)

        PLog.logThis(TAG, "objectRedactionDemo", demoCustomer, LogLevel.ERROR)

        showRedactOutput(
            title  = "── @Sensitive Field Redaction ──",
            before = raw,
            after  = redacted,
            note   = "phone=FULL_MASK  email=HASH  cardNumber=PARTIAL"
        )
    }

    @SuppressLint("SetTextI18n")
    private fun showRedactOutput(title: String, before: String, after: String, note: String) {
        redactOutputTV?.apply {
            visibility = View.VISIBLE
            text = "$title\n\nBefore:\n  $before\n\nAfter (written to log):\n  $after\n\n$note"
        }
    }

    // ── Periodic timers ───────────────────────────────────────────────────────

    private fun changeTime() {
        c.add(Calendar.HOUR_OF_DAY, 1)
        currentTime = DateTimeUtils.getHourlyFolderName(c.time.time)
        PLogTestHelper.hourlyLogFileName = currentTime
        Log.i(TAG, "changeTime: $currentTime")
    }

    @SuppressLint("SetTextI18n")
    private fun logData() {
        PLog.logThis(TAG, Fakeit.ancient().titan(), Fakeit.friends().quote(), LogLevel.INFO)
        logsPrinted++
        eventsTV?.text = "Logs Printed: $logsPrinted\nCurrent Time: $currentTime"
    }

    @SuppressLint("SetTextI18n")
    private fun refreshStats() {
        val pending = PLog.getPendingLogCount()
        val infoBP  = PLog.getDroppedCount(LogLevel.INFO,    DroppedReason.BACKPRESSURE)
        val infoQ   = PLog.getDroppedCount(LogLevel.INFO,    DroppedReason.QUOTA)
        val warnBP  = PLog.getDroppedCount(LogLevel.WARNING, DroppedReason.BACKPRESSURE)
        val warnQ   = PLog.getDroppedCount(LogLevel.WARNING, DroppedReason.QUOTA)
        val errBP   = PLog.getDroppedCount(LogLevel.ERROR,   DroppedReason.BACKPRESSURE)
        statsTV?.text =
            "── Backpressure stats ──\n" +
            "Pending in queue : $pending\n" +
            "INFO   → BP: $infoBP  Quota: $infoQ\n" +
            "WARNING→ BP: $warnBP  Quota: $warnQ\n" +
            "ERROR  → BP: $errBP\n" +
            "(ERROR/SEVERE never dropped by BP)"
    }

    private val statsRunner: Runnable = object : Runnable {
        override fun run() {
            try { refreshStats() }
            finally { mHandlerStats!!.postDelayed(this, mIntervalStats.toLong()) }
        }
    }

    private val timeRunner: Runnable = object : Runnable {
        override fun run() {
            try { changeTime() }
            finally { mHandlerTime!!.postDelayed(this, mIntervalTime.toLong()) }
        }
    }

    private val logRunner: Runnable = object : Runnable {
        override fun run() {
            try { logData() }
            finally { mHandlerLog!!.postDelayed(this, mIntervalLog.toLong()) }
        }
    }

    private fun runTimers() {
        timeRunner.run()
        logRunner.run()
        statsRunner.run()
    }

    private fun stopRepeatingTask() {
        mHandlerTime!!.removeCallbacks(timeRunner)
        mHandlerLog!!.removeCallbacks(logRunner)
        mHandlerStats!!.removeCallbacks(statsRunner)
    }

    override fun onDestroy() {
        PLog.setBackpressureDropCallback(null)
        stopRepeatingTask()
        super.onDestroy()
    }

    // ── Demo fixture ──────────────────────────────────────────────────────────

    /**
     * Sample data class that exercises all three MaskType values.
     *
     * In a real data class constructor use @field:Sensitive so Kotlin places the
     * annotation on the Java backing field where reflection can find it.
     */
    private data class DemoCustomer(
        val id: String,
        val name: String,
        @field:Sensitive                              val phone:      String,
        @field:Sensitive(maskType = MaskType.HASH)    val email:      String,
        @field:Sensitive(maskType = MaskType.PARTIAL) val cardNumber: String
    )
}