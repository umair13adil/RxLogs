package com.blackbox.plog.pLogs.config

import com.blackbox.plog.pLogs.PLog
import java.io.File


val XML_PATH = PLog.logPath + File.separator + "config.xml"

//Parent TAGs
const val ROOT_TAG = "Config"
const val LOG_TYPES_ENABLED_TAG = "LogTypesEnabled"
const val FORMAT_TYPE_TAG = "FormatType"
const val LOGS_RETENTION_TAG = "LogsRetention"
const val ZIP_RETENTION_TAG = "ZipRetention"
const val AUTO_CLEAR_TAG = "AutoClear"
const val EXPORT_NAME_TAG = "ExportName"
const val NAME_POSTFIX_TAG = "PostFixName"
const val NAME_PREFIX_TAG = "PreFixName"
const val AUTO_EXPORT_ERRORS_TAG = "ExportErrors"
const val ENCRYPTION_ENABLED_TAG = "Encryption"
const val ENCRYPTION_KEY_TAG = "EncryptionKey"
const val LOG_FILE_SIZE_TAG = "FileSizeMax"
const val LOG_FILES_MAX_TAG = "LogFilesMax"
const val DIRECTORY_TAG = "Directory"
const val LOG_SYSTEM_CRASHES_TAG = "SystemCrashes"
const val AUTO_EXPORT_TYPES_TAG = "AutoExportTypes"
const val AUTO_EXPORT_PERIOD_TAG = "AutoExportPeriod"
const val LOGS_DELETE_DATE_TAG = "LogsDeleteDate"
const val ZIP_DELETE_DATE_TAG = "ZipDeleteDate"


//Commons
const val TYPE_TAG = "Type"
const val ENABLED_TAG = "Enabled"
const val DAYS_TAG = "Days"
const val VALUE_TAG = "Value"