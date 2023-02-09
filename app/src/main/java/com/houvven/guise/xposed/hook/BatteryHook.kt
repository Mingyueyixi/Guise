package com.houvven.guise.xposed.hook

import android.content.Intent
import android.os.BatteryManager
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.afterHookedMethod

class BatteryHook : LoadPackageHandler {

    @Throws(Throwable::class)
    override fun onHook() {
        val level = config.batteryLevel
        if (level == -1) return

        BatteryManager::class.java.afterHookedMethod(
            methodName = "getIntProperty", Int::class.java
        ) { param ->
            if (param.args[0] == BatteryManager.BATTERY_PROPERTY_CAPACITY) {
                param.result = level
            }
        }

        Intent::class.java.afterHookedMethod(
            methodName = "getIntExtra", String::class.java, Int::class.java
        ) { param ->
            if (param.args[0] == BatteryManager.EXTRA_LEVEL) {
                param.result = level
            }
        }
    }
}