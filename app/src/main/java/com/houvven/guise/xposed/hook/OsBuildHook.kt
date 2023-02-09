package com.houvven.guise.xposed.hook

import android.os.Build
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.setStaticField

class OsBuildHook : LoadPackageHandler {

    override fun onHook() {
        config.run {
            mapOf(
                brand to arrayOf("BRAND", "MANUFACTURER"),
                model to arrayOf("MODEL"),
                product to arrayOf("PRODUCT"),
                device to arrayOf("DEVICE"),
                board to arrayOf("BOARD"),
                hardware to arrayOf("HARDWARE"),
                fingerPrint to arrayOf("FINGERPRINT"),
            ).forEach { (value, fields) ->
                if (value.isNotBlank()) fields.forEach { field ->
                    Build::class.java.setStaticField(field, value)
                }
            }

            mapOf(
                sdkInt to arrayOf("SDK_INT"),
                androidVersion to arrayOf("RELEASE"),
                fingerPrint to arrayOf("BASE_OS"),
            ).forEach { (value, fields) ->
                if (value.toString().isNotBlank() && value.toString() != "-1") {
                    fields.forEach { field ->
                        Build.VERSION::class.java.setStaticField(field, value)
                    }
                }
            }
        }


    }

}