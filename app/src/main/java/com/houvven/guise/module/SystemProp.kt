package com.houvven.guise.module

object SystemProp {

    @JvmStatic
    val abi: String
        get() {
            val abi = com.houvven.lib.command.ShellActuators.exec("getprop ro.product.cpu.abi", true)
            return if (abi.isSuccess) abi.getOrNull() ?: "unknown" else "unknown"
        }


    @JvmStatic
    val architecture: String
        get() = when (abi) {
            "arm64-v8a" -> "arm64"
            "armeabi-v7a" -> "arm"
            "x86_64" -> "x86_64"
            "x86" -> "x86"
            else -> "unknown"
        }

}