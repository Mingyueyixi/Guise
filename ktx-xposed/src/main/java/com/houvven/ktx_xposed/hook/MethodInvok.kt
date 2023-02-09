package com.houvven.ktx_xposed.hook

import java.lang.reflect.Method

fun Method.setMethodResult(value: Any?, type: Int = HookType.BEFORE) {
    this.declaringClass.setMethodResult(this.name, value, type)
}

