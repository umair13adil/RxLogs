package com.blackbox.plog.utils

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.structure.DirectoryStructure
import com.blackbox.plog.pLogs.utils.LOG_FOLDER
import java.io.File

private var currentNameOfDirectory = ""

fun writeToFile(path: String, data: String) {
    try {
        val file = File(path)
        if (file.exists()) {

            if (PLog.shouldWriteLog(file)) {
                file.printWriter().use { out ->
                    out.println(data)
                }
            }

        } else {
            file.createNewFile()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun appendToFile(path: String, data: String) {
    try {
        val file = File(path)

        if (file.exists()) {

            if (PLog.shouldWriteLog(file)) {
                file.appendText(data, Charsets.UTF_8)
            }

        } else {
            file.createNewFile()
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun checkFileExists(path: String): File {
    val file = File(path)

    if (!file.exists()) {
        PLog.getLogBus().send(LogEvents(EventTypes.NEW_LOG_FILE_CREATED, file.name))
        file.createNewFile()
    }

    return file
}

/*
 * This will setup directory structure according to provided 'Directory Structure' Value.
 */
fun setupFilePaths(fileName: String = ""): String {

    //Create Root folder
    val rootFolderName = LOG_FOLDER
    val rootFolderPath = PLog.logPath + rootFolderName + File.separator
    Utils.instance.createDirIfNotExists(rootFolderPath)

    when (PLog.getLogsConfig()?.directoryStructure!!) {

        DirectoryStructure.FOR_DATE -> {
            val folderPath = rootFolderPath + DateControl.instance.today
            Utils.instance.createDirIfNotExists(folderPath)

            return if (fileName.isEmpty()) { //If file name is empty, then it's PLogger file
                val hourlyFileName = DateControl.instance.today + DateControl.instance.hour //Name of File
                folderPath + File.separator + hourlyFileName + PLog.getLogsConfig()?.logFileExtension!!
            } else {
                //Otherwise it's DataLogger file.
                folderPath + File.separator + fileName + PLog.getLogsConfig()?.logFileExtension!!
            }
        }

        DirectoryStructure.FOR_EVENT -> {

            val parentPath = rootFolderPath + DateControl.instance.today
            Utils.instance.createDirIfNotExists(parentPath)

            val nameForEventDirectory = PLog.getLogsConfig()?.nameForEventDirectory!!

            val folderPath = parentPath + File.separator + nameForEventDirectory

            //Create directory for event name and check it's result
            if (Utils.instance.createDirIfNotExists(folderPath)) {
                isDirectoryChanged(nameForEventDirectory)
            }
            currentNameOfDirectory = nameForEventDirectory //Set current name of directory

            val hourlyFileName = currentNameOfDirectory + "_" + DateControl.instance.hour //Name of File

            return if (fileName.isEmpty()) { //If file name is empty, then it's PLogger file
                folderPath + File.separator + hourlyFileName + PLog.getLogsConfig()?.logFileExtension!!
            } else {
                //Otherwise it's DataLogger file.
                folderPath + File.separator + fileName + "_" + hourlyFileName + PLog.getLogsConfig()?.logFileExtension!!
            }
        }

        DirectoryStructure.SINGLE_FILE_FOR_DAY -> {
            val todayPath = DateControl.instance.today
            return if (fileName.isEmpty()) { //If file name is empty, then it's PLogger file
                rootFolderPath + File.separator + todayPath + PLog.getLogsConfig()?.logFileExtension!!
            } else {
                //Otherwise it's DataLogger file.
                rootFolderPath + File.separator + fileName + PLog.getLogsConfig()?.logFileExtension!!
            }
        }
    }
}

/*
 * This will return 'LogsPath' according to selected Directory Structure.
 */
fun getLogsSavedPaths(nameForEventDirectory: String = "", isForAll: Boolean = false): String {

    //Create Root folder
    val rootFolderName = LOG_FOLDER
    val rootFolderPath = PLog.logPath + rootFolderName + File.separator

    when (PLog.getLogsConfig()?.directoryStructure!!) {

        DirectoryStructure.FOR_DATE -> {
            val folderPath = rootFolderPath + DateControl.instance.today
            return folderPath + File.separator
        }

        DirectoryStructure.FOR_EVENT -> {
            val parentPath = rootFolderPath + DateControl.instance.today

            if (isForAll)
                return parentPath

            val folderPath = parentPath + File.separator + nameForEventDirectory
            return folderPath + File.separator
        }

        DirectoryStructure.SINGLE_FILE_FOR_DAY -> {
            val todayPath = DateControl.instance.today
            return rootFolderPath + File.separator
        }
    }
}

/*
 * This will publish a event to notify in case a new directory is created for an event.
 */
private fun isDirectoryChanged(name: String) {
    if (name.isNotEmpty() && currentNameOfDirectory.isNotEmpty() && name != currentNameOfDirectory) {
        PLog.getLogBus().send(LogEvents(EventTypes.NEW_EVENT_DIRECTORY_CREATED, name))
    }
}