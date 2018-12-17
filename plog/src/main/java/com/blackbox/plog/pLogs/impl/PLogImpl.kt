package com.blackbox.plog.pLogs.impl

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.blackbox.plog.dataLogs.DataLogger
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.config.ConfigReader
import com.blackbox.plog.pLogs.config.ConfigWriter
import com.blackbox.plog.pLogs.config.LogsConfig
import com.blackbox.plog.pLogs.config.isLogLevelEnabled
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.pLogs.formatter.LogFormatter
import com.blackbox.plog.pLogs.formatter.TimeStampFormat
import com.blackbox.plog.pLogs.models.LogData
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.operations.doOnInit
import com.blackbox.plog.pLogs.utils.*
import com.blackbox.plog.utils.DateTimeUtils
import com.blackbox.plog.utils.RxBus
import com.blackbox.plog.utils.Utils
import com.blackbox.plog.utils.Utils.createDirIfNotExists
import io.reactivex.Observable
import java.io.File

open class PLogImpl {

    internal val TAG = "PLogger"

    private lateinit var bus: RxBus

    internal var logTypes = hashMapOf<String, DataLogger>()

    internal fun getLogBus(): RxBus {
        return bus
    }

    internal fun setLogBus(listener: RxBus) {
        bus = listener
    }

    /*
     * Check if logs configuration file is set.
     */
    fun isLogsConfigSet(): Boolean {

        logsConfig?.let {
            return true
        }

        print(Throwable("No logs configuration provided!"))

        return false
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
    fun applyConfigurations(config: LogsConfig, saveToFile: Boolean = false) {

        //Create 'Save' Path
        createDirIfNotExists(config.savePath)

        //Save Context
        context = config.context

        logsConfig = config

        if (saveToFile) {
            //Only save if parameter value 'true'

            //Copy logs & zip delete data from saved XML configuration if it exists
            copyDataFromSavedConfig()

            logsConfig?.let {

                ConfigWriter.saveToXML(it)
                        .doOnNext {

                            //Perform operations on Initializing.
                            doOnInit(saveToFile)
                        }
                        .subscribe()
            }
        } else {

            //Perform operations on Initializing.
            doOnInit(saveToFile)
        }
    }

    /*
     * This will forcefully overwrite existing logs configuration.
     */
    fun forceWriteLogsConfig(config: LogsConfig) {

        //Save Context
        context = config.context

        logsConfig = config

        //Copy logs & zip delete data from saved XML configuration if it exists
        copyDataFromSavedConfig()

        logsConfig?.let {

            ConfigWriter.saveToXML(it)
                    .doOnNext {

                        //Perform operations on Initializing.
                        doOnInit(true)

                    }.subscribe()
        }
    }

    private fun copyDataFromSavedConfig() {

        //Load XML config file if exists
        if (PLog.localConfigurationExists()) {
            PLog.getLogsConfigFromXML()?.let { xmlConfig ->

                //Save previous saved dates
                logsConfig?.logsDeleteDate = xmlConfig.logsDeleteDate
                logsConfig?.zipDeleteDate = xmlConfig.zipDeleteDate
            }
        }
    }

    /*
     * Get LogsConfig XML file.
     */
    fun getLogsConfigFromXML(): LogsConfig? {

        if (localConfigurationExists()) {

            ConfigReader.readXML().let { savedConfig ->
                return savedConfig
            }
        }

        return null
    }

    /*
     * Will send 'true' if local configuration XML exists.
     */
    internal fun localConfigurationExists(): Boolean {
        return File(XML_PATH, CONFIG_FILE_NAME).exists()
    }

    /*
     * Will send 'true' if local configuration XML is deleted.
     */
    internal fun deleteLocalConfiguration(): Boolean {
        return File(XML_PATH, CONFIG_FILE_NAME).delete()
    }

    internal fun printFormattedLogs(className: String, functionName: String, text: String, type: String): String {
        val logData = LogData(className, functionName, text, getFormattedTimeStamp(), type)

        return LogFormatter.getFormatType(logData)
    }

    /*
     * This will return observable to subscribe to logger events.
     */
    internal fun getLogEvents(): Observable<LogEvents> {

        return Observable.create { it ->
            val emitter = it
            PLog.getLogBus()
                    .toObservable()
                    .doOnError {

                        if (logsConfig?.isDebuggable!!)
                            Log.e(TAG, "Error '${it.message}'")

                    }.subscribe {

                        if (it is LogEvents) {
                            emitter.onNext(it)
                        }
                    }
        }
    }

    internal fun getFormattedTimeStamp(): String {

        logsConfig?.let {
            return DateTimeUtils.getTimeFormatted(it.timeStampFormat)
        }

        return DateTimeUtils.getTimeFormatted(TimeStampFormat.TIME_FORMAT_READABLE)
    }

    fun getListOfExportedFiles(): List<File> {
        return FilterUtils.listFiles(outputPath, arrayListOf())
    }

    internal fun isLogsConfigValid(className: String, functionName: String, info: String, type: LogLevel): Pair<Boolean, String> {

        val logData = PLog.printFormattedLogs(className, functionName, info, type.level)

        if (logData.isNotEmpty()) {
            Log.i(PLog.TAG, logData)
        }

        if (logsConfig != null) {

            //Do nothing if logs are disabled
            if (!logsConfig?.enabled!!)
                return Pair(false, logData)

            //Do nothing if log level type is disabled
            if (!isLogLevelEnabled(type))
                return Pair(false, logData)

        } else {
            return Pair(false, logData)
        }

        return Pair(true, logData)
    }

    internal fun writeAndExportLog(data: String, type: LogLevel) {

        if (logsConfig?.encryptionEnabled!!) {
            LogWriter.writeEncryptedLogs(data)
        } else {
            LogWriter.writeSimpleLogs(data)
        }

        //Check if log level is of Error
        AutoExportHelper.autoExportError(data, type)
    }

    internal fun formatErrorMessage(info: String, throwable: Throwable? = null, exception: Exception? = null): String {
        return if (info.isNotEmpty()) {
            if (throwable != null)
                "$info, ${Utils.getStackTrace(throwable)}"
            else
                "$info, ${Utils.getStackTrace(exception)}"
        } else {
            if (throwable != null)
                Utils.getStackTrace(throwable)
            else
                Utils.getStackTrace(exception)
        }
    }

    companion object {
        internal var logsConfig: LogsConfig? = null

        @SuppressLint("StaticFieldLeak")
        internal var context: Context? = null
    }
}