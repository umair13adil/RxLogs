package com.blackbox.plog.pLogs

import com.blackbox.plog.utils.DateControl
import com.blackbox.plog.utils.DateTimeUtils
import io.reactivex.Observable
import java.io.*
import java.util.concurrent.Callable
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Created by umair on 04/01/2018.
 */

object LogExporter {

    private val TAG = LogExporter::class.java.simpleName

    private var zipName = ""

    fun getLogs(type: Int, attachTimeStamp: Boolean, attachNoOfFiles: Boolean, logPath: String, exportFileName: String, exportPath: String): Observable<String> {

        return Observable.fromCallable(Callable<String> {
            FileFilter.prepareOutputFile(exportPath)

            var path = ""
            var files = 0
            var timeStamp = ""
            var noOfFiles = ""


            when (type) {

                PLog.LOG_TODAY -> {

                    path = logPath + DateControl.getInstance().today
                    files = FileFilter.getFilesForToday(path)

                    if (attachTimeStamp)
                        timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_Today"

                    if (attachNoOfFiles)
                        noOfFiles = "_[$files]"

                    zipName = "$exportFileName$timeStamp$noOfFiles.zip"
                }

                PLog.LOG_LAST_HOUR -> {
                    path = logPath + DateControl.getInstance().today
                    FileFilter.getFilesForLastHour(path)

                    if (attachTimeStamp)
                        timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_LastHour"

                    if (attachNoOfFiles)
                        noOfFiles = "_[" + 1 + "]"

                    zipName = "$exportFileName$timeStamp$noOfFiles.zip"
                }

                PLog.LOG_WEEK -> {
                    FileFilter.getFilesForLastWeek(logPath)

                    if (attachTimeStamp)
                        timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_Week"

                    if (attachNoOfFiles)
                        noOfFiles = "_[" + 1 + "]"

                    zipName = "$exportFileName$timeStamp$noOfFiles.zip"
                }

                PLog.LOG_LAST_TWO_DAYS -> {
                    FileFilter.getFilesForLastTwoDays(logPath)

                    if (attachTimeStamp)
                        timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_Last2Days"

                    if (attachNoOfFiles)
                        noOfFiles = "_[" + 1 + "]"

                    zipName = "$exportFileName$timeStamp$noOfFiles.zip"
                }
            }

            val outputDirectory = File(exportPath)
            val filesToSend = outputDirectory.listFiles()

            if (filesToSend != null && filesToSend.size > 0) {
                if (PLog.pLogger.isDebuggable)
                    PLog.logThis(TAG, "createZipFile", "Start Zipping Log Files.. " + filesToSend.size, PLog.TYPE_INFO)
            } else {
                if (PLog.pLogger.isDebuggable)
                    PLog.logThis(TAG, "createZipFile", "No Files to zip!", PLog.TYPE_WARNING)
                return@Callable null
            }

            ZipOutputStream(BufferedOutputStream(FileOutputStream(exportPath + zipName))).use { zos ->

                val data = ByteArray(1024)

                for (f in filesToSend) {
                    if (f.exists() && !f.name.contains(".zip")) {

                        PLog.logThis(TAG, "zipFile", "Adding file: " + f.name, PLog.TYPE_INFO)

                        FileInputStream(f).use { fi ->
                            BufferedInputStream(fi).use { origin ->
                                val entry = ZipEntry(f.name)
                                zos.putNextEntry(entry)
                                while (true) {
                                    val readBytes = origin.read(data)
                                    if (readBytes == -1) {
                                        break
                                    }
                                    zos.write(data, 0, readBytes)
                                }
                            }
                        }
                    }
                }
            }

            if (PLog.pLogger.isDebuggable)
                PLog.logThis(TAG, "getLogs", "Output Zip: $exportPath$zipName", PLog.TYPE_INFO)

            exportPath + zipName
        })

    }

    fun getDataLogs(logFileName: String, attachTimeStamp: Boolean, logPath: String, exportFileName: String, exportPath: String, debug: Boolean): Observable<String> {

        return Observable.fromCallable(Callable<String> {
            FileFilter.prepareOutputFile(exportPath)

            var timeStamp = ""
            val noOfFiles = ""

            FileFilter.getFilesForLogName(logPath, exportPath, logFileName, debug)

            if (attachTimeStamp)
                timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis())

            val zipName = "$exportFileName$timeStamp$noOfFiles.zip"

            val outputDirectory = File(exportPath)
            val filesToSend = outputDirectory.listFiles()

            if (filesToSend != null && filesToSend.size > 0) {
                if (debug)
                    PLog.logThis(TAG, "createZipFile", "Start Zipping Log Files.. " + filesToSend.size, PLog.TYPE_INFO)
            } else {
                if (debug)
                    PLog.logThis(TAG, "createZipFile", "No Files to zip!", PLog.TYPE_WARNING)
                return@Callable null
            }

            ZipOutputStream(BufferedOutputStream(FileOutputStream(exportPath + zipName))).use { zos ->

                val data = ByteArray(1024)

                for (f in filesToSend) {
                    if (f.exists() && !f.name.contains(".zip")) {

                        PLog.logThis(TAG, "zipFile", "Adding file: " + f.name, PLog.TYPE_INFO)

                        FileInputStream(f).use { fi ->
                            BufferedInputStream(fi).use { origin ->
                                val entry = ZipEntry(f.name)
                                zos.putNextEntry(entry)
                                while (true) {
                                    val readBytes = origin.read(data)
                                    if (readBytes == -1) {
                                        break
                                    }
                                    zos.write(data, 0, readBytes)
                                }
                            }
                        }
                    }
                }
            }

            if (debug)
                PLog.logThis(TAG, "getDataLogs", "Output Zip: $exportPath$zipName", PLog.TYPE_ERROR)

            exportPath + zipName
        })

    }

}
