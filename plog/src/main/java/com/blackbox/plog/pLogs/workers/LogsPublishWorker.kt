package com.blackbox.plog.pLogs.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.blackbox.plog.mqtt.MQTTSender
import com.blackbox.plog.mqtt.PLogMQTTProvider
import com.blackbox.plog.mqtt.client.PahoMqqtClient
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.utils.PLogUtils

class LogsPublishWorker(appContext: Context, workerParams: WorkerParameters) :
        Worker(appContext, workerParams) {

    companion object {
        private val TAG = "LogsPublishWorker"
        val KEY_LOG_MESSAGE = "log_message"
    }

    override fun doWork(): Result {

        PahoMqqtClient.instance?.setConnected()

        return try {
            val message = inputData.getString(KEY_LOG_MESSAGE)

            message?.let {
                if (PLogMQTTProvider.mqttEnabled && PLogMQTTProvider.topic.isNotEmpty() && message.isNotEmpty()) {
                    MQTTSender.printMQTTMessagesSummary("retrySending")
                    MQTTSender.sendMessage(message)
                }
            }

            Result.success()
        } catch (throwable: Throwable) {
            if (PLogMQTTProvider.debug) {
                Log.e(TAG, PLogUtils.getStackTrace(throwable))
            }
            Result.failure()
        }
    }
}
