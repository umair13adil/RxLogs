package com.blackbox.plog.pLogs.filter

import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.structure.DirectoryStructure
import com.blackbox.plog.utils.DateControl
import com.blackbox.plog.utils.DateTimeUtils
import java.io.File
import java.util.zip.ZipFile


/**
 * Created by umair on 03/01/2018.
 */
internal object FileFilter {

    internal val TAG = FileFilter::class.java.simpleName
    val outputPath = PLog.getPLogger()?.exportPath!!

    /*
     * Filter files by 'Today'.
     */
    fun getFilesForToday(folderPath: String): Pair<List<File>, String> {

        return if (PLog.getPLogger()?.zipFilesOnly!!) {

            val directory = File(folderPath + DateControl.instance.today)
            val list = directory.listFiles()

            Pair(list.asList(), "")

        } else {
            File(folderPath).copyRecursively(File(outputPath), true)
            Pair(arrayListOf(), outputPath)
        }
    }


    /*
     * Filter files by '24Hours'.
     */
    fun getFilesForLast24Hours(folderPath: String): Pair<List<File>, String> {

        val today = Integer.parseInt(DateControl.instance.currentDate)
        val lastDay = Integer.parseInt(DateControl.instance.lastDay)

        val directory = File(folderPath)
        val files = directory.listFiles()

        val lisOfFiles = arrayListOf<File>()

        if (files != null) {
            for (file in files) {
                if (file != null) {
                    if (file.isDirectory) {
                        val day = FilterUtils.extractDay(file.name)

                        if (PLog.getPLogger()?.isDebuggable!!)
                            Log.i(FileFilter.TAG, "Files between dates: $lastDay & $today,Date File Present: $day")

                        if (lastDay < today) {
                            if (day in lastDay..today) {
                                lisOfFiles.addAll(getFilesForToday(file.path).first)
                            }
                        } else {
                            if (day <= today) {
                                lisOfFiles.addAll(getFilesForToday(file.path).first)
                            }
                        }
                    }
                }
            }
        }

        return Pair(lisOfFiles, outputPath)
    }

    /*
     * Filter files by 'Week'.
     */
    fun getFilesForLastWeek(folderPath: String): Pair<List<File>, String> {

        val lisOfFiles = arrayListOf<File>()
        val listOfDates = DateTimeUtils.getDatesBetween()

        if (PLog.getPLogger()?.isDebuggable!!)
            Log.i(FileFilter.TAG, "Files between dates: ${listOfDates.first()} & ${listOfDates.last()}")

        for (date in listOfDates) {

            val dateDirectory = File(folderPath + File.separator + date)

            if (dateDirectory.isDirectory) {
                val files = dateDirectory.listFiles()

                for (file in files) {
                    lisOfFiles.addAll(getFilesForToday(file.path).first)
                }
            }
        }

        return Pair(lisOfFiles, outputPath)
    }

    /*
     * Filter files by 'Hour'.
     */
    fun getFilesForLastHour(folderPath: String): Pair<List<File>, String> {

        val lisOfFiles = arrayListOf<File>()
        val directory = File(folderPath)
        val files = directory.listFiles()

        val lastHour = Integer.parseInt(DateControl.instance.hour) - 1

        if (files.isNotEmpty()) {

            for (i in files.indices) {
                val fileHour = FilterUtils.extractHour(files[i].name)

                if (PLog.getPLogger()?.isDebuggable!!)
                    Log.i(FileFilter.TAG, "Last Hour: " + lastHour + " Check File Hour: " + fileHour + " " + files[i].name)

                if (fileHour == lastHour) {
                    lisOfFiles.add(files[i])
                }
            }

        }

        return Pair(lisOfFiles, outputPath)
    }

    /*
     * Returns path of Logs based on selected Directory Structure.
     */
    fun getPathBasedOnDirectoryStructure(): String {
        when (PLog.getPLogger()?.directoryStructure) {

            DirectoryStructure.FOR_DATE -> {
                return PLog.logPath + DateControl.instance.today
            }

            DirectoryStructure.FOR_EVENT -> {
                return PLog.logPath + PLog.getPLogger()?.nameForEventDirectory!!
            }

            DirectoryStructure.SINGLE_FILE_FOR_DAY -> {
                return PLog.logPath + DateControl.instance.today
            }
        }

        return ""
    }

    fun readZip(path: String) {
        val zipFile = ZipFile(path)

        val entries = zipFile.entries()

        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            val stream = zipFile.getInputStream(entry)
            stream.bufferedReader().use {
                PLog.logThis(TAG, "readZip", it.readText(), LogLevel.INFO)
            }
        }
    }
}
