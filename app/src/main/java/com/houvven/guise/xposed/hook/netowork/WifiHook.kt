package com.houvven.guise.xposed.hook.netowork

import android.net.wifi.WifiInfo
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.setMethodResult

internal class WifiHook : LoadPackageHandler {
    override fun onHook() {
        WifiInfo::class.java.run {
            if (config.wifiSSID.isNotBlank()) setMethodResult("getSSID", "\"${config.wifiSSID}\"")
            if (config.wifiBSSID.isNotBlank()) setMethodResult("getBSSID", config.wifiBSSID)
            if (config.wifiMacAddress.isNotBlank()) setMethodResult("getMacAddress", config.wifiMacAddress)
        }
    }

}