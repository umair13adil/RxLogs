package com.blackbox.plog.pLogs.filter
import androidx.annotation.Keep

@Keep
data class PlogFilters(val dates: List<String>,
val hours: List<String>,
val files: List<String>)



