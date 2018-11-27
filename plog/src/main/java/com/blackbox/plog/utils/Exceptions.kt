package com.blackbox.plog.utils

import com.blackbox.plog.pLogs.PLog

fun dataLoggerCalledBeforePLoggerException() {
    if (!PLog.isLogsConfigSet())
        print(Throwable("PLog Not Initialized! Plogger must be initialized with config file before calling DataLogger!"))
}