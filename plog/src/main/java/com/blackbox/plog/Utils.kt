package com.blackbox.plog

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * Created by Umair on 18/11/2016.
 */
class Utils {

    private val TAG = Utils::class.java.simpleName

    companion object {
        val instance = Utils()
    }

    fun createDirIfNotExists(path: String): Boolean {
        var ret = true
        val file = File(Environment.getExternalStorageDirectory(), path)
        if (!file.exists()) {
            if (!file.mkdirs()) {
                ret = false
            } else {
                Log.i(TAG, "Directory Created!")
            }
        }
        return ret
    }

    fun checkFileExists(path: String): Boolean {
        val file = File(Environment.getExternalStorageDirectory(), path)
        return file.exists()
    }

    fun copyFile(inputPath: String, inputFile: String, outputPath: String) {

        var input: InputStream? = null
        try {

            //create output directory if it doesn't exist
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            input = FileInputStream(inputPath + File.separator + inputFile)

            File(outputPath+File.separator+inputFile).copyInputStreamToFile(input)

        } catch (fnfe1: FileNotFoundException) {
            Log.e(TAG, fnfe1.message)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }

    fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }


    fun deleteDir(dir: File): Boolean {
        if (dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete()
    }


}
