package com.blackbox.plog.utils

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.structure.DirectoryStructure
import java.io.File

fun writeToFile(path: String, data: String) {
    try {
        if (File(path).exists()) {
            File(path).printWriter().use { out ->
                out.println(data)
            }
        } else {
            File(path).createNewFile()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun appendToFile(path: String, data: String) {
    try {
        if (File(path).exists()) {
            File(path).appendText(data, Charsets.UTF_8)
        } else {
            File(path).createNewFile()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun checkFileExists(path: String) {
    if (!File(path).exists())
        File(path).createNewFile()
}

/*
 * This will setup directory structure according to provided 'Directory Structure' Value.
 */
fun setupFilePaths(fileName: String = ""): String {

    //Create Root folder
    val rootFolderName = "Logs"
    val rootFolderPath = PLog.logPath + rootFolderName + File.separator
    Utils.instance.createDirIfNotExists(rootFolderPath)

    when (PLog.logsConfig?.directoryStructure!!) {

        DirectoryStructure.FOR_DATE -> {
            val folderPath = rootFolderPath + DateControl.instance.today
            Utils.instance.createDirIfNotExists(folderPath)

            return if (fileName.isEmpty()) { //If file name is empty, then it's PLogger file
                val hourlyFileName = DateControl.instance.today + DateControl.instance.hour //Name of File
                folderPath + File.separator + hourlyFileName + PLog.logsConfig?.logFileExtension?.ext
            } else {
                //Otherwise it's DataLogger file.
                folderPath + File.separator + fileName + PLog.logsConfig?.logFileExtension?.ext
            }
        }

        DirectoryStructure.FOR_EVENT -> {

            val parentPath = rootFolderPath + DateControl.instance.today
            Utils.instance.createDirIfNotExists(parentPath)

            val folderPath = parentPath + File.separator + PLog.logsConfig?.nameForEventDirectory
            Utils.instance.createDirIfNotExists(folderPath)

            return if (fileName.isEmpty()) { //If file name is empty, then it's PLogger file
                val hourlyFileName = DateControl.instance.today + DateControl.instance.hour //Name of File
                folderPath + File.separator + hourlyFileName + PLog.logsConfig?.logFileExtension?.ext
            } else {
                //Otherwise it's DataLogger file.
                folderPath + File.separator + fileName + PLog.logsConfig?.logFileExtension?.ext
            }
        }

        DirectoryStructure.SINGLE_FILE_FOR_DAY -> {
            val todayPath = DateControl.instance.today
            return if (fileName.isEmpty()) { //If file name is empty, then it's PLogger file
                rootFolderPath + File.separator + todayPath + PLog.logsConfig?.logFileExtension?.ext
            } else {
                //Otherwise it's DataLogger file.
                rootFolderPath + File.separator + fileName + PLog.logsConfig?.logFileExtension?.ext
            }
        }
    }
}