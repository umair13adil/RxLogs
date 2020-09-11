package com.blackbox.plog.pLogs.models

import androidx.annotation.Keep

@Keep
enum class LogLevel(val level: String) {
    INFO("INFO"),
    WARNING("WARNING"),
    ERROR("ERROR"),
    SEVERE("SEVERE")
}