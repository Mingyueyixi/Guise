@file:Suppress("unused")
package com.houvven.ktx_xposed.hook

import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.jvm.Throws


@Throws(NoSuchFieldError::class)
fun Class<*>.findField(fieldName: String): Field =
    XposedHelpers.findField(this, fieldName)

fun Class<*>.findFiledIfExists(fieldName: String): Field? =
    XposedHelpers.findFieldIfExists(this, fieldName)

@Throws(NoSuchFieldError::class)
fun Class<*>.findFirstFieldByExactType(type: Class<*>): Field? =
    XposedHelpers.findFirstFieldByExactType(this, type)

/* @Throws(NoSuchFieldError::class)
fun KClass<*>.findField(fieldName: String) = this.java.findField(fieldName)

fun KClass<*>.findFiledIfExists(fieldName: String) = this.java.findFiledIfExists(fieldName)

@Throws(NoSuchFieldError::class)
fun KClass<*>.findFirstFieldByExactType(type: Class<*>) = this.java.findFirstFieldByExactType(type) */


@Throws(NoSuchMethodError::class, XposedHelpers.ClassNotFoundError::class)
fun Class<*>.findMethodExact(name: String, vararg parameterTypes: Class<*> = arrayOf()): Method =
    XposedHelpers.findMethodExact(this, name, *parameterTypes)

@Throws(NoSuchMethodError::class, XposedHelpers.ClassNotFoundError::class)
fun Class<*>.findMethodExact(name: String, vararg parameterTypes: Any): Method =
    XposedHelpers.findMethodExact(this, name, *parameterTypes)

fun Class<*>.findMethodExactIfExists(name: String, vararg parameterTypes: Class<*> = arrayOf()): Method? {
    var method: Method? = null
    kotlin
        .runCatching { this@findMethodExactIfExists.findMethodExact(name, *parameterTypes) }
        .onSuccess { method = it }

    return method
}

fun Class<*>.findMethodExactIfExists(name: String, vararg parameterTypes: Any): Method? =
    XposedHelpers.findMethodExactIfExists(this, name, *parameterTypes)

/* @Throws(NoSuchMethodError::class, XposedHelpers.ClassNotFoundError::class)
fun KClass<*>.findMethodExact(name: String, vararg parameterTypes: Class<*> = arrayOf()) =
    this.java.findMethodExact(name, *parameterTypes)

@Throws(NoSuchMethodError::class, XposedHelpers.ClassNotFoundError::class)
fun KClass<*>.findMethodExact(name: String, vararg parameterTypes: Any) =
    this.java.findMethodExact(name, *parameterTypes)

fun KClass<*>.findMethodExactIfExists(name: String, vararg parameterTypes: Class<*> = arrayOf()) =
    this.java.findMethodExactIfExists(name, *parameterTypes)

fun KClass<*>.findMethodExactIfExists(name: String, vararg parameterTypes: Any) =
    this.java.findMethodExactIfExists(name, *parameterTypes) */

@Throws(NoSuchMethodError::class)
fun Class<*>.findMethodBestMatch(name: String, vararg parameterTypes: Class<*> = arrayOf()): Method =
    XposedHelpers.findMethodBestMatch(this, name, *parameterTypes)

@Throws(NoSuchMethodError::class)
fun Class<*>.findMethodBestMatch(name: String, vararg parameterTypes: Any): Method =
    XposedHelpers.findMethodBestMatch(this, name, *parameterTypes)

fun Class<*>.findMethodBestMatch(name: String, parameterTypes: Array<Class<*>>, args: Array<Any>): Method? =
    XposedHelpers.findMethodBestMatch(this, name, parameterTypes, args)


fun Class<*>.findMethodsByExactParameters(returnType: Class<*>, vararg parameterTypes: Class<*> = arrayOf()): Array<Method> =
    XposedHelpers.findMethodsByExactParameters(this, returnType, *parameterTypes)


