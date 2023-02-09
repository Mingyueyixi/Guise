package com.houvven.guise.xposed.hook

import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.telephony.SubscriptionInfo
import android.telephony.TelephonyManager
import com.houvven.guise.constant.NetworkType
import com.houvven.guise.xposed.config.HooksValue
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.setMethodResult
import com.houvven.ktx_xposed.hook.setSomeSameNameMethodResult

internal class NetworkHook : LoadPackageHandler {

    override fun onHook() {
        if (config.networkType != HooksValue.NET_UNHOOK) this.hookNetworkType()
        if (config.wifiSSID.isNotBlank()) this.hookWifiSSID()
        if (config.wifiBSSID.isNotBlank()) this.hookWifiBSSID()
        if (config.wifiMacAddress.isNotBlank()) this.hookWifiMacAddress()
        if (config.simOperator.isNotBlank()) this.hookSimOperator()
        if (config.simOperatorName.isNotBlank()) this.hookSimOperatorName()
        if (config.simCountry.isNotBlank()) this.hookSimCountryIso()
    }


    private fun hookNetworkType() {
        val networkType = config.networkType
        this.hookBaseNetType(networkType)
        if (networkType != HooksValue.NET_WIFI) {
            this.hookMobileType(networkType)
        }
    }


    private fun hookMobileType(networkType: Int) {
        val type = when (networkType) {
            HooksValue.NET_MOBILE_2G -> TelephonyManager.NETWORK_TYPE_CDMA
            HooksValue.NET_MOBILE_3G -> TelephonyManager.NETWORK_TYPE_TD_SCDMA
            HooksValue.NET_MOBILE_4G -> TelephonyManager.NETWORK_TYPE_LTE
            HooksValue.NET_MOBILE_5G -> TelephonyManager.NETWORK_TYPE_NR
            else -> TelephonyManager.NETWORK_TYPE_UNKNOWN
            // else -> networkType
        }
        TelephonyManager::class.java.setMethodResult("getNetworkType", type)
    }


    private fun hookBaseNetType(type: Int) {
        val t = when (type) {
            HooksValue.NET_WIFI -> NetworkType.WIFI
            HooksValue.NET_MOBILE_5G,
            HooksValue.NET_MOBILE_4G,
            HooksValue.NET_MOBILE_3G,
            HooksValue.NET_MOBILE_2G -> NetworkType.MOBILE

            else -> NetworkType.NONE
        }
        NetworkInfo::class.java.setMethodResult("getType", t)
    }


    private fun hookWifiSSID() {
        setWifiInfoMethodResult("getSSID", "\"${config.wifiSSID}\"")
    }

    private fun hookWifiBSSID() {
        setWifiInfoMethodResult("getBSSID", config.wifiBSSID)
    }

    private fun hookWifiMacAddress() {
        setWifiInfoMethodResult("getMacAddress", config.wifiMacAddress)
    }

    private fun setWifiInfoMethodResult(methodName: String, value: Any?) {
        WifiInfo::class.java.setMethodResult(methodName, value)
    }

    private fun hookSimOperator() {
        val simOperator = config.simOperator
        val mcc = simOperator.substring(0, 3)
        val mnc = simOperator.substring(3)

        TelephonyManager::class.java
            .setSomeSameNameMethodResult(
                "getSimOperatorNumericForPhone",
                "getNetworkOperatorForPhone",
                value = simOperator
            )

        SubscriptionInfo::class.java.run {
            setMethodResult("getMcc", mcc.toIntOrNull())
            setMethodResult("getMnc", mnc.toIntOrNull())
            setMethodResult("getMccString", mcc)
            setMethodResult("getMncString", mnc)
        }
    }

    private fun hookSimOperatorName() {
        setSomeSameNameMethodResult(
            mapOf(
                TelephonyManager::class.java to "getSimOperatorNameForPhone",
                TelephonyManager::class.java to "getNetworkOperatorName",
                SubscriptionInfo::class.java to "getCarrierName"
            ),
            value = config.simOperatorName
        )
    }

    private fun hookSimCountryIso() {
        setSomeSameNameMethodResult(
            mapOf(
                TelephonyManager::class.java to "getSimCountryIso",
                TelephonyManager::class.java to "getNetworkCountryIso",
                SubscriptionInfo::class.java to "getCountryIso"
            ),
            value = config.simCountry
        )
    }


}