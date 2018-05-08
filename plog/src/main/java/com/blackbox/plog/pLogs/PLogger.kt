package com.blackbox.plog.pLogs

import android.os.Environment

import java.io.File

/**
 * Created by umair on 03/01/2018.
 */

class PLogger {

    private val TAG = PLogger::class.java.simpleName

    var savePath: String = Environment.getExternalStorageDirectory().toString() + File.separator + TAG
    var exportPath: String = Environment.getExternalStorageDirectory().toString() + File.separator + TAG
    var exportFileName: String = "Output"
    var attachTimeStamp: Boolean = true
    var attachNoOfFiles: Boolean = true
    var debug: Boolean = false
    var silentLogs: Boolean = false
    var formatType = LogFormatter.FORMAT_CURLY
    var customFormatOpen: String = " "
    var customFormatClose: String = " "
    var csvDeliminator: String = ","
    var timeStampFormat: String = "dd MMMM yyyy kk:mm:ss"
    var logFileExtension: String = ".txt"

    val isDebuggable: Boolean
        get() = debug

    val isSilentLog: Boolean?
        get() = silentLogs

    constructor() {

    }

    constructor(savePath: String, exportPath: String, fileName: String, attachTimeStamp: Boolean, attachNoOfFiles: Boolean, formatType: String, customFormatOpen: String, customFormatClose: String, csvDeliminator: String, debug: Boolean, timeStampFormat: String, logFileExtension: String, silentLogs: Boolean) {
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
    }

}
