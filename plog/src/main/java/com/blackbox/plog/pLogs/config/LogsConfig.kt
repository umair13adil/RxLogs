package com.blackbox.plog.pLogs.config

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.formatter.FormatType
import com.blackbox.plog.pLogs.formatter.TimeStampFormat
import com.blackbox.plog.pLogs.models.LogExtension
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.operations.Triggers
import com.blackbox.plog.pLogs.structure.DirectoryStructure
import io.reactivex.Observable
import javax.crypto.SecretKey

/*
 * This object contains all global configurations for 'PLogger' & 'DataLogger'.
 * If configurations are written to an XML on storage then all configurations will be read from storage
  * instead of what's defined in 'Application' class.
 */
data class LogsConfig(
        var isDebuggable: Boolean = true, //Debug for LogCat
        var debugFileOperations: Boolean = false, //Debug for File operations
        var forceWriteLogs: Boolean = true, //Forcefully write logs even if size exceeds
        var enableLogsWriteToFile: Boolean = true, //Logs will be written to Storage Directory if 'true' otherwise just be printed in LogCat
        var logLevelsEnabled: ArrayList<LogLevel> = arrayListOf<LogLevel>(), //Levels like Info, Warning, Error, Severe
        var logTypesEnabled: ArrayList<String> = arrayListOf<String>(),
        var formatType: FormatType = FormatType.FORMAT_CURLY, //Default format of '{String}' log data
        var attachTimeStamp: Boolean = false, //Should append 'TimeStamp' to export file name
        var attachNoOfFiles: Boolean = false, //Should append 'Size Of Logs Files' to export file name
        var timeStampFormat: String = TimeStampFormat.DATE_FORMAT_1, //TimeStamp format
        var logFileExtension: String = LogExtension.TXT, //Extension of Log Files
        var customFormatOpen: String = " ", //For log field open
        var customFormatClose: String = " ", //For log field close
        var logsRetentionPeriodInDays: Int = 0,
        var zipsRetentionPeriodInDays: Int = 0,
        var autoDeleteZipOnExport: Boolean = false, //If set true, then exported files will be cleared on each export
        var autoClearLogs: Boolean = false, //If set true, then exported files will be cleared on each export
        var zipFileName: String = "Output", //Custom name of ZIP file
        var exportFileNamePostFix: String = "",
        var exportFileNamePreFix: String = "",
        var zipFilesOnly: Boolean = true, //Only log files will be zipped, no folders
        var autoExportErrors: Boolean = true,
        var encryptionEnabled: Boolean = false, //Encryption enabled
        var encryptionKey: String = "", //Encryption Key
        var singleLogFileSize: Int = 2, //2Mb
        var logFilesLimit: Int = 100, //Max number of log files
        var directoryStructure: DirectoryStructure = DirectoryStructure.FOR_DATE, //Default Directory Structure
        var nameForEventDirectory: String = "", //Name of directory in case of 'DirectoryStructure.FOR_EVENT'
        var logSystemCrashes: Boolean = false,
        var autoExportLogTypes: ArrayList<String> = arrayListOf<String>(),
        var autoExportLogTypesPeriod: Int = 0,
        var savePath: String = "PLogs", //Path where log files will be created
        var exportPath: String = "PLogs", //Path where log files will be exported as zip
        var csvDelimiter: String = "", //Delimiter for CSV files
        var exportFormatted: Boolean? = true
) {

    val TAG = "LogsConfig"

    fun doOnSetup() {

        //Validate Configurations
        validateConfigurations()

        //Check if Logs need to be cleared, after 5 seconds delay
        //Only run if logs are enabled
        if (enableLogsWriteToFile) {
            Triggers.shouldClearLogs()
            Triggers.shouldClearExports()
        }
    }

    internal var secretKey: SecretKey? = null//SecretKey for Encryption

    /**
     * Returns Observable event listener for logger.
     */
    fun getLogEventsListener(): Observable<LogEvents> {
        return PLog.getLogEvents()
    }

    /**
     *
     * All log files will be logged in a directory named by 'Event'.
     * To change event name, simply pass new event to the builder.
     *
     * @param name name of directory to be created
     */
    fun setEventNameForDirectory(name: String) {
        nameForEventDirectory = name
    }

    /*
     * Validate configurations provided.
     */
    private fun validateConfigurations() {

        //Only validate if logs are enabled
        if (enableLogsWriteToFile) {

            //Check Directory Structure
            if (directoryStructure == DirectoryStructure.FOR_EVENT && enableLogsWriteToFile) {
                if (nameForEventDirectory.isEmpty()) {
                    throw Exception(Throwable("Name for event must be provided. Set name using this method 'PLogger.setEventNameForDirectory(name)' or set in" +
                            "'PLogBuilder().also {it.setEventNameForDirectory()}'"))
                }
            }

            if (logsRetentionPeriodInDays < 1 && autoClearLogs) {
                throw Exception(Throwable("'logsRetentionPeriodInDays=$logsRetentionPeriodInDays' can not be less than 1!"))
            }

            if (zipsRetentionPeriodInDays < 1 && autoDeleteZipOnExport) {
                throw Exception(Throwable("'zipsRetentionPeriodInDays=$zipsRetentionPeriodInDays' can not be less than 1!"))
            }
        }
    }
}
