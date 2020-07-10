package com.blackbox.plog.mqtt.client

import android.content.Context
import android.support.annotation.RawRes
import android.util.Log
import com.blackbox.plog.mqtt.PLogMQTTProvider
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.utils.PLogUtils
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException

class PahoMqqtClient {

    private var mqttAndroidClient: MqttAndroidClient? = null
    private var connectOptions: MqttConnectOptions? = null
    private var isConnected = false

    private fun setUpClient(context: Context, brokerUrl: String?, clientId: String?) {
        connectOptions = MqttConnectOptions()
        connectOptions?.connectionTimeout = PLogMQTTProvider.connectionTimeout
        connectOptions?.keepAliveInterval = PLogMQTTProvider.keepAliveIntervalSeconds
        connectOptions?.isCleanSession = PLogMQTTProvider.isCleanSession
        connectOptions?.isAutomaticReconnect = PLogMQTTProvider.isAutomaticReconnect

        mqttAndroidClient = MqttAndroidClient(context, brokerUrl, clientId)
        mqttAndroidClient?.setCallback(object : MqttCallback {

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                if (PLogImpl.getConfig()?.isDebuggable!!) {
                    Log.d(TAG, "messageArrived : Topic: $topic , Message: $message")
                }
            }

            override fun connectionLost(cause: Throwable?) {
                if (PLogImpl.getConfig()?.isDebuggable!!) {
                    Log.d(TAG, "connectionLost : ${PLogUtils.getStackTrace(cause)}")
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                if (PLogImpl.getConfig()?.isDebuggable!!) {
                    Log.d(TAG, "deliveryComplete : ${token?.message?.payload}")
                }
            }
        })
    }

    fun getMqttClient(context: Context, brokerUrl: String?, clientId: String?, @RawRes certFile: Int): MqttAndroidClient {

        setUpClient(context, brokerUrl, clientId)

        val socketFactoryOptions = SocketFactory.SocketFactoryOptions()
        socketFactoryOptions.withCaInputStream(context.resources.openRawResource(certFile))

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

        connect()

        return mqttAndroidClient!!
    }

    fun getMqttClient(context: Context, brokerUrl: String?, clientId: String?, certInputStream: InputStream): MqttAndroidClient {

        setUpClient(context, brokerUrl, clientId)

        val socketFactoryOptions = SocketFactory.SocketFactoryOptions()
        socketFactoryOptions.withCaInputStream(certInputStream)

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

        connect()

        return mqttAndroidClient!!
    }

    private fun connect() {
        try {
            val token = mqttAndroidClient!!.connect(connectOptions)
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    mqttAndroidClient!!.setBufferOpts(disconnectedBufferOptions)

                    if (PLogImpl.getConfig()?.isDebuggable!!) {
                        Log.d(TAG, "connect : Success")
                    }

                    isConnected = true
                    //subscribe(mqttAndroidClient, PLogMQTTProvider.topic, PLogMQTTProvider.qos)
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    if (PLogImpl.getConfig()?.isDebuggable!!) {
                        Log.d(TAG, "connect : Failure $exception")
                        Log.e(TAG, PLogUtils.getStackTrace(exception))
                    }
                }
            }
        } catch (e: MqttException) {
            e.printStackTrace()

            if (PLogImpl.getConfig()?.isDebuggable!!) {
                Log.e(TAG, PLogUtils.getStackTrace(e))
            }
        }
    }

    @Throws(MqttException::class)
    fun disconnect(client: MqttAndroidClient) {
        val mqttToken = client.disconnect()
        mqttToken.actionCallback = object : IMqttActionListener {
            override fun onSuccess(iMqttToken: IMqttToken) {
                if (PLogImpl.getConfig()?.isDebuggable!!) {
                    Log.d(TAG, "Successfully disconnected")
                }
            }

            override fun onFailure(iMqttToken: IMqttToken, throwable: Throwable) {
                if (PLogImpl.getConfig()?.isDebuggable!!) {
                    Log.d(TAG, "Failed to disconnected $throwable")
                }
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
        if (isConnected) {
            var encodedPayload = ByteArray(0)
            encodedPayload = msg.toByteArray(charset("UTF-8"))
            val message = MqttMessage(encodedPayload)
            message.id = msg.hashCode()
            message.isRetained = PLogMQTTProvider.retained
            message.qos = qos
            client.publish(topic, message)
        }
    }

    @Throws(MqttException::class)
    fun subscribe(client: MqttAndroidClient?, topic: String, qos: Int) {
        val token = client?.subscribe(topic, qos)
        token?.actionCallback = object : IMqttActionListener {
            override fun onSuccess(iMqttToken: IMqttToken) {
                if (PLogImpl.getConfig()?.isDebuggable!!) {
                    Log.d(TAG, "Subscribe Successfully $topic")
                }
            }

            override fun onFailure(iMqttToken: IMqttToken, throwable: Throwable) {
                if (PLogImpl.getConfig()?.isDebuggable!!) {
                    Log.e(TAG, "Subscribe Failed $topic")
                }
            }
        }
    }

    @Throws(MqttException::class)
    fun unSubscribe(client: MqttAndroidClient, topic: String) {
        val token = client.unsubscribe(topic)
        token.actionCallback = object : IMqttActionListener {
            override fun onSuccess(iMqttToken: IMqttToken) {
                if (PLogImpl.getConfig()?.isDebuggable!!) {
                    Log.d(TAG, "UnSubscribe Successfully $topic")
                }
            }

            override fun onFailure(iMqttToken: IMqttToken, throwable: Throwable) {
                if (PLogImpl.getConfig()?.isDebuggable!!) {
                    Log.e(TAG, "UnSubscribe Failed $topic")
                }
            }
        }
    }

    companion object {

        private const val TAG = "PLogger: PahoMqttClient"

        @JvmStatic
        private var pahoMqqtClient: PahoMqqtClient? = null

        @JvmStatic
        val instance: PahoMqqtClient?
            get() {
                if (pahoMqqtClient == null) pahoMqqtClient = PahoMqqtClient()
                return pahoMqqtClient
            }
    }
}