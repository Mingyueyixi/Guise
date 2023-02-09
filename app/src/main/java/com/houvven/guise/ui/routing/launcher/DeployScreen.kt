package com.houvven.guise.ui.routing.launcher

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.houvven.guise.R
import com.houvven.guise.constant.AppConfigKey
import com.houvven.guise.module.apps.AppInfo
import com.houvven.guise.module.apps.AppSortTypes
import com.houvven.guise.ui.components.simplify.NoBtnAlertDialog
import com.houvven.guise.ui.components.simplify.SimplifyIcon
import com.houvven.guise.ui.components.simplify.SimplifyImage
import com.houvven.guise.ui.routing.LauncherState
import com.houvven.guise.ui.routing.LocalNavController
import com.houvven.guise.ui.routing.NavRoutingTypes
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.Locale


private val appSortType by derivedStateOf {
    mutableStateOf(
        AppSortTypes.valueOf(
            AppConfigKey.mmkv.decodeString(
                AppConfigKey.APP_SORT_TYPE,
                AppSortTypes.NAME.name
            ) ?: AppSortTypes.NAME.name
        )
    )
}
private val searchByPackageName by derivedStateOf {
    mutableStateOf(AppConfigKey.run { mmkv.decodeBool(APP_SEARCH_BY_PACKAGE_NAME, false) })
}

private val displaySystemApp by derivedStateOf {
    mutableStateOf(AppConfigKey.run { mmkv.decodeBool(DISPLAY_SYSTEM_APP, false) })
}

private val reverseOrder by derivedStateOf {
    mutableStateOf(AppConfigKey.run { mmkv.decodeBool(APP_REVERSE_SORT, false) })
}

private val searchKeyWorld by derivedStateOf { mutableStateOf("") }


private fun generateApps(): List<AppInfo> {
    var result = LauncherState.apps.value
    if (!displaySystemApp.value) result = result.filterNot { it.isSystemApp }

    if (searchKeyWorld.value.isNotBlank()) {
        result = searchKeyWorld.let { key ->
            if (searchByPackageName.value) {
                result.filter {
                    it.packageName.contains(key.value, true) || it.label.contains(key.value, true)
                }
            } else {
                result.filter { it.label.contains(key.value, true) }
            }
        }
    }
    result = when (appSortType.value) {
        AppSortTypes.NAME -> result.sortedBy {
            Collator.getInstance(Locale.CHINA).getCollationKey(it.label)
        }

        AppSortTypes.PACKAGE_NAME -> result.sortedBy { it.packageName }
        AppSortTypes.INSTALL_TIME -> result.sortedBy { it.installTime }
        AppSortTypes.UPDATE_TIME -> result.sortedBy { it.updateTime }
    }
    if (reverseOrder.value) result = result.reversed()
    return result.sortedWith { a, b -> if (a.isEnable == b.isEnable) 0 else if (a.isEnable) -1 else 1 }
}


