package com.houvven.guise.module.preset

import com.houvven.guise.module.PresetAdapter
import com.houvven.guise.xposed.config.HooksValue

enum class NetworkPreset(override val label: String, override val value: String) :
    PresetAdapter {

    MOBILE_5G("5G", HooksValue.NET_MOBILE_5G.toString()),
    MOBILE_4G("4G", HooksValue.NET_MOBILE_4G.toString()),
    MOBILE_3G("3G", HooksValue.NET_MOBILE_3G.toString()),
    MOBILE_2G("2G", HooksValue.NET_MOBILE_2G.toString()),
    WIFI("WiFi", HooksValue.NET_WIFI.toString()),
    NONE("None", HooksValue.NET_NONE.toString());

}