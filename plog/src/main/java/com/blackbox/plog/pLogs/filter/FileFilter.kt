package com.blackbox.plog.pLogs.filter

import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.utils.DateControl
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

        if (files != null && files.size > 0) {

            size = files.size

            if (PLog.pLogger.isDebuggable!!)
                Log.i(FileFilter.TAG, "Total Files: $size")

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

                        if (PLog.pLogger.isDebuggable!!)
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

        val today = Integer.parseInt(DateControl.instance.currentDate)
        val lastWeek = Integer.parseInt(DateControl.instance.lastWeek)

        val directory = File(folderPath)
        val files = directory.listFiles()

        if (files != null) {
            for (file in files) {
                if (file != null) {
                    if (file.isDirectory) {
                        val day = FilterUtils.extractDay(file.name)

                        if (PLog.pLogger.isDebuggable!!)
                            Log.i(FileFilter.TAG, "Files between dates: $lastWeek & $today,Date File Present: $day")

                        if (lastWeek < today) {
                            if (day <= today && day >= lastWeek) {
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
