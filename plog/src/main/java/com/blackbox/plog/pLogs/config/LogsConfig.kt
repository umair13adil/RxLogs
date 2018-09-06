package com.blackbox.plog.pLogs.config

import com.blackbox.plog.pLogs.formatter.FormatType
import com.blackbox.plog.pLogs.formatter.TimeStampFormat
import com.blackbox.plog.pLogs.structure.DirectoryStructure

class LogsConfig {

    internal lateinit var logTypesEnabled: ArrayList<String>
    internal lateinit var formatType: FormatType
    internal var logsRetentionPeriodInDays: Int = 0
    internal var zipsRetentionPeriodInDays: Int = 0
    internal var autoClearLogsOnExport: Boolean = false
    internal var enabled: Boolean = true
    internal var exportFileNamePostFix: String = ""
    internal var exportFileNamePreFix: String = ""
    internal var autoExportErrors: Boolean = true
    internal var encryptionKey: String = ""
    internal var encryptionEnabled: Boolean = false
    internal var singleLogFileLimit: Int = 2048 * 2 //Size in MBs (4Mb)
    internal var logFilesLimit: Int = 100 //Max number of log files
    internal var directoryStructure: DirectoryStructure = DirectoryStructure.FOR_DATE
    internal var timeStampFormat: TimeStampFormat = TimeStampFormat.DATE_FORMAT_1
    internal var logSystemCrashes: Boolean = false
    internal lateinit var autoExportLogTypes: ArrayList<String>
    internal var autoExportLogTypesPeriod: Int = 0

}