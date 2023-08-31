package com.blackbox.plog.pLogs.filter

import android.util.Log
import androidx.annotation.Keep
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.utils.DateControl
import com.blackbox.plog.utils.DateTimeUtils
import java.io.File


/**
 * Created by umair on 03/01/2018.
 */
@Keep
internal object FileFilter {

    internal val TAG = FileFilter::class.java.simpleName
    val tempOutputPath = PLog.exportTempPath

    /*
     * Filter files by 'Today'.
     */
    fun getFilesForToday(folderPath: String): Pair<List<File>, String> {

        val path = folderPath
        val lisOfFiles = FilterUtils.listFiles(path, arrayListOf())

        if (lisOfFiles.isNotEmpty()) {
            try {
                File(folderPath).copyRecursively(File(tempOutputPath), true)

                //If folder is not created in targeted directory, then create specific folder and copy files into that folder directly.
                val tempDirPath = tempOutputPath +  File(folderPath).name + File.separator
                val tempFolder = File(tempDirPath)
                if(!tempFolder.exists()) {
                    tempFolder.mkdirs()
                    if(tempFolder.exists()) {
                        File(folderPath).copyRecursively(File(tempDirPath), true)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "getFilesForToday: Unable to get files for today!")
            }
        }

        return Pair(lisOfFiles, tempOutputPath)
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

                        if (PLogImpl.getConfig()?.debugFileOperations!!)
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

        return Pair(lisOfFiles, tempOutputPath)
    }

    /*
     * Filter files by 'Week'.
     */
    fun getFilesForLastWeek(folderPath: String): Pair<List<File>, String> {

        val lisOfFiles = arrayListOf<File>()
        val listOfDates = DateTimeUtils.getDatesBetween()

        if (PLogImpl.getConfig()?.debugFileOperations!!)
            Log.i(FileFilter.TAG, "Files between dates: ${listOfDates.first()} & ${listOfDates.last()}")

        for (date in listOfDates) {

            val dateDirectory = File(folderPath + File.separator + date)

            if (dateDirectory.isDirectory) {
                lisOfFiles.addAll(getFilesForToday(dateDirectory.path).first)
            }
        }

        return Pair(lisOfFiles, tempOutputPath)
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
                try {
                    val fileHour = FilterUtils.extractHour(files[i].name)

                    if (PLogImpl.getConfig()?.debugFileOperations!!)
                        Log.i(FileFilter.TAG, "Last Hour: " + lastHour + " Check File Hour: " + fileHour + " " + files[i].name)

                    if (fileHour == lastHour) {
                        lisOfFiles.add(files[i])
                    }
                } catch (e: NumberFormatException) {

                }
            }

        }

        return Pair(lisOfFiles, tempOutputPath)
    }

    /*
     * Filter files by 'All'.
     */
    fun getFilesForAll(folderPath: String): Pair<List<File>, String> {

        val lisOfFiles = FilterUtils.listFiles(folderPath, arrayListOf())

        if (lisOfFiles.isNotEmpty()) {
            //Copy Files to temp folder
            File(folderPath).copyRecursively(File(tempOutputPath), true)
        }

        return Pair(lisOfFiles, tempOutputPath)
    }



    /*
     * Filter files by 'Date'.
     */
    fun getFilesForDate(folderPath: String, fileNames: List<String>): Triple<List<File>, String, String> {

        val path = folderPath
        val lisOfFiles = FilterUtils.listFiles(path, arrayListOf())

        val finalFiles = if(fileNames.isEmpty()) lisOfFiles else lisOfFiles.filter { f -> fileNames.contains(f.nameWithoutExtension) ||
                fileNames.contains(f.name.postfixRemoved())
        }

        val tempDirPath = tempOutputPath +  File(folderPath).name + File.separator

        if (finalFiles.isNotEmpty()) {
            try {
                //If folder is not created in targeted directory, then create specific folder and copy files into that folder directly.
                val tempFolder = File(tempDirPath)
                if(!tempFolder.exists()) {
                    tempFolder.mkdirs()
                }

                if(tempFolder.exists()) {
                    finalFiles.forEach { f ->
                        f.copyRecursively(File(tempDirPath + f.name), true)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "getFilesForDate: Unable to get files for today!")
            }
        }

        return Triple(finalFiles, tempDirPath, tempOutputPath)
    }


}


private fun  String.postfixRemoved(): String {
    return this.split("_").first()
}