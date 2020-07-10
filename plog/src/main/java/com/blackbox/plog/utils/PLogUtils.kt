package com.blackbox.plog.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.config.LogsConfig
import com.blackbox.plog.pLogs.impl.PLogImpl
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Created by Umair Adil on 18/11/2016.
 */
object PLogUtils {

    internal fun createDirIfNotExists(path: String, config: LogsConfig? = null): Boolean {
        val file = File(path)
        if (!file.exists()) {

            if (PLogImpl.getConfig(config = config)?.debugFileOperations!!)
                Log.i(PLog.DEBUG_TAG, "createDirIfNotExists: Directory created: $path")

            return file.mkdirs()
        }
        return false
    }

    internal fun getStackTrace(e: Exception?): String {
        try {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e?.printStackTrace(pw)
            return sw.toString()
        } catch (e2: Exception) {
            e2.printStackTrace()
            try {
                e?.message?.let {
                    return it
                }
                return "Error"
            } catch (e3: Exception) {
                e3.printStackTrace()
                return "Error!"
            }
        }
    }

    internal fun getStackTrace(e: Throwable?): String {
        try {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e?.printStackTrace(pw)
            return sw.toString()
        } catch (e2: Exception) {
            e2.printStackTrace()
            try {
                e?.message?.let {
                    return it
                }
                return "Error"
            } catch (e3: Exception) {
                e3.printStackTrace()
                return "Error!"
            }

        }
    }

    internal fun bytesToReadable(b: Int): String {

        var bytes = b

        if (bytes < 1024)
            return bytes.toString() + " bytes"

        bytes /= 1024
        if (bytes < 1024)
            return bytes.toString() + " Kb"

        bytes /= 1024
        if (bytes < 1024)
            return bytes.toString() + " Mb"

        bytes /= 1024
        if (bytes < 1024)
            return bytes.toString() + " Gb"

        return ""
    }

    internal fun readAssetsXML(fileName: String, context: Context): String? {
        var xmlString: String? = null
        val am = context.assets
        try {
            val `is` = am.open(fileName)
            val length = `is`.available()
            val data = ByteArray(length)
            `is`.read(data)
            xmlString = String(data)
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return xmlString
    }

    fun permissionsGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    internal fun createDir(pathName: String?): String {
        val file = File(Environment.getExternalStorageDirectory().toString() + File.separator + pathName)
        return if (!file.exists()) {
            val result = file.mkdirs()
            file.path
        } else file.path
    }
}
