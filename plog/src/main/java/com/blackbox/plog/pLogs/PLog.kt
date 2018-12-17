package com.blackbox.plog.pLogs

/**
 * Created by Umair Adil on 12/04/2017.
 */

import android.util.Log
import com.blackbox.plog.dataLogs.DataLogger
import com.blackbox.plog.dataLogs.exporter.DataLogsExporter
import com.blackbox.plog.pLogs.exporter.ExportType
import com.blackbox.plog.pLogs.exporter.LogExporter
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.utils.LOG_FOLDER
import com.blackbox.plog.utils.RxBus
import com.blackbox.plog.utils.getLogsSavedPaths
import io.reactivex.Observable
import java.io.File

object PLog : PLogImpl() {

    init {

        //Setup RxBus for notifications.
        setLogBus(RxBus())
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

        val logsConfig = isLogsConfigValid(className, functionName, info, level)
        if (logsConfig.first) {

            //Write Log and export if an 'Error'
            writeAndExportLog(logsConfig.second, level)
        }
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

        val logsConfig = isLogsConfigValid(className, functionName, info, level)
        if (logsConfig.first) {

            val data = formatErrorMessage(info, throwable = throwable)

            //Write Log and export if an 'Error'
            writeAndExportLog(data, level)
        }
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

        val logsConfig = isLogsConfigValid(className, functionName, "", level)
        if (logsConfig.first) {

            val data = formatErrorMessage("", throwable = throwable)

            //Write Log and export if an 'Error'
            writeAndExportLog(data, level)
        }
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

        val logsConfig = isLogsConfigValid(className, functionName, info, level)
        if (logsConfig.first) {

            val data = formatErrorMessage(info, exception = exception)

            //Write Log and export if an 'Error'
            writeAndExportLog(data, level)
        }
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

        val logsConfig = isLogsConfigValid(className, functionName, "", level)
        if (logsConfig.first) {

            val data = formatErrorMessage("", exception = exception)

            //Write Log and export if an 'Error'
            writeAndExportLog(data, level)
        }
    }

    /**
     * Gets logs.
     *
     * This will export logs based on filter type to export location with export name provided.
     *
     * @return the logs
     */
    fun exportDataLogsForName(name: String, exportDecrypted: Boolean = false): Observable<String> {

        logsConfig?.let {

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

        logsConfig?.let {

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

        logsConfig?.let {

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

            if (logsConfig?.isDebuggable!!)
                Log.e(TAG, "No log type defined for provided type '$type'")

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
     * This will export logs as plain String.
     *
     * @return the String data
     */
    fun printLogsForType(type: ExportType, printDecrypted: Boolean = false): Observable<String> {
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
    }

    /**
     * Clear all zipped loges from storage directory.
     *
     */
    fun clearExportedLogs() {
        File(outputPath).deleteRecursively()
    }
}
