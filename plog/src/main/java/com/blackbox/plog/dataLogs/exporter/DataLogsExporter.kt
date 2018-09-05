package com.blackbox.plog.dataLogs.exporter

import com.blackbox.plog.dataLogs.filter.DataLogsFilter
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.exporter.decryptSaveFiles
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.utils.DateTimeUtils
import com.blackbox.plog.utils.readFileDecrypted
import com.blackbox.plog.utils.zip
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.crypto.SecretKey

object DataLogsExporter {

    private val TAG = DataLogsExporter::class.java.simpleName

    private var logPath = ""
    private var exportFileName = ""
    private var exportPath = ""
    private var attachTimeStamp = false


    /*
     * Will filter & export log files to zip package.
     */
    fun getDataLogs(logFileName: String, attachTimeStamp: Boolean, logPath: String, exportFileName: String, exportPath: String, debug: Boolean, isEncrypted: Boolean, secretKey: SecretKey?): Observable<String> {

        this.logPath = logPath
        this.exportPath = exportPath
        this.attachTimeStamp = attachTimeStamp
        this.exportFileName = exportFileName

        return Observable.create {

            val emitter = it

            FilterUtils.prepareOutputFile(exportPath)

            val files = composeDataExportFileName(logFileName, debug, exportFileName)

            //First entry is Zip Name
            this.exportFileName = files.first

            //Get list of all copied files from output directory
            val filesToSend = files.second

            if (filesToSend.isEmpty()) {
                if (!emitter.isDisposed)
                    emitter.onError(Throwable("No Files to zip!"))
            }

            if (isEncrypted) {
                decryptSaveFiles(filesToSend, secretKey, exportPath, this.exportFileName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onNext = {
                                    if (PLog.getPLogger()?.isDebuggable!!)
                                        PLog.logThis(TAG, "getZippedLog", "Output Zip: ${exportFileName}", LogLevel.INFO)

                                    emitter.onNext(it)
                                },
                                onError = {
                                    if (!emitter.isDisposed)
                                        emitter.onError(it)
                                },
                                onComplete = { }
                        )
            } else {
                zip(filesToSend, exportPath + this.exportFileName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onNext = {
                                    if (PLog.getPLogger()?.isDebuggable!!)
                                        PLog.logThis(TAG, "getZippedLog", "Output Zip: $exportPath${exportFileName}", LogLevel.INFO)

                                    emitter.onNext(exportPath + exportFileName)
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
    fun getLoggedData(logFileName: String, attachTimeStamp: Boolean, logPath: String, exportFileName: String, exportPath: String, debug: Boolean, isEncrypted: Boolean, secretKey: SecretKey?): Observable<String> {

        this.logPath = logPath
        this.exportPath = exportPath
        this.attachTimeStamp = attachTimeStamp
        this.exportFileName = exportFileName

        return Observable.create {

            val emitter = it

            val files = composeDataExportFileName(logFileName, debug, exportFileName)

            if (files.second.isEmpty()) {
                if (!emitter.isDisposed)
                    emitter.onError(Throwable("No data log files found to read!"))
            }

            for (f in files.second) {
                emitter.onNext("Start...................................................\n")
                emitter.onNext("File: ${f.name} Start..\n")

                if (isEncrypted) {
                    emitter.onNext(readFileDecrypted(secretKey!!, f.absolutePath))
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
     * Will compose file name of zip file to be exported.
     */
    private fun composeDataExportFileName(logFileName: String, debug: Boolean, exportFileName: String): Pair<String, List<File>> {
        var timeStamp = ""

        val files = DataLogsFilter.getFilesForLogName(logPath, exportPath, logFileName, debug)

        if (attachTimeStamp)
            timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis())

        val zipName = "$exportFileName$timeStamp.zip"

        return Pair(zipName, files)
    }
}