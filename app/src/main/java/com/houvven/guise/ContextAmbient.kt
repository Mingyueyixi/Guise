package com.houvven.guise

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVLogLevel

class ContextAmbient : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var current: Context

        fun getSharedPreferences(
            name: String = BuildConfig.APPLICATION_ID,
            mode: Int = Context.MODE_PRIVATE
        ) = current.getSharedPreferences(name, mode)
    }

    override fun onCreate() {
        super.onCreate()
        current = applicationContext
        MMKV.initialize(this, MMKVLogLevel.LevelNone)
    }


}