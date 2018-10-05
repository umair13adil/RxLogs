package com.blackbox.plog.pLogs.models

enum class LogType(val type: String) {
    Device("Device"),
    Location("Location"),
    Notification("Notification"),
    Network("Network"),
    Navigation("Navigation"),
    History("History"),
    Tasks("Tasks"),
    Jobs("Jobs"),
    Errors("Errors");
}