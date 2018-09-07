package com.blackbox.plog.pLogs.config

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

    //Log Types
    val logTypesEnabledElement = dom.createElement(LOG_TYPES_ENABLED_TAG)
    for (value in logsConfig.logTypesEnabled) {

        e = dom.createElement(TYPE_TAG)
        e!!.appendChild(dom.createTextNode(value))
        logTypesEnabledElement.appendChild(e)
    }
    rootEle.appendChild(logTypesEnabledElement)

    //Format Type
    val formatTypeElement = dom.createElement(FORMAT_TYPE_TAG)
    e = dom.createElement(TYPE_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.formatType.value))
    formatTypeElement.appendChild(e)
    rootEle.appendChild(formatTypeElement)

    //Logs Retention Days
    val logsRetainElement = dom.createElement(LOGS_RETENTION_TAG)
    e = dom.createElement(DAYS_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.logsRetentionPeriodInDays.toString()))
    logsRetainElement.appendChild(e)
    rootEle.appendChild(logsRetainElement)

    //Zip Retention Days
    val zipRetainElement = dom.createElement(ZIP_RETENTION_TAG)
    e = dom.createElement(DAYS_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.zipsRetentionPeriodInDays.toString()))
    zipRetainElement.appendChild(e)
    rootEle.appendChild(zipRetainElement)

    //Auto Clear Logs
    val autoClearElement = dom.createElement(AUTO_CLEAR_TAG)
    e = dom.createElement(VALUE_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.autoClearLogsOnExport.toString()))
    autoClearElement.appendChild(e)
    rootEle.appendChild(autoClearElement)

    //File Export Name
    val exportNameElement = dom.createElement(EXPORT_NAME_TAG)
    //Prefix
    e = dom.createElement(NAME_PREFIX_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.exportFileNamePreFix))
    exportNameElement.appendChild(e)
    //PostFix
    e = dom.createElement(NAME_POSTFIX_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.exportFileNamePostFix))
    exportNameElement.appendChild(e)
    rootEle.appendChild(exportNameElement)

    //Auto Export Error Logs
    val autoExportErrorElement = dom.createElement(AUTO_EXPORT_ERRORS_TAG)
    e = dom.createElement(VALUE_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.autoExportErrors.toString()))
    autoExportErrorElement.appendChild(e)
    rootEle.appendChild(autoExportErrorElement)

    //Encryption Enabled
    val encEnabledElement = dom.createElement(ENCRYPTION_ENABLED_TAG)
    e = dom.createElement(VALUE_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.encryptionEnabled.toString()))
    encEnabledElement.appendChild(e)
    rootEle.appendChild(encEnabledElement)

    //Encryption Key
    val encKeyElement = dom.createElement(ENCRYPTION_KEY_TAG)
    e = dom.createElement(VALUE_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.encryptionKey))
    encKeyElement.appendChild(e)
    rootEle.appendChild(encKeyElement)

    //Log file size MAX
    val logFileSizeElement = dom.createElement(LOG_FILE_SIZE_TAG)
    e = dom.createElement(VALUE_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.singleLogFileSize.toString()))
    logFileSizeElement.appendChild(e)
    rootEle.appendChild(logFileSizeElement)

    //Log files MAX
    val logFilesMaxElement = dom.createElement(LOG_FILES_MAX_TAG)
    e = dom.createElement(VALUE_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.logFilesLimit.toString()))
    logFilesMaxElement.appendChild(e)
    rootEle.appendChild(logFilesMaxElement)

    //Directory Structure
    val directoryElement = dom.createElement(DIRECTORY_TAG)
    e = dom.createElement(VALUE_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.directoryStructure.toString()))
    directoryElement.appendChild(e)
    rootEle.appendChild(directoryElement)

    //Log System Crashes
    val crashesElement = dom.createElement(LOG_SYSTEM_CRASHES_TAG)
    e = dom.createElement(VALUE_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.logSystemCrashes.toString()))
    crashesElement.appendChild(e)
    rootEle.appendChild(crashesElement)

    //Auto Export Types
    val autoExportTypesElement = dom.createElement(AUTO_EXPORT_TYPES_TAG)
    for (value in logsConfig.autoExportLogTypes) {

        e = dom.createElement(TYPE_TAG)
        e!!.appendChild(dom.createTextNode(value))
        autoExportTypesElement.appendChild(e)
    }
    rootEle.appendChild(autoExportTypesElement)

    //Auto Export Period
    val autoExportPeriodElement = dom.createElement(AUTO_EXPORT_PERIOD_TAG)
    e = dom.createElement(VALUE_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.autoExportLogTypesPeriod.toString()))
    autoExportPeriodElement.appendChild(e)
    rootEle.appendChild(autoExportPeriodElement)


    //Logs Delete date
    val logsDeleteDateElement = dom.createElement(LOGS_DELETE_DATE_TAG)
    e = dom.createElement(VALUE_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.logsDeleteDate))
    logsDeleteDateElement.appendChild(e)
    rootEle.appendChild(logsDeleteDateElement)

    //Zip Delete date
    val zipDeleteDateElement = dom.createElement(ZIP_DELETE_DATE_TAG)
    e = dom.createElement(VALUE_TAG)
    e!!.appendChild(dom.createTextNode(logsConfig.zipDeleteDate))
    zipDeleteDateElement.appendChild(e)
    rootEle.appendChild(zipDeleteDateElement)
}

internal fun updateValue(value: String, tag: String) {
    try {
        val filePath = XML_PATH
        val file = File(filePath)
        val docFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = docFactory.newDocumentBuilder()
        val doc = docBuilder.parse(file)

        // Change the content of node
        val nodes = doc.getElementsByTagName(tag).item(0)
        nodes.setTextContent(value)

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")

        // initialize StreamResult with File object to save to file
        val result = StreamResult(file)
        val source = DOMSource(doc)
        transformer.transform(source, result)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}