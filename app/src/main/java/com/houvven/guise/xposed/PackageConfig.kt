package com.houvven.guise.xposed

import android.content.SharedPreferences
import com.houvven.guise.BuildConfig
import com.houvven.guise.ContextAmbient
import com.houvven.guise.xposed.config.ModuleConfig
import com.houvven.ktx_xposed.SafeSharePrefs
import de.robv.android.xposed.XSharedPreferences

object PackageConfig {

    lateinit var current: ModuleConfig

    const val PREF_FILE_NAME = "XposedDeployInfo"

    val safePrefs: SharedPreferences
        get() = SafeSharePrefs.of(ContextAmbient.current, PREF_FILE_NAME)

    val xSharedPrefs by lazy {
        XSharedPreferences(BuildConfig.APPLICATION_ID, PREF_FILE_NAME).also {
            it.makeWorldReadable()
        }
    }

    fun doRefresh(packageName: String) {
        val b = xSharedPrefs.contains(packageName)
        current = when {
            b -> ModuleConfig.fromJson(xSharedPrefs.getString(packageName, "")!!)
            else -> ModuleConfig()
        }

        current.packageName = packageName
    }

}