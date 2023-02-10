package com.houvven.guise.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.houvven.guise.constant.AppConfigKey
import com.houvven.ktx_xposed.HookStatus

val isHooked: Boolean
    get() = alwaysActivate.value || HookStatus.isActivated()


val alwaysActivate by derivedStateOf {
    mutableStateOf(AppConfigKey.run {
        mmkv.decodeBool(ALWAYS_ACTIVE, false)
    })
}

/**
 * Always dark mode
 */
val alwaysDarkMode by derivedStateOf {
    mutableStateOf(AppConfigKey.run {
        mmkv.decodeBool(ALWAYS_DARK_MODE, false)
    })
}
