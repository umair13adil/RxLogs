package com.blackbox.plog.dataLogs.exporter

import com.blackbox.plog.dataLogs.filter.DataLogsFilter
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.utils.DateTimeUtils
import com.blackbox.plog.utils.zip
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File

object DataLogsExporter {

    private val TAG = DataLogsExporter::class.java.simpleName

    private var logPath = ""
    private var exportFileName = ""
    private var exportPath = ""
    private var attachTimeStamp = false

    fun getDataLogs(logFileName: String, attachTimeStamp: Boolean, logPath: String, exportFileName: String, exportPath: String, debug: Boolean): Observable<String> {

        return Observable.create {

            val emitter = it

            FilterUtils.prepareOutputFile(exportPath)

            val zipName = composeDataExportFileName(logFileName, debug)

            val outputDirectory = File(exportPath)
            val filesToSend = outputDirectory.listFiles()

            if (filesToSend.isEmpty()) {
                if (!emitter.isDisposed)
                    emitter.onError(Throwable("No Files to zip!"))
            }

            zip(filesToSend, exportPath + zipName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = {
                                if (PLog.pLogger.isDebuggable)
                                    PLog.logThis(TAG, "getLogs", "Output Zip: $exportPath${zipName}", PLog.TYPE_INFO)

                                emitter.onNext(exportPath + zipName)
                            },
                            onError = {
                                if (!emitter.isDisposed)
                                    emitter.onError(it)
                            },
                            onComplete = { }
                    )
        }
    }

    private fun composeDataExportFileName(logFileName: String, debug: Boolean): String {
        var timeStamp = ""
        val noOfFiles = ""

        DataLogsFilter.getFilesForLogName(logPath, exportPath, logFileName, debug)

        if (attachTimeStamp)
            timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis())

        return "$exportFileName$timeStamp$noOfFiles.zip"
    }
}