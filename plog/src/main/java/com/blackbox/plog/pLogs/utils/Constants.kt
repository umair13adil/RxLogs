package com.blackbox.plog.pLogs.utils

import android.net.Uri
import androidx.annotation.Keep
import com.blackbox.plog.pLogs.config.LogsConfig
import com.blackbox.plog.pLogs.impl.PLogImpl
import java.io.File
@Keep

//XML Save Path
val XML_PATH = getLogPath(PLogImpl.getConfig())
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

val PREF_LOGS_CLEAR_DATE = "sp_plogs_library_logs_clear_date"
val PREF_ZIP_DELETE_DATE = "sp_plogs_library_zip_delete_date"
val PREF_EXPORT_START_DATE = "sp_plogs_library_export_start_date"
val PREF_LOGS_CONFIG = "sp_plogs_logs_config"