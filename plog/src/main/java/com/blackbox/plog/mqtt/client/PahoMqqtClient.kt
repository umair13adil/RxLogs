package com.blackbox.plog.mqtt.client

import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import com.blackbox.plog.mqtt.PLogMQTTProvider
import io.reactivex.Observable
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

    fun setConnected() {
        isConnected = true
    }

    fun isConnected(): Boolean {
        return isConnected
    }

    private fun setUpClient(context: Context, brokerUrl: String?, clientId: String?) {
        connectOptions = MqttConnectOptions()
        connectOptions?.connectionTimeout = PLogMQTTProvider.connectionTimeout
        connectOptions?.keepAliveInterval = PLogMQTTProvider.keepAliveIntervalSeconds
        connectOptions?.isCleanSession = PLogMQTTProvider.isCleanSession
        connectOptions?.isAutomaticReconnect = PLogMQTTProvider.isAutomaticReconnect

        mqttAndroidClient = MqttAndroidClient(context, brokerUrl, clientId)
        mqttAndroidClient?.setCallback(object : MqttCallback {

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                if (PLogMQTTProvider.debug) {
                    Log.d(TAG, "messageArrived : Topic: $topic , Message: $message")
                }
            }

            override fun connectionLost(cause: Throwable?) {
                isConnected = false
                if (PLogMQTTProvider.debug) {
                    Log.d(TAG, "connectionLost.")
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

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
        mqttAndroidClient?.let {
            try {
                val token = it.connect(connectOptions)
                token.actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        it.setBufferOpts(disconnectedBufferOptions)

                        if (PLogMQTTProvider.debug) {
                            Log.d(TAG, "connect : Success")
                        }

                        isConnected = true
                    }

                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        if (PLogMQTTProvider.debug) {
                            Log.d(TAG, "connect : Unable to connect to server. Check connection.")
                        }
                    }
                }
            } catch (e: Exception) {
                //e.printStackTrace()
                isConnected = false
                if (PLogMQTTProvider.debug) {
                    Log.e(TAG, "MQTT connection closed.")
                }
            }
        }
    }

    fun dispose() {
        mqttAndroidClient?.unregisterResources();
        mqttAndroidClient?.close()
    }

    @Throws(MqttException::class)
    fun disconnect() {
        mqttAndroidClient?.let {
            try {
                val mqttToken = it.disconnect()
                mqttToken.actionCallback = object : IMqttActionListener {
                    override fun onSuccess(iMqttToken: IMqttToken) {
                        if (PLogMQTTProvider.debug) {
                            Log.d(TAG, "Successfully disconnected")
                        }
                    }

                    override fun onFailure(iMqttToken: IMqttToken, throwable: Throwable) {
                        if (PLogMQTTProvider.debug) {
                            Log.d(TAG, "Failed to disconnected $throwable")
                        }
                    }
                }
            } catch (e: Exception) {

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
    fun publishMessage(client: MqttAndroidClient?, msg: String, qos: Int, topic: String, context: Context): Observable<Boolean> {
        return Observable.create { emitter ->
            if (isConnected) {
                var encodedPayload = ByteArray(0)
                encodedPayload = msg.toByteArray(charset("UTF-8"))
                val message = MqttMessage(encodedPayload)
                message.id = msg.hashCode()
                message.isRetained = PLogMQTTProvider.retained
                message.qos = qos
                client?.publish(topic, message, context, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        if (!emitter.isDisposed) {
                            emitter.onNext(true)
                            emitter.onComplete()
                        }
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        if (!emitter.isDisposed) {
                            emitter.onNext(false)
                            emitter.onComplete()
                        }
                    }
                })
            }
        }
    }

    @Throws(MqttException::class)
    fun subscribe(client: MqttAndroidClient?, topic: String, qos: Int) {
        val token = client?.subscribe(topic, qos)
        token?.actionCallback = object : IMqttActionListener {
            override fun onSuccess(iMqttToken: IMqttToken) {
                if (PLogMQTTProvider.debug) {
                    Log.d(TAG, "Subscribe Successfully $topic")
                }
            }

            override fun onFailure(iMqttToken: IMqttToken, throwable: Throwable) {
                if (PLogMQTTProvider.debug) {
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
                if (PLogMQTTProvider.debug) {
                    Log.d(TAG, "UnSubscribe Successfully $topic")
                }
            }

            override fun onFailure(iMqttToken: IMqttToken, throwable: Throwable) {
                if (PLogMQTTProvider.debug) {
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