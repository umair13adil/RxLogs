package com.blackbox.plog.utils

/**
 * Created by umair on 15/05/2017.
 */

import io.reactivex.Observable
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


private const val TAG = "Compress"
private const val BUFFER_SIZE = 2048

fun zip(filesToSend: List<File>, outputPath: String): Observable<Boolean> {

    return Observable.create {

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
        }

        it.onNext(true)
        it.onComplete()
    }
}

fun zipAll(directory: String, zipFile: String): Observable<Boolean> {
    val sourceFile = File(directory)

    return Observable.create {
        val emitter = it

        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use {
            it.use {
                zipFiles(it, sourceFile, "")

                emitter.onNext(true)
                emitter.onComplete()
            }
        }

    }
}

private fun zipFiles(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {

    for (f in sourceFile.listFiles()) {

        if (f.isDirectory) {

            val path = f.name + File.separator
            zipOut.putNextEntry(createZipEntry(path, f))

            //Call recursively to add files within this directory
            zipFiles(zipOut, f, f.name)
        } else {

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
            zos.putNextEntry(zipEntry)
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

