package com.blackbox.plog.utils

import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Created by Umair Adil on 18/11/2016.
 */
object Utils {

    fun createDirIfNotExists(path: String): Boolean {
        val file = File(path)
        if (!file.exists()) {
            return file.mkdirs()
        }
        return false
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

    fun bytesToReadable(b: Int): String {

        var bytes = b

        if (bytes < 1024)
            return bytes.toString() + " bytes";

        bytes /= 1024;
        if (bytes < 1024)
            return bytes.toString() + " Kb";

        bytes /= 1024;
        if (bytes < 1024)
            return bytes.toString() + " Mb";

        bytes /= 1024;
        if (bytes < 1024)
            return bytes.toString() + " Gb";

        return ""
    }
}
