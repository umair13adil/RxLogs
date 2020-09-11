package com.blackbox.plog.dataLogs.exporter

import android.util.Log
import androidx.annotation.Keep
import com.blackbox.plog.dataLogs.filter.DataLogsFilter
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.exporter.ExportType
import com.blackbox.plog.pLogs.exporter.decryptSaveFiles
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.utils.RxBus
import com.blackbox.plog.utils.zip
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File

@Keep
object DataLogsExporter {

    private val TAG = DataLogsExporter::class.java.simpleName

    private var exportFileName = PLogImpl.getConfig()?.zipFileName!!
    private var exportPath = ""

    /*
     * Will filter & export log files to zip package.
     */
    fun getDataLogs(name: String = "", logPath: String, exportPath: String, exportDecrypted: Boolean): Observable<String> {

        return Observable.create {

            val emitter = it

            if (PLog.isLogsConfigSet()) {

                FilterUtils.prepareOutputFile(exportPath)

                val files: Pair<String, List<File>> = if (name.isNotEmpty()) {
                    getDataLogsForName(name, exportFileName, logPath)
                } else {
                    getDataLogsForAll(exportFileName, logPath)
                }

                //First entry is Zip Name
                this.exportFileName += files.first
                this.exportPath = exportPath

                //Get list of all copied files from output directory
                val filesToSend = files.second

                if (filesToSend.isEmpty()) {
                    if (!emitter.isDisposed)

                        if (name.isNotEmpty())
                            Log.e(TAG, "No Files to zip for $name")
                        else
                            Log.e(TAG, "No Files to zip!")
                }

                if (PLogImpl.isEncryptionEnabled() && exportDecrypted) {
                    decryptSaveFiles(filesToSend, exportPath, this.exportFileName)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy(
                                    onNext = {
                                        if (PLogImpl.getConfig()?.isDebuggable!!)
                                            Log.i(PLog.DEBUG_TAG, "Output Zip: ${exportFileName}")

                                        if (!emitter.isDisposed)
                                            emitter.onNext(it)
                                    },
                                    onError = {
                                        if (!emitter.isDisposed)
                                            emitter.onError(it)
                                    },
                                    onComplete = {
                                        RxBus.send(LogEvents(EventTypes.DATA_LOGS_EXPORTED))
                                    }
                            )
                } else {
                    zip(filesToSend, exportPath + this.exportFileName)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy(
                                    onNext = {
                                        if (PLogImpl.getConfig()?.isDebuggable!!)
                                            Log.i(PLog.DEBUG_TAG, "Output Zip: $exportPath${exportFileName}")

                                        if (!emitter.isDisposed)
                                            emitter.onNext(exportPath + exportFileName)
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
    fun printLogsForName(logFileName: String, logPath: String, printDecrypted: Boolean): Observable<String> {

        return Observable.create {

            val emitter = it

            if (PLog.isLogsConfigSet()) {

                val files = DataLogsFilter.getFilesForLogName(logPath, logFileName)

                if (files.isEmpty()) {
                    if (!emitter.isDisposed)
                        Log.e(TAG,"No data log files found to read for type '$logFileName'")
                }

                for (f in files) {
                    if (!emitter.isDisposed) {
                        emitter.onNext("Start...................................................\n")
                        emitter.onNext("File: ${f.name} Start..\n")

                        if (PLogImpl.isEncryptionEnabled() && printDecrypted) {
                            if (!emitter.isDisposed)
                                emitter.onNext(PLogImpl.encrypter.readFileDecrypted(f.absolutePath))
                        } else {
                            f.forEachLine {
                                if (!emitter.isDisposed)
                                    emitter.onNext(it)
                            }
                        }

                        if (!emitter.isDisposed)
                            emitter.onNext("...................................................End\n")
                    }
                }

                if (!emitter.isDisposed)
                    emitter.onComplete()
            } else {

                if (!emitter.isDisposed) {
                    Log.e(TAG,"No Logs configuration provided! Can not perform this action with logs configuration.")
                }
            }
        }
    }

    /*
     * Will return DataLog file for 'logFileName' along with it's composed zip name.
     */
    private fun getDataLogsForName(logFileName: String, exportFileName: String, logPath: String): Pair<String, List<File>> {

        val files = DataLogsFilter.getFilesForLogName(logPath, logFileName)
        val zipName = composeZipName(files)

        return Pair(zipName, files)
    }

    /*
     * Will return all DataLog files along with it's composed zip name.
     */
    private fun getDataLogsForAll(exportFileName: String, logPath: String): Pair<String, List<File>> {

        val files = DataLogsFilter.getFilesForAll(logPath)
        val zipName = composeZipName(files)

        return Pair(zipName, files)
    }

    private fun doOnZipComplete() {
        RxBus.send(LogEvents(EventTypes.PLOGS_EXPORTED))

        //Print zip entries
        //FilterUtils.readZipEntries(exportPath + exportFileName)

        //Clear all copied files
        FilterUtils.deleteFilesExceptZip()
    }

    private fun composeZipName(files: List<File>): String {
        var timeStamp = ""
        var noOfFiles = ""

        if (PLogImpl.getConfig()?.attachTimeStamp!!)
            timeStamp = PLog.getTimeStampForOutputFile() + "_" + ExportType.TODAY.type

        if (PLogImpl.getConfig()?.attachNoOfFiles!!)
            noOfFiles = "_[${files.size}]"

        val preName = PLogImpl.getConfig()?.exportFileNamePreFix!!
        val postName = PLogImpl.getConfig()?.exportFileNamePostFix!!

        return "$preName$exportFileName$timeStamp$noOfFiles$postName.zip"
    }
}