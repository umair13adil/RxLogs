package com.blackbox.plog.pLogs

/**
 * Created by Umair Adil on 12/04/2017.
 */

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Handler
import android.util.Log
import androidx.annotation.Keep
import com.blackbox.plog.dataLogs.DataLogger
import com.blackbox.plog.dataLogs.exporter.DataLogsExporter
import com.blackbox.plog.mqtt.MQTTSender
import com.blackbox.plog.mqtt.PLogMQTTProvider
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.exporter.ExportType
import com.blackbox.plog.pLogs.exporter.LogExporter
import com.blackbox.plog.pLogs.filter.PlogFilters
import com.blackbox.plog.pLogs.impl.AutoExportHelper
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.utils.LOG_FOLDER
import com.blackbox.plog.utils.RxBus
import com.blackbox.plog.utils.getLogsSavedPaths
import io.reactivex.Flowable
import io.reactivex.Observable
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("StaticFieldLeak")
@Keep
object PLog : PLogImpl() {

    internal val TAG = PLogImpl.TAG
    internal val DEBUG_TAG = PLogImpl.DEBUG_TAG
    internal val handler = Handler()

    /**
     * Log this.
     *
     * Logs 'String' data along with class & function name to hourly based file with formatted timestamps.
     *
     * @param className    the class name
     * @param text         the text
     */
    fun logThis(className: String, info: String) {
        if (getConfig()?.isEnabled == false) {
            return
        }
        val runnable = Runnable {
            val logsConfig = isLogsConfigValid(className, "", info, LogLevel.INFO)
            if (logsConfig.first) {
                writeLogsAsync(logsConfig.second, LogLevel.INFO)
            }
        }
        handler.post(runnable)
    }

    /**
     * Log this.
     *
     * Logs 'String' data along with class & function name to hourly based file with formatted timestamps.
     *
     * @param className    the class name
     * @param functionName the function name
     * @param text         the text
     */
    fun logThis(className: String, functionName: String, info: String) {
        if (getConfig()?.isEnabled == false) {
            return
        }
        val runnable = Runnable {
            val logsConfig = isLogsConfigValid(className, functionName, info, LogLevel.INFO)
            if (logsConfig.first) {
                writeLogsAsync(logsConfig.second, LogLevel.INFO)
            }
        }
        handler.post(runnable)
    }

    /**
     * Log this.
     *
     * Logs 'String' data along with class & function name to hourly based file with formatted timestamps.
     *
     * @param className    the class name
     * @param functionName the function name
     * @param text         the text
     * @param type         the type
     */
    fun logThis(className: String, functionName: String, info: String, level: LogLevel) {
        if (getConfig()?.isEnabled == false) {
            return
        }
        val runnable = Runnable {
            val logsConfig = isLogsConfigValid(className, functionName, info, level)

            if (logsConfig.first) {
                writeLogsAsync(logsConfig.second, level)
            }
        }
        handler.post(runnable)
    }

    /**
     * Log Exception.
     *
     * Logs 'String' data along with class & function name to hourly based file with formatted timestamps.
     *
     * @param className    the class name
     * @param functionName the function name
     * @param e             Exception
     * @param type         the type
     */
    fun logThis(className: String = "", functionName: String = "", info: String = "", throwable: Throwable, level: LogLevel = LogLevel.ERROR) {
        if (getConfig()?.isEnabled == false) {
            return
        }
        val runnable = Runnable {
            val logsConfig = isLogsConfigValid(className, functionName, info, level, throwable = throwable)
            if (logsConfig.first) {

                RxBus.send(LogEvents(EventTypes.NON_FATAL_EXCEPTION_REPORTED, throwable = throwable))

                val data = formatErrorMessage(info, throwable = throwable)

                writeLogsAsync(data, level)

            }
        }
        handler.post(runnable)
    }

