package com.blackbox.plog

/**
 * Created by Umair on 12/04/2017.
 */

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import de.mindpipe.android.logging.log4j.LogConfigurator
import org.apache.log4j.Logger
import java.io.File
import java.io.RandomAccessFile

class PLog {

    companion object Factory {
        fun create(): PLog = PLog()
    }

    val dateControl = DateControl.create()
    val dateTime = DateTimeUtils.create()
    val compress = Compress.create()

    private val TAG = PLog::class.java.simpleName
    private val _logConfigurator = LogConfigurator()

    val TYPE_INFO = "Info"
    val TYPE_ERROR = "Error"

    private val LOG_TAG_MAIN = "<LOGS" + " appid=\"" + BuildConfig.APPLICATION_ID + "\" date=\"" + dateControl.today + "\" >"
    private val LOG_TAG_INNER = "<LOG>"
    private val LOG_TAG_SCREEN = "<SCREEN>"
    private val LOG_TAG_FUNCTION = "<FUNCTION>"
    private val LOG_TAG_DATA = "<DATA>"
    private val LOG_TAG_TIME = "<TIME>"
    private val LOG_TAG_TYPE = "<TYPE>"

    private val LOG_TAG_MAIN_C = "</LOGS>"
    private val LOG_TAG_INNER_C = "</LOG>"
    private val LOG_TAG_SCREEN_C = "</SCREEN>"
    private val LOG_TAG_FUNCTION_C = "</FUNCTION>"
    private val LOG_TAG_DATA_C = "</DATA>"
    private val LOG_TAG_TIME_C = "</TIME>"
    private val LOG_TAG_TYPE_C = "</TYPE>"

    val LOG_TODAY = 1
    val LOG_LAST_HOUR = 2
    val LOG_WEEK = 3

