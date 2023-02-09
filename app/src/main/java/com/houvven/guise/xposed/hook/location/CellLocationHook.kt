package com.houvven.guise.xposed.hook.location

import android.telephony.gsm.GsmCellLocation
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.setMethodResult

@Suppress("DEPRECATION")
class CellLocationHook : LoadPackageHandler {
    override fun onHook() {
        if (config.lac != -1) {
            GsmCellLocation::class.java.setMethodResult("getLac", config.lac)
        }
        if (config.cid != -1) {
            GsmCellLocation::class.java.setMethodResult("getCid", config.cid)
        }
    }

}