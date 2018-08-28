package com.blackbox.plog.pLogs.exporter

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.utils.readFileDecrypted
import com.blackbox.plog.utils.zip
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.crypto.SecretKey

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

    fun getZippedLogs(type: Int, attachTimeStamp: Boolean, attachNoOfFiles: Boolean, logPath: String, exportFileName: String, exportPath: String, isEncrypted: Boolean, secretKey: SecretKey?): Observable<String> {

        this.logPath = logPath
        this.exportFileName = logPath
        this.exportPath = logPath
        this.attachNoOfFiles = attachNoOfFiles
        this.attachTimeStamp = attachTimeStamp
        this.zipName = "$exportFileName.zip"

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

            if (isEncrypted) {
                decryptSaveFiles(filesToSend, secretKey, exportPath, zipName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onNext = {
                                    if (PLog.pLogger.isDebuggable)
                                        PLog.logThis(TAG, "getZippedLog", "Output Zip: $zipName", PLog.TYPE_INFO)

                                    emitter.onNext(it)
                                },
                                onError = {
                                    if (!emitter.isDisposed)
                                        emitter.onError(it)
                                },
                                onComplete = { }
                        )
            } else {
                zip(filesToSend, exportPath + zipName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onNext = {
                                    if (PLog.pLogger.isDebuggable)
                                        PLog.logThis(TAG, "getZippedLog", "Output Zip: $zipName", PLog.TYPE_INFO)

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

    fun getLoggedData(type: Int, logPath: String, exportPath: String, isEncrypted: Boolean, secretKey: SecretKey?): Observable<String> {

        this.logPath = logPath
        this.exportFileName = logPath
        this.exportPath = logPath

        return Observable.create {

            val emitter = it

            FilterUtils.prepareOutputFile(exportPath)

            checkLogType(type)

            val outputDirectory = File(exportPath)
            val filesToSend = outputDirectory.listFiles()

            if (filesToSend.isEmpty()) {
                if (!emitter.isDisposed)
                    emitter.onError(Throwable("No logs found!"))
            }

            for (f in filesToSend) {
                emitter.onNext("File: ${f.name} Start..\n")
                emitter.onNext("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n")
                f.forEachLine {
                    if (isEncrypted) {
                        emitter.onNext(readFileDecrypted(secretKey!!, f.absolutePath))
                    } else {
                        emitter.onNext(it)
                    }
                }
                emitter.onNext(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n")
            }

            //Clear output files after reading
            FilterUtils.clearOutputFiles(exportPath)

            emitter.onComplete()
        }
    }
}
