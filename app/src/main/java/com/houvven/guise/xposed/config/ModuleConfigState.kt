package com.houvven.guise.xposed.config

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class ModuleConfigState private constructor(moduleConfig: ModuleConfig) {

    lateinit var brand: MutableState<String>
    lateinit var model: MutableState<String>
    lateinit var product: MutableState<String>
    lateinit var device: MutableState<String>
    lateinit var board: MutableState<String>
    lateinit var hardware: MutableState<String>
    lateinit var androidVersion: MutableState<String>
    lateinit var sdkInt: MutableState<String>
    lateinit var fingerPrint: MutableState<String>

    lateinit var networkType: MutableState<String>
    lateinit var wifiSSID: MutableState<String>
    lateinit var wifiBSSID: MutableState<String>
    lateinit var wifiMacAddress: MutableState<String>
    lateinit var simOperator: MutableState<String>
    lateinit var simOperatorName: MutableState<String>
    lateinit var simCountry: MutableState<String>


    lateinit var imei: MutableState<String>
    lateinit var androidId: MutableState<String>

    lateinit var lac: MutableState<String>
    lateinit var cid: MutableState<String>

    lateinit var longitude: MutableState<String>
    lateinit var latitude: MutableState<String>
    lateinit var randomOffset: MutableState<Boolean>
    lateinit var makeWifiLocationFail: MutableState<Boolean>
    lateinit var makeCellLocationFail: MutableState<Boolean>

    lateinit var batteryLevel: MutableState<String>
    lateinit var language: MutableState<String>
    lateinit var screenshotsFlag: MutableState<String>
    lateinit var hookSuccessHint: MutableState<Boolean>

    lateinit var passContacts: MutableState<Boolean>
    lateinit var passPhoto: MutableState<Boolean>
    lateinit var passVideo: MutableState<Boolean>
    lateinit var passAudio: MutableState<Boolean>


    init {
        val stateFields = this.javaClass.declaredFields
            .filter { it.type == MutableState::class.java }
            .toMutableList()
        val configFields = moduleConfig.javaClass.declaredFields
        val empty = ModuleConfig()
        for (configField in configFields) {
            val stateField = stateFields.find { it.name == configField.name }
            if (stateField != null) {
                configField.isAccessible = true
                when (val value = configField.get(moduleConfig)) {
                    is Boolean -> stateField.set(this, mutableStateOf(value))
                    is String -> stateField.set(this, mutableStateOf(value.toString()))
                    else -> {
                        val v = if (configField.get(empty) == value) "" else value.toString()
                        stateField.set(this, mutableStateOf(v))
                    }
                }
                stateFields.remove(stateField)
            }
            if (stateFields.isEmpty()) break
        }
    }

    internal fun clear() {
        val stateFields =
            this.javaClass.declaredFields.filter { it.type == MutableState::class.java }
        for (stateField in stateFields) {
            val state = (stateField.get(this) as MutableState<*>)
            if (state.value is Boolean) (state as MutableState<Boolean>).value = false
            else (state as MutableState<String>).value = ""
        }
    }

    companion object {
        fun of(moduleConfig: ModuleConfig) = ModuleConfigState(moduleConfig)
    }

}
