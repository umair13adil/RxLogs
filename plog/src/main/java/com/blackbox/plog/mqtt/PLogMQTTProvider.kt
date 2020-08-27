package com.blackbox.plog.mqtt

import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import com.blackbox.plog.mqtt.client.PahoMqqtClient
import com.blackbox.plog.utils.PLogUtils
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttClient
import java.io.InputStream

object PLogMQTTProvider {

    private val TAG = "PLogMQTTProvider"

    internal var mqttEnabled = false //Set this to 'true' to enable MQTT Feature
    internal var writeLogsToLocalStorage = true //Set this to 'false' if you don't want to write logs to local storage
    internal var topic: String = "" //Required
    internal var qos: Int = 2
    internal var retained: Boolean = false
    private var port: String = "8883"
    private var brokerUrl: String = "" //provide URL without scheme
    private var clientId: String = MqttClient.generateClientId() //Provide if needed
    internal var keepAliveIntervalSeconds = 180 //Default
    internal var connectionTimeout = 60 //Default
    internal var initialDelaySecondsForPublishing = 30L //Default
    internal var isCleanSession = true //Default
    internal var isAutomaticReconnect = true //Default
    internal var debug = true //Default
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
                       initialDelaySecondsForPublishing: Long = this.initialDelaySecondsForPublishing,
                       isCleanSession: Boolean = this.isCleanSession,
                       isAutomaticReconnect: Boolean = this.isAutomaticReconnect,
                       @RawRes certificateRes: Int? = null,
                       certificateStream: InputStream? = null,
                       debug: Boolean = this.debug) {

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
        this.initialDelaySecondsForPublishing = initialDelaySecondsForPublishing
        this.isCleanSession = isCleanSession
        this.isAutomaticReconnect = isAutomaticReconnect
        this.debug = debug

        val baseUrl = "ssl://${brokerUrl}:${port}"

        if (certificateRes != null) {
            androidClient = PahoMqqtClient.instance?.getMqttClient(context, baseUrl, MqttClient.generateClientId(), certificateRes)
        } else if (certificateStream != null) {
            androidClient = PahoMqqtClient.instance?.getMqttClient(context, baseUrl, MqttClient.generateClientId(), certificateStream)
        } else {
            if (debug) {
                Log.e(TAG, "No certificate provided!")
            }
        }
    }

    fun disposeMQTTClient() {
        PahoMqqtClient.instance?.dispose()
    }
}