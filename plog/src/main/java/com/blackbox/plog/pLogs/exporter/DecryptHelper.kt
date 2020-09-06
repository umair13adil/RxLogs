package com.blackbox.plog.pLogs.exporter

import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.utils.RxBus
import com.blackbox.plog.utils.zip
import com.blackbox.plog.utils.zipAll
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

fun decryptSaveFiles(filesToSend: List<File>, exportPath: String, exportFileName: String): Observable<String> {

    val exportFilesOnly = PLogImpl.getConfig()?.zipFilesOnly!!

    return Observable.create { emitter ->

        val tempPath = exportPath + File.separator + "temp"

        val decryptedPath = File(tempPath)
        decryptedPath.mkdirs()

        for (f in filesToSend) {
            val decrypted = PLogImpl.encrypter.readFileDecrypted(f.absolutePath)

            if (exportFilesOnly) {
                createNewFile(f.name, decrypted, tempPath)
            } else {
                val directoryName = getParentDirectory(f.path)
                val fileName = getFileNameFromPath(f.path)

                val directory = File(tempPath, directoryName)
                directory.mkdirs()

                createNewFile(fileName, decrypted, directory.path)
            }
        }

        val outputDirectory = File(tempPath)
        val outputPath = exportPath + exportFileName

        val file = File(outputPath)
        if (!file.exists()) {
            file.createNewFile()
        }

        if (exportFilesOnly) {
            outputDirectory.listFiles()?.toList()?.let { decryptedFiles ->
                if (decryptedFiles.isNotEmpty()) {
                    zipFilesOnly(decryptedFiles, outputPath, exportFileName, tempPath, emitter)
                }
            }
        } else {
            zipFilesAndFolder(outputPath, exportFileName, tempPath, emitter)
        }
    }
}

private fun zipFilesOnly(decryptedFiles: List<File>, outputPath: String, exportFileName: String, tempPath: String, emitter: ObservableEmitter<String>) {
    zip(decryptedFiles, outputPath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .delay(5000, TimeUnit.MILLISECONDS) //Add delay to make sure files are decrypted
            .subscribeBy(
                    onNext = {
                        if (!emitter.isDisposed)
                            emitter.onNext(exportFileName)

                        File(tempPath).deleteRecursively() //delete temp file after zip is completed
                    },
                    onError = {
                        if (!emitter.isDisposed)
                            emitter.onError(it)

                        if (PLogImpl.getConfig()?.debugFileOperations!!)
                            Log.i(PLog.DEBUG_TAG, "zipFilesOnly: Unable to zip, ${it.message}")
                    },
                    onComplete = { }
            )
}

private fun zipFilesAndFolder(outputPath: String, exportFileName: String, tempPath: String, emitter: ObservableEmitter<String>) {

    zipAll(tempPath, outputPath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                    onNext = {

                        if (!emitter.isDisposed)
                            emitter.onNext(outputPath)
                    },
                    onError = {
                        if (!emitter.isDisposed)
                            emitter.onError(it)

                        if (PLogImpl.getConfig()?.debugFileOperations!!)
                            Log.i(PLog.DEBUG_TAG, "zipFilesAndFolder: Unable to zip, ${it.message}")
                    },
                    onComplete = {
                        doOnZipComplete(outputPath)
                    }
            )
}

private fun doOnZipComplete(path: String) {
    RxBus.send(LogEvents(EventTypes.PLOGS_EXPORTED))

    //Clear all copied files
    FilterUtils.deleteFilesExceptZip()
}

private fun createNewFile(name: String, data: String, path: String) {
    try {
        val file = File(path, name)
        file.createNewFile()

        RxBus.send(LogEvents(EventTypes.NEW_EVENT_LOG_FILE_CREATED, file.name))

        if (file.exists()) {

            FileOutputStream(file.path).use { out ->
                out.write(data.toBytes())
            }

        } else {
            if (PLogImpl.getConfig()?.debugFileOperations!!)
                Log.i(PLog.DEBUG_TAG, "createNewFile: ${file.path} doesnt exists!")
        }
    } catch (e: Exception) {
        if (PLogImpl.getConfig()?.debugFileOperations!!)
            Log.i(PLog.DEBUG_TAG, "createNewFile: Unable to create new file. ${e.message}")
    }
}

private fun getParentDirectory(path: String): String {
    val file = File(path)
    return getFileNameFromPath(file.parent)
}

private fun getFileNameFromPath(path: String): String {
    return path.substring(path.lastIndexOf("/") + 1)
}

/*
     * This will convert string to byte array.
     */
@Synchronized
fun String.toBytes(): ByteArray {
    return this.toByteArray(Charsets.UTF_8)
}