package com.blackbox.plog.pLogs.operations

import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.config.EXPORT_START_DATE_TAG
import com.blackbox.plog.pLogs.config.LOGS_DELETE_DATE_TAG
import com.blackbox.plog.pLogs.config.ZIP_DELETE_DATE_TAG
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.utils.DateTimeUtils
import java.util.*

object Triggers {

    val TAG = "Triggers"

    /**
     * Has hour passed.
     *
     * Emits an event, if an hour is passed. Keeps running till user is logged in.
     */
    fun hasHourPassed() {

        try {
            val savedTime = 121213//TODO Add time

            //milliseconds
            val different = Date().time - savedTime

            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60

            val elapsedHours = different / hoursInMilli

            if (Math.abs(elapsedHours) >= 1) {

                if (PLogImpl.getConfig()?.isDebuggable!!)
                    Log.i(PLog.TAG, "An hour has passed, sending event!")
            }
        } catch (e: Exception) {
            //e.printStackTrace();
        }

    }

    /**
     * Should clear Logs based on provided logs retention days.
     *
     */
    fun shouldClearLogs() {
        //TODO Logs are cleared everyday
        try {
            if (!PLog.localConfigurationExists())
                return

            //Check if logs configuration is set
            val logsConfig = PLogImpl.getConfig() ?: return

            //Do nothing if retention days == 0
            if (logsConfig.logsRetentionPeriodInDays <= 0)
                return

            //Set Default Value
            if (logsConfig.logsDeleteDate == 0L) {

                val info = "No last delete date found!"

                if (logsConfig.isDebuggable)
                    Log.i(PLog.TAG, info)

                updateLogsDeleteDate()
            }

            var savedTime = 0L

            if (logsConfig.logsDeleteDate != 0L) {
                savedTime = logsConfig.logsDeleteDate
            }

            if (savedTime == 0L) {

                val info = "Logs were found and are cleared."

                if (logsConfig.isDebuggable)
                    Log.i(PLog.TAG, info)

                //Clear Logs
                PLog.clearLogs()

                PLog.getLogBus().send(LogEvents(EventTypes.DELETE_LOGS, info))

                updateLogsDeleteDate()

                return
            }

            if (logsConfig.isDebuggable)
                Log.i(PLog.TAG, "Last Logs delete date: ${DateTimeUtils.getFullDateTimeString(savedTime)}")

            //milliseconds
            val different = Date().time - savedTime

            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60
            val daysInMilli = hoursInMilli * 24
            val weekInMilli = daysInMilli * 7
            val monthInMilli = weekInMilli * 4

            val elapsedDays = different / daysInMilli

            if (Math.abs(elapsedDays) >= logsConfig.logsRetentionPeriodInDays) {

                val info = "$elapsedDays days has passed!"

                if (logsConfig.isDebuggable)
                    Log.i(PLog.TAG, info)

                //Clear Logs
                PLog.clearLogs()

                PLog.getLogBus().send(LogEvents(EventTypes.DELETE_LOGS, info))

                updateLogsDeleteDate()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Should clear Zips based on provided zip retention days.
     *
     */
    fun shouldClearExports() {

        try {

            if (!PLog.localConfigurationExists())
                return

            //Check if logs configuration is set
            val logsConfig = PLogImpl.getConfig() ?: return

            //Do nothing if retention days == 0
            if (logsConfig.zipsRetentionPeriodInDays <= 0)
                return

            //Set Default Value
            if (logsConfig.zipDeleteDate == 0L) {
                updateZipDeleteDate()
            }

            var savedTime = 0L

            if (logsConfig.zipDeleteDate != 0L) {
                savedTime = logsConfig.zipDeleteDate
            }

            if (savedTime == 0L) {

                val info = "Log Zip files were found and are cleared."

                if (logsConfig.isDebuggable)
                    Log.i(PLog.TAG, info)

                //Clear exported logs
                PLog.clearExportedLogs()

                PLog.getLogBus().send(LogEvents(EventTypes.DELETE_EXPORTED_FILES, info))

                updateZipDeleteDate()

                return
            }

            if (logsConfig.isDebuggable)
                Log.i(PLog.TAG, "Last Zip delete date: ${DateTimeUtils.getFullDateTimeString(savedTime)}")

            //milliseconds
            val different = Date().time - savedTime

            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60
            val daysInMilli = hoursInMilli * 24
            val weekInMilli = daysInMilli * 7
            val monthInMilli = weekInMilli * 4

            val elapsedDays = different / daysInMilli

            if (Math.abs(elapsedDays) >= logsConfig.zipsRetentionPeriodInDays) {

                val info = "$elapsedDays days has passed!"

                if (logsConfig.isDebuggable)
                    Log.i(PLog.TAG, info)

                //Clear exported logs
                PLog.clearExportedLogs()

                PLog.getLogBus().send(LogEvents(EventTypes.DELETE_EXPORTED_FILES, info))

                updateZipDeleteDate()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Should export logs based on provided period in days.
     *
     */
    fun shouldExportLogs(): Boolean {

        try {

            if (!PLog.localConfigurationExists())
                return true

            //Check if logs configuration is set
            val logsConfig = PLogImpl.getConfig()!!

            //Do nothing if no log types are present for export
            if (logsConfig.autoExportLogTypes.isEmpty())
                return false

            //Do nothing if retention days == 0
            if (logsConfig.autoExportLogTypesPeriod <= 0)
                return true

            //Set Default Value
            if (logsConfig.exportStartDate.isEmpty()) {
                setExportStartDate()
            }

            var savedTime = 0L

            if (!logsConfig.exportStartDate.isEmpty()) {
                savedTime = logsConfig.exportStartDate.toLong()
            }

            if (savedTime == 0L)
                return true

            if (logsConfig.isDebuggable)
                Log.i(PLog.TAG, "Set export start date: ${DateTimeUtils.getFullDateTimeString(savedTime)}")

            //milliseconds
            val different = Date().time - savedTime

            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60
            val daysInMilli = hoursInMilli * 24
            val weekInMilli = daysInMilli * 7
            val monthInMilli = weekInMilli * 4

            val elapsedDays = different / daysInMilli

            if (Math.abs(elapsedDays) >= logsConfig.autoExportLogTypesPeriod) {

                val info = "$elapsedDays days has passed!"

                if (logsConfig.isDebuggable)
                    Log.i(PLog.TAG, info)

                PLog.getLogBus().send(LogEvents(EventTypes.AUTO_EXPORT_PERIOD_COMPLETED, info))

                clearExportStartDate()

                return false
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }

    private fun updateLogsDeleteDate() {
        val time = System.currentTimeMillis()

        if (PLogImpl.getConfig()?.isDebuggable!!)
            Log.i(PLog.TAG, "New Date set as logs delete date: ${DateTimeUtils.getFullDateTimeString(time)}")

        PLogImpl.getConfig()?.updateDateForTAG(time.toString() + "", LOGS_DELETE_DATE_TAG)
    }

    private fun updateZipDeleteDate() {
        val time = System.currentTimeMillis()

        if (PLogImpl.getConfig()?.isDebuggable!!)
            Log.i(PLog.TAG, "New Date set as zip delete date: ${DateTimeUtils.getFullDateTimeString(time)}")

        PLogImpl.getConfig()?.updateDateForTAG(time.toString() + "", ZIP_DELETE_DATE_TAG)
    }

    private fun setExportStartDate() {
        val time = System.currentTimeMillis()

        if (PLogImpl.getConfig()?.isDebuggable!!)
            Log.i(PLog.TAG, "Set export start date: ${DateTimeUtils.getFullDateTimeString(time)}")

        PLogImpl.getConfig()?.updateDateForTAG(time.toString() + "", EXPORT_START_DATE_TAG)
    }

    private fun clearExportStartDate() {

        if (PLogImpl.getConfig()?.isDebuggable!!)
            Log.i(PLog.TAG, "Clear export start date!")

        PLogImpl.getConfig()?.updateDateForTAG("", EXPORT_START_DATE_TAG)
    }

}