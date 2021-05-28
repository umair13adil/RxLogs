package com.blackbox.plog.pLogs.config

import androidx.annotation.Keep
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.pLogs.models.LogLevel
import com.google.gson.GsonBuilder

@Keep

/*
 * Check if provided configuration file contains 'enabled' LogLevel.
 */
fun isLogLevelEnabled(logLevel: LogLevel): Boolean {

    return if (!PLog.isLogsConfigSet())
        true
    else if (PLog.isLogsConfigSet() && PLogImpl.getConfig()?.logLevelsEnabled?.isEmpty()!!)
        true
    else PLogImpl.getLogLevelsEnabled().contains(logLevel)

}

fun <T : Any?> saveLogsConfig(key: String, obj: T?) {
    val gson = GsonBuilder().serializeNulls().create()
    val json = gson.toJson(obj)
    PLogPreferences.getInstance().save(key, json)
}

fun <T : Any?> getLogsConfig(
        key: String,
        kClass: Class<T>,
        defaultValue: T? = null
): T? {
    val gson = GsonBuilder().serializeNulls().create()
    val obj = PLogPreferences.getInstance().getString(key)
    if (obj == null || obj.isEmpty())
        return defaultValue
    return gson.fromJson(obj, kClass)
}