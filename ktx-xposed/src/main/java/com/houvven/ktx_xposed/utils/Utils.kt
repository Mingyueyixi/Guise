package com.houvven.ktx_xposed.utils

import de.robv.android.xposed.XC_MethodHook.MethodHookParam

fun MethodHookParam.setNullResult() {
    result = null
}

fun MethodHookParam.hasTypeArg(type: Class<*>): Boolean {
    return args.any { it.javaClass == type }
}

fun MethodHookParam.getTypeArgIndexOfFirst(type: Class<*>): Int {
    return args.indexOfFirst { it.javaClass == type }
}