    /**
     * Log Exception.
     *
     * Logs 'String' data along with class & function name to hourly based file with formatted timestamps.
     *
     * @param className    the class name
     * @param functionName the function name
     * @param e             Exception
     * @param type         the type
     */
    fun logThis(className: String = "", functionName: String = "", throwable: Throwable, level: LogLevel = LogLevel.ERROR) {
        if (getConfig()?.isEnabled == false) {
            return
        }
        val runnable = Runnable {
            val logsConfig = isLogsConfigValid(className, functionName, "", level, throwable = throwable)
            if (logsConfig.first) {

                RxBus.send(LogEvents(EventTypes.NON_FATAL_EXCEPTION_REPORTED, throwable = throwable))

                val data = formatErrorMessage("", throwable = throwable)

                writeLogsAsync(data, level)

            }
        }
        handler.post(runnable)
    }

    /**
     * Log Exception.
     *
     * Logs 'String' data along with class & function name to hourly based file with formatted timestamps.
     *
     * @param className    the class name
     * @param functionName the function name
     * @param e             Exception
     * @param type         the type
     */
    fun logThis(className: String = "", functionName: String = "", info: String = "", exception: Exception, level: LogLevel = LogLevel.ERROR) {
        if (getConfig()?.isEnabled == false) {
            return
        }
        val runnable = Runnable {
            val logsConfig = isLogsConfigValid(className, functionName, info, level, exception = exception)
            if (logsConfig.first) {

                RxBus.send(LogEvents(EventTypes.NON_FATAL_EXCEPTION_REPORTED, exception = exception))
                val data = formatErrorMessage(info, exception = exception)

                writeLogsAsync(data, level)

            }
        }
        handler.post(runnable)
    }

    /**
     * Log Exception.
     *
     * Logs 'String' data along with class & function name to hourly based file with formatted timestamps.
     *
     * @param className    the class name
     * @param functionName the function name
     * @param e             Exception
     * @param type         the type
     */
    fun logThis(className: String = "", functionName: String = "", exception: Exception, level: LogLevel = LogLevel.ERROR) {
        if (getConfig()?.isEnabled == false) {
            return
        }
        RxBus.send(LogEvents(EventTypes.NON_FATAL_EXCEPTION_REPORTED, exception = exception))

        val runnable = Runnable {
            val logsConfig = isLogsConfigValid(className, functionName, "", level, exception = exception)
            if (logsConfig.first) {
                val data = formatErrorMessage("", exception = exception)
                writeLogsAsync(data, level)
            }
        }
        handler.post(runnable)
    }

    /**
     * Gets logs.
     *
     * This will export logs based on filter type to export location with export name provided.
     *
     * @return the logs
     */
    fun exportDataLogsForName(name: String, exportDecrypted: Boolean = false): Observable<String> {

        PLogImpl.getConfig()?.let {

            val path = getLogsSavedPaths(it.nameForEventDirectory)
            return DataLogsExporter.getDataLogs(name, path, outputPath, exportDecrypted)
        }

        return returnDefaultObservableForNoConfig()
    }

    /**
     * Gets logs.
     *
     * This will export logs based on filter type to export location with export name provided.
     *
     * @return the logs
     */
    fun exportAllDataLogs(exportDecrypted: Boolean = false): Observable<String> {

        PLogImpl.getConfig()?.let {

            val path = getLogsSavedPaths(it.nameForEventDirectory, isForAll = true)
            return DataLogsExporter.getDataLogs("", path, outputPath, exportDecrypted)
        }

        return returnDefaultObservableForNoConfig()
    }

    /**
     * Gets logs.
     *
     * This will export logs as plain String.
     *
     * @return the String data
     */
    fun printDataLogsForName(name: String, printDecrypted: Boolean = false): Observable<String> {

        PLogImpl.getConfig()?.let {

            val path = getLogsSavedPaths(it.nameForEventDirectory)
            return DataLogsExporter.printLogsForName(name, path, printDecrypted)
        }

        return returnDefaultObservableForNoConfig()
    }

    private fun returnDefaultObservableForNoConfig(): Observable<String> {
        return Observable.create {

            if (!it.isDisposed) {
                it.onError(Throwable("No Logs configuration provided! Can not perform this action with logs configuration."))
            }
        }
    }

