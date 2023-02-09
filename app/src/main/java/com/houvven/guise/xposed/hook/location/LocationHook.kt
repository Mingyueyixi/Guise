package com.houvven.guise.xposed.hook.location

import android.location.GnssStatus
import android.location.GpsStatus
import android.location.GpsStatus.GPS_EVENT_FIRST_FIX
import android.location.GpsStatus.GPS_EVENT_STARTED
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.SystemClock
import com.houvven.guise.xposed.LoadPackageHandler
import com.houvven.ktx_xposed.hook.afterHookedMethod
import com.houvven.ktx_xposed.hook.beforeHookConstructor
import com.houvven.ktx_xposed.hook.beforeHookedMethod
import com.houvven.ktx_xposed.hook.callMethod
import com.houvven.ktx_xposed.hook.callStaticMethodIfExists
import com.houvven.ktx_xposed.hook.findMethodExactIfExists
import com.houvven.ktx_xposed.hook.setAllMethodResult
import com.houvven.ktx_xposed.hook.setMethodResult
import com.houvven.ktx_xposed.hook.setSomeSameNameMethodResult


@Suppress("DEPRECATION")
class LocationHook : LoadPackageHandler, LocationHookBase() {


    private var latitude = config.latitude
    private var longitude = config.longitude

    init {
        if (config.randomOffset) {
            latitude += (Math.random() - 0.5) * 0.0001
            longitude += (Math.random() - 0.5) * 0.0001
        }
    }

    private val svCount = 5
    private val svidWithFlags = intArrayOf(1, 2, 3, 4, 5)
    private val cn0s = floatArrayOf(0F, 0F, 0F, 0F, 0F)
    private val elevations = cn0s.clone()
    private val azimuths = cn0s.clone()
    private val carrierFrequencies = cn0s.clone()
    private val basebandCn0DbHzs = cn0s.clone()


    override fun onHook() {

        if (longitude == -1.0 && latitude == -1.0) return

        if (config.makeWifiLocationFail) makeWifiLocationFail()
        if (config.makeCellLocationFail) makeCellLocationFail()

        fakeLatlng()
        setOtherServicesFail()  // 使其他定位服务失效
        hookGnssStatus()
        hookLocationUpdate()
        setLastLocation()
        removeNmeaListener()
        hookGpsStatus()
        hookGpsStatusListener()
    }

    private fun fakeLatlng() {
        Location::class.java.run {
            setMethodResult("getLongitude", longitude)
            setMethodResult("getLatitude", latitude)
        }
    }

    private fun setLastLocation() {
        LocationManager::class.java.setSomeSameNameMethodResult(
            "getLastLocation",
            "getLastKnownLocation",
            value = modifyLocation(Location(LocationManager.GPS_PROVIDER))
        )
    }

    private fun hookGpsStatusListener() {
        LocationManager::class.java.afterHookedMethod(
            methodName = "addGpsStatusListener",
            GpsStatus.Listener::class.java
        ) { param ->
            (param.args[0] as GpsStatus.Listener?)?.run {
                callMethod("onGpsStatusChanged", GPS_EVENT_STARTED)
                callMethod("onGpsStatusChanged", GPS_EVENT_FIRST_FIX)
            }
        }
    }

    private fun hookGpsStatus() {
        LocationManager::class.java.beforeHookedMethod(
            methodName = "getGpsStatus",
            GpsStatus::class.java
        ) { param ->
            val status = param.args[0] as GpsStatus? ?: return@beforeHookedMethod

            val method = GpsStatus::class.java.findMethodExactIfExists(
                "setStatus",
                Int::class.java,
                Array::class.java,
                Array::class.java,
                Array::class.java,
                Array::class.java
            )

            val method2 = GpsStatus::class.java.findMethodExactIfExists(
                "setStatus",
                GnssStatus::class.java,
                Int::class.java
            )

            if (method == null && method2 == null) {
                return@beforeHookedMethod
            }

            {
                method?.invoke(status, svCount, svidWithFlags, cn0s, elevations, azimuths)
                GnssStatus::class.java.callStaticMethodIfExists(
                    "wrap",
                    svCount,
                    svidWithFlags,
                    cn0s,
                    elevations,
                    azimuths,
                    carrierFrequencies,
                    basebandCn0DbHzs
                )?.let {
                    it as GnssStatus
                    method2?.invoke(status, it, System.currentTimeMillis().toInt())
                }
            }.let {
                it()
                param.args[0] = status
                param.result = status
                it()
                param.result = status
            }
        }
    }

    private fun hookGnssStatus() {
        GnssStatus::class.java.beforeHookConstructor(
            Int::class.java,
            IntArray::class.java,
            FloatArray::class.java,
            FloatArray::class.java,
            FloatArray::class.java,
            FloatArray::class.java,
            FloatArray::class.java
        ) {
            it.args[0] = svCount
            it.args[1] = svidWithFlags
            it.args[2] = cn0s
            it.args[3] = elevations
            it.args[4] = azimuths
            it.args[5] = carrierFrequencies
            it.args[6] = basebandCn0DbHzs
        }
    }

    private fun hookLocationUpdate() {
        val requestLocationUpdates = "requestLocationUpdates"
        val requestSingleUpdate = "requestSingleUpdate"

        LocationManager::class.java.run {
            var target: String
            for (method in declaredMethods) {
                if (method.name != requestLocationUpdates && method.name != requestSingleUpdate) continue
                val indexOf = method.parameterTypes.indexOf(LocationListener::class.java)
                if (indexOf == -1) continue
                val paramsTypes = method.parameterTypes
                target = method.name
                afterHookedMethod(target, *paramsTypes) {
                    val listener = it.args[indexOf] as LocationListener
                    val location = modifyLocation(Location(LocationManager.GPS_PROVIDER))
                    listener.onLocationChanged(location)
                }
            }
        }
    }

    private fun removeNmeaListener() {
        LocationManager::class.java.setAllMethodResult("addNmeaListener", false)
    }

    private fun modifyLocation(location: Location): Location {
        return location.also {
            it.longitude = longitude
            it.latitude = latitude
            it.provider = LocationManager.GPS_PROVIDER
            it.accuracy = 10.0f
            it.time = System.currentTimeMillis()
            it.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }
    }
}