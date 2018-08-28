package com.blackbox.plog.pLogs

/**
 * Created by Umair Adil on 12/04/2017.
 */

import android.util.Log
import com.blackbox.plog.pLogs.exporter.LogExporter
import com.blackbox.plog.pLogs.formatter.LogFormatter
import com.blackbox.plog.pLogs.models.LogData
import com.blackbox.plog.pLogs.models.PLogger
import com.blackbox.plog.utils.*
import io.reactivex.Observable
import java.io.File

object PLog {

    private val TAG = PLog::class.java.simpleName

    lateinit var pLogger: PLogger

    //Log Filters
    val LOG_TODAY = 1
    val LOG_LAST_HOUR = 2
    val LOG_WEEK = 3
    val LOG_LAST_24_HOURS = 4

    //Log Types
    val TYPE_INFO = "Info"
    val TYPE_ERROR = "Error"
    val TYPE_WARNING = "Warning"


    /**
     * Gets output path.
     *
     * Sets the export path of Logs.
     *
     * @return the output path
     */
    val outputPath: String
        get() = pLogger.exportPath + File.separator

    /**
     * Gets Logs path.
     *
     * Sets the save path of Logs.
     *
     * @return the save path
     */
    private val logPath: String
        get() = pLogger.savePath + File.separator

    internal fun setPLogger(pLog: PLogger) {
        pLogger = pLog
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
    fun logThis(className: String, functionName: String, text: String, type: String) {

        //Do nothing if logs are disabled
        if (!pLogger.enabled)
            return

        if (pLogger.encrypt) {
            writeEncryptedLogs(className, functionName, text, type)
        } else {
            writeSimpleLogs(className, functionName, text, type)
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
    fun getZippedLog(type: Int): Observable<String> {
        return LogExporter.getZippedLogs(type, pLogger.attachTimeStamp, pLogger.attachNoOfFiles, logPath, pLogger.exportFileName, outputPath)
    }

    /**
     * Gets logs.
     *
     * This will export logs as plain String.
     *
     * @return the String data
     */
    fun getLoggedData(type: Int): Observable<String> {
        return LogExporter.getLoggedData(type, logPath, outputPath, pLogger.encrypt, pLogger.secretKey)
    }

    /**
     * Clear all logs from storage directory.
     *
     */
    fun clearLogs() {
        Utils.instance.deleteDir(File(PLog.logPath))
    }

    /*
     * Write plain String logs.
     */
    private fun writeSimpleLogs(className: String, functionName: String, text: String, type: String) {
        val path = setupFilePaths(PLog.logPath)

        checkFileExists(path)

        val logData = LogData(className, functionName, text, DateTimeUtils.getTimeFormatted(pLogger.timeStampFormat), type)

        val logFormatted = LogFormatter.getFormatType(logData, pLogger)

        if (PLog.pLogger.isDebuggable)
            Log.i(TAG, logFormatted)

        appendToFile(path, logFormatted)
    }

    /*
     * Write AES encrypted String logs.
     */
    private fun writeEncryptedLogs(className: String, functionName: String, text: String, type: String) {

        if (pLogger.secretKey == null)
            return

        val path = setupFilePaths(PLog.logPath)

        checkFileExists(path)

        val logData = LogData(className, functionName, text, DateTimeUtils.getTimeFormatted(pLogger.timeStampFormat), type)

        val logFormatted = LogFormatter.getFormatType(logData, pLogger)

        if (PLog.pLogger.isDebuggable)
            Log.i(TAG, logFormatted)

        appendToFileEncrypted(logFormatted, pLogger.secretKey!!, path)
    }
}