    /*
     * This will return 'DataLogger' for log type defined in Config File.
     */
    fun getLoggerFor(type: String): DataLogger? {

        if (PLog.isLogsConfigSet()) {
            if (PLog.logTypes.containsKey(type))
                return PLog.logTypes.get(type)

            return null
        } else {
            return null
        }
    }

    /**
     * Gets logs.
     *
     * This will export logs based on filter type to export location with export name provided.
     *
     * @param type the type
     * @return the logs
     */
    fun exportLogsForType(type: ExportType, exportDecrypted: Boolean = false): Observable<String> {
        return LogExporter.getZippedLogs(type.type, exportDecrypted)
    }

    /**
     * Gets logs.
     *
     * This will export logs based on filters to export location with export name provided.
     *
     * @param filters the filters for the files
     * @return the logs
     */
    fun exportLogsForFilters(filters: PlogFilters, exportDecrypted: Boolean = false): Observable<String> {
        return LogExporter.getZippedLogs(filters, exportDecrypted)
    }

    /**
     * Gets logs.
     *
     * This will export logs as plain String.
     *
     * @return the String data
     */
    fun printLogsForType(type: ExportType, printDecrypted: Boolean = false): Flowable<String> {
        return LogExporter.printLogsForType(type.type, printDecrypted)
    }

    /**
     * Clear all logs from storage directory.
     *
     */
    fun clearLogs() {
        val rootFolderName = LOG_FOLDER
        val rootFolderPath = PLog.logPath + rootFolderName + File.separator
        File(rootFolderPath).deleteRecursively()
        MQTTSender.clearSummaryValues()
    }

