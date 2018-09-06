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
fun setupFilePaths(path: String): String {

    when (PLog.getPLogger()?.directoryStructure) {

        DirectoryStructure.FOR_DATE -> {
            val folderPath = path + DateControl.instance.today
            Utils.instance.createDirIfNotExists(folderPath)

            val fileName_raw = DateControl.instance.today + DateControl.instance.hour
            return folderPath + File.separator + fileName_raw + PLog.getPLogger()?.logFileExtension?.ext!!
        }

        DirectoryStructure.FOR_EVENT -> {

            val folderPathParent = path + PLog.getPLogger()?.nameForEventDirectory
            Utils.instance.createDirIfNotExists(folderPathParent)

            val folderPath = folderPathParent + File.separator + DateControl.instance.today
            Utils.instance.createDirIfNotExists(folderPath)

            val fileName_raw = DateControl.instance.today + DateControl.instance.hour
            return folderPath + File.separator + fileName_raw + PLog.getPLogger()?.logFileExtension?.ext!!
        }

        DirectoryStructure.SINGLE_FILE_FOR_DAY -> {
            Utils.instance.createDirIfNotExists(path)
            val fileName_raw = DateControl.instance.today
            return path + File.separator + fileName_raw + PLog.getPLogger()?.logFileExtension?.ext!!
        }
    }

    return ""
}