package com.blackbox.plog.mqtt.client

import java.io.IOException
import java.io.InputStream
import java.net.InetAddress
import java.net.Socket
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*


class SocketFactory @JvmOverloads constructor(options: SocketFactoryOptions = SocketFactoryOptions()) : SSLSocketFactory() {

    private val TAG = "SocketFactory"
    private val factory: SSLSocketFactory

    class SocketFactoryOptions {
        var caCrtInputStream: InputStream? = null
            private set
        var caClientP12InputStream: InputStream? = null
            private set
        var caClientP12Password: String? = null
            private set

        fun withCaInputStream(stream: InputStream?): SocketFactoryOptions {
            caCrtInputStream = stream
            return this
        }

        fun withClientP12InputStream(stream: InputStream?): SocketFactoryOptions {
            caClientP12InputStream = stream
            return this
        }

        fun withClientP12Password(password: String?): SocketFactoryOptions {
            caClientP12Password = password
            return this
        }

        fun hasCaCrt(): Boolean {
            return caCrtInputStream != null
        }

        fun hasClientP12Crt(): Boolean {
            return caClientP12Password != null
        }

        fun hasClientP12Password(): Boolean {
            return caClientP12Password != null && caClientP12Password != ""
        }
    }

    private val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    private val trustManagers: Array<TrustManager>
        get() = tmf.trustManagers

    private var tm: TrustManager = object : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?>? {
            return arrayOf()
        }
    }

    private val trustAllManagers: Array<TrustManager>
        get() = arrayOf(tm)

    override fun getDefaultCipherSuites(): Array<String> {
        return factory.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return factory.supportedCipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(): Socket {
        val r = factory.createSocket() as SSLSocket
        r.enabledProtocols = arrayOf("TLSv1", "TLSv1.1", "TLSv1.2")
        return r
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket {
        val r = factory.createSocket(s, host, port, autoClose) as SSLSocket
        r.enabledProtocols = arrayOf("TLSv1", "TLSv1.1", "TLSv1.2")
        return r
    }

    @Throws(IOException::class)
    override fun createSocket(host: String, port: Int): Socket {
        val r = factory.createSocket(host, port) as SSLSocket
        r.enabledProtocols = arrayOf("TLSv1", "TLSv1.1", "TLSv1.2")
        return r
    }

    @Throws(IOException::class)
    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket {
        val r = factory.createSocket(host, port, localHost, localPort) as SSLSocket
        r.enabledProtocols = arrayOf("TLSv1", "TLSv1.1", "TLSv1.2")
        return r
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress, port: Int): Socket {
        val r = factory.createSocket(host, port) as SSLSocket
        r.enabledProtocols = arrayOf("TLSv1", "TLSv1", "TLSv1.1", "TLSv1.2")
        return r
    }

    @Throws(IOException::class)
    override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket {
        val r = factory.createSocket(address, port, localAddress, localPort) as SSLSocket
        r.enabledProtocols = arrayOf("TLSv1", "TLSv1.1", "TLSv1.2")
        return r
    }

    init {
        val kmf = KeyManagerFactory.getInstance("X509")

        if (options.hasCaCrt()) {
            val caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            caKeyStore.load(null, null)
            val caCF = CertificateFactory.getInstance("X.509")
            val ca = caCF.generateCertificate(options.caCrtInputStream) as X509Certificate
            val alias = ca.subjectX500Principal.name
            caKeyStore.setCertificateEntry(alias, ca)
            tmf.init(caKeyStore)

            /*if (PLogMQTTProvider.debug) {
                Log.i(TAG, "TrustManager Provider Name: ${tmf.provider?.name}")
                Log.i(TAG, "TrustManager Provider Info: ${tmf.provider?.info}")
                Log.i(TAG, "Certificate Owner: ${ca.subjectDN}")
                Log.i(TAG, "Certificate Issuer: ${ca.issuerDN}")
                Log.i(TAG, "Certificate Serial Number: ${ca.serialNumber}")
                Log.i(TAG, "Certificate Algorithm: ${ca.sigAlgName}")
                Log.i(TAG, "Certificate Version: ${ca.version}")
                Log.i(TAG, "Certificate OID: ${ca.sigAlgOID}")
            }*/
        } else {
            val keyStore = KeyStore.getInstance("AndroidCAStore")
            keyStore.load(null)
            tmf.init(keyStore)
        }

        if (options.hasClientP12Crt()) {
            val clientKeyStore = KeyStore.getInstance("PKCS12")
            clientKeyStore.load(options.caClientP12InputStream, if (options.hasClientP12Password()) options.caClientP12Password!!.toCharArray() else CharArray(0))
            kmf.init(clientKeyStore, if (options.hasClientP12Password()) options.caClientP12Password!!.toCharArray() else CharArray(0))

            val aliasesClientCert = clientKeyStore.aliases()
            while (aliasesClientCert.hasMoreElements()) {
                val o = aliasesClientCert.nextElement()
            }
        } else {
            kmf.init(null, null)
        }

        // Create an SSLContext that uses our TrustManager
        val context = SSLContext.getInstance("TLSv1.2")
        //context.init(kmf.keyManagers, trustManagers, SecureRandom())
        context.init(kmf.keyManagers, trustAllManagers, SecureRandom())
        factory = context.socketFactory
    }
}