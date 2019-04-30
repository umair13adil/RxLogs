package com.blackbox.plog.dataLogs

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.impl.LogWriter
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.pLogs.operations.Triggers
import com.blackbox.plog.pLogs.utils.CURRENT_PART_FILE_PATH_DATALOG
import com.blackbox.plog.pLogs.utils.PART_FILE_CREATED_DATALOG
import com.blackbox.plog.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * Created by umair on 2019-02-12 09:12
 * for CubiVue Logs
 */
object DataLogWriter {

    private val autoExportTypes = PLogImpl.getConfig()?.autoExportLogTypes!!

    fun writeDataLog(logFileName: String, dataToWrite: String?, overwrite: Boolean = false) {

        dataToWrite?.let { data ->

            val logFilePath = setupFilePaths(logFileName, isPLog = false)
            dataLoggerCalledBeforePLoggerException()

            val f = checkFileExists(logFilePath, isPLog = false)

            val shouldLog = if (!PART_FILE_CREATED_DATALOG) {
                LogWriter.shouldWriteLog(f, isPLog = false, logFileName = logFileName)
            } else {
                LogWriter.shouldWriteLog(File(CURRENT_PART_FILE_PATH_DATALOG), isPLog = false, logFileName = logFileName)
            }

            if (PLogImpl.getConfig()?.encryptionEnabled!!) {

                PLogImpl.getConfig()?.secretKey?.let {

                    val secretKey = it

                    if (shouldLog.first) {
                        if (overwrite)
                            PLogImpl.encrypter.writeToFileEncrypted(data, secretKey, shouldLog.second)
                        else
                            PLogImpl.encrypter.appendToFileEncrypted(data, secretKey, shouldLog.second)
                    }
                }

            } else {

                if (shouldLog.first) {
                    if (overwrite)
                        writeToFile(shouldLog.second, data)
                    else
                        appendToFile(shouldLog.second, data)
                }
            }

            //Check if auto Export is enabled, and then  export it
            autoExportLogType(data, logFileName)
        }
    }

    private fun autoExportLogType(data: String, type: String) {

        if (autoExportTypes.contains(type)) {
            if (Triggers.shouldExportLogs()) {
                PLog.getLogBus().send(LogEvents(EventTypes.LOG_TYPE_EXPORTED, data))
            }
        }
    }
}