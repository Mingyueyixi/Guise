package com.houvven.guise.xposed.config

import com.houvven.guise.xposed.PackageConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

@Serializable
data class ModuleConfig(
    @Transient var packageName: String = "",
    var brand: String = "",
    var model: String = "",
    var product: String = "",
    var device: String = "",
    var board: String = "",
    var hardware: String = "",
    var androidVersion: String = "",
    var sdkInt: Int = -1,
    var networkType: Int = HooksValue.NET_UNHOOK,
    var fingerPrint: String = "",
    var wifiSSID: String = "",
    var wifiBSSID: String = "",
    var wifiMacAddress: String = "",
    var simOperator: String = "",
    var simOperatorName: String = "",
    var simCountry: String = "",
    var imei: String = "",
    var androidId: String = "",
    var lac: Int = -1,
    var cid: Int = -1,
    var language: String = "",
    var longitude: Double = -1.0,
    var latitude: Double = -1.0,
    var randomOffset: Boolean = false,
    var makeWifiLocationFail: Boolean = false,
    var makeCellLocationFail: Boolean = false,
    var batteryLevel: Int = -1,
    var screenshotsFlag: Int = HooksValue.SCREENSHOTS_UNHOOK,
    var hookSuccessHint: Boolean = false,
    var passContacts: Boolean = false,
    var passPhoto: Boolean = false,
    var passVideo: Boolean = false,
    var passAudio: Boolean = false,
) {
    val isEnable: Boolean get() = this != ModuleConfig(packageName)

    fun toJson() = Json.encodeToString(serializer(), this)

    fun toModuleConfigState() = ModuleConfigState.of(this)

    companion object {
        fun fromJson(json: String) = Json.decodeFromString(serializer(), json)

        fun get(packageName: String): ModuleConfig {
            val config = PackageConfig.safePrefs.getString(packageName, null)?.let { fromJson(it) }
            config?.packageName = packageName
            return config ?: ModuleConfig(packageName)
        }
    }
}
