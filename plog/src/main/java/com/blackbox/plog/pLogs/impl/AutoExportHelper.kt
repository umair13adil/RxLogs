package com.blackbox.plog.pLogs.impl

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.models.LogType

object AutoExportHelper {

    fun autoExportError(data: String, type: LogLevel) {

        if (type == LogLevel.ERROR) {
            if (PLogImpl.getLogsConfig(PLog)?.autoExportErrors!!) {
                //Send event to notify error is reported
                PLog.getLogBus().send(LogEvents(EventTypes.NEW_ERROR_REPORTED, data))
            }
        }

        if (type == LogLevel.SEVERE) {
            if (PLogImpl.getLogsConfig(PLog)?.autoExportErrors!!) {
                //Send event to notify severe error is reported
                PLog.getLogBus().send(LogEvents(EventTypes.SEVERE_ERROR_REPORTED, data))
            }
        }

        if (PLog.isLogsConfigSet()) {
            if (PLog.logTypes.containsKey(LogType.Errors.type)) {
                val errorLog = PLog.getLoggerFor(LogType.Errors.type)
                errorLog?.appendToFile(data)
            }
        }
    }

}