    /**
     * Clear only logs older than the provided retention in days.
     * Keeps logs from (today - retentionDays) and newer.
     */
    fun clearLogsOlderThan(retentionDays: Int): List<String> {
        val deletedFiles = mutableListOf<String>()
        val debugEnabled = getConfig()?.debugFileOperations == true
        if (debugEnabled) Log.d(DEBUG_TAG, "clearLogsOlderThan called with retentionDays=$retentionDays")
        if (retentionDays <= 0) {
            if (debugEnabled) Log.d(DEBUG_TAG, "Retention days <= 0, nothing to clear.")
            return deletedFiles
        }

        val rootFolderName = LOG_FOLDER
        val rootFolderPath = PLog.logPath + rootFolderName + File.separator
        val root = File(rootFolderPath)
        if (!root.exists()) {
            if (debugEnabled) Log.d(DEBUG_TAG, "Root log folder does not exist: $rootFolderPath")
            return deletedFiles
        }

        val sdf = SimpleDateFormat(com.blackbox.plog.pLogs.formatter.TimeStampFormat.DATE_FORMAT_1, Locale.ENGLISH)

        // Calculate cutoff date: today - retentionDays
        val cal = Calendar.getInstance()
        // Reset time to start of day FIRST before subtracting days
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        // Now subtract the retention days
        cal.add(Calendar.DAY_OF_YEAR, -retentionDays)
        val cutoffDate = cal.time

        if (debugEnabled) {
            val todayCal = Calendar.getInstance()
            todayCal.set(Calendar.HOUR_OF_DAY, 0)
            todayCal.set(Calendar.MINUTE, 0)
            todayCal.set(Calendar.SECOND, 0)
            todayCal.set(Calendar.MILLISECOND, 0)
            Log.d(DEBUG_TAG, "Today: ${sdf.format(todayCal.time)}")
            Log.d(DEBUG_TAG, "Cutoff date for deletion: ${sdf.format(cutoffDate)}")
            Log.d(DEBUG_TAG, "Will delete logs BEFORE ${sdf.format(cutoffDate)}, keep from ${sdf.format(cutoffDate)} onwards")
        }

        root.listFiles()?.forEach { child ->
            try {
                if (child.isDirectory) {
                    if (debugEnabled) Log.d(DEBUG_TAG, "Checking directory: ${child.name}")
                    if (child.name.length >= 8 && child.name.substring(0, 8).all { it.isDigit() }) {
                        val dateStr = child.name.substring(0, 8)
                        val folderDate = sdf.parse(dateStr)
                        if (folderDate != null) {
                            if (debugEnabled) {
                                Log.d(DEBUG_TAG, "Directory date: $dateStr (${sdf.format(folderDate)})")
                                Log.d(DEBUG_TAG, "Comparing: folderDate.before(cutoffDate) = ${folderDate.before(cutoffDate)}")
                                Log.d(DEBUG_TAG, "folderDate.time=${folderDate.time}, cutoffDate.time=${cutoffDate.time}")
                            }
                            if (folderDate.before(cutoffDate)) {
                                if (debugEnabled) Log.d(DEBUG_TAG, "Deleting directory: ${child.absolutePath}")
                                deletedFiles.add(child.absolutePath)
                                child.deleteRecursively()
                            } else {
                                if (debugEnabled) Log.d(DEBUG_TAG, "Keeping directory: ${child.absolutePath} (within retention period)")
                            }
                        } else {
                            if (debugEnabled) Log.d(DEBUG_TAG, "Failed to parse directory date: $dateStr")
                        }
                    } else {
                        if (debugEnabled) Log.d(DEBUG_TAG, "Skipping directory with malformed name: ${child.name}")
                    }
                } else if (child.isFile) {
                    if (debugEnabled) Log.d(DEBUG_TAG, "Checking file: ${child.name}")
                    if (child.name.length >= 8 && child.name.substring(0, 8).all { it.isDigit() }) {
                        val dateStr = child.name.substring(0, 8)
                        val fileDate = sdf.parse(dateStr)
                        if (fileDate != null) {
                            if (debugEnabled) {
                                Log.d(DEBUG_TAG, "File date: $dateStr (${sdf.format(fileDate)})")
                            }
                            if (fileDate.before(cutoffDate)) {
                                if (debugEnabled) Log.d(DEBUG_TAG, "Deleting file: ${child.absolutePath}")
                                deletedFiles.add(child.absolutePath)
                                child.delete()
                            } else {
                                if (debugEnabled) Log.d(DEBUG_TAG, "Keeping file: ${child.absolutePath} (within retention period)")
                            }
                        } else {
                            if (debugEnabled) Log.d(DEBUG_TAG, "Failed to parse file date: $dateStr")
                        }
                    } else {
                        if (debugEnabled) Log.d(DEBUG_TAG, "Skipping file with malformed name: ${child.name}")
                    }
                }
            } catch (e: Exception) {
                if (debugEnabled) Log.e(DEBUG_TAG, "Exception while processing ${child.name}: ${e.message}", e)
            }
        }
        if (debugEnabled) Log.d(DEBUG_TAG, "clearLogsOlderThan completed. Deleted ${deletedFiles.size} items.")
        return deletedFiles
    }

    /**
     * Clear all zipped loges from storage directory.
     *
     */
    fun clearExportedLogs() {
        File(outputPath).deleteRecursively()
    }

    private fun writeLogsAsync(dataToWrite: String, logLevel: LogLevel) {

        if (getConfig()?.isEnabled == false) {
            return
        }

        //Only write to local storage if this flag is set 'true'
        if (PLogMQTTProvider.writeLogsToLocalStorage) {

            try {
                SaveAsync(dataToWrite, logLevel).execute()
            } catch (e: Exception) {
                e.printStackTrace()

                //Write directly
                writeAndExportLog(dataToWrite, logLevel)
            }
        }
    }

    private class SaveAsync(var dataToWrite: String, var logLevel: LogLevel) : AsyncTask<String, String, Boolean>() {

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)

            if (getConfig()?.isEnabled == false) {
                return
            }

            if (getConfig()?.isDebuggable!!) {

                if (dataToWrite.isNotEmpty()) {
                    if (logLevel == LogLevel.INFO) {
                        Log.i(Companion.TAG, dataToWrite)
                    } else {
                        Log.e(Companion.TAG, dataToWrite)
                    }
                }
            }

            //Check if log level is of Error
            AutoExportHelper.autoExportError(dataToWrite, logLevel)
        }

        override fun doInBackground(vararg p0: String?): Boolean {
            writeAndExportLog(dataToWrite, logLevel)
            return true
        }
    }
}
