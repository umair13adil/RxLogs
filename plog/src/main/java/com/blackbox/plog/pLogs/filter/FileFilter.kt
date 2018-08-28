package com.blackbox.plog.pLogs.filter

import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.utils.DateControl
import com.blackbox.plog.utils.DateTimeUtils
import com.blackbox.plog.utils.Utils
import java.io.File

/**
 * Created by umair on 03/01/2018.
 */
internal object FileFilter {

    internal val TAG = FileFilter::class.java.simpleName

    /*
     * Filter files by 'Today'.
     */
    fun getFilesForToday(folderPath: String): Int {

        var size = 0

        val outputPath = PLog.outputPath
        val directory = File(folderPath)
        val files = directory.listFiles()

        if (files != null && files.isNotEmpty()) {

            size = files.size

            if (files.isNotEmpty()) {

                for (i in files.indices) {
                    Utils.instance.copyFile(folderPath, files[i].name, outputPath)
                }
            }
        }

        return size
    }

    /*
     * Filter files by '24Hours'.
     */
    fun getFilesForLast24Hours(folderPath: String) {

        val today = Integer.parseInt(DateControl.instance.currentDate)
        val lastDay = Integer.parseInt(DateControl.instance.lastDay)

        val directory = File(folderPath)
        val files = directory.listFiles()

        if (files != null) {
            for (file in files) {
                if (file != null) {
                    if (file.isDirectory) {
                        val day = FilterUtils.extractDay(file.name)

                        if (PLog.pLogger.isDebuggable)
                            Log.i(FileFilter.TAG, "Files between dates: $lastDay & $today,Date File Present: $day")

                        if (lastDay < today) {
                            if (day <= today && day >= lastDay) {
                                getFilesForToday(file.path)
                            }
                        } else {
                            if (day <= today) {
                                getFilesForToday(file.path)
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * Filter files by 'Week'.
     */
    fun getFilesForLastWeek(folderPath: String) {

        val listOfDates = DateTimeUtils.getDatesBetween()

        if (PLog.pLogger.isDebuggable)
            Log.i(FileFilter.TAG, "Files between dates: ${listOfDates.first()} & ${listOfDates.last()}")

        for (date in listOfDates) {

            val dateDirectory = File(folderPath + File.separator + date)

            if (dateDirectory.isDirectory) {
                val files = dateDirectory.listFiles()

                for (file in files) {
                    getFilesForToday(file.path)
                }
            }
        }
    }

    /*
     * Filter files by 'Hour'.
     */
    fun getFilesForLastHour(folderPath: String) {

        val directory = File(folderPath)
        val files = directory.listFiles()

        var lastHour = Integer.parseInt(DateControl.instance.hour) - 1

        if (files.isNotEmpty()) {

            val found = FilterUtils.filterFile(folderPath, files, lastHour)

            if (!found) {
                lastHour = Integer.parseInt(DateControl.instance.hour)
                FilterUtils.filterFile(folderPath, files, lastHour)
            }

        }
    }

}
