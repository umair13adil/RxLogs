package com.blackbox.plog.dataLogs

import android.os.Environment
import com.blackbox.plog.dataLogs.exporter.DataLogsExporter
import com.blackbox.plog.utils.*
import io.reactivex.Observable
import java.io.File
import javax.crypto.SecretKey

/**
 * Created by umair on 03/01/2018.
 */

class DataLogger internal constructor(savePath: String, exportPath: String, exportFileName: String, logFileName: String, attachTimeStamp: Boolean?, debug: Boolean?, encryptionEnabled: Boolean, encryptionKey: String, loggingEnabled: Boolean) {

    private var savePath = Environment.getExternalStorageDirectory().toString() + File.separator + TAG
    private var exportPath = Environment.getExternalStorageDirectory().toString() + File.separator + TAG
    private var exportFileName = "Output"
    private var logFileName = "log"
    private var attachTimeStamp = true
    private var debug = false
    internal var encrypt: Boolean = false
    internal var encryptionKey: String = ""
    internal var enabled: Boolean = true
    internal var secretKey: SecretKey? = null

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

    /**
     * Gets logs.
     *
     * This will export logs based on filter type to export location with export name provided.
     *
     * @return the logs
     */
    fun getZippedLogs(exportDecrypted: Boolean): Observable<String> {

        var isEncrypted = encrypt

        //If not encrypted
        if (exportDecrypted && !encrypt)
            isEncrypted = false
        else
            isEncrypted = exportDecrypted

        return DataLogsExporter.getDataLogs(logFileName, attachTimeStamp, logPath, exportFileName, outputPath, debug, isEncrypted, secretKey)
    }

    /**
     * Gets logs.
     *
     * This will export logs as plain String.
     *
     * @return the String data
     */
    fun getLoggedData(printDecrypted: Boolean): Observable<String> {

        val isEncrypted: Boolean

        //If not encrypted
        if (printDecrypted && !encrypt)
            isEncrypted = false
        else
            isEncrypted = printDecrypted

        return DataLogsExporter.getLoggedData(logFileName, attachTimeStamp, logPath, exportFileName, outputPath, debug, isEncrypted, secretKey)
    }

    init {
        this.savePath = savePath
        this.exportPath = exportPath
        this.exportFileName = exportFileName
        this.logFileName = logFileName
        this.attachTimeStamp = attachTimeStamp!!
        this.debug = debug!!
        this.encrypt = encryptionEnabled
        this.encryptionKey = encryptionKey
        this.enabled = loggingEnabled
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
        val path = setupPaths()

        if (encrypt) {
            writeToFileEncrypted(dataToWrite, secretKey!!, path)
        } else {
            writeToFile(path, dataToWrite)
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

        val path = setupPaths()

        if (encrypt) {
            appendToFileEncrypted(dataToWrite, secretKey!!, path)
        } else {
            appendToFile(path, dataToWrite)
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

    companion object {

        private val TAG = DataLogger::class.java.simpleName
    }

    private fun setupPaths(): String {
        Utils.instance.createDirIfNotExists(logPath)

        var fileName_raw = ""

        if (attachTimeStamp)
            fileName_raw = DateControl.instance.today + DateControl.instance.hour + "_" + logFileName
        else
            fileName_raw = logFileName

        return logPath + File.separator + fileName_raw
    }
}
