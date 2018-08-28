package com.blackbox.plog.dataLogs.models

import com.blackbox.plog.dataLogs.DataLogger

/**
 * Created by umair on 04/01/2018.
 */
class DataLogBuilder {

    var savePath: String = ""
    var exportPath: String = ""
    var logFileName: String = ""
    var exportFileName: String = ""
    var attachTimeStamp: Boolean = false
    var debug: Boolean = false

    /**
     * Sets logs save path.
     *
     *
     *
     * Should be a String Name. Can have file separators.
     *
     * @param path the path
     * @return the logs save path
     */
    fun setLogsSavePath(path: String): DataLogBuilder {
        this.savePath = path
        return this
    }

    /**
     * Sets logs export path.
     *
     *
     *
     * Should be a String Name. Can have file separators.
     *
     * @param path the path
     * @return the logs export path
     */
    fun setLogsExportPath(path: String): DataLogBuilder {
        this.exportPath = path
        return this
    }

    /**
     * Sets export file name.
     *
     *
     *
     * Should be a String name. like 'output_logs'
     * Don't add any extensions.
     * Default extension will be '.zip'.
     *
     *
     * @param fileName the file name
     * @return the export file name
     */
    fun setExportFileName(fileName: String): DataLogBuilder {
        this.exportFileName = fileName
        return this
    }

    /**
     * Sets log file name.
     *
     *
     *
     * Should be a String name. like 'location_logs.txt'
     *
     *
     * @param fileName the file name
     * @return the log file name
     */
    fun setLogFileName(fileName: String): DataLogBuilder {
        this.logFileName = fileName
        return this
    }

    /**
     * Attach time stamp to files log builder.
     *
     *
     *
     * Will attach time stamps to log and zip files.
     *
     * @param attach the attach
     * @return the p log builder
     */
    fun attachTimeStampToFiles(attach: Boolean): DataLogBuilder {
        this.attachTimeStamp = attach
        return this
    }

    /**
     * Debuggable p log builder.
     *
     *
     *
     * Prints debug logs.
     * Is true by default.
     *
     * @param debug the debug
     * @return the p log builder
     */
    fun debuggable(debug: Boolean): DataLogBuilder {
        this.debug = debug
        return this
    }

    fun build(): DataLogger {
        return DataLogger(savePath, exportPath, exportFileName, logFileName, attachTimeStamp, debug)
    }
}
