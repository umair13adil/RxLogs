package com.blackbox.plog.pLogs.events

import androidx.annotation.Keep
import java.lang.Exception

@Keep
data class LogEvents(
    var event: EventTypes,
    var data: String = "",
    var throwable: Throwable? = null,
    var exception: Exception? = null
)