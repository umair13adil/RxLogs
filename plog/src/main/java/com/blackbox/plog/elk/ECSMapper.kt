package com.blackbox.plog.elk

import android.util.Log
import com.blackbox.plog.elk.models.fields.*
import com.blackbox.plog.elk.models.schema.*
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.pLogs.models.LogData
import com.blackbox.plog.pLogs.models.LogLevel

object ECSMapper {

    private val TAG = "ECSMapper"

    private const val LOGGER_NAME = "PLogger"

    private const val LOG_LEVEL_INFO = "INFO"
    private const val LOG_LEVEL_DEBUG = "DEBUG"
    private const val LOG_LEVEL_ERROR = "ERROR"

    fun getECSMappedLogString(log: LogData): String {
        return when (log.logType) {
            LogLevel.INFO.level -> {
                mapForInfoLog(log)
            }
            LogLevel.WARNING.level -> {
                mapForInfoLog(log)
            }
            LogLevel.ERROR.level, LogLevel.SEVERE.level -> {
                mapForInfoLog(log)
            }
            else -> {
                Log.e(TAG, "Unable to map for ECS schema.")
                ""
            }
        }
    }

    private fun mapForInfoLog(logData: LogData): String {
        val metaInfo = PLogMetaInfoProvider.metaInfo
        val metaInfoJson = PLogImpl.gson.toJson(metaInfo).toString()

        val ecs = ECSInfo(log_level = LOG_LEVEL_INFO,
                labels = createLabelsMap(),
                message = logData.logText!!,
                service_name = logData.className!!,
                process_thread_name = logData.functionName!!,
                log_logger = LOGGER_NAME,
                geo = getGeo(),
                host = getHost(),
                organization = getOrganization(),
                user = getUser()
        )
        return PLogImpl.gson.toJson(ecs).toString()
    }

    private fun mapForInfoDebug(logData: LogData): String {
        val metaInfo = PLogMetaInfoProvider.metaInfo
        val metaInfoJson = PLogImpl.gson.toJson(metaInfo).toString()

        val ecs = ECSDebug(log_level = LOG_LEVEL_INFO,
                labels = createLabelsMap(),
                message = logData.logText!!,
                service_name = logData.className!!,
                process_thread_name = logData.functionName!!,
                log_logger = LOGGER_NAME,
                transaction_id = "",
                trace_id = "",
                geo = getGeo(),
                host = getHost(),
                organization = getOrganization(),
                user = getUser()
        )
        return PLogImpl.gson.toJson(ecs).toString()
    }

    private fun mapForInfoError(logData: LogData): String {
        val ecs = ECSError(log_level = LOG_LEVEL_INFO,
                labels = createLabelsMap(),
                message = logData.logText!!,
                process_thread_name = logData.functionName!!,
                log_logger = LOGGER_NAME,
                log_origin = LogOrigin(
                        file_line = 0,
                        function = logData.functionName!!,
                        file_name = logData.className!!),
                error_type = "",
                error_message = "",
                error_stack_trace = arrayListOf(),
                geo = getGeo(),
                host = getHost(),
                organization = getOrganization(),
                user = getUser()
        )
        return PLogImpl.gson.toJson(ecs).toString()
    }

    private fun createLabelsMap(): String {
        val metaInfo = PLogMetaInfoProvider.metaInfo

        val map = HashMap<String, String>()
        map["appId"] = metaInfo.appId
        map["appName"] = metaInfo.appName
        map["appVersion"] = metaInfo.appVersion
        map["language"] = metaInfo.language

        return PLogImpl.gson.toJson(map).toString()
    }

    private fun getGeo(): Geo {
        val metaInfo = PLogMetaInfoProvider.metaInfo
        return Geo(location = "{ \"lon\": ${metaInfo.longitude}, \"lat\": ${metaInfo.latitude} }")
    }

    private fun getHost(): Host {
        val metaInfo = PLogMetaInfoProvider.metaInfo
        return Host(
                architecture = "${metaInfo.deviceManufacturer} ${metaInfo.deviceSdkInt}",
                hostname = metaInfo.deviceModel,
                id = metaInfo.deviceBrand,
                ip = "0.0.0.0",
                mac = metaInfo.deviceSerial,
                name = metaInfo.deviceName,
                type = "Android"
        )
    }

    private fun getUser(): User {
        val metaInfo = PLogMetaInfoProvider.metaInfo
        return User(
                email = metaInfo.userEmail,
                full_name = metaInfo.userName,
                hash = metaInfo.deviceId,
                id = metaInfo.userId,
                name = metaInfo.userName
        )
    }

    private fun getOrganization(): Organization {
        val metaInfo = PLogMetaInfoProvider.metaInfo
        return Organization(
                id = "${metaInfo.organizationId} / ${metaInfo.environmentId}",
                name = metaInfo.environmentName
        )
    }
}