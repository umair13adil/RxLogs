package com.blackbox.library.plog

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
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
import io.reactivex.disposables.CompositeDisposable
import java.io.File


class MainApplication : MultiDexApplication() {

    companion object {
        private val TAG = "MainApplication"
        var logsConfig: LogsConfig? = null
        var isEncryptionEnabled = true

        private val compositeDisposable = CompositeDisposable()

        fun setUpPLogger(context: Context) {

            logsConfig = LogsConfig(
                logLevelsEnabled = arrayListOf(
                    LogLevel.INFO,
                    LogLevel.ERROR,
                    LogLevel.SEVERE,
                    LogLevel.WARNING
                ),
                logTypesEnabled = arrayListOf(
                    LogType.Notification.type,
                    LogType.Location.type,
                    LogType.Navigation.type,
                    LogType.Errors.type,
                    "Work"
                ),
                formatType = FormatType.FORMAT_CURLY,
                logsRetentionPeriodInDays = 7, //Will not work if local XML config file is not present
                zipsRetentionPeriodInDays = 3, //Will not work if local XML config file is not present
                autoDeleteZipOnExport = true,
                autoClearLogs = true,
                exportFileNamePreFix = "[",
                exportFileNamePostFix = "]",
                autoExportErrors = true,
                encryptionEnabled = isEncryptionEnabled,
                encryptionKey = "12345",
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
                timeStampFormat = TimeStampFormat.TIME_FORMAT_READABLE_2,
                zipFilesOnly = false,
                savePath = File(context.getExternalFilesDir(null), "PLogs").path,
                zipFileName = "MyLogs",
                exportPath = File(
                    context.getExternalFilesDir(null),
                    "PLogs" + File.separator + "PLogsOutput"
                ).path,
                exportFormatted = true,
                enableLogsWriteToFile = true,
                isEnabled = true
            ).also { it ->

                //Subscribe to Events listener
                val disposable = it.getLogEventsListener()
                    .doOnError { error ->
                        Log.e(TAG, "Error in log events listener", error)
                    }
                    .doOnNext { event ->
                        when (event.event) {
                            EventTypes.NON_FATAL_EXCEPTION_REPORTED -> {
                                event.exception?.let { exception ->
                                    Log.i(TAG, "Caught Exception: $exception")
                                    exception.printStackTrace()
                                }
                                event.throwable?.let { throwable ->
                                    Log.i(TAG, "Caught Throwable: $throwable")
                                    throwable.printStackTrace()
                                }
                            }

                            else -> {
                                Log.i(TAG, "Event: ${event.event} Data: ${event.event.data}")
                            }
                        }
                    }
                    .subscribe(
                        { _ ->
                            // No-op: view updates would go here if needed
                        },
                        { throwable ->
                            Log.e(TAG, "Error in subscription", throwable)
                        },
                        {
                            Log.d(TAG, "Events subscription completed")
                        }
                    )

                // Store the disposable for later cleanup
                compositeDisposable.add(disposable)
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

        Thread.getDefaultUncaughtExceptionHandler()?.let { fabricExceptionHandler ->
            Thread.setDefaultUncaughtExceptionHandler(
                systemHandler?.let {
                    AppExceptionHandler(
                        it,
                        fabricExceptionHandler,
                        this
                    )
                }
            )
        }
    }

    override fun onTerminate() {
        // Clean up all subscriptions to prevent memory leaks
        compositeDisposable.clear()
        super.onTerminate()
    }
}
