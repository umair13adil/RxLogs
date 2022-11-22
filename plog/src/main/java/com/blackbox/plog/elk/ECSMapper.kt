package com.blackbox.plog.elk

import android.util.Log
import com.blackbox.plog.elk.models.fields.*
import com.blackbox.plog.elk.models.schema.ECSDebug
import com.blackbox.plog.elk.models.schema.ECSError
import com.blackbox.plog.elk.models.schema.ECSInfo
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.pLogs.models.LogData
import com.blackbox.plog.pLogs.models.LogLevel

object ECSMapper {

    private val TAG = "ECSMapper"

    private const val LOGGER_NAME = "PLogger"

    private const val LOG_LEVEL_INFO = "INFO"
    private const val LOG_LEVEL_DEBUG = "DEBUG"
    private const val LOG_LEVEL_ERROR = "ERROR"

    fun getECSMappedLogString(log: LogData, exception: Exception? = null, throwable: Throwable? = null): String {
        return when (log.logType) {
            LogLevel.INFO.level -> {
                mapForInfoLog(log)
            }
            LogLevel.WARNING.level -> {
                mapForInfoDebug(log)
            }
            LogLevel.ERROR.level, LogLevel.SEVERE.level -> {
                mapForInfoError(log, exception, throwable)
            }
            else -> {
                Log.e(TAG, "Unable to map for ECS schema.")
                ""
            }
        }
    }

    private fun mapForInfoLog(logData: LogData): String {
        val ecs = ECSInfo(log_level = LOG_LEVEL_INFO,
                labels = createLabelsMap(),
                message = logData.logText!!,
                service_name = logData.className!!,
                process_thread_name = logData.functionName!!,
                log_logger = LOGGER_NAME,
                geo = getGeo(),
                host = getHost(),
                organization = getOrganization(),
                user = getUser(),
                app = getApp()
        )
        return PLogImpl.gson.toJson(ecs).toString()
    }

    private fun mapForInfoDebug(logData: LogData): String {
        val ecs = ECSDebug(log_level = LOG_LEVEL_DEBUG,
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
                user = getUser(),
                app = getApp()
        )
        return PLogImpl.gson.toJson(ecs).toString()
    }

    private fun mapForInfoError(logData: LogData, exception: Exception? = null, throwable: Throwable? = null): String {

        val stackTrace = arrayListOf<String>()
        var errorMessage = ""

        exception?.let {
            it.stackTrace.forEach {
                stackTrace.add(it.toString())
            }

            it.message?.let {
                errorMessage = it
            }
        }

        throwable?.let { it ->
            it.stackTrace.forEach {
                stackTrace.add(it.toString())
            }

            it.message?.let {
                errorMessage = it
            }
        }

        val ecs = ECSError(log_level = LOG_LEVEL_ERROR,
                labels = createLabelsMap(),
                message = logData.logText!!,
                process_thread_name = logData.functionName!!,
                log_logger = LOGGER_NAME,
                log_origin = LogOrigin(
                        file_line = 0,
                        function = logData.functionName!!,
                        file_name = logData.className!!),
                error_type = LOG_LEVEL_ERROR,
                error_message = errorMessage,
                error_stack_trace = stackTrace,
                geo = getGeo(),
                host = getHost(),
                organization = getOrganization(),
                user = getUser(),
                app = getApp()
        )
        return PLogImpl.gson.toJson(ecs).toString()
    }

    private fun createLabelsMap(): String {
        val metaInfo = PLogMetaInfoProvider.metaInfo
        val map = metaInfo.labels
        return PLogImpl.gson.toJson(map).toString()
    }

    private fun getApp(): App {
        val metaInfo = PLogMetaInfoProvider.metaInfo
        return App(
                id = metaInfo.appId,
                name = metaInfo.appName,
                version = metaInfo.appVersion,
                language = metaInfo.language,
                environmentId = metaInfo.environmentId,
                environmentName = metaInfo.environmentName
        )
    }

    private fun getGeo(): Geo {
        val metaInfo = PLogMetaInfoProvider.metaInfo
        return Geo(location = "{ \"lon\": ${metaInfo.longitude}, \"lat\": ${metaInfo.latitude} }")
    }

    private fun getHost(): Host {
        val metaInfo = PLogMetaInfoProvider.metaInfo
        return Host(
                sdkInt = metaInfo.deviceSdkInt,
                hostname = metaInfo.deviceModel,
                id = metaInfo.deviceBrand,
                mac = metaInfo.deviceSerial,
                name = metaInfo.deviceName,
                type = metaInfo.deviceManufacturer,
                batteryPercent = metaInfo.batteryPercent
        )
    }

    private fun getUser(): User {
        val metaInfo = PLogMetaInfoProvider.metaInfo
        return User(
                email = metaInfo.userEmail,
                full_name = metaInfo.userName,
                deviceId = metaInfo.deviceId,
                id = metaInfo.userId,
                name = metaInfo.userName
        )
    }

    private fun getOrganization(): Organization {
        val metaInfo = PLogMetaInfoProvider.metaInfo
        return Organization(
                id = metaInfo.organizationId,
                unitId = metaInfo.organizationUnitId,
                name = metaInfo.organizationName
        )
    }
}