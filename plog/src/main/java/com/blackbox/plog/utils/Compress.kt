package com.blackbox.plog.utils

/**
 * Created by umair on 15/05/2017.
 */

import android.content.ContentValues.TAG
import com.blackbox.plog.pLogs.PLog
import io.reactivex.Observable
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun zip(filesToSend: Array<File>, outputPath: String): Observable<Boolean> {

    return Observable.create {

        try {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(outputPath))).use { zos ->

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

        } catch (e: IOException) {
            e.printStackTrace()

            if (!it.isDisposed)
                it.onError(Throwable(e))
        }

        it.onNext(true)
        it.onComplete()
    }
}
