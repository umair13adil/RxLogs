package com.blackbox.plog.dataLogs

import android.os.AsyncTask
import android.util.Log
import com.blackbox.plog.elk.ELKLog
import com.blackbox.plog.elk.PLogMetaInfoProvider
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.formatter.LogFormatter
import com.blackbox.plog.pLogs.formatter.TimeStampFormat
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.pLogs.models.LogData
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.utils.DateTimeUtils

/**
 * Created by umair on 2019-02-12 09:10
 * for CubiVue Logs
 */
class SaveDataLogsAsync(var logFileName: String, var dataToWrite: String, var overwrite: Boolean) : AsyncTask<String, String, Boolean>() {

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
    }

    override fun doInBackground(vararg p0: String?): Boolean {

        if (PLogMetaInfoProvider.elkStackSupported) {
            val elkLog = ELKLog(tag = logFileName, subTag = "DATA_LOG", logMessage = dataToWrite, timeStamp = DateTimeUtils.getTimeFormatted(TimeStampFormat.TIME_FORMAT_READABLE), severity = LogLevel.INFO.level)
            val logData = PLogMetaInfoProvider.metaInfo
            logData.elkLog = elkLog
            dataToWrite = PLogImpl.gson.toJson(logData)
        }

        if (PLogImpl.getConfig()?.isDebuggable!!) {

            if (dataToWrite.isNotEmpty())
                Log.i(PLog.TAG, dataToWrite)
        }

        DataLogWriter.writeDataLog(logFileName, dataToWrite, overwrite)

        return true
    }
}