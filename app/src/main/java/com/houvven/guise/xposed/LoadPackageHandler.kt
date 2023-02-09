package com.houvven.guise.xposed

import com.houvven.ktx_xposed.LoadPackageHookAdapter

interface LoadPackageHandler : LoadPackageHookAdapter {
    val config get() = PackageConfig.current
}