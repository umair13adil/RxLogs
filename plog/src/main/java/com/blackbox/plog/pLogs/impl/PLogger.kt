package com.blackbox.plog.pLogs.impl

import com.blackbox.plog.dataLogs.DataLogger
import com.blackbox.plog.pLogs.config.LogsConfig
import com.blackbox.plog.pLogs.events.LogEvents
import com.blackbox.plog.pLogs.models.LogLevel
import io.reactivex.Observable

interface PLogger {

    fun applyConfigurations(config: LogsConfig, saveToFile: Boolean = false)

    fun forceWriteLogsConfig(config: LogsConfig)

    fun getLogsConfigFromXML(): LogsConfig?

    fun getLogsConfig(): LogsConfig?

    fun logThis(className: String, functionName: String, text: String, type: LogLevel)

    fun logThis(className: String, functionName: String, info: String = "", throwable: Throwable, type: LogLevel = LogLevel.ERROR)

    fun logThis(className: String, functionName: String, info: String = "", exception: Exception, type: LogLevel = LogLevel.ERROR)

    fun clearLogs()

    fun clearExportedLogs()

    fun getLogEvents(): Observable<LogEvents>

    fun getLoggerFor(type: String): DataLogger?
}