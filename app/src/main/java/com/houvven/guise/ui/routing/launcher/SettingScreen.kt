package com.houvven.guise.ui.routing.launcher

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.clipScrollableContainer
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.houvven.guise.BuildConfig
import com.houvven.guise.R
import com.houvven.guise.constant.AppConfigKey
import com.houvven.guise.constant.DonatePays
import com.houvven.guise.module.ktx.toBitmap
import com.houvven.guise.ui.alwaysActivate
import com.houvven.guise.ui.alwaysDarkMode
import com.houvven.guise.ui.components.EmailHyperLink
import com.houvven.guise.ui.components.Hyperlink
import com.houvven.guise.ui.components.simplify.SimplifyIcon
import com.houvven.guise.ui.components.simplify.SimplifyImage
import com.houvven.guise.ui.isHooked
import com.houvven.guise.ui.utils.hideLauncherIcon
import com.houvven.guise.ui.utils.isHideLauncherIcon
import kotlinx.coroutines.launch

@Composable
private fun ModuleStateView() {
    val modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)

    val colors =
        if (isHooked) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.error

    Card(
        modifier = modifier, colors = CardDefaults.cardColors(containerColor = colors)
    ) {
        Row(
            modifier = Modifier.padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SimplifyIcon(if (isHooked) Icons.Default.CheckCircle else Icons.Default.Error)
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = if (isHooked) "已激活" else "未激活",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = if (isHooked) "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})" else "请在Xposed中激活")
            }
        }
    }
}

@Composable
private fun Title(text: String, topPadding: Dp = 30.dp) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 20.dp, top = topPadding)
    )
}

@Composable
private fun Container(
    verticalPadding: Dp = 1.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@Composable
private fun ContainerSwitch(
    label: String,
    subLabel: String = "",
    state: MutableState<Boolean>,
    onChange: (Boolean) -> Unit = {},
) {
    Container(verticalPadding = 5.dp, horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            if (subLabel.isNotBlank()) Text(
                subLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Switch(checked = state.value, onCheckedChange = { state.value = it; onChange(it) })
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
internal fun SettingScreen() {
    val receiptCode = remember { mutableStateOf<Bitmap?>(null) }
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    @Composable
    fun content() {

        Title(text = "配置项")
        ContainerSwitch(
            label = "隐藏桌面图标",
            state = remember { mutableStateOf(isHideLauncherIcon()) },
            onChange = { hideLauncherIcon(it) }
        )
        ContainerSwitch(
            label = "深色主题",
            subLabel = "一直启用深色主题，不再跟随系统切换",
            state = alwaysDarkMode,
            onChange = {
                alwaysDarkMode.value = it
                AppConfigKey.run { mmkv.encode(ALWAYS_DARK_MODE, it) }
            }
        )
        ContainerSwitch(
            label = "不检测模块激活状态",
            subLabel = "这对于Xposed、EdXposed和Lsposed等框架是没有意义的，仅适用于无法正常激活本模块的免root框架。如lspatch。",
            state = alwaysActivate,
            onChange = {
                alwaysActivate.value = it
                AppConfigKey.run { mmkv.encode(ALWAYS_ACTIVE, it) }
            }
        )

        Title(text = "关于")
        Container(verticalPadding = 7.dp) {
            Text("版本:", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})",
                modifier = Modifier.padding(start = 5.dp)
            )
        }
        Container {
            Text("作者:", style = MaterialTheme.typography.bodyLarge)
            Text("Houvven", modifier = Modifier.padding(start = 5.dp))
        }

        Row(
            modifier = Modifier.padding(start = 20.dp, top = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Hyperlink(
                label = "更新地址",
                url = "https://github.com/Xposed-Modules-Repo/com.houvven.guise/releases",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
            SimplifyIcon(Icons.Default.Link, tint = MaterialTheme.colorScheme.primary)
        }

        Title(text = "反馈地址")
        Container(verticalPadding = 7.dp) {
            Row {
                Hyperlink(
                    label = "GitHub Issues",
                    url = "https://github.com/Xposed-Modules-Repo/com.houvven.guise/issues",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.width(15.dp))
                EmailHyperLink(
                    label = "Email",
                    address = "2960267005@qq.com",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.width(15.dp))
                Hyperlink(
                    label = "CoolApk",
                    url = "http://www.coolapk.com/u/3668334",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Title(text = "捐赠通道")
        Container(verticalPadding = 7.dp) {
            Row {
                Text(
                    DonatePays.ALIPAY.label,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable {
                        receiptCode.value = DonatePays.ALIPAY.base64.toBitmap()
                        coroutineScope.launch { modalBottomSheetState.show() }
                    }
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    DonatePays.WECHAT.label,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable {
                        receiptCode.value = DonatePays.WECHAT.base64.toBitmap()
                        coroutineScope.launch { modalBottomSheetState.show() }
                    })
            }
        }
        Container {
            Column {
                Text(
                    text = "未成年人请勿捐赠",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "捐赠者可备注昵称, 以便在捐赠列表中展示",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.action_setting)) })
        },
    ) {
        ModalBottomSheetLayout(
            sheetState = modalBottomSheetState,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            sheetContent = {
                Column {
                    Spacer(modifier = Modifier.height(1.dp))
                    receiptCode.value?.let {
                        SimplifyImage(it.asImageBitmap(), contentScale = ContentScale.Fit)
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(top = it.calculateTopPadding())
                    .verticalScroll(rememberScrollState())
                    .clipScrollableContainer(Orientation.Vertical)
            ) {
                ModuleStateView()
                content()
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        BackHandler(modalBottomSheetState.isVisible) {
            coroutineScope.launch { modalBottomSheetState.hide() }
        }
    }

}