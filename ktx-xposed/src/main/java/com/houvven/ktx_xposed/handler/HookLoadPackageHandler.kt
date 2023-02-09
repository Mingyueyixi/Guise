package com.houvven.ktx_xposed.handler

import com.houvven.ktx_xposed.HookStatus
import com.houvven.ktx_xposed.LoadPackageHookAdapter
import com.houvven.ktx_xposed.hook.setLpparam
import com.houvven.ktx_xposed.hook.setMethodResult
import com.houvven.ktx_xposed.logger.XposedLogger
import com.houvven.ktx_xposed.utils.runXposedCatching
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

interface HookLoadPackageHandler : IXposedHookLoadPackage {

    val packageName: String

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        setLpparam(lpparam)

        if (packageName == lpparam.packageName)
            doActivate()
        else {
            XposedLogger.doHookModuleLog()
            loadPackage(lpparam)
        }
    }

    fun loadPackage(lpparam: LoadPackageParam)

    fun doHookLoadPackage(hooks: List<LoadPackageHookAdapter>) =
        hooks.forEach { runXposedCatching { it.onHook() } }

    fun doActivate() {
        val name = HookStatus.Companion::class.java.name
        setMethodResult(name, "isActivated", true)
    }
}