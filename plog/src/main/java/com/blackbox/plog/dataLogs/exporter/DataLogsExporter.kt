package com.blackbox.plog.dataLogs.exporter

import android.util.Log
import com.blackbox.plog.dataLogs.filter.DataLogsFilter
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.exporter.ExportType
import com.blackbox.plog.pLogs.exporter.decryptSaveFiles
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.utils.readFileDecrypted
import com.blackbox.plog.utils.zip
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File

object DataLogsExporter {

    private val TAG = DataLogsExporter::class.java.simpleName

    private var exportFileName = PLogImpl.logsConfig?.zipFileName!!
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
                this.exportFileName = files.first
                this.exportPath = exportPath

                //Get list of all copied files from output directory
                val filesToSend = files.second

                if (filesToSend.isEmpty()) {
                    if (!emitter.isDisposed)

                        if (name.isNotEmpty())
                            emitter.onError(Throwable("No Files to zip for $name"))
                        else
                            emitter.onError(Throwable("No Files to zip!"))
                }

                if (PLogImpl.logsConfig?.encryptionEnabled!! && exportDecrypted) {
                    decryptSaveFiles(filesToSend, exportPath, this.exportFileName)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy(
                                    onNext = {
                                        if (PLogImpl.logsConfig?.isDebuggable!!)
                                            Log.i(PLog.TAG, "Output Zip: ${exportFileName}")

                                        emitter.onNext(it)
                                    },
                                    onError = {
                                        if (!emitter.isDisposed)
                                            emitter.onError(it)
                                    },
                                    onComplete = {
                                        PLog.getLogBus().send(LogEvents(EventTypes.DATA_LOGS_EXPORTED))
                                    }
                            )
                } else {
                    zip(filesToSend, exportPath + this.exportFileName)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy(
                                    onNext = {
                                        if (PLogImpl.logsConfig?.isDebuggable!!)
                                            Log.i(PLog.TAG, "Output Zip: $exportPath${exportFileName}")

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
                        emitter.onError(Throwable("No data log files found to read for type '$logFileName'"))
                }

                for (f in files) {
                    emitter.onNext("Start...................................................\n")
                    emitter.onNext("File: ${f.name} Start..\n")

                    if (PLogImpl.logsConfig?.encryptionEnabled!! && printDecrypted) {
                        emitter.onNext(readFileDecrypted(f.absolutePath))
                    } else {
                        f.forEachLine {
                            emitter.onNext(it)
                        }
                    }

                    emitter.onNext("...................................................End\n")
                }

                emitter.onComplete()
            } else {

                if (!emitter.isDisposed) {
                    emitter.onError(Throwable("No Logs configuration provided! Can not perform this action with logs configuration."))
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
        PLog.getLogBus().send(LogEvents(EventTypes.PLOGS_EXPORTED))

        //Print zip entries
        FilterUtils.readZipEntries(exportPath + exportFileName)

        //Clear all copied files
        FilterUtils.deleteFilesExceptZip()
    }

    private fun composeZipName(files: List<File>): String {
        var timeStamp = ""
        var noOfFiles = ""

        if (PLogImpl.logsConfig?.attachTimeStamp!!)
            timeStamp = PLog.getFormattedTimeStamp() + "_" + ExportType.TODAY.type

        if (PLogImpl.logsConfig?.attachNoOfFiles!!)
            noOfFiles = "_[${files.size}]"

        val preName = PLogImpl.logsConfig?.exportFileNamePreFix!!
        val postName = PLogImpl.logsConfig?.exportFileNamePostFix!!

        return "$preName$exportFileName$timeStamp$noOfFiles$postName.zip"
    }
}