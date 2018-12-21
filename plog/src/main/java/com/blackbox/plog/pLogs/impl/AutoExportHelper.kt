package com.blackbox.plog.pLogs.impl

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.exporter.LogExporter
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.models.LogType

object AutoExportHelper {

    fun autoExportError(data: String, type: LogLevel) {

        if (type == LogLevel.ERROR) {
            if (PLogImpl.logsConfig?.autoExportErrors!!) {

                //Send event to notify error is reported
                PLog.getLogBus().send(LogEvents(EventTypes.NEW_ERROR_REPORTED, data))

                //Send formatted error message
                PLogImpl.logsConfig?.autoExportErrors?.let {
                    val formatted = LogExporter.formatErrorMessage(data)

                    //Send event to notify error is reported
                    PLog.getLogBus().send(LogEvents(EventTypes.NEW_ERROR_REPORTED_FORMATTED, formatted))
                }
            }
        }

        if (type == LogLevel.SEVERE) {
            if (PLogImpl.logsConfig?.autoExportErrors!!) {

                //Send event to notify severe error is reported
                PLog.getLogBus().send(LogEvents(EventTypes.SEVERE_ERROR_REPORTED, data))

                //Send formatted error message
                PLogImpl.logsConfig?.autoExportErrors?.let {
                    val formatted = LogExporter.formatErrorMessage(data)

                    //Send event to notify error is reported
                    PLog.getLogBus().send(LogEvents(EventTypes.SEVERE_ERROR_REPORTED_FORMATTED, formatted))
                }
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