@Composable
private fun AppCard(appInfo: AppInfo) {
    val clickable = {
        val name = appInfo.label
        val packageName = appInfo.packageName
        LocalNavController.current.navigate("${NavRoutingTypes.DEPLOY_CONFIG_EDITOR.name}/$name/$packageName")
    }

    val disable = Modifier
        .padding(horizontal = 5.dp)

    val enable = Modifier
        .padding(horizontal = 5.dp)
        .clip(RoundedCornerShape(15.dp))
        .padding(vertical = 2.dp)
        .background(MaterialTheme.colorScheme.surfaceVariant)

    val modifier = (if (appInfo.isEnable) enable else disable)
        .clip(RoundedCornerShape(10.dp))
        .fillMaxWidth()
        .clickable(onClick = { clickable() })
        .padding(horizontal = 10.dp, vertical = 15.dp)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconModifier = Modifier
            .padding(horizontal = 10.dp)
            .size(38.dp)
        SimplifyImage(appInfo.icon.asImageBitmap(), iconModifier)
        val typography = MaterialTheme.typography
        Column {
            Text(appInfo.label, style = typography.titleMedium, softWrap = false)
            Text(appInfo.packageName, style = typography.bodyMedium, softWrap = false)
        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun DeployScreen() {

    var displayMenu by rememberSaveable { mutableStateOf(false) }
    var searching by rememberSaveable { mutableStateOf(false) }
    var refreshing by rememberSaveable { mutableStateOf(false) }

    val onRefresh = {
        refreshing = true
        GlobalScope.launch(Dispatchers.Default) {
            LauncherState.refreshApps()
            refreshing = false
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { onRefresh() },
        refreshThreshold = 60.dp,
        refreshingOffset = 80.dp
    )

    val cancelSearch = { searching = false; searchKeyWorld.value = "" }


    @Composable
    fun Bar() {
        val focusRequester = remember { FocusRequester() }
        // val focusManager = LocalFocusManager.current
        val title = @Composable {
            if (searching) {
                OutlinedTextField(
                    value = searchKeyWorld.value,
                    onValueChange = { searchKeyWorld.value = it },
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
                    leadingIcon = { SimplifyIcon(Icons.Default.Search) },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                // 将焦点移动到搜索框
                LaunchedEffect(searching) {
                    focusRequester.requestFocus()
                    // focusManager.moveFocus(FocusDirection.Down)
                }
            } else Text(
                stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.W500
            )
        }
        val actions = @Composable {
            if (searching.not()) IconButton(onClick = { searching = true }) {
                SimplifyIcon(Icons.Default.Search)
            } else IconButton(onClick = cancelSearch) {
                SimplifyIcon(Icons.Default.Close)
            }
            IconButton(onClick = { displayMenu = true }) { SimplifyIcon(Icons.Default.Menu) }
        }

        TopAppBar(title = title, actions = { actions() })
    }


    @Composable
    fun Menu() {
        val modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))

        @Composable
        fun RadioItem(text: String, value: AppSortTypes, state: MutableState<AppSortTypes>) {
            Row(
                modifier = modifier.clickable(onClick = { state.value = value }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = value == state.value, onClick = { state.value = value })
                Text(text, style = MaterialTheme.typography.bodyMedium)
            }
        }

        @Composable
        fun CheckboxItem(text: String, state: MutableState<Boolean>) {
            Row(
                modifier = modifier.clickable(onClick = { state.value = !state.value }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = state.value, onCheckedChange = { state.value = it })
                Text(text, style = MaterialTheme.typography.bodyMedium)
            }
        }

        NoBtnAlertDialog(onDismissRequest = {
            displayMenu = false
            AppConfigKey.mmkv.run {
                /* putBoolean(AppConfigKey.APP_REVERSE_SORT, reverseOrder.value)
                putBoolean(AppConfigKey.DISPLAY_SYSTEM_APP, displaySystemApp.value)
                putBoolean(AppConfigKey.APP_SEARCH_BY_PACKAGE_NAME, searchByPackageName.value)
                putString(AppConfigKey.APP_SORT_TYPE, appSortType.value.name) */
                encode(AppConfigKey.APP_SORT_TYPE, appSortType.value.name)
                encode(AppConfigKey.APP_SEARCH_BY_PACKAGE_NAME, searchByPackageName.value)
                encode(AppConfigKey.DISPLAY_SYSTEM_APP, displaySystemApp.value)
                encode(AppConfigKey.APP_REVERSE_SORT, reverseOrder.value)
            }

        }) {
            Column {
                AppSortTypes.values().forEach { RadioItem(it.depict, it, appSortType) }
                Divider(Modifier.padding(vertical = 10.dp))
                CheckboxItem(
                    text = stringResource(R.string.also_search_for_package),
                    state = searchByPackageName
                )
                CheckboxItem(
                    text = stringResource(R.string.display_system_apps),
                    state = displaySystemApp
                )
                CheckboxItem(
                    text = stringResource(R.string.apps_reverse_order),
                    state = reverseOrder
                )
            }
        }
    }


    Scaffold(
        topBar = { Bar() }
    ) { pd ->
        Box(
            modifier = Modifier
                .padding(top = pd.calculateTopPadding(), bottom = pd.calculateBottomPadding())
                .fillMaxSize()
                .pullRefresh(pullRefreshState),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn { items(generateApps()) { AppCard(it) } }
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                scale = true
            )
            if (LauncherState.apps.value.isEmpty()) onRefresh()
        }
        if (displayMenu) Menu()
    }

    BackHandler(
        enabled = searching,
        onBack = cancelSearch
    )

    // 生命周期
    DisposableEffect(Unit) {
        onDispose {
            searchKeyWorld.value = ""
        }
    }
}