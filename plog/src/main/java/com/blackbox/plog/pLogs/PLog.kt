package com.blackbox.plog.pLogs

/**
 * Created by Umair Adil on 12/04/2017.
 */

import android.util.Log
import com.blackbox.plog.dataLogs.DataLogger
import com.blackbox.plog.pLogs.config.*
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.exporter.ExportType
import com.blackbox.plog.pLogs.exporter.LogExporter
import com.blackbox.plog.pLogs.formatter.LogFormatter
import com.blackbox.plog.pLogs.models.LogData
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.operations.doOnInit
import com.blackbox.plog.utils.*
import io.reactivex.Observable
import java.io.File

object PLog {

    init {

        //Setup RxBus for notifications.
        setLogBus(RxBus())

        Log.i("PLog", "PLogger Initialized!")
    }

    private val TAG = PLog::class.java.simpleName

    @JvmStatic
    private lateinit var bus: RxBus

    internal var logsConfig: LogsConfig? = null
    internal var logTypes = hashMapOf<String, DataLogger>()

    /**
     * Gets output path.
     *
     * Sets the export path of Logs.
     *
     * @return the output path
     */
    internal val outputPath: String
        get() = logsConfig?.exportPath + File.separator

    internal val exportTempPath: String
        get() = outputPath + "Temp" + File.separator

    /**
     * Gets Logs path.
     *
     * Sets the save path of Logs.
     *
     * @return the save path
     */
    internal val logPath: String
        get() = logsConfig?.savePath + File.separator

    internal fun getLogBus(): RxBus {
        return bus
    }

    private fun setLogBus(listener: RxBus) {
        bus = listener
    }

    /*
     * This will set logs configuration.
     *
     * @param 'saveToFile' if true, file will be written to storage
     */
    fun setLogsConfig(config: LogsConfig, saveToFile: Boolean = false) {
        logsConfig = config

        if (saveToFile) {
            //Only save if parameter value 'true'

            if (!localConfigurationExists()) {
                ConfigWriter.saveToXML(config)
            }
        }

        //Perform operations on Initializing.
        doOnInit()
    }

    /*
     * This will forcefully overwrite existing logs configuration.
     */
    fun forceWriteLogsConfig(config: LogsConfig) {
        logsConfig = config

        ConfigWriter.saveToXML(config)

        //Perform operations on Initializing.
        doOnInit()
    }

    /*
     * Get LogsConfig XML file.
     */
    fun getLogsConfigFromXML(): LogsConfig? {

        if (localConfigurationExists())
            logsConfig = ConfigReader.readXML()

        return null
    }

    /*
     * Get LogsConfig object.
     */
    fun getLogsConfig(): LogsConfig? {

        if (isLogsConfigSet())
            return logsConfig

        return null
    }

    /*
     * Will send 'true' if local configuration XML exists.
     */
    fun localConfigurationExists(): Boolean {
        return File(XML_PATH).exists()
    }

