package com.blackbox.plog.pLogs.config

import android.os.Environment
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.formatter.FormatType
import com.blackbox.plog.pLogs.formatter.TimeStampFormat
import com.blackbox.plog.pLogs.impl.PLogImpl
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
data class LogsConfig(
        var isDebuggable: Boolean = true, //Debug for LogCat
        var debugFileOperations: Boolean = false, //Debug for File operations
        var forceWriteLogs: Boolean = true, //Forcefully write logs even if size exceeds
        var enabled: Boolean = true, //Logs are enabled
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
        var logsDeleteDate: Long = 0L, //Last Time logs were cleared
        var zipDeleteDate: Long = 0L, //Last Time exported zip files were cleared
        var exportStartDate: String = "", //Last Time auto-export was triggered
        var savePath: String = Environment.getExternalStorageDirectory().toString() + File.separator + "PLogs", //Path where log files will be created
        var exportPath: String = Environment.getExternalStorageDirectory().toString() + File.separator + "PLogs", //Path where log files will be exported as zip
        var csvDelimiter: String = "", //Delimiter for CSV files
        var exportFormatted: Boolean? = true
) {

    val TAG = "LogsConfig"

    fun doOnSetup(saveToFile: Boolean = false) {

        //Initializes Encryption Key
        setupEncryption()

        //Validate Configurations
        validateConfigurations(saveToFile)

        //Check if Logs need to be cleared, after 5 seconds delay
        //Only run if logs are enabled
        if (enabled) {
            Triggers.shouldClearLogs()
            Triggers.shouldClearExports()
        }
    }

    /*
     * This will update value of date in XML for specified TAG.
     */
    fun updateDateForTAG(date: String, tag: String) {

        if (PLog.localConfigurationExists()) {
            updateValue(date, tag)

            //Reload configurations from XML file
            PLog.getLogsConfigFromXML()?.let {
                PLogImpl.saveConfig(it)
            }

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
            PLogImpl.getConfig()?.secretKey = secretKey
        }
    }

    /*
     * Validate configurations provided.
     */
    private fun validateConfigurations(saveToFile: Boolean = false) {

        //Only validate if logs are enabled
        if (enabled) {

            //Check Directory Structure
            if (directoryStructure == DirectoryStructure.FOR_EVENT && enabled) {
                if (nameForEventDirectory.isEmpty()) {
                    throw Exception(Throwable("Name for event must be provided. Set name using this method 'PLogger.setEventNameForDirectory(name)' or set in" +
                            "'PLogBuilder().also {it.setEventNameForDirectory()}'"))
                }
            }

            if (logsRetentionPeriodInDays > 0 && !saveToFile) {
                throw Exception(Throwable("'logsRetentionPeriodInDays=$logsRetentionPeriodInDays' is set without saving local XML config file. To use 'Auto Clear' logs feature, local XML configuration must be enabled using: " +
                        "'PLog.applyConfigurations(logsConfig, saveToFile = true)' or use 'PLog.forceWriteLogsConfig(logsConfig)'"))
            }

            if (zipsRetentionPeriodInDays > 0 && !saveToFile) {
                throw Exception(Throwable("'zipsRetentionPeriodInDays=$zipsRetentionPeriodInDays' is set without saving local XML config file. To use 'Auto Clear' logs feature, local XML configuration must be enabled using: " +
                        "'PLog.applyConfigurations(logsConfig, saveToFile = true)' or use 'PLog.forceWriteLogsConfig(logsConfig)'"))
            }

            if (autoDeleteZipOnExport && !saveToFile) {
                throw Exception(Throwable("'autoDeleteZipOnExport=$autoDeleteZipOnExport' is set without saving local XML config file. To use 'Auto Clear' logs feature, local XML configuration must be enabled using: " +
                        "'PLog.applyConfigurations(logsConfig, saveToFile = true)' or use 'PLog.forceWriteLogsConfig(logsConfig)'"))
            }

            if (logsRetentionPeriodInDays < 1 && autoClearLogs && !saveToFile) {
                throw Exception(Throwable("'logsRetentionPeriodInDays=$logsRetentionPeriodInDays' can not be less than 1!"))
            }

            if (zipsRetentionPeriodInDays < 1 && autoDeleteZipOnExport && !saveToFile) {
                throw Exception(Throwable("'zipsRetentionPeriodInDays=$zipsRetentionPeriodInDays' can not be less than 1!"))
            }
        }
    }
}