package com.blackbox.plog.pLogs.events

enum class EventTypes(var data: String) {

    PLOGS_EXPORTED("1"),
    DATA_LOGS_EXPORTED("2"),
    LOGS_CONFIG_FOUND("3"),
    NEW_ERROR_REPORTED("4"),
    NEW_EVENT_DIRECTORY_CREATED("5"),

}

