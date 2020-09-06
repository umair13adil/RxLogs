package com.blackbox.library.plog

import android.app.Application
import android.content.Context
import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.config.LogsConfig
import com.blackbox.plog.pLogs.formatter.FormatType
import com.blackbox.plog.pLogs.formatter.TimeStampFormat
import com.blackbox.plog.pLogs.models.LogExtension
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.models.LogType
import com.blackbox.plog.pLogs.structure.DirectoryStructure
import com.blackbox.plog.utils.AppExceptionHandler
import java.io.File


class MainApplication : Application() {

    companion object {
        private val TAG = "MainApplication"
        var logsConfig: LogsConfig? = null

        fun setUpPLogger(context: Context) {

            logsConfig = LogsConfig(
                    logLevelsEnabled = arrayListOf(LogLevel.INFO, LogLevel.ERROR, LogLevel.SEVERE, LogLevel.WARNING),
                    logTypesEnabled = arrayListOf(LogType.Notification.type, LogType.Location.type, LogType.Navigation.type, LogType.Errors.type, "Deliveries"),
                    formatType = FormatType.FORMAT_CURLY,
                    logsRetentionPeriodInDays = 7, //Will not work if local XML config file is not present
                    zipsRetentionPeriodInDays = 3, //Will not work if local XML config file is not present
                    autoDeleteZipOnExport = true,
                    autoClearLogs = true,
                    enableLogsWriteToFile = true,
                    exportFileNamePreFix = "[",
                    exportFileNamePostFix = "]",
                    autoExportErrors = true,
                    encryptionEnabled = true,
                    encryptionKey = "866214031427313",
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
                    savePath = File(context.getExternalFilesDir(null), "PLogs").path,
                    zipFileName = "MyLogs",
                    exportPath = File(context.getExternalFilesDir(null), "PLogs" + File.separator + "PLogsOutput").path,
                    exportFormatted = true
            ).also { it ->

                //Subscribe to Events listener
                it.getLogEventsListener()
                        .doOnError {
                            it.printStackTrace()
                        }
                        .doOnNext {
                            Log.i(TAG, "Event: ${it.event} Data: ${it.event.data}")
                        }
                        .subscribe({ result ->
                            // updating view
                        }, { throwable ->
                            throwable.printStackTrace()
                        })
            }

            PLog.applyConfigurations(logsConfig, context = context)
        }
    }

    override fun onCreate() {
        super.onCreate()

        setupCrashHandler()
    }

    private fun setupCrashHandler() {
        val systemHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { t, e -> /* do nothing */ }

        val fabricExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler(AppExceptionHandler(systemHandler, fabricExceptionHandler, this))
    }

}


