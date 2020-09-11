package com.blackbox.plog.pLogs.impl

import android.util.Log
import androidx.annotation.Keep
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.utils.*
import com.blackbox.plog.utils.appendToFile
import com.blackbox.plog.utils.checkFileExists
import com.blackbox.plog.utils.setupFilePaths
import java.io.File
import javax.crypto.SecretKey

@Keep
object LogWriter {

    private val TAG = "LogWriter"

    var secretKey: SecretKey? = null


    init {
        if (secretKey == null)
            secretKey = PLogImpl.getConfig()?.secretKey
    }

    /*
     * Write AES encrypted String logs.
     */
    fun writeEncryptedLogs(logFormatted: String) {

        if (secretKey == null) {
            Log.e("writeEncryptedLogs", "No Key provided! Logs will not be written to a file.")
            return
        }

        val shouldLog: Pair<Boolean, String>

        val path = setupFilePaths()
        val f = checkFileExists(path)

        shouldLog = if (!PART_FILE_CREATED_PLOG) {
            shouldWriteLog(f)
        } else {
            shouldWriteLog(File(CURRENT_PART_FILE_PATH_PLOG))
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

        shouldLog = if (!PART_FILE_CREATED_PLOG) {
            shouldWriteLog(f)
        } else {
            shouldWriteLog(File(CURRENT_PART_FILE_PATH_PLOG))
        }

        if (shouldLog.first) {
            appendToFile(shouldLog.second, logFormatted)
        } else {
            if (PLogImpl.getConfig()?.debugFileOperations!!)
                Log.i(PLog.DEBUG_TAG, "writeSimpleLogs: Unable to write log file.")
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
                        Log.i(PLog.DEBUG_TAG, "File size exceeded!")

                    return Pair(false, path)
                } else {
                    //Create part file
                    createPartFile(file, isPLog, logFileName)
                }
            }
        }

        return Pair(true, path)
    }

    /*
     * This will create a new part file for existing parent file.
     */
    private fun createPartFile(file: File, isPLog: Boolean = true, logFileName: String = ""): String {

        if (PLogImpl.getConfig()?.debugFileOperations!!)
            Log.i(PLog.DEBUG_TAG, "createPartFile: Creating part file..")

        var path = ""
        val name = file.name.substringBeforeLast(".")

        if (name.contains(PART_FILE_PREFIX)) {
            val value = name.substringAfterLast(PART_FILE_PREFIX)
            val valueWithoutExt = value.substringBeforeLast(".")
            if (valueWithoutExt.isNotEmpty()) {
                val newValue = valueWithoutExt.toInt().plus(1)

                var newFileName = ""

                newFileName = if (isPLog) {
                    "$PART_FILE_PREFIX$newValue"
                } else {
                    "$logFileName$PART_FILE_PREFIX$newValue"
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

            newFileName = if (isPLog) {
                "${PART_FILE_PREFIX}2"
            } else {
                "$logFileName${PART_FILE_PREFIX}2"
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