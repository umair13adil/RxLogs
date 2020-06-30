package com.blackbox.plog.mqtt.client

import android.content.Context
import android.util.Log
import com.blackbox.plog.mqtt.PLogMQTTProvider
import com.blackbox.plog.mqtt.client.SocketFactory.SocketFactoryOptions
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException

class PAHOClient {

    private var client: MqttAndroidClient? = null
    private var connectOptions: MqttConnectOptions? = null

    fun getClient(context: Context?, brokerUrl: String?, clientId: String?): MqttAndroidClient {

        connectOptions = MqttConnectOptions()
        connectOptions?.keepAliveInterval = PLogMQTTProvider.keepAliveIntervalSeconds
        connectOptions?.isCleanSession = PLogMQTTProvider.isCleanSession
        connectOptions?.isAutomaticReconnect = PLogMQTTProvider.isAutomaticReconnect
        client = MqttAndroidClient(context, brokerUrl, clientId)

        val socketFactoryOptions = SocketFactoryOptions()

        PLogMQTTProvider.certificateInputSteam?.let {

            socketFactoryOptions.withCaInputStream(it)

            try {
                connectOptions?.socketFactory = SocketFactory(socketFactoryOptions)
            } catch (e: KeyStoreException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e: CertificateException) {
                e.printStackTrace()
            } catch (e: UnrecoverableKeyException) {
                e.printStackTrace()
            }
        }

        try {
            val token = client?.connect(connectOptions)
            token?.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    client?.setBufferOpts(disconnectedBufferOptions)
                    Log.d(TAG, "connect : Success")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.d(TAG, "connect : Failure $exception")
                }
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
        return client!!
    }

    @Throws(MqttException::class)
    fun disconnect(client: MqttAndroidClient) {
        val token = client.disconnect()
        token.actionCallback = object : IMqttActionListener {
            override fun onSuccess(iMqttToken: IMqttToken) {
                Log.d(TAG, "Successfully disconnected")
            }

            override fun onFailure(iMqttToken: IMqttToken, throwable: Throwable) {
                Log.d(TAG, "Failed to disconnected $throwable")
            }
        }
    }

    private val disconnectedBufferOptions: DisconnectedBufferOptions
        get() {
            val disconnectedBufferOptions = DisconnectedBufferOptions()
            disconnectedBufferOptions.isBufferEnabled = true
            disconnectedBufferOptions.bufferSize = 100
            disconnectedBufferOptions.isPersistBuffer = false
            disconnectedBufferOptions.isDeleteOldestMessages = false
            return disconnectedBufferOptions
        }

    @Throws(MqttException::class, UnsupportedEncodingException::class)
    fun publishMessage(client: MqttAndroidClient, msg: String, qos: Int, topic: String) {
        var encodedPayload = ByteArray(0)
        encodedPayload = msg.toByteArray(charset("UTF-8"))
        val message = MqttMessage(encodedPayload)
        message.id = 320
        message.isRetained = true
        message.qos = qos
        client.publish(topic, message)
    }

    @Throws(MqttException::class)
    fun subscribe(client: MqttAndroidClient, topic: String, qos: Int) {
        val token = client.subscribe(topic, qos)
        token.actionCallback = object : IMqttActionListener {
            override fun onSuccess(iMqttToken: IMqttToken) {
                Log.d(TAG, "Subscribe Successfully $topic")
            }

            override fun onFailure(iMqttToken: IMqttToken, throwable: Throwable) {
                Log.e(TAG, "Subscribe Failed $topic")
            }
        }
    }

    @Throws(MqttException::class)
    fun unSubscribe(client: MqttAndroidClient, topic: String) {
        val token = client.unsubscribe(topic)
        token.actionCallback = object : IMqttActionListener {
            override fun onSuccess(iMqttToken: IMqttToken) {
                Log.d(TAG, "UnSubscribe Successfully $topic")
            }

            override fun onFailure(iMqttToken: IMqttToken, throwable: Throwable) {
                Log.e(TAG, "UnSubscribe Failed $topic")
            }
        }
    }

    companion object {
        private const val TAG = "PAHOClient"
    }
}