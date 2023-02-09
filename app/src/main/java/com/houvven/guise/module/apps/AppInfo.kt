package com.houvven.guise.module.apps

import android.graphics.Bitmap

data class AppInfo(
    var isEnable: Boolean,
    val label: String,
    val packageName: String,
    val icon: Bitmap,
    val installTime: Long,
    val updateTime: Long,
    val isSystemApp: Boolean,
)
