package com.blackbox.plog.elk.models.fields

import com.google.gson.annotations.SerializedName

data class LogOrigin(

        @SerializedName("file.name") val file_name: String,
        @SerializedName("function") val function: String,
        @SerializedName("file.line") val file_line: Int
)