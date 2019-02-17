package com.blackbox.plog.dataLogs

import android.os.AsyncTask
import android.util.Log
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.impl.PLogImpl

/**
 * Created by umair on 2019-02-12 09:10
 * for CubiVue Logs
 */
class SaveDataLogsAsync(var logFileName: String, var dataToWrite: String, var overwrite: Boolean) : AsyncTask<String, String, Boolean>() {

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
    }

    override fun doInBackground(vararg p0: String?): Boolean {

        if (PLogImpl.getConfig()?.isDebuggable!!) {

            if (dataToWrite.isNotEmpty())
                Log.i(PLog.TAG, dataToWrite)
        }

        DataLogWriter.writeDataLog(logFileName,dataToWrite, overwrite)

        return true
    }
}