package com.blackbox.plog.utils

import com.blackbox.plog.pLogs.PLog

fun dataLoggerCalledBeforePLoggerException() {
    if (PLog.getPLogger() == null) {
        throw Exception(Throwable("PLog Not Initialized! Plogger must be initialized before calling DataLogger!"))
    }
}