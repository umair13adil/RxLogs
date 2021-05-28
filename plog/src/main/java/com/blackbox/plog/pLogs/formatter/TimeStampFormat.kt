package com.blackbox.plog.pLogs.formatter

import androidx.annotation.Keep


@Keep
object TimeStampFormat {

    val DATE_FORMAT_1 = "ddMMyyyy"
    val DATE_FORMAT_2 = "MM/dd/yyyy"
    val TIME_FORMAT_FULL_JOINED = "ddMMyyyy_HHmmss_a"
    val TIME_FORMAT_FULL_1 = "dd MMMM yyyy HH:mm:ss"
    val TIME_FORMAT_FULL_2 = "MM:dd:yyyy hh:mm:ss a"
    val TIME_FORMAT_24_FULL = "dd/MM/yyyy HH:mm:ss"
    val TIME_FORMAT_READABLE = "dd MMMM yyyy hh:mm:ss a"
    val TIME_FORMAT_READABLE_2 = "dd MMMM yyyy hh:mm:ss.SSS a"
    val TIME_FORMAT_SIMPLE = "HH:mm:ss"
}