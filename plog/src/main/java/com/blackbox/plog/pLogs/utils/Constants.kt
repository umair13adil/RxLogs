package com.blackbox.plog.pLogs.utils

import android.net.Uri
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.config.LogsConfig
import java.io.File

//XML Save Path
val XML_PATH = getLogPath(PLog.getLogsConfig())
const val CONFIG_FILE_NAME = "config.xml"

const val LOG_FOLDER = "Logs"
const val TEMP_FOLDER = "Temp"

const val PART_FILE_PREFIX = "_part"

//Configurations
var CURRENT_PART_FILE_PATH_PLOG = ""
var PART_FILE_CREATED_PLOG = false
var CURRENT_PART_FILE_PATH_DATALOG = ""
var PART_FILE_CREATED_DATALOG = false

fun getLogPath(logsConfig: LogsConfig?): String {
    val uri = Uri.parse(logsConfig?.savePath + File.separator)
    val file = File(uri.path)
    return file.path + File.separator
}

fun getExportPath(logsConfig: LogsConfig?): String {
    val uri = Uri.parse(logsConfig?.exportPath + File.separator)
    val file = File(uri.path)
    return file.path + File.separator
}

fun getTempExportPath(logsConfig: LogsConfig?): String {
    val uri = Uri.parse(getExportPath(logsConfig) + TEMP_FOLDER + File.separator)
    val file = File(uri.path)
    return file.path + File.separator
}