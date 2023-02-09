package com.houvven.guise.xposed.hook.location

import android.telephony.CellIdentityCdma
import android.telephony.CellIdentityGsm
import android.telephony.CellIdentityLte
import android.telephony.CellIdentityNr
import android.telephony.CellIdentityTdscdma
import android.telephony.CellIdentityWcdma
import android.telephony.CellInfoNr
import android.telephony.gsm.GsmCellLocation
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.setMethodResult

@Suppress("DEPRECATION")
class CellLocationHook : LoadPackageHandler {
    override fun onHook() {
        if (config.lac != -1) {
            GsmCellLocation::class.java.setMethodResult("getLac", config.lac)
            CellIdentityGsm::class.java.setMethodResult("getLac", config.lac)
            CellIdentityLte::class.java.setMethodResult("getTac", config.lac)
            CellIdentityTdscdma::class.java.setMethodResult("getLac", config.lac)
            CellIdentityCdma::class.java.setMethodResult("getNetworkId", config.lac)
            CellIdentityWcdma::class.java.setMethodResult("getLac", config.lac)
        }
        if (config.cid != -1) {
            GsmCellLocation::class.java.setMethodResult("getCid", config.cid)
            CellIdentityGsm::class.java.setMethodResult("getCid", config.cid)
            CellIdentityLte::class.java.setMethodResult("getCi", config.cid)
            CellIdentityTdscdma::class.java.setMethodResult("getCid", config.cid)
            CellIdentityCdma::class.java.setMethodResult("getBasestationId", config.cid)
            CellIdentityWcdma::class.java.setMethodResult("getCid", config.cid)
        }
    }

}