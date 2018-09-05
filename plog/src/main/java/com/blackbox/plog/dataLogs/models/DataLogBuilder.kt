package com.blackbox.plog.dataLogs.models

import com.blackbox.plog.dataLogs.DataLogger
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.utils.checkIfKeyValid
import com.blackbox.plog.utils.generateKey

/**
 * Created by umair on 04/01/2018.
 */
class DataLogBuilder {

    var savePath: String = ""
    var exportPath: String = ""
    var logFileName: String = ""
    var exportFileName: String = ""
    var attachTimeStamp: Boolean = false
    var debug: Boolean = false
    private var encrypt: Boolean = false
    private var encryptionKey: String = ""
    private var enabled: Boolean = true

    /**
     * Sets logs save path.
     *
     *
     *
     * Should be a String Name. Can have file separators.
     *
     * @param path the path
     * @return the logs save path
     */
    fun setLogsSavePath(path: String): DataLogBuilder {
        this.savePath = path
        return this
    }

    /**
     * Sets logs export path.
     *
     *
     *
     * Should be a String Name. Can have file separators.
     *
     * @param path the path
     * @return the logs export path
     */
    fun setLogsExportPath(path: String): DataLogBuilder {
        this.exportPath = path
        return this
    }

    /**
     * Sets export file name.
     *
     *
     *
     * Should be a String name. like 'output_logs'
     * Don't add any extensions.
     * Default extension will be '.zip'.
     *
     *
     * @param fileName the file name
     * @return the export file name
     */
    fun setExportFileName(fileName: String): DataLogBuilder {
        this.exportFileName = fileName
        return this
    }

    /**
     * Sets log file name.
     *
     *
     *
     * Should be a String name. like 'location_logs.txt'
     *
     *
     * @param fileName the file name
     * @return the log file name
     */
    fun setLogFileName(fileName: String): DataLogBuilder {
        this.logFileName = fileName
        return this
    }

    /**
     * Attach time stamp to files log builder.
     *
     *
     *
     * Will attach time stamps to log and zip files.
     *
     * @param attach the attach
     * @return the p log builder
     */
    fun attachTimeStampToFiles(attach: Boolean): DataLogBuilder {
        this.attachTimeStamp = attach
        return this
    }

    /**
     * Debuggable p log builder.
     *
     *
     *
     * Prints debug logs.
     * Is true by default.
     *
     * @param debug the debug
     * @return the p log builder
     */
    fun debuggable(debug: Boolean): DataLogBuilder {
        this.debug = debug
        return this
    }

    /**
     * Enables AES encrypted logging.
     * By default encryption is disabled.
     *
     * @param encrypt to enable/disable encryption
     */
    fun enableEncryption(encrypt: Boolean): DataLogBuilder {
        this.encrypt = encrypt
        return this
    }

    /**
     * Sets encryption key for AES encrypted logging.
     * Length of key should be at-least 32, otherwise exception will be thrown.
     *
     * @param encryptionKey Salt
     */
    fun setEncryptionKey(encryptionKey: String): DataLogBuilder {
        this.encryptionKey = encryptionKey
        return this
    }

    /**
     * Enables Logging & file writing.
     * By default always enabled.
     *
     * @param enabled to enable/disable logging
     */
    fun enabled(enabled: Boolean): DataLogBuilder {
        this.enabled = enabled
        return this
    }

    fun build(): DataLogger {
        val logger = DataLogger(savePath, exportPath, exportFileName, logFileName, attachTimeStamp, debug, encrypt, encryptionKey, enabled)

        //Initializes Encryption Key
        setupEncryption(logger)

        FilterUtils.clearOutputFiles(exportPath)

        return logger
    }

    private fun setupEncryption(dataLogger: DataLogger) {

        if (dataLogger.encrypt) {
            val key = checkIfKeyValid(dataLogger.encryptionKey)
            dataLogger.secretKey = generateKey(key)
        }
    }
}
