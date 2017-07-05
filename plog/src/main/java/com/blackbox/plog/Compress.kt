package com.blackbox.plog

/**
 * Created by umair on 15/05/2017.
 */

import android.util.Log
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class Compress() {

    private val TAG = Compress::class.java.simpleName

    companion object Factory {
        fun create(): Compress = Compress()
    }

    fun zip(_files: Array<File>, outputPath: String, _zipFile: String) {
        try {
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(outputPath + _zipFile)

                val zos = ZipOutputStream(fos)

                for (i in _files.indices) {
                    if (!_files[i].name.contains(".zip")) {

                        val f = _files[i]

                        Log.i(TAG, "Adding file: " + f.name)
                        val buffer = ByteArray(1024)
                        val fis = FileInputStream(f)
                        zos.putNextEntry(ZipEntry(f.name))
                        var length: Int = fis.read(buffer)
                        while ((length) > 0) {
                            zos.write(buffer, 0, length)
                        }
                        zos.closeEntry()
                        fis.close()
                    }
                }
                zos.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

        } catch (ioe: IOException) {
            Log.e(TAG, ioe.message)
        }

    }

}
