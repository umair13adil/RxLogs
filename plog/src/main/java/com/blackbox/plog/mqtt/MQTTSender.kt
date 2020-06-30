package com.blackbox.plog.mqtt

import org.eclipse.paho.client.mqttv3.MqttException
import java.io.UnsupportedEncodingException

object MQTTSender {

    fun publishMessage(message: String) {
        if (PLogMQTTProvider.mqttEnabled) {
            try {
                PLogMQTTProvider.client?.publishMessage(PLogMQTTProvider.androidClient!!, message, PLogMQTTProvider.qos, PLogMQTTProvider.topic)
            } catch (e: MqttException) {
                e.printStackTrace()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }
    }
}