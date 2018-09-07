package com.blackbox.plog.pLogs.config

import com.blackbox.plog.pLogs.formatter.FormatType
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
                var autoExportlogTypesTag = false

                // This method is invoked when start parse xml element node use sax parser.
                @Throws(SAXException::class)
                override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {

                    currentElement = true
                    currentValue = ""

                    when (localName) {

                        LOG_TYPES_ENABLED_TAG -> {
                            logTypesTag = true
                        }

                        FORMAT_TYPE_TAG -> {
                            logTypesTag = false
                        }

                        AUTO_EXPORT_TYPES_TAG -> {
                            autoExportlogTypesTag = true
                        }
                    }
                }

                // When sax parse xml element node finished, invoke this method.
                @Throws(SAXException::class)
                override fun endElement(uri: String, localName: String, qName: String) {
                    currentElement = false

                    when (localName) {

                        TYPE_TAG -> {

                            if (logTypesTag)
                                logsConfig.logTypesEnabled.add(currentValue)

                            if (autoExportlogTypesTag)
                                logsConfig.autoExportLogTypes.add(currentValue)
                        }

                        FORMAT_TYPE_TAG -> {
                            logsConfig.formatType = FormatType.valueOf(currentValue)
                        }

                        LOGS_RETENTION_TAG -> {
                            logsConfig.logsRetentionPeriodInDays = currentValue.toInt()
                        }

                        ZIP_RETENTION_TAG -> {
                            logsConfig.zipsRetentionPeriodInDays = currentValue.toInt()
                        }

                        AUTO_CLEAR_TAG -> {
                            logsConfig.autoClearLogsOnExport = currentValue.toBoolean()
                        }

                        NAME_PREFIX_TAG -> {
                            logsConfig.exportFileNamePreFix = currentValue
                        }

                        NAME_POSTFIX_TAG -> {
                            logsConfig.exportFileNamePostFix = currentValue
                        }

                        AUTO_EXPORT_ERRORS_TAG -> {
                            logsConfig.autoExportErrors = currentValue.toBoolean()
                        }

                        ENCRYPTION_ENABLED_TAG -> {
                            logsConfig.encryptionEnabled = currentValue.toBoolean()
                        }

                        ENCRYPTION_KEY_TAG -> {
                            logsConfig.encryptionKey = currentValue
                        }

                        LOG_FILE_SIZE_TAG -> {
                            logsConfig.singleLogFileSize = currentValue.toInt()
                        }

                        LOG_FILES_MAX_TAG -> {
                            logsConfig.logFilesLimit = currentValue.toInt()
                        }

                        DIRECTORY_TAG -> {
                            logsConfig.directoryStructure = DirectoryStructure.valueOf(currentValue)
                        }

                        LOG_SYSTEM_CRASHES_TAG -> {
                            logsConfig.logSystemCrashes = currentValue.toBoolean()
                        }

                        AUTO_EXPORT_PERIOD_TAG -> {
                            logsConfig.autoExportLogTypesPeriod = currentValue.toInt()
                        }

                        LOGS_DELETE_DATE_TAG -> {
                            logsConfig.logsDeleteDate = currentValue
                        }

                        ZIP_DELETE_DATE_TAG -> {
                            logsConfig.zipDeleteDate = currentValue
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
        } finally {
            inputStream.close()
        }

        return logsConfig
    }
}