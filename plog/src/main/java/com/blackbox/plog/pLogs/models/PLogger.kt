package com.blackbox.plog.pLogs.models

import android.os.Environment
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.formatter.FormatType
import com.blackbox.plog.pLogs.formatter.TimeStampFormat
import com.blackbox.plog.pLogs.structure.DirectoryStructure
import com.blackbox.plog.utils.checkIfKeyValid
import com.blackbox.plog.utils.generateKey
import io.reactivex.Observable
import java.io.File
import javax.crypto.SecretKey

/**
 * Created by umair on 03/01/2018.
 */

class PLogger(
        var savePath: String = Environment.getExternalStorageDirectory().toString() + File.separator + TAG, //Path where log files will be created
        var exportPath: String = Environment.getExternalStorageDirectory().toString() + File.separator + TAG, //Path where log files will be exported as zip
        var logFileExtension: LogExtension = LogExtension.TXT, //Extension of Log Files
        var zipFileName: String = "Output", //Custom name of ZIP file
        var attachTimeStamp: Boolean = false, //Should append 'TimeStamp' to export file name
        var attachNoOfFiles: Boolean = false, //Should append 'Size Of Logs Files' to export file name
        var isDebuggable: Boolean = false, //Debug for LogCat
        var silentLogs: Boolean = false, //Log without printing in LogCat
        var formatType: FormatType = FormatType.FORMAT_CURLY, //Default format of 'String' log data
        var customFormatOpen: String = " ", //For log field open
        var customFormatClose: String = " ", //For log field close
        var csvDeliminator: String = "", //Deliminator for CSV files
        var timeStampFormat: TimeStampFormat = TimeStampFormat.DATE_FORMAT_1, //TimeStamp format
        var encrypt: Boolean = false, //Encryption enabled
        var encryptionKey: String = "", //Encryption Key
        var enabled: Boolean = true, //Logs are enabled
        var directoryStructure: DirectoryStructure = DirectoryStructure.FOR_DATE, //Default Directory Structure
        var nameForEventDirectory: String = "", //Name of directory in case of 'DirectoryStructure.FOR_EVENT'
        var zipFilesOnly: Boolean = true //Only log files will be zipped, no folders
) {

    companion object {
        private val TAG = "PLogger"
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

    init {
        PLog.setPLogger(this)

        //Initializes Encryption Key
        setupEncryption()

        validateData()
    }

    private fun setupEncryption() {

        if (encrypt) {
            val key = checkIfKeyValid(encryptionKey)
            secretKey = generateKey(key)
        }
    }

    private fun validateData() {
        if (directoryStructure == DirectoryStructure.FOR_EVENT) {
            if (nameForEventDirectory.isEmpty()) {
                throw Exception(Throwable("Name for event must be provided. Set name using this method 'PLogger.setEventNameForDirectory(name)'\nor set in" +
                        "'PLogBuilder().also {it.setEventNameForDirectory()}'"))
            }
        }
    }
}
