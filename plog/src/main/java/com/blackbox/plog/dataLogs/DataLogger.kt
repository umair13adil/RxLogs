package com.blackbox.plog.dataLogs

import android.util.Log
import com.blackbox.plog.pLogs.PLog

/**
 * Created by umair on 04/01/2018.
 */
class DataLogger(var logFileName: String = "log") {

    val TAG = "DataLogger"

    /**
     * Overwrite to file.
     *
     * This function will overwrite a 'String' data to a file.
     * File will be created if it doesn't exists in path provided.
     * Filename can contain extension as well e.g 'error_log.txt'.
     * If 'attachTimeStamp' is true filename will contain date & hour in it like: '0105201812_error_log.txt'.
     * Hours are in 24h format, so each file will be unique after an hour.
     *
     *
     * @param dataToWrite the data to write can be any string data formatted or unformatted
     */
    fun overwriteToFile(dataToWrite: String) {

        if (PLog.isLogsConfigSet()) {
            val runnable = Runnable {
                val save = SaveDataLogsAsync(logFileName, dataToWrite, true)
                save.execute()
            }
            PLog.handler.post(runnable)
        } else {
            Log.i(PLog.TAG, dataToWrite)
        }
    }

    /**
     * Append to file.
     *
     * This function will append a 'String' data to a file with new line inserted.
     * File will be created if it doesn't exists in path provided.
     * Filename can contain extension as well e.g 'error_log.txt'.
     * If 'attachTimeStamp' is true filename will contain date & hour in it like: '0105201812_error_log.txt'.
     * Hours are in 24h format, so each file will be unique after an hour.
     *
     *
     * @param dataToWrite the data to write can be any string data formatted or unformatted
     */
    fun appendToFile(dataToWrite: String) {

        if (PLog.isLogsConfigSet()) {
            val runnable = Runnable {
                val save = SaveDataLogsAsync(logFileName, dataToWrite, false)
                save.execute()
            }
            PLog.handler.post(runnable)
        } else {
            Log.i(PLog.TAG, dataToWrite)
        }
    }
}