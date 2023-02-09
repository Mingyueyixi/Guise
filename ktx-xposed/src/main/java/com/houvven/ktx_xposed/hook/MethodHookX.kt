@file:Suppress("unused")

package com.houvven.ktx_xposed.hook

import com.houvven.ktx_xposed.utils.runXposedCatching
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge


object HookType {
    const val BEFORE = 0
    const val AFTER = 1
}


inline fun beforeHookedMethod(
    className: String,
    methodName: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).beforeHookedMethod(methodName, *parameterTypes, callback = callback)
}

inline fun Class<*>.beforeHookedMethod(
    methodName: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    hookMethod(this, methodName, *parameterTypes, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            callback(param)
        }
    })
}

inline fun afterHookedMethod(
    className: String,
    methodName: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).afterHookedMethod(methodName, *parameterTypes, callback = callback)
}

inline fun Class<*>.afterHookedMethod(
    methodName: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    hookMethod(this, methodName, *parameterTypes, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            callback(param)
        }
    })
}


inline fun beforeHookAllMethods(
    className: String,
    methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).beforeHookAllMethods(methodName, callback = callback)
}

inline fun Class<*>.beforeHookAllMethods(
    methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = hookAllMethods(this, methodName, object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        callback(param)
    }
})


inline fun afterHookAllMethods(
    className: String,
    methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).afterHookAllMethods(methodName, callback = callback)
}

inline fun Class<*>.afterHookAllMethods(
    methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = hookAllMethods(this, methodName, object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        callback(param)
    }
})


inline fun beforeHookConstructor(
    className: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).beforeHookConstructor(*parameterTypes, callback = callback)
}

inline fun Class<*>.beforeHookConstructor(
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = hookConstructor(this, *parameterTypes, object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        callback(param)
    }
})

inline fun afterHookConstructor(
    className: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).afterHookConstructor(*parameterTypes, callback = callback)
}

inline fun Class<*>.afterHookConstructor(
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = hookConstructor(this, *parameterTypes, object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        callback(param)
    }
})


inline fun beforeHookAllConstructors(
    className: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).beforeHookAllConstructors(callback = callback)
}

inline fun Class<*>.beforeHookAllConstructors(
    crossinline callback: (MethodHookParam) -> Unit,
) = hookAllConstructors(this, object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        callback(param)
    }
})

inline fun afterHookAllConstructors(
    className: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = runXposedCatching {
    findClass(className).afterHookAllConstructors(callback = callback)
}

inline fun Class<*>.afterHookAllConstructors(
    crossinline callback: (MethodHookParam) -> Unit,
) = hookAllConstructors(this, object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        callback(param)
    }
})


/**
 * Hook不同类下的多个同名方法 内部使用[XposedBridge.hookAllMethods]实现
 */
inline fun beforeHookSomeSameNameMethodForAnyClass(
    classAndMethodName: List<Pair<Class<*>, String>>,
    crossinline callback: (MethodHookParam) -> Unit,
) = classAndMethodName.let {
    val map = mutableMapOf<Class<*>, MutableSet<XC_MethodHook.Unhook?>>()
    it.forEach { (clazz, methodName) ->
        val unhooks = clazz.beforeHookAllMethods(methodName, callback = callback)
        map[clazz] = unhooks
    }
    map
}

inline fun afterHookSomeSameNameMethodForAnyClass(
    classAndMethodName: List<Pair<Class<*>, String>>,
    crossinline callback: (MethodHookParam) -> Unit,
) = classAndMethodName.run {
    val map = mutableMapOf<Class<*>, MutableSet<XC_MethodHook.Unhook?>>()
    forEach { (clazz, methodName) ->
        val unhooks = clazz.afterHookAllMethods(methodName, callback = callback)
        map[clazz] = unhooks
    }
    map
}

