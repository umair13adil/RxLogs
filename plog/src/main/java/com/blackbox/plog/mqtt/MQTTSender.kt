package com.blackbox.plog.mqtt

import android.util.Log
import com.blackbox.plog.mqtt.client.PahoMqqtClient
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.utils.Utils
import org.eclipse.paho.client.mqttv3.MqttException
import java.io.UnsupportedEncodingException
import java.lang.NullPointerException

object MQTTSender {

    private val TAG = "MQTTSender"

    fun publishMessage(message: String) {
        if (PLogMQTTProvider.mqttEnabled && PLogMQTTProvider.topic.isNotEmpty() && message.isNotEmpty()) {
            try {
                PLogMQTTProvider.androidClient?.let { androidClient ->
                    PahoMqqtClient.instance?.publishMessage(androidClient, message, PLogMQTTProvider.qos, PLogMQTTProvider.topic)
                }
            } catch (e: Exception) {
                if (PLogImpl.getConfig()?.isDebuggable!!) {
                    Log.e(TAG, Utils.getStackTrace(e))
                }
            }
        }
    }
}