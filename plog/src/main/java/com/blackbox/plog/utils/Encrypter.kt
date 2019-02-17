package com.blackbox.plog.utils

import android.annotation.SuppressLint
import com.blackbox.plog.pLogs.impl.PLogImpl
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
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
        aes2.init(Cipher.DECRYPT_MODE, PLogImpl.getConfig()?.secretKey!!)

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
    val cleanedUp = cleanUpFile(data)

    return cleanedUp
}

private fun cleanUpFile(text: String): String {
    val t0 = cleanTextContent(text)
    val t1 = t0.replace(Regex("[ \t]+"), " ")
    val t2 = t1.replace(Regex("[\r]"), "")
    val t3 = t2.replace(Regex("[\\x08]"), "")
    val t4 = t3.replace(Regex("[\\x07]"), "")
    val t5 = t4.replace(Regex("[\\x06]"), "")
    val t6 = t5.replace(Regex("[\\x0e]"), "")
    val t7 = t6.replace(Regex("[\n\r]{2,}"), "\n")
    return t7.trim()
}

private fun cleanTextContent(textInput: String): String {
    var text = textInput
    // strips off all non-ASCII characters
    text = text.replace(Regex("[^\\x00-\\x7F]"), "\n")

    // erases all the ASCII control characters
    text = text.replace(Regex("[\\p{Cntrl}&&[^\r\n\t]]"), "\n")

    // removes non-printable characters from Unicode
    text = text.replace(Regex("\\p{C}"), "\n")

    return text
}

/*
 * This will append 'String' data to file after encrypting it.
 */
@SuppressLint("GetInstance")
fun appendToFileEncrypted(data: String, key: SecretKey, filePath: String): Flowable<Boolean> {
    return Flowable.create({ emitter ->
        try {
            val aes = Cipher.getInstance(ALGORITHM)
            aes.init(Cipher.ENCRYPT_MODE, key)
            val fs = FileOutputStream(File(filePath), true)
            val out = CipherOutputStream(fs, aes)
            out.write(data.toBytes())
            out.flush()
            out.close()

            if (!emitter.isCancelled)
                emitter.onNext(true)

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()

            if (!emitter.isCancelled)
                emitter.onError(e)

        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()

            if (!emitter.isCancelled)
                emitter.onError(e)

        } catch (e: InvalidKeyException) {
            e.printStackTrace()

            if (!emitter.isCancelled)
                emitter.onError(e)

        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()

            if (!emitter.isCancelled)
                emitter.onError(e)

        } catch (e: BadPaddingException) {
            e.printStackTrace()

            if (!emitter.isCancelled)
                emitter.onError(e)

        } catch (e: Exception) {
            e.printStackTrace()

            if (!emitter.isCancelled)
                emitter.onError(e)
        }
    }, BackpressureStrategy.LATEST)
}

/*
 * This will replace 'String' data to file after encrypting it.
 */
@SuppressLint("GetInstance")
fun writeToFileEncrypted(data: String, key: SecretKey, filePath: String): Flowable<Boolean> {

    return Flowable.create({ emitter ->
        try {
            val aes = Cipher.getInstance(ALGORITHM)
            aes.init(Cipher.ENCRYPT_MODE, key)
            val fs = FileOutputStream(filePath)
            val out = CipherOutputStream(fs, aes)
            out.write(data.toBytes())
            out.flush()
            out.close()

            if (!emitter.isCancelled)
                emitter.onNext(true)

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()

            if (!emitter.isCancelled)
                emitter.onError(e)

        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()

            if (!emitter.isCancelled)
                emitter.onError(e)

        } catch (e: InvalidKeyException) {
            e.printStackTrace()

            if (!emitter.isCancelled)
                emitter.onError(e)

        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()

            if (!emitter.isCancelled)
                emitter.onError(e)

        } catch (e: BadPaddingException) {
            e.printStackTrace()

            if (!emitter.isCancelled)
                emitter.onError(e)

        } catch (e: Exception) {
            e.printStackTrace()

            if (!emitter.isCancelled)
                emitter.onError(e)
        }
    }, BackpressureStrategy.LATEST)
}

/*
 * This will convert string to byte array.
 */
fun String.toBytes(): ByteArray {
    return this.toByteArray(Charsets.UTF_8)
}