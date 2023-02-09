package com.houvven.guise.ui.routing.template

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.houvven.guise.db.Template
import com.houvven.guise.module.apps.AppInfo
import com.houvven.guise.ui.components.simplify.SimplifyIcon
import com.houvven.guise.ui.routing.LauncherState
import com.houvven.guise.ui.routing.LocalNavController
import com.houvven.guise.xposed.PackageConfig
import java.text.Collator
import java.util.Locale

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun EnableTemplateScreen(template: Template) {

    val all = PackageConfig.safePrefs.all

    // 系统与用户APP过滤
    var selectedTabIndex by remember { mutableStateOf(0) }
    val apps by remember { mutableStateOf(LauncherState.apps.value) }

    val selects = remember {
        mutableStateListOf(
            *apps
                .filter { all.containsKey(it.packageName) && all[it.packageName] == template.configuration }
                .map { it.packageName }.toTypedArray()
        )
    }

    val unselects = remember { mutableStateListOf<String>() }

    fun filterApps() =
        apps.toList()
            .filter { if (selectedTabIndex == 0) !it.isSystemApp else it.isSystemApp }
            .sortedBy { Collator.getInstance(Locale.CHINA).getCollationKey(it.label) }
            .sortedWith { o1, o2 ->
                if (selects.contains(o1.packageName) == selects.contains(o2.packageName)) 0
                else if (selects.contains(o1.packageName)) -1
                else 1
            }

    @Composable
    fun ItemCard(appInfo: AppInfo) {
        val selected = selects.contains(appInfo.packageName)
        val colors =
            if (selected) CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.inversePrimary)
            else CardDefaults.outlinedCardColors()
        val onclick =
            fun() {
                if (selected) {
                    selects.remove(appInfo.packageName)
                    unselects.add(appInfo.packageName)
                } else {
                    selects.add(appInfo.packageName)
                    unselects.remove(appInfo.packageName)
                }
            }

        OutlinedCard(
            modifier = Modifier.padding(5.dp),
            colors = colors,
            onClick = onclick,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp)
                    .padding(start = 5.dp),
                // horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SimplifyIcon(
                    appInfo.icon.asImageBitmap(),
                    modifier = Modifier.size(30.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = appInfo.label,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = template.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // 模拟系统返回键
                        LocalNavController.current.popBackStack()
                    }) {
                        SimplifyIcon(Icons.Default.ArrowBack)
                    }
                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        // 判断滑动方向
                        if (delta > 0) {
                            if (selectedTabIndex == 1) selectedTabIndex = 0
                        } else {
                            if (selectedTabIndex == 0) selectedTabIndex = 1
                        }
                    }
                )
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    text = { Text(text = "用户应用") },
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 }
                )
                Tab(
                    text = { Text(text = "系统应用") },
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 }
                )
            }

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(3),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(filterApps()) { appInfo -> ItemCard(appInfo) }
            }
        }
    }


    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val lifecycleObserver = remember {
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                unselects.forEach {
                    PackageConfig.safePrefs.edit { remove(it) }
                    LauncherState.apps.value.find { app -> app.packageName == it }?.let {
                        it.isEnable = false
                    }
                }
                selects.forEach {
                    PackageConfig.safePrefs.edit { putString(it, template.configuration) }
                    LauncherState.apps.value.find { app -> app.packageName == it }?.let {
                        it.isEnable = true
                    }
                }
            }
        }
    }

    DisposableEffect(lifecycle, lifecycleObserver) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycle.removeObserver(lifecycleObserver) }
    }

}


