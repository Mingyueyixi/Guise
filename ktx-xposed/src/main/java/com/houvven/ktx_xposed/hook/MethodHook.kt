package com.houvven.ktx_xposed.hook

import com.houvven.ktx_xposed.utils.runXposedCatching
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import kotlin.jvm.Throws


lateinit var lppram: LoadPackageParam

fun setLpparam(lpparam: LoadPackageParam) {
    lppram = lpparam
}

val classLoader: ClassLoader
    get() = if (::lppram.isInitialized) lppram.classLoader else throw IllegalStateException("lpparam is not initialized")

@Throws(XposedHelpers.ClassNotFoundError::class)
fun findClass(className: String): Class<*> = XposedHelpers.findClass(className, classLoader)

fun findClassIfExists(className: String): Class<*>?  =
    XposedHelpers.findClassIfExists(className, classLoader)


fun hookMethod(clazz: Class<*>, methodName: String, vararg parameterTypesAndCallback: Any) =
    runXposedCatching {
        XposedHelpers.findAndHookMethod(clazz, methodName, *parameterTypesAndCallback)
    }

fun hookAllMethods(clazz: Class<*>, methodName: String, callback: XC_MethodHook) =
    XposedBridge.hookAllMethods(clazz, methodName, callback)

fun hookConstructor(clazz: Class<*>, vararg parameterTypesAndCallback: Any) =
    runXposedCatching {
        XposedHelpers.findAndHookConstructor(clazz, *parameterTypesAndCallback)
    }

fun hookAllConstructors(clazz: Class<*>, callback: XC_MethodHook) =
    XposedBridge.hookAllConstructors(clazz, callback)

