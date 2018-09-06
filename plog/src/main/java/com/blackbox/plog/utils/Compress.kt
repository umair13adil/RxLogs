package com.blackbox.plog.utils

/**
 * Created by umair on 15/05/2017.
 */

import android.content.ContentValues.TAG
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.models.LogLevel
import io.reactivex.Observable
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


fun zip(filesToSend: List<File>, outputPath: String): Observable<Boolean> {

    return Observable.create {

        try {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(outputPath))).use { zos ->

                val data = ByteArray(2048)

                for (f in filesToSend) {
                    if (f.exists() && !f.name.contains(".zip")) {

                        PLog.logThis(TAG, "zipFile", "Adding file: " + f.name, LogLevel.INFO)

                        FileInputStream(f).use { fi ->
                            BufferedInputStream(fi).use { origin ->
                                val entry = ZipEntry(f.name)
                                entry.time = f.lastModified() //Will keep last modified time of file
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
        } catch (e: Exception) {
            e.printStackTrace()
        }

        it.onNext(true)
        it.onComplete()
    }
}

fun zipAll(directory: String, outputPath: String) {

    val fileToCompress = File(directory)
    val filesToSend = fileToCompress.listFiles()

    try {
        ZipOutputStream(BufferedOutputStream(FileOutputStream(outputPath))).use { zos ->

            val data = ByteArray(2048)

            for (f in filesToSend) {
                if (f.exists() && !f.name.contains(".zip")) {
                    if (f.isDirectory) {
                        zos.putNextEntry(ZipEntry(f.name + File.separator))
                        zipAll(f.path, outputPath)
                        continue
                    } else {
                        PLog.logThis(TAG, "zipFile", "Adding file: " + f.name, LogLevel.INFO)

                        FileInputStream(f).use { fi ->
                            BufferedInputStream(fi).use { origin ->
                                val entry = ZipEntry(f.name)
                                entry.time = f.lastModified() //Will keep last modified time of file
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
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}