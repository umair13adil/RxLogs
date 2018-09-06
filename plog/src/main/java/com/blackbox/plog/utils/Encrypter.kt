package com.blackbox.plog.utils

import android.annotation.SuppressLint
import com.blackbox.plog.pLogs.PLog
import java.io.*
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

//Algorithm type used for encryption & decryption
private const val ALGORITHM = "AES/ECB/PKCS5Padding"

/*
 * This will validate key length.
 */
fun checkIfKeyValid(encKey: String): String {

    if (encKey.isEmpty())
        throw(Throwable("Invalid key provided. Can not encrypt with empty key."))

    var key = encKey
    if (key.length < 32) {
        key = encKey.padStart(32, '0') //If length of key is less than 32 then key will be padded with '0's
    }

    return key
}

/*
 * This will generate secret key for encryption.
 */
@Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
fun generateKey(encKey: String): SecretKey {
    val salt = checkIfKeyValid(encKey)
    val key = salt.toBytes()
    return SecretKeySpec(key, "AES")
}

/*
 * This will read encrypted file & output decrypted 'String' data.
 */
@SuppressLint("GetInstance")
fun readFileDecrypted(filePath: String): String {
    var data = ""

    try {

        val aes2 = Cipher.getInstance(ALGORITHM)
        aes2.init(Cipher.DECRYPT_MODE, PLog.getPLogger()?.secretKey!!)

        val fis = FileInputStream(filePath)
        val cipherInputStream = CipherInputStream(fis, aes2)
        val baos = ByteArrayOutputStream()

        baos.write(cipherInputStream.readBytes())

        //Convert decrypted bytes to String
        data = String(baos.toByteArray())

    } catch (ex: NoSuchAlgorithmException) {
        ex.printStackTrace()
    } catch (ex: NoSuchPaddingException) {
        ex.printStackTrace()
    } catch (ex: InvalidKeyException) {
        ex.printStackTrace()
    } catch (ex: IOException) {
        ex.printStackTrace()
    }

    return data
}

/*
 * This will append 'String' data to file after encrypting it.
 */
@SuppressLint("GetInstance")
fun appendToFileEncrypted(data: String, key: SecretKey, filePath: String) {
    try {
        val aes = Cipher.getInstance(ALGORITHM)
        aes.init(Cipher.ENCRYPT_MODE, key)
        val fs = FileOutputStream(File(filePath), true)
        val out = CipherOutputStream(fs, aes)
        out.write(data.toBytes())
        out.flush()
        out.close()
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
@SuppressLint("GetInstance")
fun writeToFileEncrypted(data: String, key: SecretKey, filePath: String) {
    try {
        val aes = Cipher.getInstance(ALGORITHM)
        aes.init(Cipher.ENCRYPT_MODE, key)
        val fs = FileOutputStream(filePath)
        val out = CipherOutputStream(fs, aes)
        out.write(data.toBytes())
        out.flush()
        out.close()
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
fun String.toBytes(): ByteArray {
    return this.toByteArray(Charsets.UTF_8)
}