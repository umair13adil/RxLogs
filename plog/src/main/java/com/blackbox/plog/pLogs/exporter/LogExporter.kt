package com.blackbox.plog.pLogs.exporter

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.pLogs.models.LogLevel
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
    internal lateinit var files: Pair<String, List<File>>
    internal var timeStamp = ""
    internal var noOfFiles = ""
    internal var logPath = ""
    internal var exportFileName = ""
    internal var exportPath = ""
    internal var attachNoOfFiles = false
    internal var attachTimeStamp = false

    /*
     * Will filter & export log files to zip package.
     */
    fun getZippedLogs(type: Int, attachTimeStamp: Boolean, attachNoOfFiles: Boolean, logPath: String, exportFileName: String, exportPath: String, isEncrypted: Boolean, secretKey: SecretKey?): Observable<String> {

        this.logPath = logPath
        this.exportFileName = logPath
        this.exportPath = logPath
        this.attachNoOfFiles = attachNoOfFiles
        this.attachTimeStamp = attachTimeStamp
        this.zipName = exportFileName

        return Observable.create {

            val emitter = it

            FilterUtils.prepareOutputFile(exportPath)

            this.files = getFilesForRequestedType(type)

            //First entry is Zip Name
            this.zipName = files.first

            //Get list of all copied files from output directory
            val filesToSend = files.second

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
                                    if (PLog.getPLogger()?.isDebuggable!!)
                                        PLog.logThis(TAG, "getZippedLog", "Output Zip: $zipName", LogLevel.INFO)

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
                                    if (PLog.getPLogger()?.isDebuggable!!)
                                        PLog.logThis(TAG, "getZippedLog", "Output Zip: $zipName", LogLevel.INFO)

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

    /*
     * Will return logged data in log files.
     */
    fun getLoggedData(type: Int, logPath: String, exportPath: String, isEncrypted: Boolean, secretKey: SecretKey?): Observable<String> {

        this.logPath = logPath
        this.exportFileName = logPath
        this.exportPath = logPath

        return Observable.create {

            val emitter = it

            val files = getFilesForRequestedType(type)

            if (files.second.isEmpty()) {
                if (!emitter.isDisposed)
                    emitter.onError(Throwable("No logs found!"))
            }

            for (f in files.second) {
                emitter.onNext("Start<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n")
                emitter.onNext("File: ${f.name} Start..\n")

                if (isEncrypted) {
                    emitter.onNext(readFileDecrypted(secretKey!!, f.absolutePath))
                } else {
                    f.forEachLine {
                        emitter.onNext(it)
                    }
                }

                emitter.onNext(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>End\n")
            }

            emitter.onComplete()
        }
    }
}
