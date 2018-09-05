package com.blackbox.plog.pLogs.models

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.utils.checkIfKeyValid
import com.blackbox.plog.utils.generateKey

/**
 * Created by umair on 03/01/2018.
 */

class PLogBuilder {

    private var savePath: String = ""
    private var exportPath: String = ""
    private var logFileExtension: String = ""
    private var exportFileName: String = ""
    private var attachTimeStamp: Boolean = false
    private var attachNoOfFiles: Boolean = false
    private var debug: Boolean = false
    private var silentLogs: Boolean = false
    private var formatType: String = ""
    private var customFormatOpen: String = ""
    private var customFormatClose: String = ""
    private var csvDeliminator: String = ""
    private var timeStampFormat: String = ""
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
    fun setLogsSavePath(path: String): PLogBuilder {
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
    fun setLogsExportPath(path: String): PLogBuilder {
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
    fun setExportFileName(fileName: String): PLogBuilder {
        this.exportFileName = fileName
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
    fun attachTimeStampToFiles(attach: Boolean): PLogBuilder {
        this.attachTimeStamp = attach
        return this
    }

    /**
     * Attach no of files to files p log builder.
     *
     *
     *
     * Will attach no of files zipped to export file name.
     *
     * @param attach the attach
     * @return the p log builder
     */
    fun attachNoOfFilesToFiles(attach: Boolean): PLogBuilder {
        this.attachNoOfFiles = attach
        return this
    }

    /**
     * Log silently p log builder.
     *
     *
     *
     * If true will not print logs in LogCat.
     * It is true by default.
     *
     * @param silentLogs the silent logs
     * @return the p log builder
     */
    fun logSilently(silentLogs: Boolean): PLogBuilder {
        this.silentLogs = silentLogs
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
    fun debuggable(debug: Boolean): PLogBuilder {
        this.debug = debug
        return this
    }

    /**
     * Sets log format type.
     *
     *
     *
     * Sets the default format type.
     * Can choose from 'LogFormatter' class:
     * Following types are available:
     *
     *
     * 1. LogFormatter.FORMAT_CURLY
     * 2. LogFormatter.FORMAT_SQUARE
     * 3. LogFormatter.FORMAT_CSV
     * 4. LogFormatter.FORMAT_CUSTOM
     *
     *
     * CSV format will require a deliminator, by default it is comma ','.
     * Custom formats will require opening & closing characters. Like {},[], ' ' etc
     *
     *
     * @param type the type
     * @return the log format type
     */
    fun setLogFormatType(type: String): PLogBuilder {
        this.formatType = type
        return this
    }

    /**
     * Sets custom format open.
     *
     *
     *
     * Could be any character like: '{' , '[' etc
     *
     * @param f the f
     * @return the custom format open
     */
    fun setCustomFormatOpen(f: String): PLogBuilder {
        this.customFormatOpen = f
        return this
    }

    /**
     * Sets custom format close.
     *
     *
     *
     * Could be any character like: '}' , ']' etc
     *
     * @param f the f
     * @return the custom format close
     */
    fun setCustomFormatClose(f: String): PLogBuilder {
        this.customFormatClose = f
        return this
    }

    /**
     * Sets csv deliminator.
     *
     *
     *
     * Could be any deliminator like: ';' , ':' etc
     *
     * @param f the f
     * @return the csv deliminator
     */
    fun setCSVDeliminator(f: String): PLogBuilder {
        this.csvDeliminator = f
        return this
    }

    /**
     * Sets time stamp format.
     *
     *
     * Sets default time stamp format of logs.
     * e.g DDMMYYY
     *
     * @param f the f
     * @return the time stamp format
     */
    fun setTimeStampFormat(f: String): PLogBuilder {
        this.timeStampFormat = f
        return this
    }

    /**
     * Sets log file extension.
     *
     *
     * By default extension is '.txt'.
     * If you don't want any extensions in logs then pass empty string to this value.
     *
     * @param f the f
     * @return the log file extension
     */
    fun setLogFileExtension(f: String): PLogBuilder {
        this.logFileExtension = f
        return this
    }

    /**
     * Enables AES encrypted logging.
     * By default encryption is disabled.
     *
     * @param encrypt to enable/disable encryption
     */
    fun enableEncryption(encrypt: Boolean): PLogBuilder {
        this.encrypt = encrypt
        return this
    }

    /**
     * Sets encryption key for AES encrypted logging.
     * Length of key should be at-least 32, otherwise exception will be thrown.
     *
     * @param encryptionKey Salt
     */
    fun setEncryptionKey(encryptionKey: String): PLogBuilder {
        this.encryptionKey = encryptionKey
        return this
    }

    /**
     * Enables Logging & file writing.
     * By default always enabled.
     *
     * @param enabled to enable/disable logging
     */
    fun enabled(enabled: Boolean): PLogBuilder {
        this.enabled = enabled
        return this
    }

    fun build(): PLogger {
        val pLogger = PLogger(savePath, exportPath, exportFileName, attachTimeStamp, attachNoOfFiles, formatType, customFormatOpen, customFormatClose, csvDeliminator, debug, timeStampFormat, logFileExtension, silentLogs, encrypt, encryptionKey, enabled)
        PLog.pLogger = pLogger

        //Initializes Encryption Key
        setupEncryption(pLogger)

        FilterUtils.clearOutputFiles(pLogger.exportPath)

        return pLogger
    }

    private fun setupEncryption(pLogger: PLogger) {

        if (pLogger.encrypt) {
            val key = checkIfKeyValid(pLogger.encryptionKey)
            pLogger.secretKey = generateKey(key)
        }
    }
}
