package com.blackbox.library.plog

import android.app.Application
import android.os.Environment
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.config.LogsConfig
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.formatter.FormatType
import com.blackbox.plog.pLogs.formatter.TimeStampFormat
import com.blackbox.plog.pLogs.models.LogExtension
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.models.LogType
import com.blackbox.plog.pLogs.structure.DirectoryStructure
import com.blackbox.plog.utils.AppExceptionHandler
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_hourly_logs_test.*
import java.io.File


class MainApplication : Application() {

    private val TAG = "MainApplication"

    companion object {
        var logsConfig: LogsConfig? = null
    }

    override fun onCreate() {
        super.onCreate()

        setUpPLogger()

        setupCrashHandler()
    }

    private fun setupCrashHandler() {
        val systemHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { t, e -> /* do nothing */ }

        val fabricExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler(AppExceptionHandler(systemHandler, fabricExceptionHandler, this))
    }

    private fun setUpPLogger() {
        val logsPath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "PLogs"

        logsConfig = LogsConfig(
                logLevelsEnabled = arrayListOf(LogLevel.ERROR, LogLevel.SEVERE, LogLevel.INFO, LogLevel.WARNING),
                logTypesEnabled = arrayListOf(LogType.Notification.type, LogType.Location.type, LogType.Navigation.type, LogType.Errors.type, "Deliveries"),
                formatType = FormatType.FORMAT_CURLY,
                logsRetentionPeriodInDays = 7, //Will not work if local XML config file is not present
                zipsRetentionPeriodInDays = 3, //Will not work if local XML config file is not present
                autoDeleteZipOnExport = true,
                autoClearLogs = true,
                enabled = true,
                exportFileNamePreFix = "[",
                exportFileNamePostFix = "]",
                autoExportErrors = true,
                encryptionEnabled = true,
                encryptionKey = "1234567891234",
                singleLogFileSize = 1, //1Mb
                logFilesLimit = 30,
                directoryStructure = DirectoryStructure.FOR_DATE,
                nameForEventDirectory = "MyLogs",
                logSystemCrashes = true,
                autoExportLogTypes = arrayListOf(),
                autoExportLogTypesPeriod = 3,
                isDebuggable = true,
                debugFileOperations = true,
                logFileExtension = LogExtension.LOG,
                attachTimeStamp = true,
                attachNoOfFiles = true,
                timeStampFormat = TimeStampFormat.TIME_FORMAT_READABLE,
                zipFilesOnly = false,
                savePath = logsPath,
                zipFileName = "MyLogs",
                exportPath = logsPath + File.separator + "PLogsOutput",
                exportFormatted = true
        ).also { it ->

            //Subscribe to Events listener
            it.getLogEventsListener()
                    .doOnError {
                        it.printStackTrace()
                    }
                    .doOnNext {

                        when (it.event) {
                            EventTypes.NEW_ERROR_REPORTED -> {
                                PLog.logThis(TAG, "getLogEventsListener", it.data, LogLevel.INFO)
                            }
                            EventTypes.SEVERE_ERROR_REPORTED -> {
                                PLog.logThis(TAG, "getLogEventsListener", it.data, LogLevel.INFO)
                            }
                            EventTypes.NEW_ERROR_REPORTED_FORMATTED -> {
                                PLog.logThis(TAG, "getLogEventsListener", it.data, LogLevel.INFO)
                            }
                            EventTypes.SEVERE_ERROR_REPORTED_FORMATTED -> {
                                PLog.logThis(TAG, "getLogEventsListener", it.data, LogLevel.INFO)
                            }
                            EventTypes.PLOGS_EXPORTED -> {
                            }
                            EventTypes.DATA_LOGS_EXPORTED -> {
                            }
                            EventTypes.LOGS_CONFIG_FOUND -> {
                                PLog.getLogsConfigFromXML()?.let { config ->
                                    val gson = GsonBuilder().setPrettyPrinting().create()
                                    PLog.logThis(TAG, "getLogEventsListener", gson.toJson(config).toString(), LogLevel.INFO)
                                }
                            }
                            EventTypes.NEW_EVENT_LOG_FILE_CREATED -> {
                                PLog.logThis(TAG, "getLogEventsListener", "New log file created: " + it.data, LogLevel.INFO)
                            }
                            EventTypes.NEW_EVENT_DIRECTORY_CREATED -> {
                                PLog.logThis(TAG, "getLogEventsListener", "New directory created: " + it.data, LogLevel.INFO)
                            }
                            EventTypes.LOG_TYPE_EXPORTED -> {
                                PLog.logThis(TAG, "getLogEventsListener", "Log Type: " + it.data, LogLevel.INFO)
                            }
                            EventTypes.DELETE_LOGS -> {
                                PLog.logThis(TAG, "getLogEventsListener", "DELETE_LOGS: " + it.data, LogLevel.INFO)
                            }
                            EventTypes.DELETE_EXPORTED_FILES -> {
                                PLog.logThis(TAG, "getLogEventsListener", "DELETE_EXPORTED_FILES: " + it.data, LogLevel.INFO)
                            }
                            else -> {
                            }
                        }
                    }
                    .subscribe({ result ->
                        // updating view
                    }, { throwable ->
                        throwable.printStackTrace()
                    })
        }

        PLog.applyConfigurations(logsConfig!!, saveToFile = true)
    }

}


