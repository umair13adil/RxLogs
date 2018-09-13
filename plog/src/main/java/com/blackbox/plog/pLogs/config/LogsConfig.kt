package com.blackbox.plog.pLogs.config

import android.os.Environment
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.formatter.FormatType
import com.blackbox.plog.pLogs.formatter.TimeStampFormat
import com.blackbox.plog.pLogs.models.LogExtension
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.operations.Triggers
import com.blackbox.plog.pLogs.structure.DirectoryStructure
import com.blackbox.plog.utils.checkIfKeyValid
import com.blackbox.plog.utils.generateKey
import io.reactivex.Observable
import java.io.File
import javax.crypto.SecretKey

/*
 * This object contains all global configurations for 'PLogger' & 'DataLogger'.
 * If configurations are written to an XML on storage then all configurations will be read from storage
  * instead of what's defined in 'Application' class.
 */
class LogsConfig(
        var isDebuggable: Boolean = false, //Debug for LogCat
        var enabled: Boolean = true, //Logs are enabled
        var logLevelsEnabled: ArrayList<LogLevel> = arrayListOf<LogLevel>(), //Levels like Info, Warning, Error, Severe
        var logTypesEnabled: ArrayList<String> = arrayListOf<String>(),
        var formatType: FormatType = FormatType.FORMAT_CURLY, //Default format of '{String}' log data
        var attachTimeStamp: Boolean = false, //Should append 'TimeStamp' to export file name
        var attachNoOfFiles: Boolean = false, //Should append 'Size Of Logs Files' to export file name
        var timeStampFormat: TimeStampFormat = TimeStampFormat.DATE_FORMAT_1, //TimeStamp format
        var logFileExtension: LogExtension = LogExtension.TXT, //Extension of Log Files
        var customFormatOpen: String = " ", //For log field open
        var customFormatClose: String = " ", //For log field close
        var logsRetentionPeriodInDays: Int = 0,
        var zipsRetentionPeriodInDays: Int = 0,
        var autoClearLogsOnExport: Boolean = false, //If set true, then exported files will be cleared on each export
        var zipFileName: String = "Output", //Custom name of ZIP file
        var exportFileNamePostFix: String = "",
        var exportFileNamePreFix: String = "",
        var zipFilesOnly: Boolean = true, //Only log files will be zipped, no folders
        var autoExportErrors: Boolean = true,
        var encryptionEnabled: Boolean = false, //Encryption enabled
        var encryptionKey: String = "", //Encryption Key
        var singleLogFileSize: Int = 2048 * 2, //Size in MBs (4Mb)
        var logFilesLimit: Int = 100, //Max number of log files
        var directoryStructure: DirectoryStructure = DirectoryStructure.FOR_DATE, //Default Directory Structure
        var nameForEventDirectory: String = "", //Name of directory in case of 'DirectoryStructure.FOR_EVENT'
        var logSystemCrashes: Boolean = false,
        var autoExportLogTypes: ArrayList<String> = arrayListOf<String>(),
        var autoExportLogTypesPeriod: Int = 0,
        var logsDeleteDate: String = "", //Last Time logs were cleared
        var zipDeleteDate: String = "", //Last Time exported zip files were cleared
        var exportStartDate: String = "", //Last Time auto-export was triggered
        var savePath: String = Environment.getExternalStorageDirectory().toString() + File.separator + "PLogs", //Path where log files will be created
        var exportPath: String = Environment.getExternalStorageDirectory().toString() + File.separator + "PLogs", //Path where log files will be exported as zip
        var csvDelimiter: String = "" //Delimiter for CSV files
) {

    val TAG = "LogsConfig"

    fun doOnSetup() {

        //Initializes Encryption Key
        setupEncryption()

        validateConfigurations()

        //Check if Logs need to be cleared
        Triggers.shouldClearLogs()
        Triggers.shouldClearExports()
    }

    /*
     * This will update value of date in XML for specified TAG.
     */
    fun updateDateForTAG(date: String, tag: String) {

        if (PLog.localConfigurationExists()) {
            updateValue(date, tag)

            //Reload configurations from XML file
            PLog.getLogsConfigFromXML()
        } else
            throw Exception(Throwable("No local XML configuration file found!"))
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
     * Validate & setup encryption.
     */
    private fun setupEncryption() {

        if (encryptionEnabled && enabled) {
            val key = checkIfKeyValid(encryptionKey)
            secretKey = generateKey(key)
        }
    }

    /*
     * Validate configurations provided.
     */
    private fun validateConfigurations() {

        //Check Directory Structure
        if (directoryStructure == DirectoryStructure.FOR_EVENT && enabled) {
            if (nameForEventDirectory.isEmpty()) {
                throw Exception(Throwable("Name for event must be provided. Set name using this method 'PLogger.setEventNameForDirectory(name)'\nor set in" +
                        "'PLogBuilder().also {it.setEventNameForDirectory()}'"))
            }
        }
    }
}
