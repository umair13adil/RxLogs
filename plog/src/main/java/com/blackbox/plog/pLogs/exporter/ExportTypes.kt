package com.blackbox.plog.pLogs.exporter

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.filter.FileFilter
import com.blackbox.plog.utils.DateControl
import com.blackbox.plog.utils.DateTimeUtils

/*
 * Get logs for export type.
 */
internal fun checkLogType(type: Int) {
    when (type) {

        PLog.LOG_TODAY -> {
            getLogsForToday()
        }

        PLog.LOG_LAST_HOUR -> {
            getLogsForLastHour()
        }

        PLog.LOG_WEEK -> {
            getLogsForWeek()
        }

        PLog.LOG_LAST_24_HOURS -> {
            getLogsForLast24Hours()
        }
    }
}

/*
 * Get file path of logs for 'Today'
 */
private fun getLogsForToday() {
    LogExporter.path = LogExporter.logPath + DateControl.instance.today
    LogExporter.files = FileFilter.getFilesForToday(LogExporter.path)

    if (LogExporter.attachTimeStamp)
        LogExporter.timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_Today"

    if (LogExporter.attachNoOfFiles)
        LogExporter.noOfFiles = "_[${LogExporter.files}]"

    LogExporter.zipName = "${LogExporter.exportFileName}${LogExporter.timeStamp}${LogExporter.noOfFiles}.zip"
}

/*
 * Get file path of logs for 'Last Hour'
 */
private fun getLogsForLastHour() {
    LogExporter.path = LogExporter.logPath + DateControl.instance.today
    FileFilter.getFilesForLastHour(LogExporter.path)

    if (LogExporter.attachTimeStamp)
        LogExporter.timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_LastHour"

    if (LogExporter.attachNoOfFiles)
        LogExporter.noOfFiles = "_[" + 1 + "]"

    LogExporter.zipName = "${LogExporter.exportFileName}${LogExporter.timeStamp}${LogExporter.noOfFiles}.zip"
}

/*
 * Get file path of logs for 'Week'
 */
private fun getLogsForWeek() {
    FileFilter.getFilesForLastWeek(LogExporter.logPath)

    if (LogExporter.attachTimeStamp)
        LogExporter.timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_Week"

    if (LogExporter.attachNoOfFiles)
        LogExporter.noOfFiles = "_[" + 1 + "]"

    LogExporter.zipName = "${LogExporter.exportFileName}${LogExporter.timeStamp}${LogExporter.noOfFiles}.zip"
}

/*
 * Get file path of logs for '24 Hours'
 */
private fun getLogsForLast24Hours() {
    FileFilter.getFilesForLast24Hours(LogExporter.logPath)

    if (LogExporter.attachTimeStamp)
        LogExporter.timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_Last24Hours"

    if (LogExporter.attachNoOfFiles)
        LogExporter.noOfFiles = "_[" + 1 + "]"

    LogExporter.zipName = "${LogExporter.exportFileName}${LogExporter.timeStamp}${LogExporter.noOfFiles}.zip"
}