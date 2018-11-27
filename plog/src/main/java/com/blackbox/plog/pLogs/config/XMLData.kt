package com.blackbox.plog.pLogs.config

import com.blackbox.plog.pLogs.utils.CONFIG_FILE_NAME
import com.blackbox.plog.pLogs.utils.XML_PATH
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

internal fun dataToWrite(dom: Document, logsConfig: LogsConfig, rootEle: Element) {
    var e: Element? = null

    //Log Levels
    val logLevelsEnabledElement = dom.createElement(LOG_LEVELS_ENABLED_TAG)
    for (value in logsConfig.logLevelsEnabled) {

        e = dom.createElement(VALUE_ATTR)
        e!!.appendChild(dom.createTextNode(value.level))
        logLevelsEnabledElement.appendChild(e)
    }
    rootEle.appendChild(logLevelsEnabledElement)

    //Log Types
    val logTypesEnabledElement = dom.createElement(LOG_TYPES_ENABLED_TAG)
    for (value in logsConfig.logTypesEnabled) {

        e = dom.createElement(VALUE_ATTR)
        e!!.appendChild(dom.createTextNode(value))
        logTypesEnabledElement.appendChild(e)
    }
    rootEle.appendChild(logTypesEnabledElement)

    //Format Type
    val formatTypeElement = dom.createElement(FORMAT_TYPE_TAG)
    formatTypeElement.setAttribute(VALUE_ATTR, logsConfig.formatType.value)
    formatTypeElement.setAttribute(ATTACH_TIME_STAMPS_ATTR, logsConfig.attachTimeStamp.toString())
    formatTypeElement.setAttribute(ATTACH_NO_OF_FILES_ATTR, logsConfig.attachNoOfFiles.toString())
    formatTypeElement.setAttribute(TIME_STAMP_FORMAT_ATTR, logsConfig.timeStampFormat)
    formatTypeElement.setAttribute(LOG_FILE_EXT_ATTR, logsConfig.logFileExtension)
    formatTypeElement.setAttribute(FORMAT_CUSTOM_OPEN_ATTR, logsConfig.customFormatOpen)
    formatTypeElement.setAttribute(FORMAT_CUSTOM_CLOSE_ATTR, logsConfig.customFormatClose)
    rootEle.appendChild(formatTypeElement)

    //Logs Retention Days
    val logsRetainElement = dom.createElement(LOGS_RETENTION_TAG)
    logsRetainElement.setAttribute(VALUE_ATTR, logsConfig.logsRetentionPeriodInDays.toString())
    rootEle.appendChild(logsRetainElement)

    //Zip Retention Days
    val zipRetainElement = dom.createElement(ZIP_RETENTION_TAG)
    zipRetainElement.setAttribute(VALUE_ATTR, logsConfig.zipsRetentionPeriodInDays.toString())
    rootEle.appendChild(zipRetainElement)

    //Auto Clear Logs
    val autoClearElement = dom.createElement(AUTO_CLEAR_TAG)
    autoClearElement.setAttribute(VALUE_ATTR, logsConfig.autoClearLogsOnExport.toString())
    rootEle.appendChild(autoClearElement)

    //File Export Name
    val exportNameElement = dom.createElement(EXPORT_TAG)
    exportNameElement.setAttribute(ZIP_FILE_NAME_ATTR, logsConfig.zipFileName)
    exportNameElement.setAttribute(NAME_PREFIX_ATTR, logsConfig.exportFileNamePreFix)
    exportNameElement.setAttribute(NAME_POSTFIX_ATTR, logsConfig.exportFileNamePostFix)
    exportNameElement.setAttribute(ZIP_FILES_ATTR, logsConfig.zipFilesOnly.toString())
    rootEle.appendChild(exportNameElement)

    //Auto Export Error Logs
    val autoExportErrorElement = dom.createElement(AUTO_EXPORT_ERRORS_TAG)
    autoExportErrorElement.setAttribute(VALUE_ATTR, logsConfig.autoExportErrors.toString())
    rootEle.appendChild(autoExportErrorElement)

    //Encryption Enabled
    val encEnabledElement = dom.createElement(ENCRYPTION_ENABLED_TAG)
    encEnabledElement.setAttribute(VALUE_ATTR, logsConfig.encryptionEnabled.toString())
    encEnabledElement.setAttribute(ENCRYPTION_KEY_ATTR, logsConfig.encryptionKey)
    rootEle.appendChild(encEnabledElement)

    //Log file size MAX
    val logFileSizeElement = dom.createElement(LOG_FILE_SIZE_TAG)
    logFileSizeElement.setAttribute(VALUE_ATTR, logsConfig.singleLogFileSize.toString())
    rootEle.appendChild(logFileSizeElement)

    //Log files MAX
    val logFilesMaxElement = dom.createElement(LOG_FILES_MAX_TAG)
    logFilesMaxElement.setAttribute(VALUE_ATTR, logsConfig.logFilesLimit.toString())
    rootEle.appendChild(logFilesMaxElement)

    //Directory Structure
    val directoryElement = dom.createElement(DIRECTORY_TAG)
    directoryElement.setAttribute(VALUE_ATTR, logsConfig.directoryStructure.toString())
    directoryElement.setAttribute(NAME_FOR_EVENT_DIR_ATTR, logsConfig.nameForEventDirectory)
    rootEle.appendChild(directoryElement)

    //Log System Crashes
    val crashesElement = dom.createElement(LOG_SYSTEM_CRASHES_TAG)
    crashesElement.setAttribute(VALUE_ATTR, logsConfig.logSystemCrashes.toString())
    rootEle.appendChild(crashesElement)

    //Auto Export Types
    val autoExportTypesElement = dom.createElement(AUTO_EXPORT_TYPES_TAG)
    autoExportTypesElement.setAttribute(AUTO_EXPORT_PERIOD_ATTR, logsConfig.autoExportLogTypesPeriod.toString())
    for (value in logsConfig.autoExportLogTypes) {

        e = dom.createElement(VALUE_ATTR)
        e!!.appendChild(dom.createTextNode(value))
        autoExportTypesElement.appendChild(e)
    }
    rootEle.appendChild(autoExportTypesElement)

    //Logs Delete date
    val logsDeleteDateElement = dom.createElement(LOGS_DELETE_DATE_TAG)
    rootEle.appendChild(logsDeleteDateElement)

    //Zip Delete date
    val zipDeleteDateElement = dom.createElement(ZIP_DELETE_DATE_TAG)
    rootEle.appendChild(zipDeleteDateElement)

    //Auto Export Start date
    val exportStartDateElement = dom.createElement(EXPORT_START_DATE_TAG)
    rootEle.appendChild(exportStartDateElement)

    //Save Path for Log Files
    val logsSavePathElement = dom.createElement(LOGS_SAVE_PATH_TAG)
    logsSavePathElement.setAttribute(VALUE_ATTR, logsConfig.savePath)
    rootEle.appendChild(logsSavePathElement)

    //Save Path for Log Files
    val exportPathElement = dom.createElement(LOGS_EXPORT_PATH_TAG)
    exportPathElement.setAttribute(VALUE_ATTR, logsConfig.exportPath)
    rootEle.appendChild(exportPathElement)

    //File Export Name
    val csvElement = dom.createElement(CSV_TAG)
    csvElement.setAttribute(CSV_DELIMITER_ATTR, logsConfig.csvDelimiter)
    rootEle.appendChild(csvElement)
}

internal fun updateValue(value: String, tag: String) {
    try {

        val file = File(XML_PATH, CONFIG_FILE_NAME)
        val docFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = docFactory.newDocumentBuilder()
        val doc = docBuilder.parse(file)

        // Change the content of node
        val nodes = doc.getElementsByTagName(tag).item(0)
        nodes.textContent = value

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")

        // initialize StreamResult with File object to save to file
        val result = StreamResult(File(XML_PATH, CONFIG_FILE_NAME).path)
        val source = DOMSource(doc)
        transformer.transform(source, result)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}