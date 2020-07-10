package com.blackbox.plog.elk.models.fields

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("user.email") val email: String,
        @SerializedName("user.full_name") val full_name: String,
        @SerializedName("user.deviceId") val deviceId: String,
        @SerializedName("user.id") val id: String,
        @SerializedName("user.name") val name: String
)