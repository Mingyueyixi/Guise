package com.houvven.guise.xposed.other

import android.app.Application
import android.content.Context
import com.houvven.guise.module.ktx.showToast
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.afterHookedMethod

class HookSuccessHint : LoadPackageHandler {

    override fun onHook() {
        if (!config.hookSuccessHint) return
        Application::class.java.afterHookedMethod(
            methodName = "attach",
            Context::class.java
        ) { param ->
            val context = param.args[0] as Context
            context.showToast("Guise's hook success prompt")
        }
    }
}