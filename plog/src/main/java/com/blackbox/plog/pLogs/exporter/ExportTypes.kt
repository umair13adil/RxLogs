package com.blackbox.plog.pLogs.exporter

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.filter.FileFilter
import com.blackbox.plog.pLogs.filter.FileFilter.getPathBasedOnDirectoryStructure
import com.blackbox.plog.pLogs.models.LogRequestType
import com.blackbox.plog.utils.DateTimeUtils
import java.io.File

private val path = PLog.logPath
private var timeStamp = ""
private var noOfFiles = ""

/*
 * Get logs for export type.
 */
internal fun getFilesForRequestedType(type: Int): Triple<String, List<File>, String> {
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

    return Triple("", arrayListOf(), "")
}

/*
 * Get file path of logs for 'Today'
 */
private fun getLogsForToday(): Triple<String, List<File>, String> {

    val path = getPathBasedOnDirectoryStructure()
    val files = FileFilter.getFilesForToday(path)

    if (PLog.getPLogger()?.attachTimeStamp!!)
        timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + LogRequestType.TODAY.type

    if (PLog.getPLogger()?.attachNoOfFiles!!)
        noOfFiles = "_[${files.first.size}]"

    val zipName = "$timeStamp$noOfFiles.zip"

    return Triple(zipName, files.first, files.second)
}

/*
 * Get file path of logs for 'Last Hour'
 */
private fun getLogsForLastHour(): Triple<String, List<File>, String> {

    val path = getPathBasedOnDirectoryStructure()
    val files = FileFilter.getFilesForLastHour(path)

    if (PLog.getPLogger()?.attachTimeStamp!!)
        timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + LogRequestType.LAST_HOUR.type

    if (PLog.getPLogger()?.attachNoOfFiles!!)
        noOfFiles = "_[${files.first.size}]"

    val zipName = "$timeStamp$noOfFiles.zip"

    return Triple(zipName, files.first, files.second)
}

/*
 * Get file path of logs for 'Week'
 */
private fun getLogsForWeek(): Triple<String, List<File>, String> {

    val files = FileFilter.getFilesForLastWeek(path)

    if (PLog.getPLogger()?.attachTimeStamp!!)
        timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + LogRequestType.WEEKS.type

    if (PLog.getPLogger()?.attachNoOfFiles!!)
        noOfFiles = "_[${files.first.size}]"

    val zipName = "$timeStamp$noOfFiles.zip"

    return Triple(zipName, files.first, files.second)
}

/*
 * Get file path of logs for '24 Hours'
 */
private fun getLogsForLast24Hours(): Triple<String, List<File>, String> {

    val files = FileFilter.getFilesForLast24Hours(path)

    if (PLog.getPLogger()?.attachTimeStamp!!)
        timeStamp = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + LogRequestType.LAST_24_HOURS.type

    if (PLog.getPLogger()?.attachNoOfFiles!!)
        noOfFiles = "_[${files.first.size}]"

    val zipName = "$timeStamp$noOfFiles.zip"

    return Triple(zipName, files.first, files.second)
}