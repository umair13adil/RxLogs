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

    internal var BUFFER_SIZE: Int? = 1024

    @Throws(IOException::class)
    fun zip(files: Array<File>, outputPath: String, zipFile: String) {
        var origin: BufferedInputStream? = null

        val out = ZipOutputStream(BufferedOutputStream(FileOutputStream(outputPath + zipFile)))

        try {
            val data = ByteArray(BUFFER_SIZE!!)

            for (file: File in files) {
                if (!file.name.contains(".zip")) {

                    val fi = FileInputStream(file)
                    origin = BufferedInputStream(fi, BUFFER_SIZE!!)
                    try {
                        Log.i(TAG, "Adding file: " + file.name)

                        val entry = ZipEntry(file.name)
                        out.putNextEntry(entry)

                        var count: Int = origin.read(data, 0, BUFFER_SIZE!!)
                        out.write(data, 0, count)
                    } finally {
                        origin.close()
                    }
                }
            }
        } finally {
            out.close()
        }

    }

}
