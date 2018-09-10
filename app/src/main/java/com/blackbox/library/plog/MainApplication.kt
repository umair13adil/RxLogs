package com.blackbox.library.plog

import android.app.Application
import android.os.Environment
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.config.LogsConfig
import com.blackbox.plog.pLogs.formatter.FormatType
import com.blackbox.plog.pLogs.formatter.TimeStampFormat
import com.blackbox.plog.pLogs.models.LogExtension
import com.blackbox.plog.pLogs.models.LogLevel
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
                logTypesEnabled = arrayListOf("Locations", "APIs", "Notifications"),
                formatType = FormatType.FORMAT_CURLY,
                logsRetentionPeriodInDays = 7,
                zipsRetentionPeriodInDays = 7,
                autoClearLogsOnExport = true,
                enabled = true,
                exportFileNamePreFix = "",
                exportFileNamePostFix = "",
                autoExportErrors = true,
                encryptionEnabled = false,
                encryptionKey = "",
                singleLogFileSize = 2048 * 2,
                logFilesLimit = 30,
                directoryStructure = DirectoryStructure.FOR_DATE,
                logSystemCrashes = true,
                autoExportLogTypes = arrayListOf("Notifications", "Locations"),
                autoExportLogTypesPeriod = 3,
                logsDeleteDate = "",
                zipDeleteDate = "",
                savePath = logsPath,
                exportPath = logsPath + File.separator + "PLogsOutput",
                zipFileName = "MyLogs",
                isDebuggable = true,
                logFileExtension = LogExtension.TXT,
                attachTimeStamp = false,
                attachNoOfFiles = true,
                timeStampFormat = TimeStampFormat.DATE_FORMAT_1,
                nameForEventDirectory = "My Name",
                zipFilesOnly = false
        ).also {
            it.getLogEventsListener()
                    .doOnNext {
                        PLog.logThis("PLogger", "getLogEventsListener", "Event: $it", LogLevel.INFO)

                        PLog.getLogsConfigFromXML()?.also {
                            PLog.logThis("PLog", "XML", Gson().toJson(it).toString(), LogLevel.INFO)
                        }
                    }
                    .subscribe()
        }

        logsConfig.setEventNameForDirectory("My Name 2")
        PLog.setLogsConfig(logsConfig, saveToFile = true)
    }

}


