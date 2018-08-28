package com.blackbox.plog.pLogs.filter

import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.utils.Utils
import java.io.File

object FilterUtils {

    internal fun extractDay(name: String): Int {
        return Integer.parseInt(name.substring(0, 2))
    }

    internal fun extractHour(name: String): Int {
        return Integer.parseInt(name.substring(8, 10))
    }

    fun prepareOutputFile(outputPath: String) {
        Utils.instance.deleteDir(File(outputPath))
        Utils.instance.createDirIfNotExists(outputPath)
    }

    internal fun filterFile(folderPath: String, files: Array<File>, lastHour: Int): Boolean {
        var found = false

        for (i in files.indices) {
            val fileHour = extractHour(files[i].name)

            if (PLog.pLogger.isDebuggable!!)
                Log.i(FileFilter.TAG, "Last Hour: " + lastHour + " Check File Hour: " + fileHour + " " + files[i].name)

            if (fileHour == lastHour) {
                found = true
                Utils.instance.copyFile(folderPath, files[i].name, PLog.outputPath)
            }
        }

        return found
    }
}