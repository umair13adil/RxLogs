package com.blackbox.plog.pLogs.formatter


enum class TimeStampFormat(val value: String) {

    DATE_FORMAT_1("ddMMyyyy"),
    DATE_FORMAT_2("MM/dd/yyyy"),
    TIME_FORMAT_FULL_JOINED("ddMMyyyy_kkmmss_a"),
    TIME_FORMAT_FULL_1("dd MMMM yyyy kk:mm:ss"),
    TIME_FORMAT_FULL_2("MM:dd:yyyy hh:mm:ss a"),
    TIME_FORMAT_24_FULL("dd/MM/yyyy kk:mm:ss"),
    TIME_FORMAT_READABLE("dd MMMM yyyy hh:mm:ss a"),
    TIME_FORMAT_SIMPLE("kk:mm:ss")
}