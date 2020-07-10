package com.blackbox.plog.elk.models.fields

import com.google.gson.annotations.SerializedName

data class Host(
        @SerializedName("host.hostname") val hostname: String,
        @SerializedName("host.id") val id: String,
        @SerializedName("host.mac") val mac: String,
        @SerializedName("host.name") val name: String,
        @SerializedName("host.type") val type: String,
        @SerializedName("host.sdkInt") val sdkInt: String,
        @SerializedName("host.batteryPercent") val batteryPercent: String
)