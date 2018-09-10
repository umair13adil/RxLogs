package com.blackbox.plog.pLogs.config

import com.blackbox.plog.pLogs.PLog
import java.io.File

//XML Save Path
val XML_PATH = PLog.logPath + File.separator + "config.xml"

//Parent TAGs
const val ROOT_TAG = "Config"
const val LOG_TYPES_ENABLED_TAG = "LogTypesEnabled"
const val LOG_LEVELS_ENABLED_TAG = "LogLevelsEnabled"
const val FORMAT_TYPE_TAG = "FormatType"
const val LOGS_RETENTION_TAG = "LogsRetention"
const val ZIP_RETENTION_TAG = "ZipRetention"
const val AUTO_CLEAR_TAG = "AutoClear"
const val AUTO_EXPORT_ERRORS_TAG = "ExportErrors"
const val ENCRYPTION_ENABLED_TAG = "Encryption"
const val LOG_FILE_SIZE_TAG = "FileSizeMax"
const val LOG_FILES_MAX_TAG = "LogFilesMax"
const val DIRECTORY_TAG = "Directory"
const val LOG_SYSTEM_CRASHES_TAG = "SystemCrashes"
const val AUTO_EXPORT_TYPES_TAG = "AutoExportTypes"
const val LOGS_DELETE_DATE_TAG = "LogsDeleteDate"
const val ZIP_DELETE_DATE_TAG = "ZipDeleteDate"
const val LOGS_SAVE_PATH_TAG = "LogsSavePath"
const val LOGS_EXPORT_PATH_TAG = "LogsExportPath"
const val EXPORT_TAG = "Export"
const val CSV_TAG = "CSV"


//Attributes
const val VALUE_ATTR = "value"
const val IS_DEBUGGABLE_ATTR = "isDebuggable"
const val LOG_FILE_EXT_ATTR = "extension"
const val NAME_POSTFIX_ATTR = "postFixName"
const val NAME_PREFIX_ATTR = "preFixName"
const val CSV_DELIMINATOR_ATTR = "csvDeliminator"
const val FORMAT_CUSTOM_OPEN_ATTR = "open"
const val FORMAT_CUSTOM_CLOSE_ATTR = "close"
const val ZIP_FILES_ATTR = "zipFilesOnly"
const val ENCRYPTION_KEY_ATTR = "encryptionKey"
const val ATTACH_TIME_STAMPS_ATTR = "attachTimeStamp"
const val ATTACH_NO_OF_FILES_ATTR = "attachNoOfFiles"
const val TIME_STAMP_FORMAT_ATTR = "timeStampFormat"
const val ENABLED_ATTR = "enabled"
const val NAME_FOR_EVENT_DIR_ATTR = "nameForEventDirectory"
const val ZIP_FILE_NAME_ATTR = "zipFileName"
const val AUTO_EXPORT_PERIOD_ATTR = "autoExportPeriod"