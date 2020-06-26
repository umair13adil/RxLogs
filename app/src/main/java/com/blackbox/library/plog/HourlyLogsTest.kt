package com.blackbox.library.plog

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.blackbox.plog.elk.PLogMetaInfoProvider
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.tests.PLogTestHelper
import com.blackbox.plog.utils.DateTimeUtils
import com.google.gson.GsonBuilder
import com.mooveit.library.Fakeit
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_hourly_logs_test.*
import java.util.*


class HourlyLogsTest : AppCompatActivity() {

    private val TAG = "HourlyLogsTest"
    private val c: Calendar = Calendar.getInstance()

    private var logsPrinted = 0
    private var currentTime = ""

    private val mIntervalTime = 15000
    private val mIntervalLog = 100

    private var mHandlerTime: Handler? = null
    private var mHandlerLog: Handler? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hourly_logs_test)

        PLogTestHelper.isTestingHourlyLogs = true
        PLogMetaInfoProvider.elkStackSupported = false

        //Initialize FakeIt
        Fakeit.initWithLocale(Locale.ENGLISH)

        mHandlerTime = Handler()
        mHandlerLog = Handler()

        runTimers()

        MainApplication.logsConfig?.getLogEventsListener()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeBy(
                        onNext = {
                            when (it.event) {
                                EventTypes.NEW_EVENT_LOG_FILE_CREATED -> {
                                    PLog.logThis(TAG, "getLogEventsListener", "New log file created: " + it.data, LogLevel.INFO)

                                    events?.text = "New log file created: " + it.data
                                }
                                EventTypes.NEW_EVENT_DIRECTORY_CREATED -> {
                                    PLog.logThis(TAG, "getLogEventsListener", "New directory created: " + it.data, LogLevel.INFO)
                                }
                                else -> {
                                }
                            }
                        },
                        onError = {
                            it.printStackTrace()
                        }
                )
    }

    private fun changeTime() {
        c.add(Calendar.HOUR_OF_DAY, 1)
        currentTime = DateTimeUtils.getHourlyFolderName(c.time.time)
        PLogTestHelper.hourlyLogFileName = currentTime
        PLog.logThis(TAG, "changeTime", "Time changed: $currentTime")
    }

    @SuppressLint("SetTextI18n")
    private fun logData() {
        PLog.logThis(TAG, Fakeit.ancient().titan(), Fakeit.friends().quote(), LogLevel.INFO)
        logsPrinted++

        status?.text = "Logs Printed: $logsPrinted\nCurrent Time: $currentTime"
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