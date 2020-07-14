package com.blackbox.plog.mqtt

import android.content.Context
import android.util.Log
import androidx.work.*
import com.blackbox.plog.mqtt.client.PahoMqqtClient
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.pLogs.workers.LogsPublishWorker
import com.blackbox.plog.utils.PLogUtils

object MQTTSender {

    private val TAG = "MQTTSender"

    private var totalQueued = 0
    private var totalSent = 0
    private var totalAdded = 0

    fun publishMessage(message: String) {

        totalAdded++

        if (PLogMQTTProvider.mqttEnabled && PLogMQTTProvider.topic.isNotEmpty() && message.isNotEmpty()) {

            PLogImpl.context?.let { context ->
                if (!PLogUtils.isConnected(context)) {
                    enqueueMessage(message, context)
                    return
                }

                PahoMqqtClient.instance?.let {
                    if (!it.isConnected()) {
                        enqueueMessage(message, context)
                        return
                    }
                }
            }

            sendMessage(message)
        }
    }

    fun sendMessage(message: String) {
        try {
            PLogMQTTProvider.androidClient?.let { androidClient ->
                PahoMqqtClient.instance?.publishMessage(androidClient, message, PLogMQTTProvider.qos, PLogMQTTProvider.topic)
            }
        } catch (e: Exception) {
            if (PLogMQTTProvider.debug) {
                Log.e(TAG, PLogUtils.getStackTrace(e))
            }
        }
    }

    private fun createInputData(message: String): Data {
        val builder = Data.Builder()
        builder.putString(LogsPublishWorker.KEY_LOG_MESSAGE, message)
        return builder.build()
    }

    private fun enqueueMessage(message: String, context: Context) {

        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()


        val request: WorkRequest = OneTimeWorkRequestBuilder<LogsPublishWorker>()
                .setConstraints(constraints)
                .setInputData(createInputData(message))
                .build()

        WorkManager.getInstance(context)
                .enqueue(request)

        totalQueued++

        if (PLogMQTTProvider.debug) {
            printMQTTMessagesSummary("enqueueMessage")
        }
    }

    internal fun printMQTTMessagesSummary(eventName: String) {
        if (PLogMQTTProvider.debug) {
            if (totalQueued > 0) {
                Log.i(TAG, "Event: [$eventName] Total Messages: $totalAdded, Total Delivered: $totalSent, Total Queued: $totalQueued")
            } else {
                Log.i(TAG, "Event: [$eventName] Total Messages: $totalAdded, Total Delivered: $totalSent")
            }
        }
    }

    internal fun clearSummaryValues() {
        totalSent = 0
        totalQueued = 0
        totalAdded = 0
    }

    internal fun doOnMessageDelivered() {
        totalSent++
        if (totalQueued > 0)
            totalQueued--
    }
}