inline fun beforeHookSomeSameNameMethod(
    className: String,
    vararg methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = methodName.run {
    map {
        beforeHookAllMethods(className, it, callback = callback)
    }.toSet()
}

inline fun Class<*>.beforeHookSomeSameNameMethod(
    vararg methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = methodName.run {
    map {
        this@beforeHookSomeSameNameMethod.beforeHookAllMethods(it, callback = callback)
    }.toSet()
}

inline fun afterHookSomeSameNameMethod(
    className: String,
    vararg methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = methodName.run {
    map {
        afterHookAllMethods(className, it, callback = callback)
    }.toSet()
}

inline fun Class<*>.afterHookSomeSameNameMethod(
    vararg methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = methodName.run {
    map {
        this@afterHookSomeSameNameMethod.afterHookAllMethods(it, callback = callback)
    }.toSet()
}


inline fun replaceMethod(
    clazz: Class<*>,
    methodName: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Any?,
) = hookMethod(clazz, methodName, *parameterTypes, object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam): Any? {
        return callback(param)
    }
})


// 扩展方法 Time: 2023/1/20

inline fun <reified T> beforeHookedMethod(
    methodName: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.beforeHookedMethod(methodName, *parameterTypes, callback = callback)

inline fun <reified T> afterHookedMethod(
    methodName: String,
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.afterHookedMethod(methodName, *parameterTypes, callback = callback)

inline fun <reified T> beforeHookAllMethods(
    methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.beforeHookAllMethods(methodName, callback = callback)

inline fun <reified T> afterHookAllMethods(
    methodName: String,
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.afterHookAllMethods(methodName, callback = callback)

inline fun <reified T> beforeHookConstructor(
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.beforeHookConstructor(*parameterTypes, callback = callback)

inline fun <reified T> afterHookConstructor(
    vararg parameterTypes: Class<*>,
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.afterHookConstructor(*parameterTypes, callback = callback)

inline fun <reified T> beforeHookAllConstructors(
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.beforeHookAllConstructors(callback = callback)

inline fun <reified T> afterHookAllConstructors(
    crossinline callback: (MethodHookParam) -> Unit,
) = T::class.java.afterHookAllConstructors(callback = callback)


fun setMethodResult(
    className: String,
    methodName: String,
    value: Any?,
    type: Int = HookType.BEFORE,
    vararg parameterTypes: Class<*>,
) = runXposedCatching {
    findClass(className).setMethodResult(methodName, value, type, *parameterTypes)
}

fun Class<*>.setMethodResult(
    methodName: String,
    value: Any?,
    type: Int = HookType.BEFORE,
    vararg parameterTypes: Class<*>,
) = this.run {
    if (type == HookType.BEFORE)
        beforeHookedMethod(methodName, *parameterTypes) { it.result = value }
    else
        afterHookedMethod(methodName, *parameterTypes) { it.result = value }
}

fun setAllMethodResult(
    className: String,
    methodName: String,
    value: Any?,
    type: Int = HookType.BEFORE,
) = runXposedCatching {
    findClass(className).setAllMethodResult(methodName, value, type)
}

fun Class<*>.setAllMethodResult(
    methodName: String,
    value: Any?,
    type: Int = HookType.BEFORE,
) = this.run {
    if (type == 0) beforeHookAllMethods(methodName) { it.result = value }
    else afterHookAllMethods(methodName) { it.result = value }
}

fun setSomeSameNameMethodResultForAnyClass(
    classAndMethodName: List<Pair<Class<*>, String>>,
    value: Any?,
    type: Int = HookType.BEFORE,
) = if (type == HookType.BEFORE)
    beforeHookSomeSameNameMethodForAnyClass(classAndMethodName) { it.result = value }
else
    afterHookSomeSameNameMethodForAnyClass(classAndMethodName) { it.result = value }


fun Class<*>.setSomeSameNameMethodResult(
    vararg methodName: String,
    value: Any?,
    type: Int = HookType.BEFORE,
) = runXposedCatching {
    if (type == HookType.BEFORE)
        this.beforeHookSomeSameNameMethod(*methodName) { it.result = value }
    else
        this.afterHookSomeSameNameMethod(*methodName) { it.result = value }
}

