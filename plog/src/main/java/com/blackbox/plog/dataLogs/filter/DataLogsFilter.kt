package com.blackbox.plog.dataLogs.filter

import java.io.File

object DataLogsFilter {

    fun getFilesForLogName(logsPath: String, outputPath: String, logFileName: String, debug: Boolean): List<File> {

        var size = 0
        val listOfFiles = arrayListOf<File>()

        val directory = File(logsPath)
        val files = directory.listFiles()

        if (files != null && files.size > 0) {

            size = files.size

            if (files.isNotEmpty()) {

                for (i in files.indices) {
                    if (files[i].name.contains(logFileName))
                        listOfFiles.add(files[i])
                }
            }
        }

        return listOfFiles
    }
}