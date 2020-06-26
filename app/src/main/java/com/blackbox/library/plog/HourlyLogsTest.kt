package com.blackbox.library.plog

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.blackbox.plog.elk.PLogMetaInfoProvider
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.utils.DateTimeUtils
import com.mooveit.library.Fakeit
import kotlinx.android.synthetic.main.activity_hourly_logs_test.*
import java.util.*


class HourlyLogsTest : AppCompatActivity() {

    private val TAG = "HourlyLogsTest"
    private val c: Calendar = Calendar.getInstance()

    private var logsPrinted = 0
    private var currentTime = 0L

    private val mIntervalTime = 5000
    private val mIntervalLog = 500

    private var mHandlerTime: Handler? = null
    private var mHandlerLog: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hourly_logs_test)

        PLogMetaInfoProvider.elkStackSupported = false

        //Initialize FakeIt
        Fakeit.initWithLocale(Locale.ENGLISH)

        mHandlerTime = Handler()
        mHandlerLog = Handler()

        runTimers()
    }


    private fun changeTime() {
        c.add(Calendar.HOUR_OF_DAY, 1)
        currentTime = c.time.time
        PLog.logThis(TAG, "changeTime", "Time changed: ${DateTimeUtils.getFullDateTimeStringCompressed(currentTime)}")
    }

    @SuppressLint("SetTextI18n")
    private fun logData() {
        PLog.logThis(TAG, Fakeit.ancient().titan(), Fakeit.friends().quote(), LogLevel.INFO)
        logsPrinted++

        status.text = "Logs Printed: $logsPrinted\nCurrent Time: ${DateTimeUtils.getFullDateTimeStringCompressed(currentTime)}"
    }

    private var timeRunner: Runnable = object : Runnable {
        override fun run() {
            try {
                changeTime()
            } finally {
                mHandlerTime!!.postDelayed(this, mIntervalTime.toLong())
            }
        }
    }

    private var logRunner: Runnable = object : Runnable {
        override fun run() {
            try {
                logData()
            } finally {
                mHandlerLog!!.postDelayed(this, mIntervalLog.toLong())
            }
        }
    }

    private fun runTimers() {
        timeRunner.run()
        logRunner.run()
    }

    private fun stopRepeatingTask() {
        mHandlerTime!!.removeCallbacks(timeRunner)
        mHandlerLog!!.removeCallbacks(logRunner)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRepeatingTask()
    }
}