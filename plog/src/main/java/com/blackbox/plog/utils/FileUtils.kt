package com.blackbox.plog.utils

import com.blackbox.plog.pLogs.PLog
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

fun setupFilePaths(path: String): String {
    val folderPath = path + DateControl.instance.today
    Utils.instance.createDirIfNotExists(folderPath)

    val fileName_raw = DateControl.instance.today + DateControl.instance.hour
    return folderPath + File.separator + fileName_raw + PLog.getPLogger()?.logFileExtension!!
}