package com.blackbox.plog.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.Keep
import com.blackbox.plog.pLogs.impl.PLogImpl
import java.io.*
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@SuppressLint("GetInstance")
@Keep
class Encrypter() {

    //Algorithm type used for encryption & decryption
    private val ALGORITHM = "AES"

    var aes2: Cipher? = null

    init {
        try {
            aes2 = Cipher.getInstance(ALGORITHM)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }


    }

    private fun generateIV(): IvParameterSpec {
        val r = SecureRandom()
        val ivBytes = ByteArray(16)
        r.nextBytes(ivBytes)
        return IvParameterSpec(ivBytes)
    }

    /*
     * This will validate key length.
     */
    fun checkIfKeyValid(encKey: String): String {

        if (encKey.isEmpty()) {
            Log.e("checkIfKeyValid", "No Key provided!")
            return ""
        }

        var key = encKey
        if (key.length < 32) {
            key = encKey.padStart(32, '0') //If length of key is less than 32 then key will be padded with '0's
        }

        return key
    }

    /*
     * This will generate secret key for encryption.
     */
    @Synchronized
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun generateKey(encKey: String): SecretKey {
        val salt = checkIfKeyValid(encKey)
        val key = salt.toBytes()
        return SecretKeySpec(key, "AES")
    }

    /*
     * This will read encrypted file & output decrypted 'String' data.
     */
    @Synchronized
    fun readFileDecrypted(filePath: String): String {
        var data = ""
        try {

            PLogImpl.getConfig()?.secretKey?.let {

                aes2?.init(Cipher.DECRYPT_MODE, it, generateIV())

                FileInputStream(filePath).use { fis ->
                    val cipherInputStream = CipherInputStream(fis, aes2)

                    ByteArrayOutputStream().use { os ->
                        os.write(cipherInputStream.readBytes())
                        data = os.toByteArray().toString(Charsets.UTF_8)
                    }
                }
            }

        } catch (ex: NoSuchAlgorithmException) {
            ex.printStackTrace()
        } catch (ex: NoSuchPaddingException) {
            ex.printStackTrace()
        } catch (ex: InvalidKeyException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return cleanUpFile(data)
    }


    private fun cleanUpFile(text: String): String {
        try {
            val t1 = cleanTextContent(text).replace(Regex("[^\\x00-\\x7f]+"), "")
            val t2 = t1.replace("[\r\n]+".toRegex(), "\n")
            val t3 = t2.trimStart()
            val t4 = t3.replace(Regex("[\r\t ]+"), " ")
            return t4
        } catch (e: Exception) {
            e.printStackTrace()
            return text
        }
    }

    private fun cleanTextContent(text: String): String {
        var text = text

        // erases all the ASCII control characters
        text = text.replace("[\\p{Cntrl}]".toRegex(), "\n")

        // removes non-printable characters from Unicode
        //text = text.replace("\\p{C}".toRegex(), "")

        return text.trim { it <= ' ' }
    }

    /*
     * This will append 'String' data to file after encrypting it.
     */
    @Synchronized
    fun appendToFileEncrypted(dataToWrite: String, key: SecretKey, filePath: String) {

        try {
            aes2?.init(Cipher.ENCRYPT_MODE, key, generateIV())

            FileOutputStream(File(filePath), true).use { fs ->
                val out = CipherOutputStream(fs, aes2)
                out.bufferedWriter().use {
                    it.write(dataToWrite)
                }
            }

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     * This will replace 'String' data to file after encrypting it.
     */
    @Synchronized
    fun writeToFileEncrypted(dataToWrite: String, key: SecretKey, filePath: String) {

        try {
            aes2?.init(Cipher.ENCRYPT_MODE, key, generateIV())

            FileOutputStream(filePath).use { fs ->
                val out = CipherOutputStream(fs, aes2)
                out.bufferedWriter().use {
                    it.write(dataToWrite)
                }
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     * This will convert string to byte array.
     */
    @Synchronized
    fun String.toBytes(): ByteArray {
        return this.toByteArray(Charsets.UTF_8)
    }
}