package com.blackbox.plog.pLogs.operations

import android.util.Log
import androidx.annotation.Keep
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.config.PLogPreferences
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.pLogs.utils.PREF_EXPORT_START_DATE
import com.blackbox.plog.pLogs.utils.PREF_LOGS_CLEAR_DATE
import com.blackbox.plog.pLogs.utils.PREF_ZIP_DELETE_DATE
import com.blackbox.plog.utils.DateTimeUtils
import com.blackbox.plog.utils.RxBus
import java.util.*

@Keep
object Triggers {

    val TAG = "Triggers"

    /**
     * Should clear Logs based on provided logs retention days.
     *
     */
    fun shouldClearLogs() {
        try {

            //Check if logs configuration is set
            val logsConfig = PLogImpl.getConfig() ?: return

            //Do nothing if retention days == 0
            if (logsConfig.logsRetentionPeriodInDays <= 0)
                return

            //Set Default Value
            if (PLogPreferences.getInstance().getLong(PREF_LOGS_CLEAR_DATE) == 0L) {

                val info = "No last delete date found!"

                if (logsConfig.isDebuggable)
                    Log.i(PLog.DEBUG_TAG, info)

                updateLogsDeleteDate()
            }

            var savedTime = 0L

            if (PLogPreferences.getInstance().getLong(PREF_LOGS_CLEAR_DATE) != 0L) {
                savedTime = PLogPreferences.getInstance().getLong(PREF_LOGS_CLEAR_DATE)
            }

            if (savedTime == 0L) {

                val info = "Logs were found and are cleared."

                if (logsConfig.isDebuggable)
                    Log.i(PLog.DEBUG_TAG, info)

                //Clear Logs
                PLog.clearLogs()

                RxBus.send(LogEvents(EventTypes.DELETE_LOGS, info))

                updateLogsDeleteDate()

                return
            }

            if (logsConfig.isDebuggable)
                Log.i(PLog.DEBUG_TAG, "Last Logs delete date: ${DateTimeUtils.getFullDateTimeString(savedTime)}")

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
                    Log.i(PLog.DEBUG_TAG, info)

                //Clear Logs
                PLog.clearLogs()

                RxBus.send(LogEvents(EventTypes.DELETE_LOGS, info))

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

            //Check if logs configuration is set
            val logsConfig = PLogImpl.getConfig() ?: return

            //Do nothing if retention days == 0
            if (logsConfig.zipsRetentionPeriodInDays <= 0)
                return

            //Set Default Value
            if (PLogPreferences.getInstance().getLong(PREF_ZIP_DELETE_DATE) == 0L) {
                updateZipDeleteDate()
            }

            var savedTime = 0L

            if (PLogPreferences.getInstance().getLong(PREF_ZIP_DELETE_DATE) != 0L) {
                savedTime = PLogPreferences.getInstance().getLong(PREF_ZIP_DELETE_DATE)
            }

            if (savedTime == 0L) {

                val info = "Log Zip files were found and are cleared."

                if (logsConfig.isDebuggable)
                    Log.i(PLog.DEBUG_TAG, info)

                //Clear exported logs
                PLog.clearExportedLogs()

                RxBus.send(LogEvents(EventTypes.DELETE_EXPORTED_FILES, info))

                updateZipDeleteDate()

                return
            }

            if (logsConfig.isDebuggable)
                Log.i(PLog.DEBUG_TAG, "Last Zip delete date: ${DateTimeUtils.getFullDateTimeString(savedTime)}")

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
                    Log.i(PLog.DEBUG_TAG, info)

                //Clear exported logs
                PLog.clearExportedLogs()

                RxBus.send(LogEvents(EventTypes.DELETE_EXPORTED_FILES, info))

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

            //Check if logs configuration is set
            val logsConfig = PLogImpl.getConfig()!!

            //Do nothing if no log types are present for export
            if (logsConfig.autoExportLogTypes.isEmpty())
                return false

            //Do nothing if retention days == 0
            if (logsConfig.autoExportLogTypesPeriod <= 0)
                return true

            //Set Default Value
            if (PLogPreferences.getInstance().getString(PREF_EXPORT_START_DATE).isNullOrEmpty()) {
                setExportStartDate()
            }

            var savedTime = 0L

            if (!PLogPreferences.getInstance().getString(PREF_EXPORT_START_DATE).isNullOrEmpty()) {
                savedTime = PLogPreferences.getInstance().getLong(PREF_EXPORT_START_DATE)
            }

            if (savedTime == 0L)
                return true

            if (logsConfig.isDebuggable)
                Log.i(PLog.DEBUG_TAG, "Set export start date: ${DateTimeUtils.getFullDateTimeString(savedTime)}")

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
                    Log.i(PLog.DEBUG_TAG, info)

                RxBus.send(LogEvents(EventTypes.AUTO_EXPORT_PERIOD_COMPLETED, info))

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
            Log.i(PLog.DEBUG_TAG, "New Date set as logs delete date: ${DateTimeUtils.getFullDateTimeString(time)}")

        PLogPreferences.getInstance().save(PREF_LOGS_CLEAR_DATE, time)
    }

    private fun updateZipDeleteDate() {
        val time = System.currentTimeMillis()

        if (PLogImpl.getConfig()?.isDebuggable!!)
            Log.i(PLog.DEBUG_TAG, "New Date set as zip delete date: ${DateTimeUtils.getFullDateTimeString(time)}")

        PLogPreferences.getInstance().save(PREF_ZIP_DELETE_DATE, time)
    }

    private fun setExportStartDate() {
        val time = System.currentTimeMillis()

        if (PLogImpl.getConfig()?.isDebuggable!!)
            Log.i(PLog.DEBUG_TAG, "Set export start date: ${DateTimeUtils.getFullDateTimeString(time)}")

        PLogPreferences.getInstance().save(PREF_EXPORT_START_DATE, time)
    }

    private fun clearExportStartDate() {

        if (PLogImpl.getConfig()?.isDebuggable!!)
            Log.i(PLog.DEBUG_TAG, "Clear export start date!")

        PLogPreferences.getInstance().save(PREF_EXPORT_START_DATE, 0L)
    }

}