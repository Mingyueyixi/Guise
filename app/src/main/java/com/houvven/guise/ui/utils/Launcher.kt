package com.houvven.guise.ui.utils

import android.content.ComponentName
import android.content.pm.PackageManager
import com.houvven.guise.ContextAmbient
import com.houvven.guise.ui.MainActivity


fun hideLauncherIcon(flag: Boolean) {
    ContextAmbient.current.packageManager.setComponentEnabledSetting(
        ComponentName(ContextAmbient.current, MainActivity::class.java.name + "Alias"),
        if (!flag) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
    )
}

fun isHideLauncherIcon(): Boolean {
    return ContextAmbient.current.packageManager.getComponentEnabledSetting(
        ComponentName(ContextAmbient.current, MainActivity::class.java.name + "Alias")
    ) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
}