package com.houvven.guise.xposed.hook

import android.content.ContentResolver
import android.provider.Settings
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.beforeHookedMethod
import com.houvven.ktx_xposed.hook.setMethodResult

class UniquelyIdHook : LoadPackageHandler {

    override fun onHook() {
        if (config.androidId.isNotBlank()) this.hookAndroidId()
        if (config.imei.isNotBlank()) this.hookImei()
    }

    private fun hookAndroidId() {
        Secure::class.java.beforeHookedMethod(
            methodName = "getStringForUser",
            ContentResolver::class.java, String::class.java, Int::class.java
        ) {
            if (it.args[1] == Secure.ANDROID_ID) {
                it.result = config.androidId
            }
        }

        Settings.System::class.java.beforeHookedMethod(
            methodName = "getStringForUser",
            ContentResolver::class.java, String::class.java, Int::class.java
        ) {
            if (it.args[1] == Settings.System.ANDROID_ID) {
                it.result = config.androidId
            }
        }
    }

    private fun hookImei() {
        TelephonyManager::class.java.setMethodResult(
            "getImei", config.imei, parameterTypes = arrayOf(Int::class.java)
        )
    }

}