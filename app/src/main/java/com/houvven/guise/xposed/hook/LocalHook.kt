package com.houvven.guise.xposed.hook

import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.setMethodResult
import java.util.Locale

class LocalHook : LoadPackageHandler {

    override fun onHook() {
        var language = config.language
        var country: String

        if (language.isBlank()) return

        language.split("_").let {
            language = it[0]
            country = if (it.size < 2) "" else it[1]
        }

        runCatching {
            if (country.isBlank()) Locale(language)
            else Locale(language, country)
        }.onSuccess { locale ->
            country = locale.country
            val displayLanguage = locale.displayLanguage
            val displayCountry = locale.displayCountry
            val displayName = locale.displayName
            val displayVariant = locale.displayVariant
            val displayScript = locale.displayScript
            val script = locale.script
            val variant = locale.variant
            val toLanguageTag = locale.toLanguageTag()
            val toString = locale.toString()

            Locale::class.java.run {
                setMethodResult("getDefault", locale)
                setMethodResult("getLanguage", language)
                setMethodResult("getCountry", country)
                setMethodResult("getVariant", variant)
                setMethodResult("getScript", script)
                setMethodResult("getDisplayLanguage", displayLanguage)
                setMethodResult("getDisplayCountry", displayCountry)
                setMethodResult("getDisplayName", displayName)
                setMethodResult("getDisplayVariant", displayVariant)
                setMethodResult("getDisplayScript", displayScript)
                setMethodResult("toLanguageTag", toLanguageTag)
                setMethodResult("toString", toString)
            }
        }
    }
}