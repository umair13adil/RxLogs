package com.blackbox.plog.dataLogs.filter

import android.util.Log
import com.blackbox.plog.pLogs.filter.FileFilter
import com.blackbox.plog.utils.Utils
import java.io.File

object DataLogsFilter{

    fun getFilesForLogName(logsPath: String, outputPath: String, logFileName: String, debug: Boolean): Int {

        var size = 0

        val directory = File(logsPath)
        val files = directory.listFiles()

        if (files != null && files.size > 0) {

            size = files.size

            if (files.isNotEmpty()) {

                for (i in files.indices) {
                    if (files[i].name.contains(logFileName))
                        Utils.instance.copyFile(logsPath, files[i].name, outputPath)
                }
            }
        }

        return size
    }
}