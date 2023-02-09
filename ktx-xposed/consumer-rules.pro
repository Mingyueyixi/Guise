-keep class com.houvven.ktx_xposed.HookStatus {*;}
-keep class com.houvven.ktx_xposed.HookStatus$* {*;}
-keep class com.houvven.ktx_xposed.HookStatus$Companion {*;}

-keep class * implements de.robv.android.xposed.IXposedHookLoadPackage {
    public void *(de.robv.android.xposed.callbacks.XC_LoadPackage$LoadPackageParam);
}

-dontwarn com.houvven.ktx_xposed.**