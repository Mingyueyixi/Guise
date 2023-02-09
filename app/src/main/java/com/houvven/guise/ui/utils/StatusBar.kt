package com.houvven.guise.ui.utils

import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun StatusBarImmerse(window: Window, color: Color = MaterialTheme.colorScheme.surface) {
    window.setDecorFitsSystemWindows(false)
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = color, darkIcons = !isSystemInDarkTheme())
    Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars).fillMaxSize())
}

@Composable
fun StatusBarImmerse(color: Color = MaterialTheme.colorScheme.surface) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = color, darkIcons = !isSystemInDarkTheme())
}