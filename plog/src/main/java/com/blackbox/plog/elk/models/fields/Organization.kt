package com.blackbox.plog.elk.models.fields

import com.google.gson.annotations.SerializedName

data class Organization(
        @SerializedName("organization.id") val id: String,
        @SerializedName("organization.unitId") val unitId: String,
        @SerializedName("organization.name") val name: String
)