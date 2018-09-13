package com.blackbox.plog.pLogs.config

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.formatter.FormatType
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.structure.DirectoryStructure
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import javax.xml.parsers.SAXParserFactory


object ConfigReader {

    val TAG = "ConfigReader"

    fun readXML(): LogsConfig {

        val logsConfig = LogsConfig()

        val inputStream = File(XML_PATH).inputStream()

        try {
            val factory = SAXParserFactory.newInstance()
            val saxParser = factory.newSAXParser()

            val handler = object : DefaultHandler() {

                //SAX Parser Variables
                var currentValue = ""
                var currentElement = false
                var logTypesTag = false
                var logLevelsTag = false
                var autoExportlogTypesTag = false

                // This method is invoked when start parse xml element node use sax parser.
                @Throws(SAXException::class)
                override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {

                    currentElement = true
                    currentValue = ""

                    when (localName) {

                        ROOT_TAG -> {
                            logsConfig.isDebuggable = readAttribute(attributes, IS_DEBUGGABLE_ATTR).toBoolean()
                            logsConfig.enabled = readAttribute(attributes, ENABLED_ATTR).toBoolean()
                        }

                        LOG_LEVELS_ENABLED_TAG -> {
                            logLevelsTag = true
                            logTypesTag = false
                            autoExportlogTypesTag = false
                        }

                        LOG_TYPES_ENABLED_TAG -> {
                            logLevelsTag = false
                            logTypesTag = true
                            autoExportlogTypesTag = false
                        }

                        FORMAT_TYPE_TAG -> {
                            logsConfig.formatType = FormatType.valueOf(readAttribute(attributes, VALUE_ATTR))
                            logsConfig.attachTimeStamp = readAttribute(attributes, ATTACH_TIME_STAMPS_ATTR).toBoolean()
                            logsConfig.attachNoOfFiles = readAttribute(attributes, ATTACH_NO_OF_FILES_ATTR).toBoolean()
                            //TODO Add TimeStampFormat
                            //TODO Add Extensions
                            logsConfig.customFormatOpen = readAttribute(attributes, FORMAT_CUSTOM_OPEN_ATTR)
                            logsConfig.customFormatClose = readAttribute(attributes, FORMAT_CUSTOM_CLOSE_ATTR)
                        }

                        LOGS_RETENTION_TAG -> {
                            logsConfig.logsRetentionPeriodInDays = readAttribute(attributes, VALUE_ATTR).toInt()
                        }

                        ZIP_RETENTION_TAG -> {
                            logsConfig.zipsRetentionPeriodInDays = readAttribute(attributes, VALUE_ATTR).toInt()
                        }

                        AUTO_CLEAR_TAG -> {
                            logsConfig.autoClearLogsOnExport = readAttribute(attributes, VALUE_ATTR).toBoolean()
                        }

                        EXPORT_TAG -> {
                            logsConfig.exportFileNamePreFix = readAttribute(attributes, NAME_PREFIX_ATTR)
                            logsConfig.exportFileNamePostFix = readAttribute(attributes, NAME_POSTFIX_ATTR)
                            logsConfig.zipFileName = readAttribute(attributes, ZIP_FILE_NAME_ATTR)
                            logsConfig.zipFilesOnly = readAttribute(attributes, ZIP_FILES_ATTR).toBoolean()
                        }

                        AUTO_EXPORT_ERRORS_TAG -> {
                            logsConfig.autoExportErrors = readAttribute(attributes, VALUE_ATTR).toBoolean()
                        }

                        ENCRYPTION_ENABLED_TAG -> {
                            logsConfig.encryptionEnabled = readAttribute(attributes, VALUE_ATTR).toBoolean()
                            logsConfig.encryptionKey = readAttribute(attributes, ENCRYPTION_KEY_ATTR)
                        }

                        LOG_FILE_SIZE_TAG -> {
                            logsConfig.singleLogFileSize = readAttribute(attributes, VALUE_ATTR).toInt()
                        }

                        LOG_FILES_MAX_TAG -> {
                            logsConfig.logFilesLimit = readAttribute(attributes, VALUE_ATTR).toInt()
                        }

                        DIRECTORY_TAG -> {
                            logsConfig.directoryStructure = DirectoryStructure.valueOf(readAttribute(attributes, VALUE_ATTR))
                            logsConfig.nameForEventDirectory = readAttribute(attributes, NAME_FOR_EVENT_DIR_ATTR)
                        }

                        LOG_SYSTEM_CRASHES_TAG -> {
                            logsConfig.logSystemCrashes = readAttribute(attributes, VALUE_ATTR).toBoolean()
                        }

                        AUTO_EXPORT_TYPES_TAG -> {
                            logLevelsTag = false
                            logTypesTag = false
                            autoExportlogTypesTag = true
                            logsConfig.autoExportLogTypesPeriod = readAttribute(attributes, AUTO_EXPORT_PERIOD_ATTR).toInt()
                        }

                        LOGS_SAVE_PATH_TAG -> {
                            logsConfig.savePath = readAttribute(attributes, VALUE_ATTR)
                        }

                        LOGS_EXPORT_PATH_TAG -> {
                            logsConfig.exportPath = readAttribute(attributes, VALUE_ATTR)
                        }

                        CSV_TAG -> {
                            logsConfig.csvDelimiter = readAttribute(attributes, CSV_DELIMITER_ATTR)
                        }
                    }
                }

                // When sax parse xml element node finished, invoke this method.
                @Throws(SAXException::class)
                override fun endElement(uri: String, localName: String, qName: String) {
                    currentElement = false

                    when (localName) {

                        VALUE_ATTR -> {

                            if (logLevelsTag) {
                                logsConfig.logLevelsEnabled.add(LogLevel.valueOf(currentValue))
                            }

                            if (logTypesTag)
                                logsConfig.logTypesEnabled.add(currentValue)

                            if (autoExportlogTypesTag)
                                logsConfig.autoExportLogTypes.add(currentValue)
                        }

                        LOGS_DELETE_DATE_TAG -> {
                            val logsDeleteTime = currentValue
                            logsConfig.logsDeleteDate = logsDeleteTime
                        }

                        ZIP_DELETE_DATE_TAG -> {
                            val zipDeleteTime = currentValue
                            logsConfig.zipDeleteDate = zipDeleteTime
                        }

                        EXPORT_START_DATE_TAG -> {
                            val startDate = currentValue
                            logsConfig.exportStartDate = startDate
                        }

                    }
                }

                // This method is invoked when sax parser parse xml node text.
                @Throws(SAXException::class)
                override fun characters(ch: CharArray, start: Int, length: Int) {
                    if (currentElement) {
                        currentValue = String(ch, start, length)
                        currentElement = false
                    }
                }

            }

            saxParser.parse(inputStream, handler)
        } catch (e: Exception) {
            e.printStackTrace()

            //Delete configuration file
            PLog.deleteLocalConfiguration()

        } finally {
            inputStream.close()
        }

        return logsConfig
    }
}

fun readAttribute(attributes: Attributes?, name: String?): String {
    if (name != null && attributes != null) {
        return attributes.getValue(name)
    } else {
        return ""
    }
}