    /*
     * Check if logs configuration file is set.
     */
    fun isLogsConfigSet(): Boolean {

        logsConfig?.let {
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
    fun logThis(className: String, functionName: String, text: String, type: LogLevel) {

        //Do nothing if logs are disabled
        if (!logsConfig?.enabled!!)
            return

        //Do nothing if log level type is disabled
        if (!isLogLevelEnabled(type))
            return

        if (logsConfig?.encryptionEnabled!!) {
            writeEncryptedLogs(className, functionName, text, type.level)
        } else {
            writeSimpleLogs(className, functionName, text, type.level)
        }

        //Check if log level is of Error
        if (type == LogLevel.ERROR || type == LogLevel.SEVERE) {
            //Send event to notify error is reported
            PLog.getLogBus().send(LogEvents(EventTypes.NEW_ERROR_REPORTED, text))
        }
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
    fun logExc(className: String, functionName: String, e: Throwable, type: LogLevel = LogLevel.ERROR) {

        //Do nothing if logs are disabled
        if (!logsConfig?.enabled!!)
            return

        //Do nothing if log level type is disabled
        if (!isLogLevelEnabled(type))
            return

        val data = Utils.instance.getStackTrace(e)

        if (logsConfig?.encryptionEnabled!!) {
            writeEncryptedLogs(className, functionName, data, type.level)
        } else {
            writeSimpleLogs(className, functionName, data, type.level)
        }

        //Send event to notify error is reported
        PLog.getLogBus().send(LogEvents(EventTypes.NEW_ERROR_REPORTED, data = data))
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
    fun logExc(className: String, functionName: String, e: Exception, type: LogLevel = LogLevel.ERROR) {

        //Do nothing if logs are disabled
        if (!logsConfig?.enabled!!)
            return

        //Do nothing if log level type is disabled
        if (!isLogLevelEnabled(type))
            return

        val data = Utils.instance.getStackTrace(e)

        if (logsConfig?.encryptionEnabled!!) {
            writeEncryptedLogs(className, functionName, data, type.level)
        } else {
            writeSimpleLogs(className, functionName, data, type.level)
        }

        //Send event to notify error is reported
        PLog.getLogBus().send(LogEvents(EventTypes.NEW_ERROR_REPORTED, data = data))
    }

    /**
     * Gets logs.
     *
     * This will export logs based on filter type to export location with export name provided.
     *
     * @param type the type
     * @return the logs
     */
    fun getZippedLog(type: ExportType, exportDecrypted: Boolean): Observable<String> {
        return LogExporter.getZippedLogs(type.type, exportDecrypted)
    }

    /**
     * Gets logs.
     *
     * This will export logs as plain String.
     *
     * @return the String data
     */
    fun getLoggedData(type: ExportType, printDecrypted: Boolean): Observable<String> {
        return LogExporter.getLoggedData(type.type, printDecrypted)
    }

    /**
     * Clear all logs from storage directory.
     *
     */
    fun clearLogs() {
        File(PLog.logPath).deleteRecursively()
    }

    /*
     * Write plain String logs.
     */
    private fun writeSimpleLogs(className: String, functionName: String, text: String, type: String) {
        val path = setupFilePaths()

        checkFileExists(path)

        val logData = LogData(className, functionName, text, DateTimeUtils.getTimeFormatted(logsConfig?.timeStampFormat?.value!!), type)

        val logFormatted = LogFormatter.getFormatType(logData)

        if (PLog.logsConfig?.isDebuggable!!)
            Log.i(TAG, logFormatted)

        appendToFile(path, logFormatted)
    }

    /*
     * Write AES encrypted String logs.
     */
    private fun writeEncryptedLogs(className: String, functionName: String, text: String, type: String) {

        if (logsConfig?.secretKey == null)
            return

        val path = setupFilePaths()

        checkFileExists(path)

        val logData = LogData(className, functionName, text, DateTimeUtils.getTimeFormatted(logsConfig?.timeStampFormat?.value), type)

        val logFormatted = LogFormatter.getFormatType(logData)

        if (PLog.logsConfig?.isDebuggable!!)
            Log.i(TAG, logFormatted)

        appendToFileEncrypted(logFormatted, logsConfig?.secretKey!!, path)
    }


    /*
     * This will return observable to subscribe to logger events.
     */
    fun getLogEvents(): Observable<LogEvents> {

        return Observable.create {
            val emitter = it
            getLogBus().toObservable().subscribe {
                if (it is LogEvents) {
                    emitter.onNext(it)
                }
            }
        }
    }

    /*
     * This will return 'DataLogger' for log type defined in Config File.
     */
    fun getLoggerFor(type: String): DataLogger? {

        if (PLog.isLogsConfigSet()) {
            if (logTypes.containsKey(type))
                return logTypes.get(type)

            if (logsConfig?.isDebuggable!!)
                Log.e(TAG, "No log type defined for provided type '$type'")

            return null
        } else {
            return null
        }
    }
}
