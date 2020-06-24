package com.blackbox.plog.pLogs.impl

import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.utils.*
import com.blackbox.plog.utils.Utils
import com.blackbox.plog.utils.appendToFile
import com.blackbox.plog.utils.checkFileExists
import com.blackbox.plog.utils.setupFilePaths
import java.io.File
import javax.crypto.SecretKey

object LogWriter {

    private val TAG = "LogWriter"

    var secretKey: SecretKey? = null


    init {
        secretKey = PLogImpl.getConfig()?.secretKey
    }

    /*
     * Write AES encrypted String logs.
     */
    fun writeEncryptedLogs(logFormatted: String) {

        if (secretKey == null){
            Log.e("writeEncryptedLogs","No Key provided!")
            return
        }

        val shouldLog: Pair<Boolean, String>

        val path = setupFilePaths()
        val f = checkFileExists(path)

        if (!PART_FILE_CREATED_PLOG) {
            shouldLog = shouldWriteLog(f)
        } else {
            shouldLog = shouldWriteLog(File(CURRENT_PART_FILE_PATH_PLOG))
        }

        if (shouldLog.first) {
            secretKey?.let {
                PLogImpl.encrypter.appendToFileEncrypted(logFormatted, it, shouldLog.second)
            }
        }
    }

    /*
     * Write plain String logs.
     */
    fun writeSimpleLogs(logFormatted: String) {

        val shouldLog: Pair<Boolean, String>

        val path = setupFilePaths()
        val f = checkFileExists(path)

        if (!PART_FILE_CREATED_PLOG) {
            shouldLog = shouldWriteLog(f)
        } else {
            shouldLog = shouldWriteLog(File(CURRENT_PART_FILE_PATH_PLOG))
        }

        if (shouldLog.first) {

            appendToFile(shouldLog.second, logFormatted)
        }
    }

    /*
     * Verify if logs can be written.
     */
    fun shouldWriteLog(file: File, isPLog: Boolean = true, logFileName: String = ""): Pair<Boolean, String> {
        val path = file.path

        if (file.length() > 0) {
            val length = file.length()
            val maxLength = PLogImpl.getConfig()?.singleLogFileSize!! * (1024 * 1024)

            if (length > maxLength) {

                if (isPLog)
                    PART_FILE_CREATED_PLOG = true
                else
                    PART_FILE_CREATED_DATALOG = true

                if (!PLogImpl.getConfig()?.forceWriteLogs!!) {

                    if (PLogImpl.getConfig()?.debugFileOperations!!)
                        Log.i(PLog.TAG, "File size exceeded!")

                    return Pair(false, path)
                } else {
                    //Create part file
                    createPartFile(file, isPLog, logFileName)
                }
            } else {

                if (PLogImpl.getConfig()?.debugFileOperations!!)
                    Log.i(PLog.TAG, "File Length: ${Utils.bytesToReadable(length.toInt())} < ${Utils.bytesToReadable(maxLength)}")
            }
        }

        //TODO update no of files created in config XML for easy access
        /*val totalFiles = FilterUtils.listFiles(logPath, arrayListOf())
        if (totalFiles.isNotEmpty()) {
            if (totalFiles.size > PLog.getLogsConfig()?.logFilesLimit!!)

                if (PLog.getLogsConfig()?.isDebuggable!!)
                    Log.i(PLog.TAG, "No of log files exceeded!")

            return false
        }*/

        return Pair(true, path)
    }

    /*
     * This will create a new part file for existing parent file.
     */
    private fun createPartFile(file: File, isPLog: Boolean = true, logFileName: String = ""): String {
        var path = ""
        val name = file.name.substringBeforeLast(".")

        if (name.contains(PART_FILE_PREFIX)) {
            val value = name.substringAfterLast(PART_FILE_PREFIX)
            val valueWithoutExt = value.substringBeforeLast(".")
            if (valueWithoutExt.isNotEmpty()) {
                val newValue = valueWithoutExt.toInt().plus(1)

                var newFileName = ""

                if (isPLog) {
                    newFileName = "$PART_FILE_PREFIX$newValue"
                } else {
                    newFileName = "$logFileName$PART_FILE_PREFIX$newValue"
                }

                path = setupFilePaths(fileName = newFileName, isPLog = isPLog)

                if (isPLog)
                    CURRENT_PART_FILE_PATH_PLOG = path
                else
                    CURRENT_PART_FILE_PATH_DATALOG = path

                checkFileExists(path, isPLog = isPLog)
            }
        } else {
            var newFileName = ""

            if (isPLog) {
                newFileName = "${PART_FILE_PREFIX}2"
            } else {
                newFileName = "$logFileName${PART_FILE_PREFIX}2"
            }

            path = setupFilePaths(fileName = newFileName, isPLog = isPLog)

            if (isPLog)
                CURRENT_PART_FILE_PATH_PLOG = path
            else
                CURRENT_PART_FILE_PATH_DATALOG = path

            checkFileExists(path, isPLog = isPLog)
        }
        return path
    }
}