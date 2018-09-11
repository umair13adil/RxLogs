package com.blackbox.plog.dataLogs.exporter

import android.util.Log
import com.blackbox.plog.dataLogs.filter.DataLogsFilter
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.exporter.ExportType
import com.blackbox.plog.pLogs.exporter.decryptSaveFiles
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.utils.DateTimeUtils
import com.blackbox.plog.utils.readFileDecrypted
import com.blackbox.plog.utils.zip
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File

object DataLogsExporter {

    private val TAG = DataLogsExporter::class.java.simpleName

    private var exportFileName = PLog.getLogsConfig()?.zipFileName!!
    private var exportPath = ""

    /*
     * Will filter & export log files to zip package.
     */
    fun getDataLogs(name: String = "", logPath: String, exportPath: String, exportDecrypted: Boolean): Observable<String> {

        return Observable.create {

            val emitter = it

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
                    emitter.onError(Throwable("No Files to zip!"))
            }

            if (PLog.getLogsConfig()?.encryptionEnabled!! && exportDecrypted) {
                decryptSaveFiles(filesToSend, exportPath, this.exportFileName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onNext = {
                                    if (PLog.getLogsConfig()?.isDebuggable!!)
                                       Log.i(TAG, "Output Zip: ${exportFileName}")

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
                                    if (PLog.getLogsConfig()?.isDebuggable!!)
                                        Log.i(TAG, "Output Zip: $exportPath${exportFileName}")

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
        }
    }

    /*
    * Will return logged data in log files.
    */
    fun printLogsForName(logFileName: String, logPath: String, printDecrypted: Boolean): Observable<String> {

        return Observable.create {

            val emitter = it

            val files = DataLogsFilter.getFilesForLogName(logPath, logFileName)

            if (files.isEmpty()) {
                if (!emitter.isDisposed)
                    emitter.onError(Throwable("No data log files found to read!"))
            }

            for (f in files) {
                emitter.onNext("Start...................................................\n")
                emitter.onNext("File: ${f.name} Start..\n")

                if (PLog.getLogsConfig()?.encryptionEnabled!! && printDecrypted) {
                    emitter.onNext(readFileDecrypted(f.absolutePath))
                } else {
                    f.forEachLine {
                        emitter.onNext(it)
                    }
                }

                emitter.onNext("...................................................End\n")
            }

            emitter.onComplete()
        }
    }

    /*
     * Will return DataLog file for 'logFileName' along with it's composed zip name.
     */
    private fun getDataLogsForName(logFileName: String, exportFileName: String, logPath: String): Pair<String, List<File>> {

        var timeStamp = ""
        var noOfFiles = ""

        val files = DataLogsFilter.getFilesForLogName(logPath, logFileName)

        if (PLog.getLogsConfig()?.attachTimeStamp!!)
            timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_" + ExportType.TODAY.type

        if (PLog.getLogsConfig()?.attachNoOfFiles!!)
            noOfFiles = "_[${files.size}]"

        val zipName = "$exportFileName$timeStamp$noOfFiles.zip"

        return Pair(zipName, files)
    }

    /*
     * Will return all DataLog files along with it's composed zip name.
     */
    private fun getDataLogsForAll(exportFileName: String, logPath: String): Pair<String, List<File>> {

        var timeStamp = ""
        var noOfFiles = ""

        val files = DataLogsFilter.getFilesForAll(logPath)

        if (PLog.getLogsConfig()?.attachTimeStamp!!)
            timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_" + ExportType.TODAY.type

        if (PLog.getLogsConfig()?.attachNoOfFiles!!)
            noOfFiles = "_[${files.size}]"

        val zipName = "$exportFileName$timeStamp$noOfFiles.zip"

        return Pair(zipName, files)
    }

    private fun doOnZipComplete() {
        PLog.getLogBus().send(LogEvents(EventTypes.PLOGS_EXPORTED))

        //Print zip entries
        FilterUtils.readZipEntries(exportPath + exportFileName)

        //Clear all copied files
        FilterUtils.deleteFilesExceptZip()
    }
}