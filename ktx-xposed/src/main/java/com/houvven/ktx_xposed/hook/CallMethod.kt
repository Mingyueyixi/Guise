package com.houvven.ktx_xposed.hook

import com.houvven.ktx_xposed.utils.runXposedCatching
import de.robv.android.xposed.XposedHelpers


@Throws(
    NoSuchMethodError::class,
    XposedHelpers.InvocationTargetError::class
)
fun Class<*>.callStaticMethod(
    methodName: String, vararg args: Any?
): Any? = runXposedCatching { XposedHelpers.callStaticMethod(this, methodName, *args) }


@Throws(
    NoSuchMethodError::class,
    XposedHelpers.InvocationTargetError::class
)
fun Class<*>.callStaticMethod(
    methodName: String, parameterTypes: Array<Class<*>>, vararg args: Any?
): Any? =
    runXposedCatching { XposedHelpers.callStaticMethod(this, methodName, parameterTypes, *args) }


@Throws(
    NoSuchMethodError::class,
    XposedHelpers.InvocationTargetError::class
)
fun Any.callMethod(
    methodName: String, vararg args: Any?
): Any? = runXposedCatching { XposedHelpers.callMethod(this, methodName, *args) }


@Throws(
    NoSuchMethodError::class,
    XposedHelpers.InvocationTargetError::class
)
fun Any.callMethod(
    methodName: String, parameterTypes: Array<Class<*>>, vararg args: Any?
): Any? = runXposedCatching { XposedHelpers.callMethod(this, methodName, parameterTypes, *args) }


//


fun Any.callMethodIfExists(
    methodName: String, vararg args: Any
): Any? {
    var result: Any? = null
    runCatching { this.callMethod(methodName, *args) }.onSuccess { result = it }
    return result
}


fun Any.callMethodIfExists(
    methodName: String, parameterTypes: Array<Class<*>>, vararg args: Any
): Any? {
    var result: Any? = null
    runCatching { this.callMethod(methodName, parameterTypes, *args) }.onSuccess { result = it }
    return result
}

fun Class<*>.callStaticMethodIfExists(
    methodName: String, vararg args: Any
): Any? {
    var result: Any? = null
    runCatching { this.callStaticMethod(methodName, *args) }.onSuccess { result = it }
    return result
}

fun Class<*>.callStaticMethodIfExists(
    methodName: String, parameterTypes: Array<Class<*>>, vararg args: Any
): Any? {
    var result: Any? = null
    runCatching { this.callStaticMethod(methodName, parameterTypes, *args) }.onSuccess { result = it }
    return result
}

