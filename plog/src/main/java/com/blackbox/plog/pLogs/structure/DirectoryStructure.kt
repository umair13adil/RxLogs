package com.blackbox.plog.pLogs.structure

import androidx.annotation.Keep

@Keep
enum class DirectoryStructure(val value: String) {
    FOR_DATE("FOR_DATE"),
    FOR_EVENT("FOR_EVENT"),
    SINGLE_FILE_FOR_DAY("SINGLE_FILE_FOR_DAY"),
}