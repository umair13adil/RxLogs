package com.blackbox.plog.pLogs.impl

import android.util.Log
import com.blackbox.plog.dataLogs.DataLogger
import com.blackbox.plog.dataLogs.exporter.DataLogsExporter
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.config.ConfigReader
import com.blackbox.plog.pLogs.config.ConfigWriter
import com.blackbox.plog.pLogs.config.LogsConfig
import com.blackbox.plog.pLogs.config.isLogLevelEnabled
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.exporter.ExportType
import com.blackbox.plog.pLogs.exporter.LogExporter
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.pLogs.formatter.LogFormatter
import com.blackbox.plog.pLogs.models.LogData
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.operations.doOnInit
import com.blackbox.plog.pLogs.utils.*
import com.blackbox.plog.utils.*
import com.blackbox.plog.utils.Utils.bytesToReadable
import com.blackbox.plog.utils.Utils.createDirIfNotExists
import com.blackbox.plog.utils.Utils.getStackTrace
import io.reactivex.Observable
import java.io.File

open class PLogImpl : PLogger {

    val TAG = "PLogger"

    private lateinit var bus: RxBus

    internal var logsConfig: LogsConfig? = null
    internal var logTypes = hashMapOf<String, DataLogger>()

    internal fun getLogBus(): RxBus {
        return bus
    }

    fun setLogBus(listener: RxBus) {
        bus = listener
    }

    /**
     * Gets output path.
     *
     * Sets the export path of Logs.
     *
     * @return the output path
     */
    internal val outputPath: String
        get() = getExportPath(logsConfig)

    internal val exportTempPath: String
        get() = getTempExportPath(logsConfig)

    /**
     * Gets Logs path.
     *
     * Sets the save path of Logs.
     *
     * @return the save path
     */
    internal val logPath: String
        get() = getLogPath(logsConfig)

    /*
     * This will set logs configuration.
     *
     * @param 'saveToFile' if true, file will be written to storage
     */
    override fun applyConfigurations(config: LogsConfig, saveToFile: Boolean) {

        //Create 'Save' Path
        createDirIfNotExists(config.savePath)

        PLog.logsConfig = config

        if (saveToFile) {
            //Only save if parameter value 'true'
            if (!PLog.localConfigurationExists()) {
                ConfigWriter.saveToXML(config)
            }
        }

        //Perform operations on Initializing.
        doOnInit()
    }

    /*
     * This will forcefully overwrite existing logs configuration.
     */
    override fun forceWriteLogsConfig(config: LogsConfig) {
        PLog.logsConfig = config

        ConfigWriter.saveToXML(config)

        //Perform operations on Initializing.
        doOnInit()
    }

    /*
     * Get LogsConfig XML file.
     */
    override fun getLogsConfigFromXML(): LogsConfig? {

        if (localConfigurationExists()) {
            PLog.logsConfig = ConfigReader.readXML()
            return PLog.logsConfig
        }

        return null
    }

    /*
     * Get LogsConfig object.
     */
    override fun getLogsConfig(): LogsConfig? {

        if (isLogsConfigSet())
            return PLog.logsConfig

        return null
    }

    /*
     * Will send 'true' if local configuration XML exists.
     */
    fun localConfigurationExists(): Boolean {
        return File(XML_PATH, CONFIG_FILE_NAME).exists()
    }

    /*
     * Will send 'true' if local configuration XML is deleted.
     */
    fun deleteLocalConfiguration(): Boolean {
        return File(XML_PATH, CONFIG_FILE_NAME).delete()
    }

