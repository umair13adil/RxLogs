package com.blackbox.plog.pLogs.events

import androidx.annotation.Keep

@Keep
data class LogEvents(var event: EventTypes, var data: String = "")