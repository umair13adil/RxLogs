package com.blackbox.plog.pLogs.impl

import android.content.Context
import android.util.Log
import com.blackbox.plog.dataLogs.DataLogger
import com.blackbox.plog.elk.ECSMapper
import com.blackbox.plog.elk.PLogMetaInfoProvider
import com.blackbox.plog.mqtt.MQTTSender
import com.blackbox.plog.mqtt.PLogMQTTProvider
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.config.*
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
import com.blackbox.plog.utils.PLogUtils
import com.blackbox.plog.utils.PLogUtils.createDirIfNotExists
import com.blackbox.plog.utils.RxBus
import com.google.gson.Gson
import io.reactivex.Observable
import java.io.File


open class PLogImpl {

    internal var logTypes = hashMapOf<String, DataLogger>()

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
    fun applyConfigurations(config: LogsConfig?, context: Context) {

        //Initialize Preferences Class here
        PLogPreferences.init(context)

        //Save context
        PLogImpl.context = context

        if (config == null) {
            return
        }

        //Create 'Save' Path
        createDirIfNotExists(config.savePath, config = config)

        saveConfig(config)

        if (config.encryptionEnabled) {
            if (config.encryptionKey.isNotEmpty()) {
                config.encryptionKey.let {
                    val key = encrypter.checkIfKeyValid(it)
                    LogWriter.secretKey = encrypter.generateKey(key)
                }
            }
        }

        //Perform operations on Initializing.
        doOnInit()
    }

    /*
     * This will forcefully overwrite existing logs configuration.
     */
    fun forceWriteLogsConfig(config: LogsConfig) {

        PLogImpl.saveConfig(config)

        getConfig()?.let {

            //Perform operations on Initializing.
            doOnInit()
        }
    }

    /*
     * Will send 'true' if local configuration XML is deleted.
     */
    internal fun deleteLocalConfiguration(): Boolean {
        return File(XML_PATH, CONFIG_FILE_NAME).delete()
    }

    internal fun printFormattedLogs(className: String, functionName: String, text: String, type: String, exception: Exception? = null, throwable: Throwable? = null): String {
        val logData = LogData(className, functionName, text, getFormattedTimeStamp(), type)

        return if (!PLogMetaInfoProvider.elkStackSupported) {
            LogFormatter.getFormatType(logData)
        } else {
            ECSMapper.getECSMappedLogString(logData, exception, throwable)
        }
    }

    /*
     * This will return observable to subscribe to logger events.
     */
    internal fun getLogEvents(): Observable<LogEvents> {
        return RxBus.listen(LogEvents::class.java)
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

    internal fun isLogsConfigValid(className: String, functionName: String, info: String, type: LogLevel, exception: Exception? = null, throwable: Throwable? = null): Pair<Boolean, String> {

        val logData = PLog.printFormattedLogs(className, functionName, info, type.level, exception, throwable)

        if (!isEnabled())
            return Pair(false, logData)

        //Do nothing if log level type is disabled
        if (!isLogLevelEnabled(type))
            return Pair(false, logData)

        //Publish to MQTT
        if (PLogMQTTProvider.mqttEnabled) {
            MQTTSender.publishMessage(logData)
        }

        return Pair(true, logData)
    }

    internal fun writeAndExportLog(data: String, type: LogLevel) {
        if (PLogImpl.isEncryptionEnabled()) {
            LogWriter.writeEncryptedLogs(data)
        } else {
            LogWriter.writeSimpleLogs(data)
        }
    }

    internal fun formatErrorMessage(info: String, throwable: Throwable? = null, exception: Exception? = null): String {
        return if (info.isNotEmpty()) {
            if (throwable != null)
                "$info, ${PLogUtils.getStackTrace(throwable)}"
            else
                "$info, ${PLogUtils.getStackTrace(exception)}"
        } else {
            if (throwable != null)
                PLogUtils.getStackTrace(throwable)
            else
                PLogUtils.getStackTrace(exception)
        }
    }

    companion object {

        internal val TAG = "PLogger"
        internal val DEBUG_TAG = "PLogger_DEBUG"

        private var isEnabled = true
        private var encryptionEnabled = false
        private var logLevelsEnabled: ArrayList<LogLevel> = arrayListOf<LogLevel>()

        @JvmStatic
        private var logsConfig: LogsConfig? = null

        @JvmStatic
        internal val encrypter by lazy { Encrypter() }

        @JvmStatic
        internal val gson = Gson()

        @JvmStatic
        internal var context: Context? = null

        @JvmStatic
        internal fun getConfig(config: LogsConfig? = null): LogsConfig? {
            return if (logsConfig != null) {
                logsConfig?.let { logsConfig ->
                    isEnabled = logsConfig.enabled
                    logLevelsEnabled = logsConfig.logLevelsEnabled
                    encryptionEnabled = logsConfig.encryptionEnabled
                    logsConfig
                }
            } else {
                getLogsConfig(PREF_LOGS_CONFIG, LogsConfig::class.java, config)?.let { logsConfig ->
                    isEnabled = logsConfig.enabled
                    logLevelsEnabled = logsConfig.logLevelsEnabled
                    encryptionEnabled = logsConfig.encryptionEnabled
                    logsConfig
                }
            }
        }

        internal fun isEnabled(): Boolean {
            return isEnabled
        }

        internal fun isEncryptionEnabled(): Boolean {
            return encryptionEnabled
        }

        internal fun getLogLevelsEnabled(): ArrayList<LogLevel> {
            return logLevelsEnabled
        }

        @JvmStatic
        internal fun saveConfig(config: LogsConfig?) {

            config?.let {
                //Set up encryption Key
                if (config.encryptionEnabled) {
                    config.encryptionKey.let { key ->
                        if (key.isNotEmpty()) {
                            val key = encrypter.checkIfKeyValid(key)
                            config.secretKey = encrypter.generateKey(key)
                        }
                    }
                }

                //Set Logs Configuration
                logsConfig = config

                //Save Configuration
                saveLogsConfig(PREF_LOGS_CONFIG, it)
            } ?: Log.e(TAG, "saveConfig: Configurations not provided.")

        }
    }
}