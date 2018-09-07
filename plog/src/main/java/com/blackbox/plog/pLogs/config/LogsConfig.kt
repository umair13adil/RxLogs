package com.blackbox.plog.pLogs.config

import com.blackbox.plog.pLogs.formatter.FormatType
import com.blackbox.plog.pLogs.structure.DirectoryStructure

class LogsConfig(
        var logTypesEnabled: ArrayList<String> = arrayListOf<String>(),
        var formatType: FormatType = FormatType.FORMAT_CURLY,
        var logsRetentionPeriodInDays: Int = 0,
        var zipsRetentionPeriodInDays: Int = 0,
        var autoClearLogsOnExport: Boolean = false,
        var enabled: Boolean = true,
        var exportFileNamePostFix: String = "",
        var exportFileNamePreFix: String = "",
        var autoExportErrors: Boolean = true,
        var encryptionEnabled: Boolean = false,
        var encryptionKey: String = "",
        var singleLogFileSize: Int = 2048 * 2, //Size in MBs (4Mb)
        var logFilesLimit: Int = 100, //Max number of log files
        var directoryStructure: DirectoryStructure = DirectoryStructure.FOR_DATE,
        var logSystemCrashes: Boolean = false,
        var autoExportLogTypes: ArrayList<String> = arrayListOf(),
        var autoExportLogTypesPeriod: Int = 0,
        var logsDeleteDate: String = "", //Last Time logs were cleared
        var zipDeleteDate: String = "" //Last Time exported zip files were cleared
) {

    fun updateLogsDeleteDate(date: String) {
        updateValue(date, LOGS_DELETE_DATE_TAG)
    }

    fun updateZipDeleteDate(date: String) {
        updateValue(date, ZIP_DELETE_DATE_TAG)
    }
}
