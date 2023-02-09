package com.houvven.guise.module.apps

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import com.houvven.guise.ContextAmbient
import com.houvven.guise.xposed.PackageConfig
import com.houvven.ktx_xposed.SafeSharePrefs

@SuppressLint("StaticFieldLeak")
object AppInfoProvider {

    private val context = ContextAmbient.current

    private val packageManager get() = context.packageManager
    private val safeSharePrefs get() = SafeSharePrefs.of(context, PackageConfig.PREF_FILE_NAME)


    fun getList(): ArrayList<AppInfo> {
        val list = arrayListOf<AppInfo>()
        this.getInstalledPackages().forEach { packageInfo ->
            list.add(generateAppInfo(packageInfo))
        }
        return list
    }

    fun getMap(): HashMap<String, AppInfo> {
        val map = hashMapOf<String, AppInfo>()
        this.getInstalledPackages().forEach { packageInfo ->
            map[packageInfo.packageName] = generateAppInfo(packageInfo)
        }
        return map
    }

    private fun getInstalledPackages(): List<PackageInfo> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0L))
        else packageManager.getInstalledPackages(0)


    private fun generateAppInfo(packageInfo: PackageInfo): AppInfo {
        val applicationInfo = packageInfo.applicationInfo
        val packageName = applicationInfo.packageName

        val isEnable = safeSharePrefs.contains(packageName)
        val label = applicationInfo.loadLabel(packageManager).toString()
        val icon = applicationInfo.loadIcon(packageManager).toBitmap()
        val installTime = packageInfo.firstInstallTime
        val updateTime = packageInfo.lastUpdateTime
        val isSystemApp = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

        return AppInfo(
            isEnable = isEnable,
            label = label,
            packageName = packageName,
            icon = icon,
            installTime = installTime,
            updateTime = updateTime,
            isSystemApp = isSystemApp
        )
    }


}