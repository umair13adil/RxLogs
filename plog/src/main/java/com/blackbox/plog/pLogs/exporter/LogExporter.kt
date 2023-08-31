package com.blackbox.plog.pLogs.exporter

import android.util.Log
import androidx.annotation.Keep
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.pLogs.filter.PlogFilters
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.utils.RxBus
import com.blackbox.plog.utils.zip
import com.blackbox.plog.utils.zipAll
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * Created by umair on 04/01/2018.
 */

/**
 * Created by umair on 04/01/2018.
 */
@Keep
object LogExporter {

    private val TAG = LogExporter::class.java.simpleName

    private lateinit var files: Triple<String, List<File>, String>
    private val exportPath = PLog.outputPath
    private var zipName = ""

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
 * Will filter & export log files to zip package.
 */
    fun getZippedLogs(filters: PlogFilters, exportDecrypted: Boolean): Observable<String> {

        return Observable.create {

            val emitter = it

            if (PLog.isLogsConfigSet()) {

                FilterUtils.prepareOutputFile(exportPath)

                this.files = getLogsForCustomFilter(filters)

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
    fun printLogsForType(type: String, printDecrypted: Boolean): Flowable<String> {

        val flowableOnSubscribe = FlowableOnSubscribe<String> { emitter ->

            if (PLog.isLogsConfigSet()) {

                val files = getFilesForRequestedType(type)
                Log.i(TAG, "printLogsForType: Found ${files.second.size} files.")

                if (files.second.isEmpty()) {
                    Log.e(TAG, "No logs found for type '$type'")
                }

                files.second.forEach { f ->
                    emitter.onNext("Start<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n")
                    emitter.onNext("File: ${f.name} Start..\n")

                    if (PLogImpl.isEncryptionEnabled() && printDecrypted) {
                        emitter.onNext(PLogImpl.encrypter.readFileDecrypted(f.absolutePath))
                    } else {
                        f.forEachLine {
                            emitter.onNext(it)
                        }
                    }

                    emitter.onNext(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>End\n")
                }
                emitter.onComplete()
            } else {
                Log.e(TAG, "No Logs configuration provided! Can not perform this action with logs configuration.")
            }
        }

        return Flowable.create(flowableOnSubscribe, BackpressureStrategy.BUFFER)
    }

    private fun compressPackage(emitter: ObservableEmitter<String>, exportDecrypted: Boolean) {

        //First entry is Zip Name
        this.zipName = files.first

        val filesToSend = files.second //List of filtered files

        if (PLogImpl.getConfig()?.zipFilesOnly!!) {

            if (filesToSend.isEmpty()) {
                if (!emitter.isDisposed)
                    emitter.onError(Throwable("No Files to zip!"))
            }

            if (PLogImpl.isEncryptionEnabled() && exportDecrypted) {
                decryptFirstThenZip(emitter, filesToSend = filesToSend)
            } else {
                zipFilesOnly(emitter, filesToSend)
            }

        } else {

            if (PLogImpl.isEncryptionEnabled() && exportDecrypted) {
                decryptFirstThenZip(emitter, filesToSend = filesToSend, exportedPath = "")
            } else {
                if (File(this.files.third).exists())
                    zipFilesAndFolder(emitter, this.files.third)
            }
        }
    }

    private fun decryptFirstThenZip(emitter: ObservableEmitter<String>, filesToSend: List<File> = arrayListOf<File>(), exportedPath: String = "") {
        decryptSaveFiles(filesToSend, exportPath, zipName)
                .subscribeBy(
                        onNext = {
                            if (PLogImpl.getConfig()?.isDebuggable!!)
                                Log.i(PLog.DEBUG_TAG, "Output Zip: $zipName")

                            if (!emitter.isDisposed)
                                emitter.onNext(it)
                        },
                        onError = {
                            if (!emitter.isDisposed)
                                emitter.onError(it)
                        },
                        onComplete = {
                            RxBus.send(LogEvents(EventTypes.PLOGS_EXPORTED))
                        }
                )
    }

    private fun zipFilesOnly(emitter: ObservableEmitter<String>, filesToSend: List<File>) {
        zip(filesToSend, exportPath + zipName)
                .subscribeBy(
                        onNext = {
                            if (PLogImpl.getConfig()?.isDebuggable!!)
                                Log.i(PLog.DEBUG_TAG, "Output Zip: $zipName")

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
                .subscribeBy(
                        onNext = {
                            if (PLogImpl.getConfig()?.isDebuggable!!)
                                Log.i(PLog.DEBUG_TAG, "Output Zip: $zipName")

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
        RxBus.send(LogEvents(EventTypes.PLOGS_EXPORTED))

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
