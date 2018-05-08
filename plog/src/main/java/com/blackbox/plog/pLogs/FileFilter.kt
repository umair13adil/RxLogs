package com.blackbox.plog.pLogs

import android.util.Log

import com.blackbox.plog.utils.DateControl
import com.blackbox.plog.utils.Utils

import java.io.File

/**
 * Created by umair on 03/01/2018.
 */
internal object FileFilter {

    private val TAG = FileFilter::class.java.simpleName

    fun getFilesForLastHour(folderPath: String) {

        val directory = File(folderPath)
        val files = directory.listFiles()

        var lastHour = Integer.parseInt(DateControl.getInstance().hour) - 1

        if (files.isNotEmpty()) {

            val found = filterFile(folderPath, files, lastHour)

            if (!found) {
                lastHour = Integer.parseInt(DateControl.getInstance().hour)
                filterFile(folderPath, files, lastHour)
            }

        }
    }

    private fun filterFile(folderPath: String, files: Array<File>, lastHour: Int): Boolean {
        var found = false

        for (i in files.indices) {
            val fileHour = extractHour(files[i].name)

            if (PLog.pLogger.isDebuggable!!)
                Log.i(TAG, "Last Hour: " + lastHour + " Check File Hour: " + fileHour + " " + files[i].name)

            if (fileHour == lastHour) {
                found = true
                Utils.getInstance().copyFile(folderPath, files[i].name, PLog.outputPath)
            }
        }

        return found
    }

    private fun extractDay(name: String): Int {
        return Integer.parseInt(name.substring(0, 2))
    }

    private fun extractHour(name: String): Int {
        return Integer.parseInt(name.substring(8, 10))
    }

    fun getFilesForLastWeek(folderPath: String) {

        val today = Integer.parseInt(DateControl.getInstance().currentDate)
        val lastWeek = Integer.parseInt(DateControl.getInstance().lastWeek)

        val directory = File(folderPath)
        val files = directory.listFiles()

        if (files != null) {
            for (file in files) {
                if (file != null) {
                    if (file.isDirectory) {
                        val day = extractDay(file.name)

                        if (PLog.pLogger.isDebuggable!!)
                            Log.i(TAG, "Files between dates: $lastWeek & $today,Date File Present: $day")

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

    fun getFilesForLastTwoDays(folderPath: String) {

        val today = Integer.parseInt(DateControl.getInstance().currentDate)
        val lastDay = Integer.parseInt(DateControl.getInstance().lastDay)

        val directory = File(folderPath)
        val files = directory.listFiles()

        if (files != null) {
            for (file in files) {
                if (file != null) {
                    if (file.isDirectory) {
                        val day = extractDay(file.name)

                        if (PLog.pLogger.isDebuggable!!)
                            Log.i(TAG, "Files between dates: $lastDay & $today,Date File Present: $day")

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

    fun prepareOutputFile(outputPath: String) {
        Utils.getInstance().deleteDir(File(outputPath))
        Utils.getInstance().createDirIfNotExists(outputPath)
    }


    fun getFilesForToday(folderPath: String): Int {

        var size = 0

        val outputPath = PLog.outputPath
        val directory = File(folderPath)
        val files = directory.listFiles()

        if (files != null && files.size > 0) {

            size = files.size

            if (PLog.pLogger.isDebuggable!!)
                Log.i(TAG, "Total Files: $size")

            if (files.isNotEmpty()) {

                for (i in files.indices) {
                    Utils.getInstance().copyFile(folderPath, files[i].name, outputPath)
                }
            }
        }

        return size
    }

    fun getFilesForLogName(logsPath: String, outputPath: String, logFileName: String, debug: Boolean): Int {

        var size = 0

        val directory = File(logsPath)
        val files = directory.listFiles()

        if (files != null && files.size > 0) {

            size = files.size

            if (debug)
                Log.i(TAG, "Total Files: $size")

            if (files.isNotEmpty()) {

                for (i in files.indices) {
                    if (files[i].name.contains(logFileName))
                        Utils.getInstance().copyFile(logsPath, files[i].name, outputPath)
                }
            }
        }

        return size
    }

}
