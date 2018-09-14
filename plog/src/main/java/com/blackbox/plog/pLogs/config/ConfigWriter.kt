package com.blackbox.plog.pLogs.config

import com.blackbox.plog.pLogs.utils.CONFIG_FILE_NAME
import com.blackbox.plog.pLogs.utils.XML_PATH
import org.w3c.dom.Document
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

object ConfigWriter {

    fun saveToXML(logsConfig: LogsConfig) {
        val dom: Document

        // instance of a DocumentBuilderFactory
        val dbf = DocumentBuilderFactory.newInstance()
        try {
            // use factory to get an instance of document builder
            val db = dbf.newDocumentBuilder()
            // create instance of DOM
            dom = db.newDocument()

            // create the root element
            val rootEle = dom.createElement(ROOT_TAG)
            rootEle.setAttribute(IS_DEBUGGABLE_ATTR, logsConfig.isDebuggable.toString())
            rootEle.setAttribute(ENABLED_ATTR, logsConfig.enabled.toString())

            dataToWrite(dom, logsConfig, rootEle)

            dom.appendChild(rootEle)

            try {
                val tr = TransformerFactory.newInstance().newTransformer()
                tr.setOutputProperty(OutputKeys.INDENT, "yes")
                tr.setOutputProperty(OutputKeys.METHOD, "xml")
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
                tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd")
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")

                // send DOM to file
                tr.transform(DOMSource(dom), StreamResult(FileOutputStream(File(XML_PATH, CONFIG_FILE_NAME).path)))

            } catch (te: TransformerException) {
                println(te.message)
            } catch (ioe: IOException) {
                println(ioe.message)
            }

        } catch (pce: ParserConfigurationException) {
            println("UsersXML: Error trying to instantiate DocumentBuilder $pce")
        }

    }
}
