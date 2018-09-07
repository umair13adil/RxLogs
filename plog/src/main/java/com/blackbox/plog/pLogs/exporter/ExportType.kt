package com.blackbox.plog.pLogs.exporter

enum class ExportType(val type: String) {
    TODAY("today"),
    LAST_HOUR("last_hour"),
    WEEKS("weeks"),
    LAST_24_HOURS("last_24_hours"),
    ALL("all")
}