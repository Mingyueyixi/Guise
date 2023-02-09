package com.houvven.guise.xposed.hook.location

import android.location.Criteria
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.UserHandle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.INCLUDE_LOCATION_DATA_NONE
import android.telephony.gsm.GsmCellLocation
import com.houvven.ktx_xposed.hook.beforeHookSomeSameNameMethod
import com.houvven.ktx_xposed.hook.setMethodResult
import com.houvven.ktx_xposed.hook.setSomeSameNameMethodResult

@Suppress("DEPRECATION")
open class LocationHookBase {

    protected fun setOtherServicesFail() {
        setProviderState()
        setTelLocationFail()
    }

    private fun setProviderState() {
        LocationManager::class.java.apply {
            setMethodResult(
                methodName = "isLocationEnabledForUser",
                value = true,
                parameterTypes = arrayOf(UserHandle::class.java)
            )
            beforeHookSomeSameNameMethod(
                "isProviderEnabledForUser", "hasProvider"
            ) {
                when (it.args[0] as String) {
                    LocationManager.GPS_PROVIDER -> it.result = true
                    LocationManager.FUSED_PROVIDER,
                    LocationManager.NETWORK_PROVIDER,
                    LocationManager.PASSIVE_PROVIDER,
                    -> it.result = false
                }
            }
            setSomeSameNameMethodResult(
                "getProviders", "getAllProviders",
                value = listOf(LocationManager.GPS_PROVIDER)
            )
            setMethodResult(
                methodName = "getBestProvider",
                value = LocationManager.GPS_PROVIDER,
                parameterTypes = arrayOf(Criteria::class.java, Boolean::class.java)
            )
        }
    }


    private fun setTelLocationFail() {
        TelephonyManager::class.java.run {
            setSomeSameNameMethodResult(
                "getCellLocation",
                "getAllCellInfo",
                "getNeighboringCellInfo",
                "getLastKnownCellIdentity",
                value = null
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                setMethodResult(
                    methodName = "getLocationData",
                    value = INCLUDE_LOCATION_DATA_NONE
                )
            }
        }

        PhoneStateListener::class.java
            .setSomeSameNameMethodResult(
                "onCellLocationChanged",
                "onCellInfoChanged",
                value = null
            )

    }

    protected fun makeWifiLocationFail() {
        WifiManager::class.java.run {
            setMethodResult("getScanResults", emptyList<ScanResult>())
            setMethodResult("isWifiEnabled", false)
            setMethodResult("isScanAlwaysAvailable", false)
            setMethodResult("getWifiState", WifiManager.WIFI_STATE_DISABLED)
        }
        WifiInfo::class.java.run {
            setMethodResult("getMacAddress", "00:00:00:00:00:00")
            setMethodResult("getBSSID", "00:00:00:00:00:00")
        }
    }

    protected fun makeCellLocationFail() {
        GsmCellLocation::class.java.run {
            setMethodResult("getPsc", -1)
            setMethodResult("getLac", -1)
        }
    }


}