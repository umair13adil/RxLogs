package com.blackbox.plog.dataLogs

import android.os.AsyncTask
import android.util.Log
import com.blackbox.plog.elk.ECSMapper
import com.blackbox.plog.elk.PLogMetaInfoProvider
import com.blackbox.plog.mqtt.MQTTSender
import com.blackbox.plog.mqtt.PLogMQTTProvider
import com.blackbox.plog.pLogs.PLog
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
            val logData = LogData(className = logFileName, functionName = "DATA_LOG", logText = dataToWrite, logTime = DateTimeUtils.getTimeFormatted(TimeStampFormat.TIME_FORMAT_READABLE), logType = LogLevel.INFO.level)
            dataToWrite = ECSMapper.getECSMappedLogString(logData)
        }

        if (PLogImpl.getConfig()?.isDebuggable!!) {

            if (dataToWrite.isNotEmpty())
                Log.i(PLog.TAG, dataToWrite)
        }

        //Publish to MQTT
        if (PLogMQTTProvider.mqttEnabled) {
            MQTTSender.publishMessage(dataToWrite)
        }

        //Only write to local storage if this flag is set 'true'
        if (PLogMQTTProvider.writeLogsToLocalStorage)
            DataLogWriter.writeDataLog(logFileName, dataToWrite, overwrite)

        return true
    }
}