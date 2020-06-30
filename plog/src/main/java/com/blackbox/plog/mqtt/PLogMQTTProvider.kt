package com.blackbox.plog.mqtt

import android.content.Context
import android.support.annotation.RawRes
import com.blackbox.plog.mqtt.client.PahoMqqtClient
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttClient

object PLogMQTTProvider {

    private val TAG = "PLogMQTTProvider"

    internal var mqttEnabled = false //Set this to 'true' to enable MQTT Feature
    internal var writeLogsToLocalStorage = true //Set this to 'false' if you don't want to write logs to local storage
    internal var topic: String = "" //Required
    internal var qos: Int = 1
    internal var retained: Boolean = false
    private var port: String = "8883"
    private var brokerUrl: String = "" //provide URL without scheme
    private var clientId: String = MqttClient.generateClientId() //Provide if needed
    internal var keepAliveIntervalSeconds = 180 //Default
    internal var connectionTimeout = 60 //Default
    internal var isCleanSession = true //Default
    internal var isAutomaticReconnect = true //Default

    internal var androidClient: MqttAndroidClient? = null

    fun initMQTTClient(context: Context,
                       writeLogsToLocalStorage: Boolean = this.writeLogsToLocalStorage,
                       topic: String = "",
                       qos: Int = this.qos,
                       retained: Boolean = this.retained,
                       brokerUrl: String = "",
                       port: String = this.port,
                       clientId: String = this.clientId,
                       keepAliveIntervalSeconds: Int = this.keepAliveIntervalSeconds,
                       connectionTimeout: Int = this.connectionTimeout,
                       isCleanSession: Boolean = this.isCleanSession,
                       isAutomaticReconnect: Boolean = this.isAutomaticReconnect,
                       @RawRes certificateRes: Int) {

        this.mqttEnabled = true
        this.writeLogsToLocalStorage = writeLogsToLocalStorage
        this.topic = topic
        this.qos = qos
        this.retained = retained
        this.brokerUrl = brokerUrl
        this.port = port
        this.clientId = clientId
        this.keepAliveIntervalSeconds = keepAliveIntervalSeconds
        this.connectionTimeout = connectionTimeout
        this.isCleanSession = isCleanSession
        this.isAutomaticReconnect = isAutomaticReconnect

        val baseUrl = "ssl://${brokerUrl}:${port}"

        androidClient = PahoMqqtClient.instance?.getMqttClient(context, baseUrl, MqttClient.generateClientId(), certificateRes)
    }
}