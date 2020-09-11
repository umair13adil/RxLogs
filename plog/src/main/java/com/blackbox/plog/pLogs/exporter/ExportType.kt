package com.blackbox.plog.pLogs.exporter

import androidx.annotation.Keep

@Keep
enum class ExportType(val type: String) {
    TODAY("today"),
    LAST_HOUR("last_hour"),
    WEEKS("weeks"),
    LAST_24_HOURS("last_24_hours"),
    ALL("all")
}