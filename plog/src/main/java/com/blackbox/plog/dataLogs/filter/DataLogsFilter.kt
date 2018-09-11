package com.blackbox.plog.dataLogs.filter

import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.filter.FilterUtils
import java.io.File

object DataLogsFilter {

    val TAG = "DataLogsFilter"
    val enabledTypes: List<String>

    init {
        enabledTypes = PLog.getLogsConfig()?.logTypesEnabled!!
    }

    fun getFilesForLogName(logsPath: String, logFileName: String): List<File> {

        val listOfFiles = arrayListOf<File>()
        val files = FilterUtils.listFiles(logsPath, arrayListOf())

        if (PLog.getLogsConfig()?.isDebuggable!!)
            Log.i(TAG, "Found files: ${files.size}")

        if (files.isNotEmpty()) {

            for (i in files.indices) {

                val fileName = files[i]

                val fileNameShort = fileName.name?.substringBefore("_")
                if (fileNameShort == logFileName) {
                    listOfFiles.add(fileName)
                }
            }
        }

        return listOfFiles
    }

    fun getFilesForAll(logsPath: String): List<File> {

        val listOfFiles = arrayListOf<File>()
        val files = FilterUtils.listFiles(logsPath, arrayListOf())

        if (PLog.getLogsConfig()?.isDebuggable!!)
            Log.i(TAG, "Found files: ${files.size}")

        if (files.isNotEmpty()) {

            for (i in files.indices) {

                val fileName = files[i]

                val fileNameShort = fileName.name?.substringBefore("_")
                if (enabledTypes.contains(fileNameShort)) {
                    listOfFiles.add(fileName)
                }
            }
        }

        return listOfFiles
    }
}