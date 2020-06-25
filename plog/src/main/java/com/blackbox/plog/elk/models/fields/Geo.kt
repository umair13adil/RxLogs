package com.blackbox.plog.elk.models.fields

import com.google.gson.annotations.SerializedName

data class Geo(
        @SerializedName("geo.location") val location: String
)