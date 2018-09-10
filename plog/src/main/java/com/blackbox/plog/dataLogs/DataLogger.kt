package com.blackbox.plog.dataLogs

import com.blackbox.plog.dataLogs.exporter.DataLogsExporter
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.exporter.ExportType
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.utils.*
import io.reactivex.Observable
import java.io.File

/**
 * Created by umair on 04/01/2018.
 */
class DataLogger(
        var exportPath: String = FilterUtils.getPathForType(ExportType.TODAY),
        var logFileName: String = "log",
        var savePath: String = setupFilePaths(logFileName),
        var zipFileName: String = PLog.logsConfig?.zipFileName!!
) {

    companion object {
        private val TAG = "DataLogger"
    }

    /**
     * Gets logs.
     *
     * This will export logs based on filter type to export location with export name provided.
     *
     * @return the logs
     */
    fun getZippedLogs(exportDecrypted: Boolean): Observable<String> {
        return DataLogsExporter.getDataLogs(logFileName, logPath, zipFileName, outputPath, exportDecrypted)
    }

    /**
     * Gets logs.
     *
     * This will export logs as plain String.
     *
     * @return the String data
     */
    fun getLoggedData(printDecrypted: Boolean): Observable<String> {
        return DataLogsExporter.getLoggedData(logFileName, logPath, zipFileName, outputPath, printDecrypted)
    }

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

        dataLoggerCalledBeforePLoggerException()

        if (PLog.logsConfig?.encryptionEnabled!!) {
            writeToFileEncrypted(dataToWrite, PLog.logsConfig?.secretKey!!, logPath)
        } else {
            writeToFile(logPath, dataToWrite)
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

        dataLoggerCalledBeforePLoggerException()

        if (PLog.logsConfig?.encryptionEnabled!!) {
            appendToFileEncrypted(dataToWrite, PLog.logsConfig?.secretKey!!, logPath)
        } else {
            appendToFile(logPath, dataToWrite)
        }
    }

    /**
     * Clear logs boolean.
     *
     * Will return true if delete was successful
     *
     * @return the boolean
     */
    fun clearLogs() {
        File(logPath).deleteRecursively()
    }

    /**
     * Gets output path.
     *
     * Sets the export path of Logs.
     *
     * @return the output path
     */
    private val outputPath: String
        get() = exportPath + File.separator

    /**
     * Gets Logs path.
     *
     * Sets the save path of Logs.
     *
     * @return the save path
     */
    private val logPath: String
        get() = savePath + File.separator
}