    /*
     * Check if logs configuration file is set.
     */
    fun isLogsConfigSet(): Boolean {

        PLog.logsConfig?.let {
            return true
        }

        throw IllegalArgumentException(Throwable("No logs configuration provided!"))
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
    override fun logThis(className: String, functionName: String, text: String, type: LogLevel) {

        //Do nothing if logs are disabled
        if (!PLog.getLogsConfig()?.enabled!!)
            return

        //Do nothing if log level type is disabled
        if (!isLogLevelEnabled(type))
            return

        if (PLog.getLogsConfig()?.encryptionEnabled!!) {
            writeEncryptedLogs(className, functionName, text, type.level)
        } else {
            writeSimpleLogs(className, functionName, text, type.level)
        }

        //Check if log level is of Error
        autoExportError(text, type)
    }

    /**
     * Log Exception.
     *
     * Logs 'String' data along with class & function name to hourly based file with formatted timestamps.
     *
     * @param className    the class name
     * @param functionName the function name
     * @param e             Exception
     * @param type         the type
     */
    override fun logExc(className: String, functionName: String, e: Throwable, type: LogLevel) {

        //Do nothing if logs are disabled
        if (!PLog.getLogsConfig()?.enabled!!)
            return

        //Do nothing if log level type is disabled
        if (!isLogLevelEnabled(type))
            return

        val data = getStackTrace(e)

        if (PLog.getLogsConfig()?.encryptionEnabled!!) {
            writeEncryptedLogs(className, functionName, data, type.level)
        } else {
            writeSimpleLogs(className, functionName, data, type.level)
        }

        //Check if log level is of Error
        autoExportError(data, type)
    }

    /**
     * Log Exception.
     *
     * Logs 'String' data along with class & function name to hourly based file with formatted timestamps.
     *
     * @param className    the class name
     * @param functionName the function name
     * @param e             Exception
     * @param type         the type
     */
    override fun logExc(className: String, functionName: String, e: Exception, type: LogLevel) {

        //Do nothing if logs are disabled
        if (!PLog.getLogsConfig()?.enabled!!)
            return

        //Do nothing if log level type is disabled
        if (!isLogLevelEnabled(type))
            return

        val data = getStackTrace(e)

        if (PLog.getLogsConfig()?.encryptionEnabled!!) {
            writeEncryptedLogs(className, functionName, data, type.level)
        } else {
            writeSimpleLogs(className, functionName, data, type.level)
        }

        //Check if log level is of Error
        autoExportError(data, type)
    }

    /**
     * Gets logs.
     *
     * This will export logs based on filter type to export location with export name provided.
     *
     * @param type the type
     * @return the logs
     */
    fun exportLogsForType(type: ExportType, exportDecrypted: Boolean = false): Observable<String> {
        return LogExporter.getZippedLogs(type.type, exportDecrypted)
    }

    /**
     * Gets logs.
     *
     * This will export logs as plain String.
     *
     * @return the String data
     */
    fun printLogsForType(type: ExportType, printDecrypted: Boolean = false): Observable<String> {
        return LogExporter.printLogsForType(type.type, printDecrypted)
    }

    /**
     * Clear all logs from storage directory.
     *
     */
    override fun clearLogs() {
        val rootFolderName = LOG_FOLDER
        val rootFolderPath = PLog.logPath + rootFolderName + File.separator
        File(rootFolderPath).deleteRecursively()
    }

    /**
     * Clear all zipped loges from storage directory.
     *
     */
    override fun clearExportedLogs() {
        File(PLog.outputPath).deleteRecursively()
    }

    /*
     * Write plain String logs.
     */
    private fun writeSimpleLogs(className: String, functionName: String, text: String, type: String) {
        val path = setupFilePaths()

        val f = checkFileExists(path)

        if (PLog.shouldWriteLog(f)) {

            val logData = LogData(className, functionName, text, getFormattedTimeStamp(), type)

            val logFormatted = LogFormatter.getFormatType(logData)

            if (PLog.getLogsConfig()?.isDebuggable!!)
                Log.i(PLog.TAG, logFormatted)

            appendToFile(path, logFormatted)
        }
    }

    /*
     * Write AES encrypted String logs.
     */
    private fun writeEncryptedLogs(className: String, functionName: String, text: String, type: String) {

        if (PLog.getLogsConfig()?.secretKey == null)
            return

        val path = setupFilePaths()

        val f = checkFileExists(path)

        if (PLog.shouldWriteLog(f)) {

            val logData = LogData(className, functionName, text, getFormattedTimeStamp(), type)

            val logFormatted = LogFormatter.getFormatType(logData)

            if (PLog.getLogsConfig()?.isDebuggable!!)
                Log.i(PLog.TAG, logFormatted)

            appendToFileEncrypted(logFormatted, PLog.getLogsConfig()?.secretKey!!, path)

        }
    }


    /*
     * This will return observable to subscribe to logger events.
     */
    override fun getLogEvents(): Observable<LogEvents> {

        return Observable.create { it ->
            val emitter = it
            PLog.getLogBus()
                    .toObservable()
                    .doOnError {

                        if (PLog.getLogsConfig()?.isDebuggable!!)
                            Log.e(TAG, "Error '${it.message}'")

                    }.subscribe {

                        if (it is LogEvents) {
                            emitter.onNext(it)
                        }
                    }
        }
    }

    /*
     * This will return 'DataLogger' for log type defined in Config File.
     */
    override fun getLoggerFor(type: String): DataLogger? {

        if (PLog.isLogsConfigSet()) {
            if (PLog.logTypes.containsKey(type))
                return PLog.logTypes.get(type)

            if (PLog.getLogsConfig()?.isDebuggable!!)
                Log.e(TAG, "No log type defined for provided type '$type'")

            return null
        } else {
            return null
        }
    }

    /**
     * Gets logs.
     *
     * This will export logs based on filter type to export location with export name provided.
     *
     * @return the logs
     */
    fun exportDataLogsForName(name: String, exportDecrypted: Boolean = false): Observable<String> {
        val path = getLogsSavedPaths(PLog.getLogsConfig()?.nameForEventDirectory!!)
        return DataLogsExporter.getDataLogs(name, path, outputPath, exportDecrypted)
    }

    /**
     * Gets logs.
     *
     * This will export logs based on filter type to export location with export name provided.
     *
     * @return the logs
     */
    fun exportAllDataLogs(exportDecrypted: Boolean = false): Observable<String> {
        val path = getLogsSavedPaths(PLog.getLogsConfig()?.nameForEventDirectory!!, isForAll = true)
        return DataLogsExporter.getDataLogs("", path, outputPath, exportDecrypted)
    }

    /**
     * Gets logs.
     *
     * This will export logs as plain String.
     *
     * @return the String data
     */
    fun printDataLogsForName(name: String, printDecrypted: Boolean = false): Observable<String> {
        val path = getLogsSavedPaths(PLog.getLogsConfig()?.nameForEventDirectory!!)
        return DataLogsExporter.printLogsForName(name, path, printDecrypted)
    }

    private fun autoExportError(data: String, type: LogLevel) {
        if (type == LogLevel.ERROR || type == LogLevel.SEVERE) {
            if (PLog.getLogsConfig()?.autoExportErrors!!) {
                //Send event to notify error is reported
                PLog.getLogBus().send(LogEvents(EventTypes.NEW_ERROR_REPORTED, data))
            }
        }
    }

    fun getFormattedTimeStamp(): String {
        return DateTimeUtils.getTimeFormatted(PLog.getLogsConfig()?.timeStampFormat!!)
    }

    fun getListOfExportedFiles(): List<File> {
        return FilterUtils.listFiles(outputPath, arrayListOf())
    }

    /*
     * Verify if logs can be written.
     */
    fun shouldWriteLog(file: File): Boolean {

        if (file.length() > 0) {
            val length = file.length()
            val maxLength = PLog.getLogsConfig()?.singleLogFileSize!! * (1024 * 1024)

            if (length > maxLength) {

                if (PLog.getLogsConfig()?.isDebuggable!!)
                    Log.i(PLog.TAG, "File size exceeded!")

                return false
            } else {
                if (PLog.getLogsConfig()?.isDebuggable!!)
                    Log.i(PLog.TAG, "File Length: ${bytesToReadable(length.toInt())} < ${bytesToReadable(maxLength)}")
            }
        }

        //TODO update no of files created in config XML for easy access
        /*val totalFiles = FilterUtils.listFiles(logPath, arrayListOf())
        if (totalFiles.isNotEmpty()) {
            if (totalFiles.size > PLog.getLogsConfig()?.logFilesLimit!!)

                if (PLog.getLogsConfig()?.isDebuggable!!)
                    Log.i(PLog.TAG, "No of log files exceeded!")

            return false
        }*/

        return true
    }
}