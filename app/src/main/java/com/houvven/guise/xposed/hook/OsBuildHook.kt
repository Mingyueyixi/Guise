package com.houvven.guise.xposed.hook

import android.os.Build
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.setStaticField

class OsBuildHook : LoadPackageHandler {

    override fun onHook() {
        config.run {
            mapOf(
                arrayOf("BRAND", "MANUFACTURER") to brand,
                arrayOf("MODEL") to model,
                arrayOf("PRODUCT") to product,
                arrayOf("DEVICE") to device,
                arrayOf("BOARD") to board,
                arrayOf("HARDWARE") to hardware,
                arrayOf("FINGERPRINT") to fingerPrint,
            ).forEach { (fields, value) ->
                if (value.isNotBlank()) fields.forEach { field ->
                    Build::class.java.setStaticField(field, value)
                }
            }

            mapOf(
                arrayOf("SDK_INT") to sdkInt,
                arrayOf("RELEASE") to androidVersion,
                arrayOf("BASE_OS") to fingerPrint,
            ).forEach { (fields, value) ->
                if (value.toString().isNotBlank() && value.toString() != "-1") {
                    fields.forEach { field ->
                        Build.VERSION::class.java.setStaticField(field, value)
                    }
                }
            }
        }


    }

}