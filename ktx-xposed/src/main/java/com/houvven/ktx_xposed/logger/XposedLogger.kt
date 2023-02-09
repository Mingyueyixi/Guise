package com.houvven.ktx_xposed.logger

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.contentValuesOf
import com.houvven.ktx_xposed.hook.afterHookedMethod
import com.houvven.ktx_xposed.hook.lppram
import java.io.File


@SuppressLint("StaticFieldLeak")
object XposedLogger {

    private const val TAG = "XposedLogger"

    object Level {
        const val DEBUG = 'D'
        const val INFO = 'I'
        const val ERROR = 'E'
    }

    private val logList = mutableListOf<Pair<Char, String>>()

    private val uri = Uri.parse("content://com.houvven.xposed.runtime.log/module_log")

    fun d(msg: String) {
        basicLog(Level.DEBUG, msg)
    }

    fun i(msg: String) {
        basicLog(Level.INFO, msg)
    }

    fun e(msg: String) {
        basicLog(Level.ERROR, msg)
    }

    fun e(throwable: Throwable) {
        basicLog(Level.ERROR, throwable.toString())
    }

    @SuppressLint("PrivateApi")
    private fun basicLog(level: Char, msg: String) {
        logList.add(level to msg)
    }

    fun doHookModuleLog() {
        Activity::class.java.afterHookedMethod("onPause") { hookParam ->
            val application = hookParam.thisObject as Activity
            if (logList.isEmpty()) return@afterHookedMethod

            runCatching {
                logList.forEach { log ->
                    contentValuesOf(
                        "type" to log.first.toString(),
                        "source" to lppram.packageName,
                        "message" to log.second
                    ).let {
                        application.contentResolver.insert(uri, it)
                    }
                }
                logList.clear()
            }.onFailure {

            }
        }
    }


}
