package com.blackbox.plog.pLogs.exporter

import com.blackbox.plog.utils.readFileDecrypted
import com.blackbox.plog.utils.toBytes
import com.blackbox.plog.utils.zip
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey

fun decryptSaveFiles(filesToSend: List<File>, secretKey: SecretKey?, exportPath: String, exportFileName: String): Observable<String> {

    return Observable.create {
        val emitter = it

        val tempPath = exportPath + File.separator + "temp"

        val decryptedPath = File(tempPath)
        if (!decryptedPath.exists())
            decryptedPath.mkdirs()

        for (f in filesToSend) {
            val decrypted = readFileDecrypted(secretKey!!, f.absolutePath)
            createNewFile(f.name, decrypted, tempPath)
        }

        val outputDirectory = File(tempPath)
        val decryptedFiles = outputDirectory.listFiles().toList()

        if (decryptedFiles.isNotEmpty()) {

            zip(decryptedFiles, exportPath + exportFileName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .delay(5000, TimeUnit.MILLISECONDS)
                    .subscribeBy(
                            onNext = {
                                emitter.onNext(exportFileName)
                                File(tempPath).deleteRecursively() //delete temp file after zip is completed
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

private fun createNewFile(name: String, data: String, path: String) {
    val filePath = path + File.separator + name
    val out = FileOutputStream(filePath)
    out.write(data.toBytes())
    out.flush()
    out.close()
}