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
import com.google.gson.Gson
import java.io.File

class MainApplication : Application() {

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

        val logsConfig = LogsConfig(
                logLevelsEnabled = arrayListOf(LogLevel.ERROR, LogLevel.SEVERE, LogLevel.INFO, LogLevel.WARNING),
                logTypesEnabled = arrayListOf(LogType.Notification.type, LogType.Location.type, LogType.Navigation.type, "Deliveries"),
                formatType = FormatType.FORMAT_CURLY,
                logsRetentionPeriodInDays = 1,
                zipsRetentionPeriodInDays = 2,
                autoClearLogsOnExport = true,
                enabled = true,
                exportFileNamePreFix = "",
                exportFileNamePostFix = "",
                autoExportErrors = true,
                encryptionEnabled = false,
                encryptionKey = "",
                singleLogFileSize = 2048 * 2,
                logFilesLimit = 30,
                directoryStructure = DirectoryStructure.FOR_EVENT,
                nameForEventDirectory = "Job_101",
                logSystemCrashes = true,
                autoExportLogTypes = arrayListOf(),
                autoExportLogTypesPeriod = 3,
                logsDeleteDate = "",
                zipDeleteDate = "",
                isDebuggable = true,
                logFileExtension = LogExtension.TXT,
                attachTimeStamp = false,
                attachNoOfFiles = true,
                timeStampFormat = TimeStampFormat.DATE_FORMAT_1,
                zipFilesOnly = false,
                savePath = logsPath,
                zipFileName = "MyLogs",
                exportPath = logsPath + File.separator + "PLogsOutput"
        ).also {
            it.getLogEventsListener()
                    .doOnNext {

                        when (it.event) {
                            EventTypes.NEW_ERROR_REPORTED -> {
                                PLog.logThis("PLogger", "event", it.data, LogLevel.INFO)
                            }
                            EventTypes.PLOGS_EXPORTED -> {
                            }
                            EventTypes.DATA_LOGS_EXPORTED -> {
                            }
                            EventTypes.LOGS_CONFIG_FOUND -> {
                                PLog.getLogsConfigFromXML()?.also {
                                    PLog.logThis("PLogger", "event", Gson().toJson(it).toString(), LogLevel.INFO)
                                }
                            }
                            EventTypes.NEW_EVENT_DIRECTORY_CREATED -> {
                                PLog.logThis("PLogger", "event", "New directory created: " + it.data, LogLevel.INFO)
                            }
                            EventTypes.LOG_TYPE_EXPORTED -> {
                                PLog.logThis("PLogger", "event", "Log Type: " + it.data, LogLevel.INFO)
                            }
                            else -> {
                            }
                        }
                    }
                    .subscribe()
        }

        PLog.applyConfigurations(logsConfig)
        //PLog.forceWriteLogsConfig(logsConfig)
    }

}


