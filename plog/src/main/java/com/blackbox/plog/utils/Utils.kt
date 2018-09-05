package com.blackbox.plog.utils

import android.os.Environment
import android.util.Log
import java.io.*

/**
 * Created by Umair Adil on 18/11/2016.
 */
class Utils {

    private val TAG = Utils::class.java.simpleName

    fun createDirIfNotExists(path: String) {
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    fun checkFileExists(path: String): Boolean {
        val file = File(Environment.getExternalStorageDirectory(), path)
        return file.exists()
    }

    fun copyFile(inputPath: String, inputFile: String, outputPath: String) {

        var inputStream: InputStream? = null
        var out: OutputStream? = null
        try {

            //create output directory if it doesn't exist
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            inputStream = FileInputStream(inputPath + File.separator + inputFile)
            out = FileOutputStream(outputPath + inputFile)

            out.write(inputStream.readBytes())

            // write the output file (You have now copied the file)
            out.flush()
            out.close()

            File(inputPath + inputFile).delete()

        } catch (fnfe1: FileNotFoundException) {
            Log.e(TAG, fnfe1.message)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }


    fun deleteDir(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory)
            for (child in fileOrDirectory.listFiles())
                deleteDir(child)
        fileOrDirectory.delete()
    }

    companion object {
        val instance = Utils()
    }

    fun getStackTrace(e: Exception): String {
        try {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e.printStackTrace(pw)
            return sw.toString()
        } catch (e2: Exception) {
            e2.printStackTrace()
            try {
                e.message?.let {
                    return it
                }
                return "Error"
            } catch (e3: Exception) {
                e3.printStackTrace()
                return "Error!"
            }

        }

    }

    fun getStackTrace(e: Throwable): String {
        try {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e.printStackTrace(pw)
            return sw.toString()
        } catch (e2: Exception) {
            e2.printStackTrace()
            try {
                e.message?.let {
                    return it
                }
                return "Error"
            } catch (e3: Exception) {
                e3.printStackTrace()
                return "Error!"
            }

        }

    }
}
