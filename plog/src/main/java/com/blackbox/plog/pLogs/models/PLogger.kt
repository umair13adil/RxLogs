package com.blackbox.plog.pLogs.models

import android.os.Environment
import com.blackbox.plog.pLogs.formatter.LogFormatter

import java.io.File
import javax.crypto.SecretKey

/**
 * Created by umair on 03/01/2018.
 */

class PLogger {

    private val TAG = PLogger::class.java.simpleName

    internal var savePath: String = Environment.getExternalStorageDirectory().toString() + File.separator + TAG
    internal var exportPath: String = Environment.getExternalStorageDirectory().toString() + File.separator + TAG
    internal var exportFileName: String = "Output"
    internal var attachTimeStamp: Boolean = true
    internal var attachNoOfFiles: Boolean = true
    internal var debug: Boolean = false
    internal var silentLogs: Boolean = false
    internal var formatType = LogFormatter.FORMAT_CURLY
    internal var customFormatOpen: String = " "
    internal var customFormatClose: String = " "
    internal var csvDeliminator: String = ","
    internal var timeStampFormat: String = "dd MMMM yyyy kk:mm:ss"
    internal var logFileExtension: String = ".txt"
    internal var encrypt: Boolean = false
    internal var encryptionKey: String = ""
    internal var enabled: Boolean = true
    internal var secretKey: SecretKey? = null

    internal val isDebuggable: Boolean
        get() = debug

    internal val isSilentLog: Boolean?
        get() = silentLogs

    constructor() {

    }

    constructor(savePath: String, exportPath: String, fileName: String, attachTimeStamp: Boolean, attachNoOfFiles: Boolean, formatType: String, customFormatOpen: String, customFormatClose: String, csvDeliminator: String, debug: Boolean, timeStampFormat: String, logFileExtension: String, silentLogs: Boolean, encryptionEnabled: Boolean, encryptionKey: String, loggingEnabled: Boolean) {
        this.savePath = savePath
        this.exportPath = exportPath
        this.exportFileName = fileName
        this.attachTimeStamp = attachTimeStamp
        this.attachNoOfFiles = attachNoOfFiles
        this.formatType = formatType
        this.customFormatOpen = customFormatOpen
        this.customFormatClose = customFormatClose
        this.csvDeliminator = csvDeliminator
        this.debug = debug
        this.timeStampFormat = timeStampFormat
        this.logFileExtension = logFileExtension
        this.silentLogs = silentLogs
        this.encrypt = encryptionEnabled
        this.encryptionKey = encryptionKey
        this.enabled = loggingEnabled
    }

}
