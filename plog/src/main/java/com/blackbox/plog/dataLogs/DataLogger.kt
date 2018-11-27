package com.blackbox.plog.dataLogs

import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.impl.LogWriter
import com.blackbox.plog.pLogs.impl.PLogImpl
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
    val autoExportTypes = PLogImpl.logsConfig?.autoExportLogTypes!!

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

        if (PLog.isLogsConfigSet()) {
            writeDataLog(dataToWrite, overwrite = true)
        } else {
            Log.i(PLog.TAG, dataToWrite)
        }
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

        if (PLog.isLogsConfigSet()) {
            writeDataLog(dataToWrite, overwrite = false)
        } else {
            Log.i(PLog.TAG, dataToWrite)
        }
    }

    private fun autoExportLogType(data: String, type: String) {

        if (autoExportTypes.contains(type)) {
            if (Triggers.shouldExportLogs()) {
                PLog.getLogBus().send(LogEvents(EventTypes.LOG_TYPE_EXPORTED, data))
            }
        }
    }

    private fun writeDataLog(dataToWrite: String?, overwrite: Boolean = false) {

        dataToWrite?.let {

            val logFilePath = setupFilePaths(logFileName, isPLog = false)
            dataLoggerCalledBeforePLoggerException()

            val f = checkFileExists(logFilePath, isPLog = false)

            val shouldLog = if (!PART_FILE_CREATED_DATALOG) {
                LogWriter.shouldWriteLog(f, isPLog = false, logFileName = logFileName)
            } else {
                LogWriter.shouldWriteLog(File(CURRENT_PART_FILE_PATH_DATALOG), isPLog = false, logFileName = logFileName)
            }

            if (PLogImpl.logsConfig?.encryptionEnabled!!) {

                val secretKey = PLogImpl.logsConfig?.secretKey!!

                if (shouldLog.first) {
                    if (overwrite)
                        writeToFileEncrypted(it, secretKey, shouldLog.second)
                    else
                        appendToFileEncrypted(it, secretKey, shouldLog.second)
                }

            } else {

                if (shouldLog.first) {
                    if (overwrite)
                        writeToFile(shouldLog.second, it)
                    else
                        appendToFile(shouldLog.second, it)
                }
            }

            if (PLogImpl.logsConfig?.isDebuggable!!)
                Log.i(PLog.TAG, it)

            //Check if auto Export is enabled, and then  export it
            autoExportLogType(it, logFileName)
        }
    }
}
