package com.houvven.ktx_xposed.hook

import com.houvven.ktx_xposed.utils.runXposedCatching
import de.robv.android.xposed.XposedHelpers

inline fun <reified T> setStaticField(className: String, fieldName: String, value: T) {
    runXposedCatching {
        findClass(className).setStaticField(fieldName, value)
    }
}

inline fun <reified T> Class<*>.setStaticField(fieldName: String, value: T) {
    runXposedCatching {
        when (value) {
            is Boolean -> XposedHelpers.setStaticBooleanField(this, fieldName, value)
            is Byte -> XposedHelpers.setStaticByteField(this, fieldName, value)
            is Char -> XposedHelpers.setStaticCharField(this, fieldName, value)
            is Short -> XposedHelpers.setStaticShortField(this, fieldName, value)
            is Int -> XposedHelpers.setStaticIntField(this, fieldName, value)
            is Long -> XposedHelpers.setStaticLongField(this, fieldName, value)
            is Float -> XposedHelpers.setStaticFloatField(this, fieldName, value)
            is Double -> XposedHelpers.setStaticDoubleField(this, fieldName, value)
            else -> XposedHelpers.setStaticObjectField(this, fieldName, value)
        }
    }
}

inline fun <reified T> setInstanceField(instance: Any, fieldName: String, value: T) {
    runXposedCatching {
        when (value) {
            is Boolean -> XposedHelpers.setBooleanField(instance, fieldName, value)
            is Byte -> XposedHelpers.setByteField(instance, fieldName, value)
            is Char -> XposedHelpers.setCharField(instance, fieldName, value)
            is Short -> XposedHelpers.setShortField(instance, fieldName, value)
            is Int -> XposedHelpers.setIntField(instance, fieldName, value)
            is Long -> XposedHelpers.setLongField(instance, fieldName, value)
            is Float -> XposedHelpers.setFloatField(instance, fieldName, value)
            is Double -> XposedHelpers.setDoubleField(instance, fieldName, value)
            else -> XposedHelpers.setObjectField(instance, fieldName, value)
        }
    }
}
