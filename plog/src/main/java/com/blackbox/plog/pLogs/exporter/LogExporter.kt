package com.blackbox.plog.pLogs.exporter

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.utils.zip
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * Created by umair on 04/01/2018.
 */

object LogExporter {

    private val TAG = LogExporter::class.java.simpleName

    internal var zipName = ""
    internal var path = ""
    internal var files = 0
    internal var timeStamp = ""
    internal var noOfFiles = ""
    internal var logPath = ""
    internal var exportFileName = ""
    internal var exportPath = ""
    internal var attachNoOfFiles = false
    internal var attachTimeStamp = false

    fun getLogs(type: Int, attachTimeStamp: Boolean, attachNoOfFiles: Boolean, logPath: String, exportFileName: String, exportPath: String): Observable<String> {

        LogExporter.logPath = logPath
        LogExporter.exportFileName = logPath
        LogExporter.exportPath = logPath
        LogExporter.attachNoOfFiles = attachNoOfFiles
        LogExporter.attachTimeStamp = attachTimeStamp

        return Observable.create {

            val emitter = it

            FilterUtils.prepareOutputFile(exportPath)

            checkLogType(type)

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
                                    PLog.logThis(TAG, "getLogs", "Output Zip: $exportPath$zipName", PLog.TYPE_INFO)

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
}
