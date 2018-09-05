package com.blackbox.plog.pLogs.models

enum class LogRequestType(val type: Int) {
    TODAY(1),
    LAST_HOUR(2),
    WEEKS(3),
    LAST_24_HOURS(4)
}