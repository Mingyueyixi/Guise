package com.houvven.guise.ui.routing.launcher

import android.os.Environment
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.houvven.guise.R
import com.houvven.guise.ui.GlobalSnackbarHost
import com.houvven.guise.ui.components.simplify.SimplifyIcon
import com.houvven.guise.ui.routing.LauncherState
import com.houvven.guise.ui.utils.saveFileToDownloadDir
import com.houvven.ktx_xposed.logger.ModuleLogDBHelper
import com.houvven.ktx_xposed.logger.XposedLogger
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


@OptIn(
    DelicateCoroutinesApi::class, ExperimentalMaterial3Api::class
)
@Composable
internal fun LogScreen() {
    val moduleLogDao = ModuleLogDBHelper.moduleLogDao
    var logs by remember { mutableStateOf(moduleLogDao.getAll()) }

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = stringResource(R.string.action_log)) }, actions = {
            IconButton({
                GlobalScope.launch(Dispatchers.Default) {
                    moduleLogDao.clearAll()
                    logs = moduleLogDao.getAll()
                }
            }) { SimplifyIcon(Icons.Outlined.Delete) }

            IconButton(onClick = {
                saveFileToDownloadDir(
                    "Guise-log-${System.currentTimeMillis()}.log",  buildString {
                        logs.forEach { log -> appendLine(log.toString()) }
                    }
                ).onSuccess {
                    GlobalSnackbarHost.showByDismissPrevious("保存成功 ${it.absolutePath}") // TODO: 国际化
                }.onFailure {
                    GlobalSnackbarHost.showOnErrorByDismissPrevious("保存失败 ${it.message}") // TODO:  国际化
                }
            }) {
                SimplifyIcon(Icons.Outlined.Save)
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { logs = moduleLogDao.getAll() },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            SimplifyIcon(Icons.Default.Refresh)
        }
    }) {
        Column(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (logs.isNotEmpty()) Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(logs) { log ->
                        val color = when (log.type.uppercase().toCharArray()[0]) {
                            XposedLogger.Level.ERROR -> MaterialTheme.colorScheme.error
                            XposedLogger.Level.DEBUG -> MaterialTheme.colorScheme.primary
                            XposedLogger.Level.INFO -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                        Text(
                            text = log.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.SansSerif,
                            color = color
                        )
                    }
                }
            } else {
                Text(text = "仅在本APP运行时才会记录模块运行时日志") // TODO: 国际化
                Text(
                    text = "No logs found", // TODO: 国际化
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}