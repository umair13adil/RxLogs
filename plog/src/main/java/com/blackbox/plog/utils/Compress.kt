package com.blackbox.plog.utils

/**
 * Created by umair on 15/05/2017.
 */

import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.impl.PLogImpl
import io.reactivex.Observable
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipOutputStream


private const val TAG = "Compress"
private const val BUFFER_SIZE = 2048

fun zip(filesToSend: List<File>, outputPath: String): Observable<Boolean> {

    val file = File(outputPath)
    if (!file.exists()) {
        file.createNewFile()
    }

    return Observable.create {
        if (File(outputPath).exists() && filesToSend.isNotEmpty()) {
            try {
                ZipOutputStream(BufferedOutputStream(FileOutputStream(outputPath))).use { zos ->

                    for (f in filesToSend) {
                        if (f.exists() && !f.name.contains(".zip")) {

                            //Write file to zip
                            writeToZip(f, zos, createZipEntry(f.name, f))
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()

                if (!it.isDisposed) {
                    it.onError(e)
                    it.onComplete()
                }
            }

            if (!it.isDisposed) {
                it.onNext(true)
                it.onComplete()
            }
        } else {
            Log.e(TAG, "$outputPath File doesn't exist or list of files provided is empty.")
            if (!it.isDisposed) {
                it.onNext(false)
                it.onComplete()
            }
        }
    }
}

fun zipAll(directory: String, zipFile: String): Observable<Boolean> {

    val sourceFile = File(directory)

    return Observable.create {
        val emitter = it

        try {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use {
                it.use {
                    zipDirectories(it, sourceFile, "")

                    if (!emitter.isDisposed) {
                        emitter.onNext(true)
                        emitter.onComplete()
                    }
                }
            }
        } catch (e: Exception) {

            if (!emitter.isDisposed) {
                emitter.onError(e)
                emitter.onComplete()
            }
        }

    }
}

private fun zipDirectories(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {
    try {
        val listOfDirectory = arrayListOf<String>()

        for (f in sourceFile.listFiles()) {

            if (f.isDirectory) {
                listOfDirectory.add(f.path)
            }
        }

        listOfDirectory.forEach {

            val path = it + File.separator

            if (PLogImpl.getConfig()?.debugFileOperations!!)
                Log.i(PLog.DEBUG_TAG, "Adding directory: $path")


            //Call recursively to add files within this directory
            zipFiles(zipOut, File(it), File(it).name)
        }
    } catch (e: Exception) {

    }
}


private fun zipFiles(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {

    for (f in sourceFile.listFiles()) {

        if (!f.name.contains(".zip")) { //If folder contains a file with extension ".zip", skip it

            val path = parentDirPath + File.separator + f.name

            //Write file to zip
            writeToZip(f, zipOut, createZipEntry(path, f))

        } else {
            zipOut.closeEntry()
            zipOut.close()
        }
    }
}

private fun createZipEntry(path: String, f: File): ZipEntry {

    val entry = ZipEntry(path)
    entry.time = f.lastModified()
    entry.isDirectory
    entry.size = f.length()

    return entry
}

private fun writeToZip(f: File, zos: ZipOutputStream, zipEntry: ZipEntry) {
    val data = ByteArray(BUFFER_SIZE)

    FileInputStream(f).use { fi ->
        BufferedInputStream(fi).use { origin ->

            try {
                zos.putNextEntry(zipEntry)
            } catch (e: ZipException) {
                if (PLogImpl.getConfig()?.debugFileOperations!!) {
                    e.message?.let { Log.e(TAG, it) }
                }
            }

            if (PLogImpl.getConfig()?.debugFileOperations!!)
                Log.i(PLog.DEBUG_TAG, "Adding file: ${f.path}")


            while (true) {
                val readBytes = origin.read(data)
                if (readBytes == -1) {
                    break
                }

                try {
                    zos.write(data, 0, readBytes)
                } catch (e: ZipException) {
                    if (PLogImpl.getConfig()?.debugFileOperations!!) {
                        e.message?.let { Log.e(TAG, it) }
                    }
                }
            }
        }
    }
}

private fun getParentDirectory(path: String): String {
    val file = File(path)
    return getFileNameFromPath(file.parent)
}

private fun getFileNameFromPath(path: String): String {
    return path.substring(path.lastIndexOf("/") + 1)
}
