package com.blackbox.plog.utils

import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.pLogs.structure.DirectoryStructure
import com.blackbox.plog.pLogs.utils.LOG_FOLDER
import com.blackbox.plog.pLogs.utils.PART_FILE_CREATED_DATALOG
import com.blackbox.plog.pLogs.utils.PART_FILE_CREATED_PLOG
import com.blackbox.plog.pLogs.utils.PART_FILE_PREFIX
import com.blackbox.plog.utils.Utils.createDirIfNotExists
import java.io.File

private var currentNameOfDirectory = ""

fun writeToFile(path: String, data: String) {
    try {
        val file = File(path)
        if (file.exists()) {
            file.printWriter().use { out ->
                out.println(data)
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
            file.appendText(data, Charsets.UTF_8)
        } else {
            file.createNewFile()
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun checkFileExists(path: String, isPLog: Boolean = true): File {
    val file = File(path)

    if (!file.exists()) {
        file.createNewFile()

        //Check if file created if part file
        if (isPLog && !file.name.contains(PART_FILE_PREFIX)) {
            PART_FILE_CREATED_PLOG = false
        } else if (!isPLog && !file.name.contains(PART_FILE_PREFIX)) {
            PART_FILE_CREATED_DATALOG = false
        }

        if (PLogImpl.getLogsConfig(PLog)?.debugFileOperations!!)
            Log.i(PLog.TAG, "New file created: ${file.path}")
    }

    return file
}

/*
 * This will setup directory structure according to provided 'Directory Structure' Value.
 */
fun setupFilePaths(fileName: String = "", isPLog: Boolean = true): String {

    //Create Root folder
    val rootFolderName = LOG_FOLDER
    val rootFolderPath = PLog.logPath + rootFolderName + File.separator
    createDirIfNotExists(rootFolderPath)

    when (PLogImpl.getLogsConfig(PLog)?.directoryStructure!!) {

        DirectoryStructure.FOR_DATE -> {
            val folderPath = rootFolderPath + DateControl.instance.today
            createDirIfNotExists(folderPath)
            val hourlyFileName = DateControl.instance.today + DateControl.instance.hour //Name of File

            return if (fileName.isEmpty()) { //If file name is empty, then it's PLogger file
                folderPath + File.separator + hourlyFileName + PLogImpl.getLogsConfig(PLog)?.logFileExtension!!
            } else {
                if (isPLog) {
                    folderPath + File.separator + hourlyFileName + fileName + PLogImpl.getLogsConfig(PLog)?.logFileExtension!!
                } else {
                    //Otherwise it's DataLogger file.
                    folderPath + File.separator + fileName + PLogImpl.getLogsConfig(PLog)?.logFileExtension!!
                }
            }
        }

        DirectoryStructure.FOR_EVENT -> {

            val parentPath = rootFolderPath + DateControl.instance.today
            createDirIfNotExists(parentPath)

            val nameForEventDirectory = PLogImpl.getLogsConfig(PLog)?.nameForEventDirectory!!

            val folderPath = parentPath + File.separator + nameForEventDirectory

            //Create directory for event name and check it's result
            if (createDirIfNotExists(folderPath)) {
                isDirectoryChanged(nameForEventDirectory)
            }
            currentNameOfDirectory = nameForEventDirectory //Set current name of directory

            val hourlyFileName = currentNameOfDirectory + "_" + DateControl.instance.hour //Name of File

            return if (fileName.isEmpty()) { //If file name is empty, then it's PLogger file
                folderPath + File.separator + hourlyFileName + PLogImpl.getLogsConfig(PLog)?.logFileExtension!!
            } else {
                if (isPLog) {
                    folderPath + File.separator + hourlyFileName + fileName + PLogImpl.getLogsConfig(PLog)?.logFileExtension!!
                } else {
                    //Otherwise it's DataLogger file.
                    folderPath + File.separator + fileName + PLogImpl.getLogsConfig(PLog)?.logFileExtension!!
                }
            }
        }

        DirectoryStructure.SINGLE_FILE_FOR_DAY -> {
            val todayPath = DateControl.instance.today
            return if (fileName.isEmpty()) { //If file name is empty, then it's PLogger file
                rootFolderPath + File.separator + todayPath + PLogImpl.getLogsConfig(PLog)?.logFileExtension!!
            } else {
                //Otherwise it's DataLogger file.
                rootFolderPath + File.separator + fileName + PLogImpl.getLogsConfig(PLog)?.logFileExtension!!
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

    when (PLogImpl.getLogsConfig(PLog)?.directoryStructure!!) {

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