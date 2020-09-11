package com.blackbox.plog.dataLogs.filter

import android.util.Log
import androidx.annotation.Keep
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.filter.FilterUtils
import com.blackbox.plog.pLogs.impl.PLogImpl
import java.io.File

@Keep
object DataLogsFilter {

    val TAG = "DataLogsFilter"
    val enabledTypes: List<String>

    init {
        enabledTypes = PLogImpl.getConfig()?.logTypesEnabled!!
    }

    fun getFilesForLogName(logsPath: String, logFileName: String): List<File> {

        val listOfFiles = arrayListOf<File>()
        val files = FilterUtils.listFiles(logsPath, arrayListOf())

        if (PLogImpl.getConfig()?.isDebuggable!!)
            Log.i(PLog.DEBUG_TAG, "Found files: ${files.size}")

        if (files.isNotEmpty()) {

            for (i in files.indices) {

                val fileName = files[i]

                val fName = fileName.name

                val fileNameShort = if (fName.contains("_")) {
                    fName?.substringBefore("_")!! //Check if is a part file
                } else {
                    fName?.substringBefore(".")!! //In case of simple file, match name before ext
                }

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

        if (PLogImpl.getConfig()?.isDebuggable!!)
            Log.i(PLog.DEBUG_TAG, "Found files: ${files.size}")

        if (files.isNotEmpty()) {

            for (i in files.indices) {

                val fileName = files[i]

                val fName = fileName.name

                val fileNameShort = if (fName.contains("_")) {
                    fName?.substringBefore("_")!! //Check if is a part file
                } else {
                    fName?.substringBefore(".")!! //In case of simple file, match name before ext
                }

                if (enabledTypes.contains(fileNameShort)) {
                    listOfFiles.add(fileName)
                }
            }
        }

        return listOfFiles
    }
}