package com.blackbox.plog.pLogs.operations

import com.blackbox.plog.dataLogs.DataLogger
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.impl.PLogImpl

internal fun doOnInit() {

    //Run only if Config file is set
    if (PLog.isLogsConfigSet()) {

        //Overwrite with XML data, Send Event to notify that XML is loaded
        PLog.getLogsConfigFromXML() ?: PLog.getLogBus().send(LogEvents(EventTypes.LOGS_CONFIG_FOUND))

        //Do Initial tasks
        PLogImpl.getLogsConfig(PLog)?.doOnSetup()

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

    for (logType in PLogImpl.getLogsConfig(PLog)?.logTypesEnabled!!) {
        val logger = DataLogger(logFileName = logType)
        map[logType] = logger
    }

    //This will assign map of logTypes to 'PLog' logTypes.
    PLog.logTypes = map
}