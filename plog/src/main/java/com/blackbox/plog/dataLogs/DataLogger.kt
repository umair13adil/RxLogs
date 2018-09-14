package com.blackbox.plog.dataLogs

import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.operations.Triggers
import com.blackbox.plog.utils.*

/**
 * Created by umair on 04/01/2018.
 */
class DataLogger(var logFileName: String = "log") {

    val TAG = "DataLogger"
    val autoExportTypes = PLog.getLogsConfig()?.autoExportLogTypes!!

    /**
     * Overwrite to file.
     *
     * This function will overwrite a 'String' data to a file.
     * File will be created if it doesn't exists in path provided.
     * Filename can contain extension as well e.g 'error_log.txt'.
     * If 'attachTimeStamp' is true filename will contain date & hour in it like: '0105201812_error_log.txt'.
     * Hours are in 24h format, so each file will be unique after an hour.
     *
     *
     * @param dataToWrite the data to write can be any string data formatted or unformatted
     */
    fun overwriteToFile(dataToWrite: String) {

        val logFilePath = setupFilePaths(logFileName)

        dataLoggerCalledBeforePLoggerException()

        if (PLog.getLogsConfig()?.encryptionEnabled!!) {
            writeToFileEncrypted(dataToWrite, PLog.getLogsConfig()?.secretKey!!, logFilePath)
        } else {
            writeToFile(logFilePath, dataToWrite)
        }

        if (PLog.getLogsConfig()?.isDebuggable!!)
            Log.i(PLog.TAG, "Written: $dataToWrite in '$logFilePath'")

        //Check if auto Export is enabled, and then  export it
        autoExportLogType(dataToWrite, logFileName)
    }

    /**
     * Append to file.
     *
     * This function will append a 'String' data to a file with new line inserted.
     * File will be created if it doesn't exists in path provided.
     * Filename can contain extension as well e.g 'error_log.txt'.
     * If 'attachTimeStamp' is true filename will contain date & hour in it like: '0105201812_error_log.txt'.
     * Hours are in 24h format, so each file will be unique after an hour.
     *
     *
     * @param dataToWrite the data to write can be any string data formatted or unformatted
     */
    fun appendToFile(dataToWrite: String) {

        val logFilePath = setupFilePaths(logFileName)

        dataLoggerCalledBeforePLoggerException()

        if (PLog.getLogsConfig()?.encryptionEnabled!!) {
            appendToFileEncrypted(dataToWrite, PLog.getLogsConfig()?.secretKey!!, logFilePath)
        } else {
            appendToFile(logFilePath, dataToWrite)
        }

        if (PLog.getLogsConfig()?.isDebuggable!!)
            Log.i(PLog.TAG, "Appended: $dataToWrite in '$logFilePath'")

        //Check if auto Export is enabled, and then  export it
        autoExportLogType(dataToWrite, logFileName)
    }

    private fun autoExportLogType(data: String, type: String) {

        if (autoExportTypes.contains(type)) {
            if (Triggers.shouldExportLogs()) {
                PLog.getLogBus().send(LogEvents(EventTypes.LOG_TYPE_EXPORTED, data))
            }
        }
    }
}
