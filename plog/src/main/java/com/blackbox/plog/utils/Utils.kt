package com.blackbox.plog.utils

import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Created by Umair Adil on 18/11/2016.
 */
class Utils {

    private val TAG = Utils::class.java.simpleName

    fun createDirIfNotExists(path: String): Boolean {
        val file = File(path)
        if (!file.exists()) {
            return file.mkdirs()
        }
        return false
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
