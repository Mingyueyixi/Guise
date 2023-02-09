package com.houvven.guise.module.preset

import com.houvven.guise.module.PresetAdapter

enum class LanguagePreset(override val label: String, override val value: String) : PresetAdapter {

    SimplifiedChinese("简体中文", "zh_CN"),
    HansChinese("简体中文(Hans)", "zh_Hans"),
    TraditionalChineseTW("繁體中文(TW)", "zh_TW"),
    TraditionalChineseHK("繁體中文(HK)", "zh_HK"),
    English("English", "en_US"),
    Japanese("日本語", "ja_JP"),
    Korean("한국어", "ko_KR"),
    French("Français", "fr_FR"),
    German("Deutsch", "de_DE"),
    Malay("Bahasa Melayu", "ms_MY"),
    Indonesian("Bahasa Indonesia", "id_ID"),
    Thai("ภาษาไทย", "th_TH"),
    Vietnamese("Tiếng Việt", "vi_VN"),
    ;

}