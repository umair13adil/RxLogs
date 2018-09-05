package com.blackbox.plog.pLogs.exporter

import com.blackbox.plog.pLogs.filter.FileFilter
import com.blackbox.plog.pLogs.models.LogRequestType
import com.blackbox.plog.utils.DateControl
import com.blackbox.plog.utils.DateTimeUtils
import java.io.File

/*
 * Get logs for export type.
 */
internal fun getFilesForRequestedType(type: Int): Pair<String, List<File>> {
    when (type) {

        LogRequestType.TODAY.type -> {
            return getLogsForToday()
        }

        LogRequestType.LAST_HOUR.type -> {
            return getLogsForLastHour()
        }

        LogRequestType.WEEKS.type -> {
            return getLogsForWeek()
        }

        LogRequestType.LAST_24_HOURS.type -> {
            return getLogsForLast24Hours()
        }
    }

    return Pair("", arrayListOf())
}

/*
 * Get file path of logs for 'Today'
 */
private fun getLogsForToday(): Pair<String, List<File>> {
    LogExporter.path = LogExporter.logPath + DateControl.instance.today
    val files = FileFilter.getFilesForToday(LogExporter.path)

    if (LogExporter.attachTimeStamp)
        LogExporter.timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + LogRequestType.TODAY.type

    if (LogExporter.attachNoOfFiles)
        LogExporter.noOfFiles = "_[${LogExporter.files.second.size}]"

    val zipName = "${LogExporter.timeStamp}${LogExporter.noOfFiles}.zip"

    return Pair(zipName, files)
}

/*
 * Get file path of logs for 'Last Hour'
 */
private fun getLogsForLastHour(): Pair<String, List<File>> {
    LogExporter.path = LogExporter.logPath + DateControl.instance.today
    val files = FileFilter.getFilesForLastHour(LogExporter.path)

    if (LogExporter.attachTimeStamp)
        LogExporter.timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + LogRequestType.LAST_HOUR.type

    if (LogExporter.attachNoOfFiles)
        LogExporter.noOfFiles = "_[${LogExporter.files.second.size}]"

    val zipName = "${LogExporter.timeStamp}${LogExporter.noOfFiles}.zip"

    return Pair(zipName, files)
}

/*
 * Get file path of logs for 'Week'
 */
private fun getLogsForWeek(): Pair<String, List<File>> {
    val files = FileFilter.getFilesForLastWeek(LogExporter.logPath)

    if (LogExporter.attachTimeStamp)
        LogExporter.timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + LogRequestType.WEEKS.type

    if (LogExporter.attachNoOfFiles)
        LogExporter.noOfFiles = "_[${LogExporter.files.second.size}]"

    val zipName = "${LogExporter.timeStamp}${LogExporter.noOfFiles}.zip"

    return Pair(zipName, files)
}

/*
 * Get file path of logs for '24 Hours'
 */
private fun getLogsForLast24Hours(): Pair<String, List<File>> {
    val files = FileFilter.getFilesForLast24Hours(LogExporter.logPath)

    if (LogExporter.attachTimeStamp)
        LogExporter.timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + LogRequestType.LAST_24_HOURS.type

    if (LogExporter.attachNoOfFiles)
        LogExporter.noOfFiles = "_[${LogExporter.files.second.size}]"

    val zipName = "${LogExporter.timeStamp}${LogExporter.noOfFiles}.zip"

    return Pair(zipName, files)
}