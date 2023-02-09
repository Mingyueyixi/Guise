package com.houvven.ktx_xposed

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

/**
 * @constructor
 * @param context
 * @param name
 * @param strict default is true
 */
@SuppressLint("WorldReadableFiles")
class SafeSharePrefs private constructor(context: Context, name: String, strict: Boolean) {

    companion object {
        @JvmStatic
        fun of(context: Context, name: String) = SafeSharePrefs(context, name, false).prefs

        @JvmStatic
        fun ofStrict(context: Context, name: String) = SafeSharePrefs(context, name, true).prefs
    }

    var prefs: SharedPreferences

    init {
        prefs = when {
            HookStatus.isActivated() -> context.getSharedPreferences(
                name, Context.MODE_WORLD_READABLE
            )

            !strict -> context.getSharedPreferences(name, Context.MODE_PRIVATE)
            else -> throw RuntimeException("This Mode's SafeSharePrefs is strict, but mode is not activated.")
        }
    }
}
