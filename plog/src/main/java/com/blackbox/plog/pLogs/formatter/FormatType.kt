package com.blackbox.plog.pLogs.formatter

import androidx.annotation.Keep

@Keep
enum class FormatType(val value: String) {
    FORMAT_CURLY("FORMAT_CURLY"),
    FORMAT_SQUARE("FORMAT_SQUARE"),
    FORMAT_CSV("FORMAT_CSV"),
    FORMAT_CUSTOM("FORMAT_CUSTOM");
}