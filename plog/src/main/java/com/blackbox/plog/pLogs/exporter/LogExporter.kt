package com.blackbox.plog.pLogs.exporter

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.filter.FileFilter.readZip
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.utils.readFileDecrypted
import com.blackbox.plog.utils.zip
import com.blackbox.plog.utils.zipAll
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * Created by umair on 04/01/2018.
 */

object LogExporter {

    private val TAG = LogExporter::class.java.simpleName

    private var path = PLog.logPath
    private lateinit var files: Triple<String, List<File>, String>
    private val exportPath = PLog.outputPath
    private var zipName = PLog.getPLogger()?.zipFileName!!

    /*
     * Will filter & export log files to zip package.
     */
    fun getZippedLogs(type: Int, encrypted: Boolean): Observable<String> {

        return Observable.create {

            val emitter = it

            FilterUtils.prepareOutputFile(exportPath)

            this.files = getFilesForRequestedType(type)

            compressPackage(emitter, encrypted)
        }
    }

    /*
     * Will return logged data in log files.
     */
    fun getLoggedData(type: Int, isEncrypted: Boolean): Observable<String> {


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
                    emitter.onNext(readFileDecrypted(f.absolutePath))
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

    private fun compressPackage(emitter: ObservableEmitter<String>, isEncrypted: Boolean) {
        //First entry is Zip Name
        this.zipName = files.first

        if (PLog.getPLogger()?.zipFilesOnly!!) {

            val filesToSend = files.second //List of filtered files

            if (filesToSend.isEmpty()) {
                if (!emitter.isDisposed)
                    emitter.onError(Throwable("No Files to zip!"))
            }

            if (isEncrypted) {
                decryptFirstThenZip(emitter, filesToSend = filesToSend)
            } else {
                zipFilesOnly(emitter, filesToSend)
            }

        } else {

            if (isEncrypted) {
                decryptFirstThenZip(emitter, exportedPath = "")
            } else {
                zipFilesAndFolder(emitter, this.files.third)
            }
        }
    }

    private fun decryptFirstThenZip(emitter: ObservableEmitter<String>, filesToSend: List<File> = arrayListOf<File>(), exportedPath: String = "") {
        decryptSaveFiles(filesToSend, exportPath, zipName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            if (PLog.getPLogger()?.isDebuggable!!)
                                PLog.logThis(TAG, "decryptFirstThenZip", "Output Zip: $zipName", LogLevel.INFO)

                            emitter.onNext(it)
                        },
                        onError = {
                            if (!emitter.isDisposed)
                                emitter.onError(it)
                        },
                        onComplete = {
                            PLog.getLogBus().send(LogEvents.PLOGS_EXPORTED)
                        }
                )
    }

    private fun zipFilesOnly(emitter: ObservableEmitter<String>, filesToSend: List<File>) {
        zip(filesToSend, exportPath + zipName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            if (PLog.getPLogger()?.isDebuggable!!)
                                PLog.logThis(TAG, "zipFilesOnly", "Output Zip: $zipName", LogLevel.INFO)

                            emitter.onNext(exportPath + zipName)
                        },
                        onError = {
                            if (!emitter.isDisposed)
                                emitter.onError(it)
                        },
                        onComplete = {
                            PLog.getLogBus().send(LogEvents.PLOGS_EXPORTED)
                        }
                )
    }

    private fun zipFilesAndFolder(emitter: ObservableEmitter<String>, directory: String) {
        zipAll(directory, exportPath + zipName)

        readZip(exportPath + zipName)
        /* .subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
         .subscribeBy(
                 onNext = {
                     if (PLog.getPLogger()?.isDebuggable!!)
                         PLog.logThis(TAG, "zipFilesAndFolder", "Output Zip: $zipFileName", LogLevel.INFO)

                     emitter.onNext(exportPath + zipFileName)
                 },
                 onError = {
                     if (!emitter.isDisposed)
                         emitter.onError(it)
                 },
                 onComplete = {
                     PLog.getLogBus().send(LogEvents.PLOGS_EXPORTED)
                 }
         )*/
    }
}
