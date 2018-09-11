package com.blackbox.plog.pLogs.exporter

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.filter.FileFilter
import com.blackbox.plog.pLogs.filter.FilterUtils.getPathForType
import com.blackbox.plog.utils.DateTimeUtils
import java.io.File

private val path = PLog.logPath
private var timeStamp = ""
private var noOfFiles = ""

/*
 * Get logs for export type.
 */
internal fun getFilesForRequestedType(type: String): Triple<String, List<File>, String> {
    when (type) {

        ExportType.TODAY.type -> {
            return getLogsForToday()
        }

        ExportType.LAST_HOUR.type -> {
            return getLogsForLastHour()
        }

        ExportType.WEEKS.type -> {
            return getLogsForWeek()
        }

        ExportType.LAST_24_HOURS.type -> {
            return getLogsForLast24Hours()
        }

        ExportType.ALL.type -> {
            return getLogsForAllInRoot()
        }
    }

    return Triple("", arrayListOf(), "")
}

/*
 * Get file path of logs for 'Today'
 */
private fun getLogsForToday(): Triple<String, List<File>, String> {

    val path = getPathForType(ExportType.TODAY)
    val files = FileFilter.getFilesForToday(path)

    if (PLog.getLogsConfig()?.attachTimeStamp!!)
        timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_" + ExportType.TODAY.type

    if (PLog.getLogsConfig()?.attachNoOfFiles!!)
        noOfFiles = "_[${files.first.size}]"

    val zipName = "$timeStamp$noOfFiles.zip"

    return Triple(zipName, files.first, files.second)
}

/*
 * Get file path of logs for 'Last Hour'
 */
private fun getLogsForLastHour(): Triple<String, List<File>, String> {

    val path = getPathForType(ExportType.LAST_HOUR)
    val files = FileFilter.getFilesForLastHour(path)

    if (PLog.getLogsConfig()?.attachTimeStamp!!)
        timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_" + ExportType.LAST_HOUR.type

    if (PLog.getLogsConfig()?.attachNoOfFiles!!)
        noOfFiles = "_[${files.first.size}]"

    val zipName = "$timeStamp$noOfFiles.zip"

    return Triple(zipName, files.first, files.second)
}

/*
 * Get file path of logs for 'Week'
 */
private fun getLogsForWeek(): Triple<String, List<File>, String> {

    val path = getPathForType(ExportType.WEEKS)
    val files = FileFilter.getFilesForLastWeek(path)

    if (PLog.getLogsConfig()?.attachTimeStamp!!)
        timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_" + ExportType.WEEKS.type

    if (PLog.getLogsConfig()?.attachNoOfFiles!!)
        noOfFiles = "_[${files.first.size}]"

    val zipName = "$timeStamp$noOfFiles.zip"

    return Triple(zipName, files.first, files.second)
}

/*
 * Get file path of logs for '24 Hours'
 */
private fun getLogsForLast24Hours(): Triple<String, List<File>, String> {

    val path = getPathForType(ExportType.LAST_24_HOURS)
    val files = FileFilter.getFilesForLast24Hours(path)

    if (PLog.getLogsConfig()?.attachTimeStamp!!)
        timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_" + ExportType.LAST_24_HOURS.type

    if (PLog.getLogsConfig()?.attachNoOfFiles!!)
        noOfFiles = "_[${files.first.size}]"

    val zipName = "$timeStamp$noOfFiles.zip"

    return Triple(zipName, files.first, files.second)
}

/*
 * Get file path of logs for All logs in root directory.
 */
private fun getLogsForAllInRoot(): Triple<String, List<File>, String> {

    val path = getPathForType(ExportType.ALL)
    val files = FileFilter.getFilesForAll(path)

    if (PLog.getLogsConfig()?.attachTimeStamp!!)
        timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_" + ExportType.ALL.type

    if (PLog.getLogsConfig()?.attachNoOfFiles!!)
        noOfFiles = "_[${files.first.size}]"

    val zipName = "$timeStamp$noOfFiles.zip"

    return Triple(zipName, files.first, files.second)
}