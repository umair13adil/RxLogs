package com.blackbox.plog.pLogs.operations

import com.blackbox.plog.dataLogs.DataLogger
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.LogEvents

internal fun doOnInit() {

    //Run only if Config file is set
    if (PLog.isLogsConfigSet()) {

        //Overwrite with XML data
        PLog.getLogsConfigFromXML()?.let {

            //If config XML is found, use those configurations Instead
            PLog.logsConfig = it

            //Send Event to notify that XML is loaded
            PLog.getLogBus().send(LogEvents.LOGS_CONFIG_FOUND)
        }

        //Create LogTypes for types Enabled
        createLogTypes()
    }
}

/*
 * This will create 'DataLogger' objects for all types defined in XML file.
 * These DataLogger files will be accessed through map Object.
 */
private fun createLogTypes() {

    val map = hashMapOf<String, DataLogger>()

    for (logType in PLog.logsConfig?.logTypesEnabled!!) {
        val logger = DataLogger(logFileName = logType)
        map[logType] = logger
    }

    //This will assign map of logTypes to 'PLog' logTypes.
    PLog.logTypes = map
}