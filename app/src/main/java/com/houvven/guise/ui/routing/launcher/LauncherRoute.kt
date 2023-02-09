package com.houvven.guise.ui.routing.launcher

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.houvven.guise.R
import com.houvven.guise.ui.components.simplify.SimplifyIcon


private enum class LauncherScreenType(
    val label: @Composable () -> String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    DEPLOY(
        { stringResource(id = R.string.action_deploy) },
        Icons.Outlined.Sell,
        Icons.Rounded.Sell
    ),
    TEMPLATE(
        { stringResource(id = R.string.action_template) },
        Icons.Outlined.Layers,
        Icons.Rounded.Layers
    ),
    LOG(
        { stringResource(id = R.string.action_log) },
        Icons.Outlined.Description,
        Icons.Rounded.Description
    ),
    SETTINGS(
        { stringResource(id = R.string.action_setting) },
        Icons.Outlined.Settings,
        Icons.Rounded.Settings
    )
}


private val currentPage by derivedStateOf { mutableStateOf(LauncherScreenType.DEPLOY) }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherRoute() {

    @Composable
    fun RowScope.LauncherNavBarItem(
        routerType: LauncherScreenType
    ) {
        val selected = currentPage.value == routerType
        val icon = if (selected) routerType.selectedIcon else routerType.icon
        val w = if (selected) FontWeight.W900 else FontWeight.Normal

        NavigationBarItem(
            selected = currentPage.value == routerType,
            onClick = { currentPage.value = routerType },
            icon = { SimplifyIcon(icon) },
            label = { Text(routerType.label(), fontWeight = w) },
            alwaysShowLabel = true,
            modifier = Modifier.wrapContentHeight(),
            colors = NavigationBarItemDefaults.colors(
                selectedTextColor = MaterialTheme.colorScheme.primary,
                selectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.surface,
            )
        )
    }

    Scaffold(bottomBar = {
        Column {
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            NavigationBar(
                modifier = Modifier.wrapContentHeight(),
                tonalElevation = 0.dp
            ) {
                LauncherScreenType.values().forEach { LauncherNavBarItem(it) }
            }
        }
    }) { pd ->
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .padding(top = pd.calculateTopPadding(), bottom = pd.calculateBottomPadding())
                .fillMaxSize()
        ) {
            Column {
                Crossfade(currentPage.value, animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = 0,
                    easing = { it }
                )) { screen ->
                    when (screen) {
                        LauncherScreenType.DEPLOY -> DeployScreen()
                        LauncherScreenType.TEMPLATE -> TemplateScreen()
                        LauncherScreenType.LOG -> LogScreen()
                        LauncherScreenType.SETTINGS -> SettingScreen()
                    }
                }
            }
        }

    }
}