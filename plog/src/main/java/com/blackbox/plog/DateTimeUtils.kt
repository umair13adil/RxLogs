package com.blackbox.plog

import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtils {

    private val TIME_FORMAT_FULL = "MM:dd:yyyy hh:mm:ss a"

    companion object Factory {
        fun create(): DateTimeUtils = DateTimeUtils()
    }

    fun getTimeFormatted(timestamp: Long): String {
        val date1 = Date(timestamp)
        val f1 = SimpleDateFormat(TIME_FORMAT_FULL, Locale.ENGLISH)
        val formatted = f1.format(date1)
        return formatted
    }


}
