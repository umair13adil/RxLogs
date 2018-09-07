package com.blackbox.plog.pLogs

/**
 * Created by Umair Adil on 12/04/2017.
 */

import android.util.Log
import com.blackbox.plog.pLogs.config.ConfigReader
import com.blackbox.plog.pLogs.config.ConfigWriter
import com.blackbox.plog.pLogs.config.LogsConfig
import com.blackbox.plog.pLogs.config.XML_PATH
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.exporter.ExportType
import com.blackbox.plog.pLogs.exporter.LogExporter
import com.blackbox.plog.pLogs.formatter.LogFormatter
import com.blackbox.plog.pLogs.models.LogData
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.models.PLogger
import com.blackbox.plog.utils.*
import io.reactivex.Observable
import java.io.File

object PLog {

    init {
        setLogBus(RxBus())
    }

    private val TAG = PLog::class.java.simpleName

    @JvmStatic
    private var pLogger: PLogger = PLogger()

    @JvmStatic
    private lateinit var bus: RxBus

    private var logsConfig: LogsConfig? = null

    /**
     * Gets output path.
     *
     * Sets the export path of Logs.
     *
     * @return the output path
     */
    internal val outputPath: String
        get() = pLogger.exportPath + File.separator

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
        get() = pLogger.savePath + File.separator

    internal fun setPLogger(pLog: PLogger) {
        pLogger = pLog
    }

    internal fun getPLogger(): PLogger? {
        return pLogger
    }

    internal fun getLogBus(): RxBus {
        return bus
    }

    private fun setLogBus(listener: RxBus) {
        bus = listener
    }

    fun setLogsConfig(config: LogsConfig) {
        logsConfig = config
        ConfigWriter.saveToXML(config)
    }

    fun getLogsConfig(): LogsConfig? {
        return if (File(XML_PATH).exists() && logsConfig == null) {
            logsConfig = ConfigReader.readXML()
            logsConfig
        } else if (!File(XML_PATH).exists() && logsConfig == null) {
            ConfigWriter.saveToXML(LogsConfig())
            logsConfig = ConfigReader.readXML()
            logsConfig
        } else {
            logsConfig
        }
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
        if (!pLogger.enabled)
            return

        if (pLogger.encrypt) {
            writeEncryptedLogs(className, functionName, text, type.level)
        } else {
            writeSimpleLogs(className, functionName, text, type.level)
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
        if (!pLogger.enabled)
            return

        if (pLogger.encrypt) {
            writeEncryptedLogs(className, functionName, Utils.instance.getStackTrace(e), type.level)
        } else {
            writeSimpleLogs(className, functionName, Utils.instance.getStackTrace(e), type.level)
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
    fun logExc(className: String, functionName: String, e: Exception, type: LogLevel = LogLevel.ERROR) {

        //Do nothing if logs are disabled
        if (!pLogger.enabled)
            return

        if (pLogger.encrypt) {
            writeEncryptedLogs(className, functionName, Utils.instance.getStackTrace(e), type.level)
        } else {
            writeSimpleLogs(className, functionName, Utils.instance.getStackTrace(e), type.level)
        }
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

        val logData = LogData(className, functionName, text, DateTimeUtils.getTimeFormatted(pLogger.timeStampFormat.value), type)

        val logFormatted = LogFormatter.getFormatType(logData, pLogger)

        if (PLog.pLogger.isDebuggable)
            Log.i(TAG, logFormatted)

        appendToFile(path, logFormatted)
    }

    /*
     * Write AES encrypted String logs.
     */
    private fun writeEncryptedLogs(className: String, functionName: String, text: String, type: String) {

        if (pLogger.secretKey == null)
            return

        val path = setupFilePaths()

        checkFileExists(path)

        val logData = LogData(className, functionName, text, DateTimeUtils.getTimeFormatted(pLogger.timeStampFormat.value), type)

        val logFormatted = LogFormatter.getFormatType(logData, pLogger)

        if (PLog.pLogger.isDebuggable)
            Log.i(TAG, logFormatted)

        appendToFileEncrypted(logFormatted, pLogger.secretKey!!, path)
    }


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
}
