package com.blackbox.plog.pLogs

/**
 * Created by Umair Adil on 12/04/2017.
 */

import android.util.Log
import com.blackbox.plog.utils.DateControl
import com.blackbox.plog.utils.DateTimeUtils
import com.blackbox.plog.utils.Utils
import io.reactivex.Observable
import java.io.File
import java.nio.charset.Charset

object PLog {

    private val TAG = PLog::class.java.simpleName

    lateinit var pLogger: PLogger

    //Log Filters
    val LOG_TODAY = 1
    val LOG_LAST_HOUR = 2
    val LOG_WEEK = 3
    val LOG_LAST_TWO_DAYS = 4

    //Log Types
    val TYPE_INFO = "Info"
    val TYPE_ERROR = "Error"
    val TYPE_WARNING = "Warning"


    /**
     * Gets output path.
     *
     *
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
     *
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
     *
     *
     * Logs 'String' data along with class & function name to hourly based file with formatted timestamps.
     *
     * @param className    the class name
     * @param functionName the function name
     * @param text         the text
     * @param type         the type
     */
    fun logThis(className: String, functionName: String, text: String, type: String) {

        //Make sure what is logged is unique
        if (!Utils.getInstance().isLoggedOnce(text)) {

            val folderPath = logPath + DateControl.getInstance().today
            Utils.getInstance().createDirIfNotExists(folderPath)

            val fileName_raw = DateControl.getInstance().today + DateControl.getInstance().hour
            val path = folderPath + File.separator + fileName_raw + pLogger.logFileExtension

            if (!File(path).exists())
                File(path).createNewFile()

            val logData = LogData(className, functionName, text, DateTimeUtils.getTimeFormatted(pLogger.timeStampFormat), type)

            val logFormatted = LogFormatter.getFormatType(logData, pLogger)

            if (PLog.pLogger.isDebuggable)
                Log.i(TAG, logFormatted)

            appendToFile(path, logFormatted)
        }
    }

    /**
     * Gets logs.
     *
     *
     *
     * This will export logs based on filter type to export location with export name provided.
     *
     * @param type the type
     * @return the logs
     */
    fun getLogs(type: Int): Observable<String> {
        return LogExporter.getLogs(type, pLogger.attachTimeStamp, pLogger.attachNoOfFiles, logPath, pLogger.exportFileName, outputPath)
    }

    /**
     * Clear logs boolean.
     *
     *
     *
     * Will return true if delete was successful
     *
     * @return the boolean
     */
    fun clearLogs(): Boolean {
        return Utils.getInstance().deleteDir(File(logPath))
    }

    fun writeToFile(path: String, data: String) {
        try {
            if (File(path).exists()) {
                File(path).printWriter().use { out ->
                    out.println(data)
                }
            } else {
                File(path).createNewFile()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun appendToFile(path: String, data: String) {
        try {
            if (File(path).exists()) {
                File(path).appendText(data, Charset.defaultCharset())
            } else {
                File(path).createNewFile()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
