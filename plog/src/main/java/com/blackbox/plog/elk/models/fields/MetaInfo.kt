package com.blackbox.plog.elk.models.fields

data class MetaInfo(
        /**App**/
        var appId: String = "",
        var appName: String = "",
        var appVersion: String = "",
        var language: String = "",

        /**Environment**/
        var environmentId: String = "",
        var environmentName: String = "",

        /**Organization**/
        var organizationId: String = "",
        var organizationName: String = "",
        var organizationUnitId: String = "",

        /**User**/
        var userId: String = "",
        var userName: String = "",
        var userEmail: String = "",
        var deviceId: String = "",

        /**Device**/
        var deviceSerial: String = "",
        var deviceBrand: String = "",
        var deviceName: String = "",
        var deviceManufacturer: String = "",
        var deviceModel: String = "",
        var deviceSdkInt: String = "",
        var batteryPercent: String = "",

        /**Location**/
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,

        /**Labels**/
        var labels: HashMap<String, String> = hashMapOf<String, String>()
)