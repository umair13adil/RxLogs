package com.blackbox.plog.elk.models.fields

import com.google.gson.annotations.SerializedName

data class App(
        @SerializedName("app.id") val id: String,
        @SerializedName("app.name") val name: String,
        @SerializedName("app.version") val version: String,
        @SerializedName("app.language") val language: String,
        @SerializedName("app.environmentId") val environmentId: String,
        @SerializedName("app.environmentName") val environmentName: String
)