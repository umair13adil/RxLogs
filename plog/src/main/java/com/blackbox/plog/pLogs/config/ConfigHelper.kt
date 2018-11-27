package com.blackbox.plog.pLogs.config

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.pLogs.models.LogLevel

/*
 * Check if provided configuration file contains 'enabled' LogLevel.
 */
fun isLogLevelEnabled(logLevel: LogLevel): Boolean {

    return if (!PLog.isLogsConfigSet())
        true
    else if (PLog.isLogsConfigSet() && PLogImpl.logsConfig?.logLevelsEnabled?.isEmpty()!!)
        true
    else PLogImpl.logsConfig?.logLevelsEnabled?.contains(logLevel)!!

}