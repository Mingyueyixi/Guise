package com.houvven.guise.ui

import com.houvven.ktx_xposed.HookStatus

val isHooked: Boolean
    get() = HookStatus.isActivated()