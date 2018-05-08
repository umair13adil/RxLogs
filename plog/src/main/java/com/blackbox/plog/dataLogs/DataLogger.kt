package com.blackbox.plog.dataLogs

import android.os.Environment
import com.blackbox.plog.pLogs.LogExporter
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.utils.DateControl
import com.blackbox.plog.utils.Utils
import io.reactivex.Observable
import java.io.File

/**
 * Created by umair on 03/01/2018.
 */

class DataLogger internal constructor(savePath: String, exportPath: String, exportFileName: String, logFileName: String, attachTimeStamp: Boolean?, debug: Boolean?) {

    private var savePath = Environment.getExternalStorageDirectory().toString() + File.separator + TAG
    private var exportPath = Environment.getExternalStorageDirectory().toString() + File.separator + TAG
    private var exportFileName = "Output"
    private var logFileName = "log"
    private var attachTimeStamp = true
    private var debug = false

    /**
     * Gets output path.
     *
     *
     *
     * Sets the export path of Logs.
     *
     * @return the output path
     */
    private val outputPath: String
        get() = exportPath + File.separator

    /**
     * Gets Logs path.
     *
     *
     *
     * Sets the save path of Logs.
     *
     * @return the save path
     */
    private val logPath: String
        get() = savePath + File.separator

    /**
     * Gets logs.
     *
     *
     *
     * This will export logs based on filter type to export location with export name provided.
     *
     * @return the logs
     */
    val logs: Observable<String>
        get() = LogExporter.getDataLogs(logFileName, attachTimeStamp, logPath, exportFileName, outputPath, debug)

    init {
        this.savePath = savePath
        this.exportPath = exportPath
        this.exportFileName = exportFileName
        this.logFileName = logFileName
        this.attachTimeStamp = attachTimeStamp!!
        this.debug = debug!!
    }

    /**
     * Overwrite to file.
     *
     *
     *
     * This function will overwrite a 'String' data to a file.
     * File will be created if it doesn't exists in path provided.
     * Filename can contain extension as well e.g 'error_log.txt'.
     * If 'attachTimeStamp' is true filename will contain date & hour in it like: '0105201812_error_log.txt'.
     * Hours are in 24h format, so each file will be unique after an hour.
     *
     *
     * @param dataToWrite the data to write can be any string data formatted or unformatted
     */
    fun overwriteToFile(dataToWrite: String) {

        Utils.getInstance().createDirIfNotExists(logPath)

        var fileName_raw = ""

        if (attachTimeStamp)
            fileName_raw = DateControl.getInstance().today + DateControl.getInstance().hour + "_" + logFileName
        else
            fileName_raw = logFileName

        val path_raw = logPath + File.separator + fileName_raw

        PLog.writeToFile(path_raw, dataToWrite)
    }

    /**
     * Append to file.
     *
     *
     *
     * This function will append a 'String' data to a file with new line inserted.
     * File will be created if it doesn't exists in path provided.
     * Filename can contain extension as well e.g 'error_log.txt'.
     * If 'attachTimeStamp' is true filename will contain date & hour in it like: '0105201812_error_log.txt'.
     * Hours are in 24h format, so each file will be unique after an hour.
     *
     *
     * @param dataToWrite the data to write can be any string data formatted or unformatted
     */
    fun appendToFile(dataToWrite: String) {

        Utils.getInstance().createDirIfNotExists(logPath)

        var fileName_raw = ""

        if (attachTimeStamp)
            fileName_raw = DateControl.getInstance().today + DateControl.getInstance().hour + "_" + logFileName
        else
            fileName_raw = logFileName

        val path_raw = logPath + File.separator + fileName_raw

        PLog.appendToFile(path_raw, dataToWrite)

    }

    /**
     * Clear logs boolean.
     *
     *
     *
     * Will return true if delete was successful
     *
     * @return the boolean
     */
    fun clearLogs(): Boolean {
        return Utils.getInstance().deleteDir(File(logPath))
    }

    companion object {

        private val TAG = DataLogger::class.java.simpleName
    }
}
