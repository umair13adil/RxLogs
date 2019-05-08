package com.blackbox.plog.pLogs.exporter

import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.utils.zip
import com.blackbox.plog.utils.zipAll
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by umair on 04/01/2018.
 */

object LogExporter {

    private val TAG = LogExporter::class.java.simpleName

    private lateinit var files: Triple<String, List<File>, String>
    private val exportPath = PLog.outputPath
    private var zipName = PLogImpl.getConfig()?.zipFileName

    /*
     * Will filter & export log files to zip package.
     */
    fun getZippedLogs(type: String, exportDecrypted: Boolean): Observable<String> {

        return Observable.create {

            val emitter = it

            if (PLog.isLogsConfigSet()) {

                FilterUtils.prepareOutputFile(exportPath)

                this.files = getFilesForRequestedType(type)

                compressPackage(emitter, exportDecrypted)
            } else {

                if (!emitter.isDisposed) {
                    emitter.onError(Throwable("No Logs configuration provided! Can not perform this action with logs configuration."))
                }
            }
        }
    }

    /*
     * Will return logged data in log files.
     */
    fun printLogsForType(type: String, printDecrypted: Boolean): Observable<String> {


        return Observable.create {

            val emitter = it

            if (PLog.isLogsConfigSet()) {

                val files = getFilesForRequestedType(type)

                if (files.second.isEmpty()) {
                    if (!emitter.isDisposed)
                        emitter.onError(Throwable("No logs found for type '$type'"))
                }

                for (f in files.second) {
                    if (!emitter.isDisposed) {
                        emitter.onNext("Start<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n")
                        emitter.onNext("File: ${f.name} Start..\n")

                        if (PLogImpl.getConfig()?.encryptionEnabled!! && printDecrypted) {
                            emitter.onNext(PLogImpl.encrypter.readFileDecrypted(f.absolutePath))
                        } else {
                            f.forEachLine {
                                emitter.onNext(it)
                            }
                        }

                        emitter.onNext(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>End\n")
                    }
                }

                if (!emitter.isDisposed)
                    emitter.onComplete()
            } else {

                if (!emitter.isDisposed) {
                    emitter.onError(Throwable("No Logs configuration provided! Can not perform this action with logs configuration."))
                }
            }
        }
    }

    private fun compressPackage(emitter: ObservableEmitter<String>, exportDecrypted: Boolean) {
        //First entry is Zip Name
        this.zipName += files.first

        val filesToSend = files.second //List of filtered files

        if (PLogImpl.getConfig()?.zipFilesOnly!!) {

            if (filesToSend.isEmpty()) {
                if (!emitter.isDisposed)
                    emitter.onError(Throwable("No Files to zip!"))
            }

            if (PLogImpl.getConfig()?.encryptionEnabled!! && exportDecrypted) {
                decryptFirstThenZip(emitter, filesToSend = filesToSend)
            } else {
                zipFilesOnly(emitter, filesToSend)
            }

        } else {

            if (PLogImpl.getConfig()?.encryptionEnabled!! && exportDecrypted) {
                decryptFirstThenZip(emitter,filesToSend = filesToSend, exportedPath = "")
            } else {
                zipFilesAndFolder(emitter, this.files.third)
            }
        }
    }

    private fun decryptFirstThenZip(emitter: ObservableEmitter<String>, filesToSend: List<File> = arrayListOf<File>(), exportedPath: String = "") {
        decryptSaveFiles(filesToSend, exportPath, zipName!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeBy(
                        onNext = {
                            if (PLogImpl.getConfig()?.isDebuggable!!)
                                Log.i(PLog.TAG, "Output Zip: $zipName")

                            if (!emitter.isDisposed)
                                emitter.onNext(it)
                        },
                        onError = {
                            if (!emitter.isDisposed)
                                emitter.onError(it)
                        },
                        onComplete = {
                            PLog.getLogBus().send(LogEvents(EventTypes.PLOGS_EXPORTED))
                        }
                )
    }

    private fun zipFilesOnly(emitter: ObservableEmitter<String>, filesToSend: List<File>) {
        zip(filesToSend, exportPath + zipName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeBy(
                        onNext = {
                            if (PLogImpl.getConfig()?.isDebuggable!!)
                                Log.i(PLog.TAG, "Output Zip: $zipName")

                            if (!emitter.isDisposed)
                                emitter.onNext(exportPath + zipName)
                        },
                        onError = {
                            if (!emitter.isDisposed)
                                emitter.onError(it)
                        },
                        onComplete = {
                            doOnZipComplete()
                        }
                )
    }

    private fun zipFilesAndFolder(emitter: ObservableEmitter<String>, directory: String) {
        zipAll(directory, exportPath + zipName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeBy(
                        onNext = {
                            if (PLogImpl.getConfig()?.isDebuggable!!)
                                Log.i(PLog.TAG, "Output Zip: $zipName")

                            if (!emitter.isDisposed)
                                emitter.onNext(exportPath + zipName)
                        },
                        onError = {
                            if (!emitter.isDisposed)
                                emitter.onError(it)
                        },
                        onComplete = {
                            doOnZipComplete()
                        }
                )
    }

    private fun doOnZipComplete() {
        PLog.getLogBus().send(LogEvents(EventTypes.PLOGS_EXPORTED))

        //Print zip entries
        //FilterUtils.readZipEntries(exportPath + zipName)

        //Clear all copied files
        FilterUtils.deleteFilesExceptZip()
    }

    fun formatErrorMessage(errorMessage: String): String {

        val OPENING_TAG_HEADER = "<br/><b style=\"color:gray;\">"
        val CLOSING_TAG_HEADER = "&nbsp;</b>"

        val OPENING_TAG = "<br/><style=\"color:gray;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
        val CLOSING_TAG = "&nbsp;</>"

        return try {
            val lines = errorMessage.split("\\t".toRegex())

            var formatted = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<body>"

            formatted += ("$OPENING_TAG_HEADER${lines.first()}$CLOSING_TAG_HEADER")

            lines.forEachIndexed { index, s ->
                if (index > 0)
                    formatted += ("$OPENING_TAG$s$CLOSING_TAG")
            }

            formatted += "</body>\n" +
                    "</html>"

            formatted

        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage
        }
    }
}
