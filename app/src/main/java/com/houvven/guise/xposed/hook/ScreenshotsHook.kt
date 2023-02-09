package com.houvven.guise.xposed.hook

import android.app.Activity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.guise.xposed.config.HooksValue
import com.houvven.ktx_xposed.hook.afterHookSomeSameNameMethod
import com.houvven.ktx_xposed.hook.afterHookedMethod
import com.houvven.ktx_xposed.hook.callMethod

class ScreenshotsHook : LoadPackageHandler {


    override fun onHook() {
        if (config.screenshotsFlag == HooksValue.SCREENSHOTS_UNHOOK) return
        if (config.screenshotsFlag == HooksValue.SCREENSHOTS_DISABLE) disableScreenshots()
        else if (config.screenshotsFlag == HooksValue.SCREENSHOTS_ENABLE) enableScreenshots()
    }

    private fun disableScreenshots() {
        Activity::class.java.afterHookedMethod(
            methodName = "onCreate", Bundle::class.java
        ) {
            val activity = it.thisObject as Activity
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
    }

    private fun enableScreenshots() {
        /* Window::class.java.beforeHookSomeSameNameMethod(
            "setFlags",
            "setPrivateFlags",
            "addFlags",
            "addPrivateFlags",
            "addSystem",
            "addSystemFlags"
        ) {
            if (it.args[0] == WindowManager.LayoutParams.FLAG_SECURE) it.setNullResult()
        } */

        Window::class.java.afterHookSomeSameNameMethod(
            "setFlags",
            "setPrivateFlags",
            "addFlags",
            "addPrivateFlags",
            "addSystemFlags"
        ) {
            if (it.args[0] == WindowManager.LayoutParams.FLAG_SECURE)
                it.thisObject.callMethod("clearFlags", WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

}