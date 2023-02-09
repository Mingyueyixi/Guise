package com.houvven.guise.xposed.hook.netowork

import android.net.NetworkInfo
import com.houvven.guise.constant.NetworkType
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.guise.xposed.config.HooksValue
import com.houvven.ktx_xposed.hook.setMethodResult

internal class NetworkHook : LoadPackageHandler {

    override fun onHook() {
        if (config.networkType != HooksValue.NET_UNHOOK) this.hookNetworkType()
        listOf(WifiHook(), SimHook()).forEach { it.onHook() }
    }


    private fun hookNetworkType() {
        val networkType = config.networkType
        this.hookBaseNetType(networkType)
        if (networkType != HooksValue.NET_WIFI) {
            SimHook().hookMobileType(networkType)
        }
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

}