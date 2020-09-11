package com.blackbox.plog.pLogs.impl

import androidx.annotation.Keep
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.events.EventTypes
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.exporter.LogExporter
import com.blackbox.plog.pLogs.formatter.TimeStampFormat
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.models.LogType
import com.blackbox.plog.utils.DateTimeUtils
import com.blackbox.plog.utils.RxBus

@Keep
object AutoExportHelper {

    fun autoExportError(data: String, type: LogLevel) {

        if (type == LogLevel.ERROR) {
            if (PLogImpl.getConfig()?.autoExportErrors!!) {

                //Send event to notify error is reported
                RxBus.send(LogEvents(EventTypes.NEW_ERROR_REPORTED, data))

                //Send formatted error message
                PLogImpl.getConfig()?.autoExportErrors?.let {
                    val formatted = LogExporter.formatErrorMessage(data)

                    //Send event to notify error is reported
                    RxBus.send(LogEvents(EventTypes.NEW_ERROR_REPORTED_FORMATTED, formatted))
                }
            }
        }

        if (type == LogLevel.SEVERE) {
            if (PLogImpl.getConfig()?.autoExportErrors!!) {

                //Send event to notify severe error is reported
                RxBus.send(LogEvents(EventTypes.SEVERE_ERROR_REPORTED, data))

                //Send formatted error message
                PLogImpl.getConfig()?.autoExportErrors?.let {
                    val formatted = LogExporter.formatErrorMessage(data)

                    //Send event to notify error is reported
                    RxBus.send(LogEvents(EventTypes.SEVERE_ERROR_REPORTED_FORMATTED, formatted))
                }
            }
        }

        if (type == LogLevel.ERROR || type == LogLevel.SEVERE || type == LogLevel.WARNING) {
            if (PLog.isLogsConfigSet()) {
                if (PLog.logTypes.containsKey(LogType.Errors.type)) {
                    val errorLog = PLog.getLoggerFor(LogType.Errors.type)
                    val timeStamp = "\n[${DateTimeUtils.getTimeFormatted(TimeStampFormat.TIME_FORMAT_READABLE)}]\n"
                    errorLog?.appendToFile("\n" + data + timeStamp)
                }
            }
        }
    }

}