package com.blackbox.plog.pLogs.impl

import android.util.Log
import com.blackbox.plog.dataLogs.DataLogger
import com.blackbox.plog.elk.ECSMapper
import com.blackbox.plog.elk.PLogMetaInfoProvider
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
import com.blackbox.plog.utils.Encrypter
import com.blackbox.plog.utils.RxBus
import com.blackbox.plog.utils.Utils
import com.blackbox.plog.utils.Utils.createDirIfNotExists
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
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

        getConfig()?.let {
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
        get() = getExportPath(getConfig())

    internal val exportTempPath: String
        get() = getTempExportPath(getConfig())

    /**
     * Gets Logs path.
     *
     * Sets the save path of Logs.
     *
     * @return the save path
     */
    internal val logPath: String
        get() = getLogPath(getConfig())

    /*
     * This will set logs configuration.
     *
     * @param 'saveToFile' if true, file will be written to storage
     */
    fun applyConfigurations(config: LogsConfig, saveToFile: Boolean = false) {

        //Create 'Save' Path
        createDirIfNotExists(config.savePath)

        PLogImpl.saveConfig(config)

        if (saveToFile) {
            //Only save if parameter value 'true'

            //Copy logs & zip delete data from saved XML configuration if it exists
            copyDataFromSavedConfig()

            getConfig()?.let {

                ConfigWriter.saveToXML(it)
                        .subscribeBy(
                                onNext = {
                                    //Perform operations on Initializing.
                                    doOnInit(saveToFile)
                                },
                                onError = {
                                    it.printStackTrace()
                                },
                                onComplete = {}
                        )
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

        PLogImpl.saveConfig(config)

        //Copy logs & zip delete data from saved XML configuration if it exists
        copyDataFromSavedConfig()

        getConfig()?.let {

            ConfigWriter.saveToXML(it)
                    .subscribeBy(
                            onNext = {
                                //Perform operations on Initializing.
                                doOnInit(true)
                            },
                            onError = {
                                it.printStackTrace()
                            },
                            onComplete = {}
                    )
        }
    }

    private fun copyDataFromSavedConfig() {

        //Load XML config file if exists
        if (PLog.localConfigurationExists()) {
            PLog.getLogsConfigFromXML()?.let { xmlConfig ->

                //Save previous saved dates
                getConfig()?.logsDeleteDate = xmlConfig.logsDeleteDate
                getConfig()?.zipDeleteDate = xmlConfig.zipDeleteDate
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

        return if (!PLogMetaInfoProvider.elkStackSupported) {
            LogFormatter.getFormatType(logData)
        } else {
            ECSMapper.getECSMappedLogString(logData)
        }
    }

    /*
     * This will return observable to subscribe to logger events.
     */
    internal fun getLogEvents(): Observable<LogEvents> {

        return Observable.create { it ->
            val emitter = it
            PLog.getLogBus()
                    .toObservable()
                    .subscribeBy(
                            onNext = {
                                if (it is LogEvents) {
                                    if (!emitter.isDisposed)
                                        emitter.onNext(it)
                                }
                            },
                            onError = { error ->

                                getConfig()?.let {
                                    if (it.isDebuggable)
                                        Log.e(TAG, "Error '${error.message}'")
                                }
                            },
                            onComplete = {

                            }
                    )
        }
    }

    private fun getFormattedTimeStamp(): String {

        getConfig()?.let {
            return DateTimeUtils.getTimeFormatted(it.timeStampFormat)
        }

        return DateTimeUtils.getTimeFormatted(TimeStampFormat.TIME_FORMAT_READABLE)
    }

    internal fun getTimeStampForOutputFile(): String {
        return DateTimeUtils.getTimeFormatted(TimeStampFormat.TIME_FORMAT_FULL_JOINED)
    }

    fun getListOfExportedFiles(): List<File> {
        return FilterUtils.listFiles(outputPath, arrayListOf())
    }

    internal fun isLogsConfigValid(className: String, functionName: String, info: String, type: LogLevel, printNow: Boolean = true): Pair<Boolean, String> {

        val logData = PLog.printFormattedLogs(className, functionName, info, type.level)

        if (getConfig() != null) {

            //Do nothing if logs are disabled
            if (!getConfig()?.enabled!!)
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

        if (getConfig()?.encryptionEnabled!!) {
            LogWriter.writeEncryptedLogs(data)
        } else {
            LogWriter.writeSimpleLogs(data)
        }
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
        private var logsConfig: LogsConfig? = null
        internal val encrypter by lazy { Encrypter() }
        internal val gson = Gson()

        internal fun getConfig(): LogsConfig? {
            return logsConfig
        }

        internal fun saveConfig(config: LogsConfig) {

            //Set up encryption Key
            getConfig()?.encryptionEnabled?.let {
                getConfig()?.encryptionKey?.let {
                    if (it.isNotEmpty()) {
                        val key = encrypter.checkIfKeyValid(it)
                        config.secretKey = encrypter.generateKey(key)
                    }
                }
            }

            //Set Logs Configuration
            logsConfig = config
        }
    }
}