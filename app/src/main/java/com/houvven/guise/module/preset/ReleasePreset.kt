package com.houvven.guise.module.preset

import com.houvven.guise.module.PresetAdapter

enum class ReleasePreset(override val label: String, override val value: String) : PresetAdapter {
    ANDROID_1("Android 1", "1"),
    ANDROID_1_1("Android 1.1", "1.1"),
    ANDROID_1_5("Android 1.5", "1.5"),
    ANDROID_1_6("Android 1.6", "1.6"),
    ANDROID_2_0("Android 2.0", "2"),
    ANDROID_2_0_1("Android 2.0.1", "2.0.1"),
    ANDROID_2_1("Android 2.1", "2.1"),
    ANDROID_2_2("Android 2.2", "2.2"),
    ANDROID_2_3("Android 2.3", "2.3"),
    ANDROID_2_3_3("Android 2.3.3", "2.3.3"),
    ANDROID_3_0("Android 3.0", "3"),
    ANDROID_3_1("Android 3.1", "3.1"),
    ANDROID_3_2("Android 3.2", "3.2"),
    ANDROID_4_0("Android 4.0", "4"),
    ANDROID_4_0_3("Android 4.0.3", "4.0.3"),
    ANDROID_4_1("Android 4.1", "4.1"),
    ANDROID_4_2("Android 4.2", "4.2"),
    ANDROID_4_3("Android 4.3", "4.3"),
    ANDROID_4_4("Android 4.4", "4.4"),
    ANDROID_4_4W("Android 4.4W", "4.4W"),
    ANDROID_5_0("Android 5.0", "5"),
    ANDROID_5_1("Android 5.1", "5.1"),
    ANDROID_6_0("Android 6.0", "6"),
    ANDROID_7_0("Android 7.0", "7"),
    ANDROID_7_1("Android 7.1", "7.1"),
    ANDROID_8_0("Android 8.0", "8"),
    ANDROID_8_1("Android 8.1", "8.1"),
    ANDROID_9_0("Android 9.0", "9"),
    ANDROID_10_0("Android 10", "10"),
    ANDROID_11_0("Android 11", "11"),
    ANDROID_12_0("Android 12", "12"),
    ANDROID_13_0("Android 13", "13")

    ;
}