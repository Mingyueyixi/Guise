package com.houvven.guise.xposed

import com.houvven.guise.BuildConfig
import com.houvven.guise.xposed.config.ModuleConfig
import com.houvven.guise.xposed.hook.BatteryHook
import com.houvven.guise.xposed.hook.LocalHook
import com.houvven.guise.xposed.hook.location.LocationHook
import com.houvven.guise.xposed.hook.netowork.NetworkHook
import com.houvven.guise.xposed.hook.OsBuildHook
import com.houvven.guise.xposed.hook.ScreenshotsHook
import com.houvven.guise.xposed.hook.UniquelyIdHook
import com.houvven.guise.xposed.hook.location.CellLocationHook
import com.houvven.guise.xposed.other.BlankPass
import com.houvven.guise.xposed.other.HookSuccessHint
import com.houvven.ktx_xposed.handler.HookLoadPackageHandler
import com.houvven.ktx_xposed.logger.XposedLogger
import de.robv.android.xposed.callbacks.XC_LoadPackage

@Suppress("unused")
class HookInit : HookLoadPackageHandler {

    private val packageConfig: ModuleConfig
        get() = PackageConfig.current

    override val packageName = BuildConfig.APPLICATION_ID

    override fun loadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        XposedLogger.i("start loadPackage: ${lpparam.packageName} [${lpparam.appInfo.name}]")
        PackageConfig.doRefresh(lpparam.packageName)
        if (!packageConfig.isEnable) {
            XposedLogger.i("loadPackage: ${lpparam.packageName} is not enable, skip.")
            return
        }

        listOf(
            HookSuccessHint(),
            BatteryHook(),
            LocalHook(),
            LocationHook(),
            CellLocationHook(),
            NetworkHook(),
            OsBuildHook(),
            ScreenshotsHook(),
            UniquelyIdHook(),
            BlankPass(),
        ).let { doHookLoadPackage(it) }
    }

}