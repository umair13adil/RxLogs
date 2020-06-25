package com.blackbox.plog.elk.models.schema

import com.blackbox.plog.elk.models.fields.*
import com.google.gson.annotations.SerializedName

data class ECSError(
        @SerializedName("labels") val labels: String,
        @SerializedName("log.level") val log_level: String,
        @SerializedName("message") val message: String,
        @SerializedName("process.thread.name") val process_thread_name: String,
        @SerializedName("log.logger") val log_logger: String,
        @SerializedName("log.origin") val log_origin: LogOrigin,
        @SerializedName("error.type") val error_type: String,
        @SerializedName("error.message") val error_message: String,
        @SerializedName("error.stack_trace") val error_stack_trace: List<String>,
        @SerializedName("geo") val geo: Geo,
        @SerializedName("host") val host: Host,
        @SerializedName("organization") val organization: Organization,
        @SerializedName("user") val user: User,
        @SerializedName("app") val app: App
)