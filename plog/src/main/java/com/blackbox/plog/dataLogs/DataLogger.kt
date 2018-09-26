package com.blackbox.plog.dataLogs

import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.operations.Triggers
import com.blackbox.plog.pLogs.utils.CURRENT_PART_FILE_PATH_DATALOG
import com.blackbox.plog.pLogs.utils.PART_FILE_CREATED_DATALOG
import com.blackbox.plog.utils.*
import java.io.File

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

        val logFilePath = setupFilePaths(logFileName, isPLog = false)
        dataLoggerCalledBeforePLoggerException()

        val shouldLog: Pair<Boolean, String>
        val f = checkFileExists(logFilePath, isPLog = false)

        if (!PART_FILE_CREATED_DATALOG) {
            shouldLog = PLog.shouldWriteLog(f, isPLog = false, logFileName = logFileName)
        } else {
            shouldLog = PLog.shouldWriteLog(File(CURRENT_PART_FILE_PATH_DATALOG), isPLog = false, logFileName = logFileName)
        }

        if (PLog.getLogsConfig()?.encryptionEnabled!!) {

            if (shouldLog.first) {
                writeToFileEncrypted(dataToWrite, PLog.getLogsConfig()?.secretKey!!, shouldLog.second)
            }

        } else {

            if (shouldLog.first) {
                writeToFile(shouldLog.second, dataToWrite)
            }
        }

        if (PLog.getLogsConfig()?.isDebuggable!!)
            Log.i(PLog.TAG, dataToWrite)

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

        val logFilePath = setupFilePaths(logFileName, isPLog = false)
        dataLoggerCalledBeforePLoggerException()

        val shouldLog: Pair<Boolean, String>
        val f = checkFileExists(logFilePath, isPLog = false)

        if (!PART_FILE_CREATED_DATALOG) {
            shouldLog = PLog.shouldWriteLog(f, isPLog = false, logFileName = logFileName)
        } else {
            shouldLog = PLog.shouldWriteLog(File(CURRENT_PART_FILE_PATH_DATALOG), isPLog = false, logFileName = logFileName)
        }

        if (PLog.getLogsConfig()?.encryptionEnabled!!) {

            if (shouldLog.first) {
                appendToFileEncrypted(dataToWrite, PLog.getLogsConfig()?.secretKey!!, shouldLog.second)
            }
        } else {

            if (shouldLog.first) {
                appendToFile(shouldLog.second, dataToWrite)
            }
        }

        if (PLog.getLogsConfig()?.isDebuggable!!)
            Log.i(PLog.TAG, dataToWrite)

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
