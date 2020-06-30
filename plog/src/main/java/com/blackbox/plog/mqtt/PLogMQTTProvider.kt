package com.blackbox.plog.mqtt

import android.content.Context
import com.blackbox.plog.mqtt.client.PAHOClient
import org.eclipse.paho.android.service.MqttAndroidClient
import java.io.InputStream
import java.util.*

object PLogMQTTProvider {

    internal var mqttEnabled = false //Set this to 'true' to enable MQTT Feature
    internal var writeLogsToLocalStorage = true //Set this to 'false' if you don't want to write logs to local storage
    internal var topic: String = "" //Required
    internal var qos: Int = 0
    private var port: String = "8883"
    private var url: String = "" //provide URL without scheme
    private var clientId: String = UUID.randomUUID().toString() //Provide if needed
    internal var certificateInputSteam: InputStream? = null //Provide certificate for SSL connection
    internal var keepAliveIntervalSeconds = 180 //Default
    internal var isCleanSession = true //Default
    internal var isAutomaticReconnect = true //Default

    internal var client: PAHOClient? = null
    internal var androidClient: MqttAndroidClient? = null

    fun initMQTTClient(context: Context,
                       writeLogsToLocalStorage: Boolean = true,
                       topic: String = "",
                       qos: Int = 0,
                       url: String = "",
                       port: String = "8883",
                       clientId: String = UUID.randomUUID().toString(),
                       keepAliveIntervalSeconds: Int = 180,
                       isCleanSession: Boolean = true,
                       isAutomaticReconnect: Boolean = true,
                       certificateInputSteam: InputStream) {

        this.mqttEnabled = true
        this.writeLogsToLocalStorage = writeLogsToLocalStorage
        this.topic = topic
        this.qos = qos
        this.url = url
        this.port = port
        this.clientId = clientId
        this.keepAliveIntervalSeconds = keepAliveIntervalSeconds
        this.isCleanSession = isCleanSession
        this.isAutomaticReconnect = isAutomaticReconnect
        this.certificateInputSteam = certificateInputSteam

        client = PAHOClient()
        val baseUrl = "ssl://${url}:${port}"
        androidClient = client?.getClient(context, baseUrl, clientId)
    }
}