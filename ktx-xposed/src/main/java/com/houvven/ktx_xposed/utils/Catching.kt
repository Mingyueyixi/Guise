package com.houvven.ktx_xposed.utils

import com.houvven.ktx_xposed.logger.XposedLogger

inline fun <R> runXposedCatching(block: () -> R): R? {
    return try {
        block()
    } catch (e: Throwable) {
        XposedLogger.e(e)
        null
    }
}