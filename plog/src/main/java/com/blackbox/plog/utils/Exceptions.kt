package com.blackbox.plog.utils

import android.util.Log
import com.blackbox.plog.pLogs.PLog

fun dataLoggerCalledBeforePLoggerException() {
    if (!PLog.isLogsConfigSet())
        Log.e(PLog.TAG,"PLog Not Initialized! Plogger must be initialized with config file before calling DataLogger!")
}