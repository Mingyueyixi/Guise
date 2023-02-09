package com.houvven.guise.ui.utils

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver


@SuppressLint("ComposableNaming")
@Composable
fun requestPermission(permission: String, onResult: (Boolean) -> Unit = {}) {
    val context = LocalContext.current
    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { onResult(it) }
    ).let {
        if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            it.launch(permission)
        } else {
            onResult(true)
            return
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun requestPermissions(
    permissions: Array<String>,
    onResult: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { booleanMap -> onResult(booleanMap.values.all { it }) }
    )

    val lifecycleObserver = remember {
        LifecycleEventObserver { _, event ->
            if (event != Lifecycle.Event.ON_START) {
                return@LifecycleEventObserver
            }
            if (permissions.any { context.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }) {
                launcher.launch(permissions)
            } else {
                onResult(true)
                return@LifecycleEventObserver
            }
        }
    }

    DisposableEffect(lifecycle, lifecycleObserver) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycle.removeObserver(lifecycleObserver) }
    }


}