package com.blackbox.plog.pLogs.config

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.models.LogLevel

/*
 * Check if provided configuration file contains 'enabled' LogLevel.
 */
fun isLogLevelEnabled(logLevel: LogLevel): Boolean {

    return if (!PLog.isLogsConfigSet())
        true
    else if (PLog.isLogsConfigSet() && PLog.getLogsConfig()?.logLevelsEnabled?.isEmpty()!!)
        true
    else PLog.getLogsConfig()?.logLevelsEnabled?.contains(logLevel)!!

}