    /**
     * Configure Log4j

     * @param fileName      Name of the log file
     * *
     * @param filePattern   Output format of the log line
     * *
     * @param maxBackupSize Maximum number of backed up log files
     * *
     * @param maxFileSize   Maximum size of log file until rolling
     */
    private fun Configure(fileName: String, filePattern: String,
                          maxBackupSize: Int, maxFileSize: Long, logEvent: Boolean) {

        try {
            _logConfigurator.fileName = fileName
            _logConfigurator.filePattern = filePattern
            _logConfigurator.maxBackupSize = maxBackupSize
            _logConfigurator.maxFileSize = maxFileSize
            _logConfigurator.isInternalDebugging = false
            _logConfigurator.isUseLogCatAppender = logEvent
            _logConfigurator.configure()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    public fun logThis(className: String, functionName: String, text: String, type: String) {
        try {

            val folderPath = logPath + dateControl.today

            Utils.instance.createDirIfNotExists(folderPath)
            val fileName = dateControl.today + dateControl.hour
            val path = folderPath + File.separator + fileName
            val exists = Utils.instance.checkFileExists(path)
            if (!exists) {
                val filePattern = "%m%n"
                val maxBackupSize = 250
                val maxFileSize = (2048 * 2048).toLong()
                Configure(path, filePattern, maxBackupSize, maxFileSize, false)
                addHeader(path)
            }

            Logger.getLogger(TAG).info(format(className, functionName, cleanString(text), dateTime.getTimeFormatted(System.currentTimeMillis()), type))


            val fileName_raw = dateControl.today + dateControl.hour + "_raw"
            val path_raw = folderPath + File.separator + fileName_raw
            val existsRaw = Utils.instance.checkFileExists(path_raw)
            if (!existsRaw) {
                val filePattern = "%m%n"
                val maxBackupSize = 250
                val maxFileSize = (2048 * 2048).toLong()
                Configure(path_raw, filePattern, maxBackupSize, maxFileSize, true)
            }

            Logger.getLogger("RAW").info(formatSimple(className, functionName, text, dateTime.getTimeFormatted(System.currentTimeMillis()), type))

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun format(SCREEN: String, FUNCTION: String, DATA: String, TIME: String, TYPE: String): String {
        return LOG_TAG_INNER +
                LOG_TAG_SCREEN + SCREEN + LOG_TAG_SCREEN_C +
                LOG_TAG_FUNCTION + FUNCTION + LOG_TAG_FUNCTION_C +
                LOG_TAG_DATA + DATA + LOG_TAG_DATA_C +
                LOG_TAG_TIME + TIME + LOG_TAG_TIME_C +
                LOG_TAG_TYPE + TYPE + LOG_TAG_TYPE_C +
                LOG_TAG_INNER_C
    }

    private fun isEmpty(`val`: String): Boolean {
        return TextUtils.isEmpty(`val`)
    }

    private fun formatSimple(SCREEN: String, FUNCTION: String, DATA: String, TIME: String, TYPE: String): String {
        return "{$SCREEN}  {$FUNCTION}  {$DATA}  {$TIME}  {$TYPE}"
    }

    private fun addHeader(path: String) {
        var f: RandomAccessFile? = null
        val file = File(path)
        try {
            f = RandomAccessFile(file, "rw")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            f!!.seek(0) // to the beginning
            f.write(LOG_TAG_MAIN.toByteArray())
            f.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private val outputPath: String
        get() = logPath + Constants.FOLDER_OUTGOING + File.separator

    private fun prepareOutputFile() {
        val outputPath = outputPath
        Log.i(TAG, "Output Path: " + outputPath)
        Utils.instance.deleteDir(File(outputPath))
        Utils.instance.createDirIfNotExists(outputPath)
    }

    private fun createZipFile(): String? {
        val zipName = "Logs.zip"

        Log.i(TAG, "Zip Name: " + zipName)

        val outputPath = outputPath
        val outputDirectory = File(outputPath)
        val filesToSend = outputDirectory.listFiles()

        if (filesToSend != null && filesToSend.isNotEmpty()) {
            Log.i(TAG, "Start Zipping Log Files.. " + filesToSend.size)
            compress.zip(filesToSend, outputPath, zipName)
            return outputPath + zipName
        }

        return null
    }

    fun getFilesForToday(folderPath: String) {

        val outputPath = outputPath
        val directory = File(folderPath)
        val files = directory.listFiles()

        Log.i(TAG, "Files: "+files.size)

        if (files.isNotEmpty()) {
            for (file:File in files) {
                Utils.instance.copyFile(folderPath, file.name, outputPath)
            }
        } else {
            Log.i(TAG, "No Log Files")
        }
    }

    private val logPath: String = Environment.getExternalStorageDirectory().path + File.separator + "PLOG" + File.separator + Constants.FOLDER_LOG + File.separator


    private fun cleanString(text: String): String {
        val t1 = text.replace("<--".toRegex(), "")
        val t2 = t1.replace("-->".toRegex(), "")
        val t3 = t2.replace("&".toRegex(), "&amp;")
        return t2
    }

    fun getLogs(type: Int): String {

        prepareOutputFile()

        var path = ""

        when (type) {
            LOG_TODAY -> {
                Log.i(TAG, "Get All Logs for Today's Date")
                path = logPath + dateControl.today
                getFilesForToday(path)
            }

            LOG_LAST_HOUR -> {
                Log.i(TAG, "Get All Logs for Last Hour")
                path = logPath + dateControl.today
                getFilesForLastHour(path)
            }

            LOG_WEEK -> {
                Log.i(TAG, "Get All Logs for Last Week")
                getFilesForLastWeek(logPath)
            }
        }

        val output = createZipFile()
        Log.i(TAG, "Output Zip: " + output!!)
        return output
    }

    private fun getFilesForLastHour(folderPath: String): String {

        val directory = File(folderPath)
        val files = directory.listFiles()

        var lastHour = Integer.parseInt(dateControl.hour) - 1

        if (files.isNotEmpty()) {

            val found = filterFile(folderPath, files, lastHour)

            if (!found) {
                lastHour = Integer.parseInt(dateControl.hour)
                filterFile(folderPath, files, lastHour)
            }

        }
        return ""
    }

    private fun filterFile(folderPath: String, files: Array<File>, lastHour: Int): Boolean {
        var found = false

        for (i in files.indices) {
            val fileHour = extractHour(files[i].name)
            Log.i(TAG, "Last Hour: " + lastHour + " Check File Hour: " + fileHour + " " + files[i].name)
            if (fileHour == lastHour) {
                found = true
                Utils.instance.copyFile(folderPath, files[i].name, outputPath)
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


    private fun getFilesForLastWeek(folderPath: String) {

        val today = Integer.parseInt(dateControl.currentDate)
        val lastWeek = Integer.parseInt(dateControl.lastWeek)

        val directory = File(folderPath)
        val files = directory.listFiles()

        if (files != null) {
            for (file in files) {
                if (file != null) {
                    if (file.isDirectory) {
                        val day = extractDay(file.name)

                        Log.i(TAG, "Today: $today  Last Week: $lastWeek File Date: $day")
                        if (day <= today && day >= lastWeek) {
                            getFilesForToday(file.path)
                        }
                    }
                }
            }
        }
    }
}
