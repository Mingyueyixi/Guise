package com.houvven.guise.ui.routing.launcher

import android.content.Intent
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DoNotDisturbOnTotalSilence
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.houvven.guise.R
import com.houvven.guise.db.Template
import com.houvven.guise.ui.GlobalSnackbarHost
import com.houvven.guise.ui.components.simplify.SimplifyDropdownMenuItem
import com.houvven.guise.ui.components.simplify.SimplifyIcon
import com.houvven.guise.ui.components.simplify.SimplifyImage
import com.houvven.guise.ui.routing.LauncherState
import com.houvven.guise.ui.routing.LocalNavController
import com.houvven.guise.ui.routing.NavRoutingTypes
import com.houvven.guise.ui.routing.navigateAndArgument
import com.houvven.guise.ui.routing.template.EnableTemplateDialog
import com.houvven.guise.ui.utils.saveFileToDownloadDir
import com.houvven.guise.util.android.UriUtil
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private object TemplateTypeFilter {
    const val ALL = -1
    const val COMMON = Template.Type.COMMON
    const val EXCLUSIVE = Template.Type.EXCLUSIVE
}

private val typeFilter by derivedStateOf { mutableStateOf(TemplateTypeFilter.ALL) }

private val requestEnable by derivedStateOf { mutableStateOf(false) }
private val requestEnableTemplate by derivedStateOf { mutableStateOf<Template?>(null) }


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TemplateCard(template: Template) {
    val context = LocalContext.current
    val installed = remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }


    val headIcon = @Composable {
        if (template.type == TemplateTypeFilter.EXCLUSIVE) {
            val modifier = Modifier
                .size(25.dp)
                .padding(bottom = 5.dp)
            runCatching {
                context.packageManager.getApplicationIcon(template.packageName!!)
            }.onFailure {
                installed.value = false
                SimplifyImage(
                    Icons.Default.DoNotDisturbOnTotalSilence,
                    modifier = modifier
                )
            }.onSuccess {
                val bitmap = it.toBitmap().asImageBitmap()
                SimplifyImage(bitmap, modifier)
            }
        }
    }

    val content = @Composable {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            headIcon()
            Text(
                text = template.name,
                style = MaterialTheme.typography.titleMedium
            )
            if (template.type == Template.Type.EXCLUSIVE) {
                Text(
                    text = template.packageName!!,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            if (template.description.isNullOrBlank().not()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = template.description!!, style = MaterialTheme.typography.labelMedium)
            }
        }
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 6.dp, vertical = 5.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (template.type == Template.Type.EXCLUSIVE && !installed.value) {
                        GlobalSnackbarHost.showOnErrorByDismissPrevious(
                            "该专属模板的目标应用未安装, 请先安装目标应用" // TODO: 国际化
                        )
                    } else if (template.type == Template.Type.EXCLUSIVE) {
                        requestEnable.value = true
                        requestEnableTemplate.value = template
                    } else {
                        val navHostController = LocalNavController.current
                        navHostController.navigateAndArgument(
                            NavRoutingTypes.ENABLE_TEMPLATE.name,
                            args = listOf(Pair("template", template))
                        )
                    }
                },
                onLongClick = {
                    expanded = true
                }
            ),
        /* colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.inversePrimary.copy(.4F)
        ), */
        shape = RoundedCornerShape(10.dp)
    ) {
        content()
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(10.dp, (-5).dp)
        ) {
            SimplifyDropdownMenuItem(text = "修改" /* todo 国际化 */, onClick = {
                expanded = false
                LocalNavController.current.navigateAndArgument(
                    NavRoutingTypes.EDIT_TEMPLATE.name,
                    args = listOf(Pair("template", template))
                )
            })
            SimplifyDropdownMenuItem(text = "删除" /* todo 国际化 */, onClick = {
                expanded = false
                LauncherState.deleteTemplate(template)
            })
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun TemplateScreen() {
    val context = LocalContext.current
    val navController = LocalNavController.current

    val topBar = @Composable {
        var topBarMenuExpanded by remember { mutableStateOf(false) }
        TopAppBar(
            title = { Text(stringResource(R.string.action_template)) },
            actions = {
                IconButton(onClick = { topBarMenuExpanded = true }) {
                    SimplifyIcon(Icons.Default.MoreVert)
                }
                DropdownMenu(
                    expanded = topBarMenuExpanded,
                    onDismissRequest = { topBarMenuExpanded = false })
                {

                    val resultLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { result ->
                        if (result != null) {
                            UriUtil.getFileRealPath(context, result)?.let {
                                val templates =
                                    Json.decodeFromString<List<Template>>(File(it).readText())
                                LauncherState.addTemplates(templates)
                            }
                        }
                        topBarMenuExpanded = false
                        GlobalSnackbarHost.showByDismissPrevious("导入成功")
                    }

                    SimplifyDropdownMenuItem(
                        text = "导入" /* todo 国际化 */,
                        onClick = {
                            runCatching {
                                resultLauncher.launch("application/json")
                            }.onFailure {
                                GlobalSnackbarHost.showByDismissPrevious("导入失败 ${it.message}")
                            }
                        }
                    )
                    SimplifyDropdownMenuItem(
                        text = "导出" /* todo 国际化 */,
                        onClick = {
                            saveFileToDownloadDir(
                                "Guise-Template-${System.currentTimeMillis()}.json",
                                Json.encodeToString(LauncherState.templates.value)
                            ).onSuccess {
                                GlobalSnackbarHost.showByDismissPrevious("导出成功 ${it.absolutePath}") // TODO: 国际化
                            }.onFailure {
                                GlobalSnackbarHost.showOnErrorByDismissPrevious("导出失败 ${it.message}") // TODO:  国际化
                            }
                        }
                    )
                }
            }
        )
    }

    val floatingButton = @Composable {
        FloatingActionButton(
            onClick = { navController.navigate(NavRoutingTypes.ADD_TEMPLATE.name) },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            SimplifyIcon(Icons.Default.Add)
        }
    }

    @Composable
    fun TypeFilter() {
        @Composable
        fun TypeFilterChip(label: String, value: Int) {
            FilterChip(
                selected = typeFilter.value == value,
                onClick = { typeFilter.value = value },
                label = { Text(label) }
            )
        }
        Row(modifier = Modifier.padding(start = 15.dp)) {
            TypeFilterChip(
                label = stringResource(R.string.template_type_all),
                value = TemplateTypeFilter.ALL
            )
            Spacer(modifier = Modifier.width(5.dp))
            TypeFilterChip(
                label = stringResource(R.string.template_type_common),
                value = TemplateTypeFilter.COMMON
            )
            Spacer(modifier = Modifier.width(5.dp))
            TypeFilterChip(
                label = stringResource(R.string.template_type_app),
                value = TemplateTypeFilter.EXCLUSIVE
            )
        }
    }


    // 脚手架
    Scaffold(
        topBar = topBar,
        floatingActionButton = floatingButton,
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
        ) {
            TypeFilter()
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 10.dp),
            ) {
                val items = if (typeFilter.value != TemplateTypeFilter.ALL)
                    LauncherState.templates.value.filter { it.type == typeFilter.value }
                else
                    LauncherState.templates.value

                items(items) { template -> TemplateCard(template) }

            }

            requestEnableTemplate.value?.let {
                EnableTemplateDialog(state = requestEnable, template = it)
            }
        }
    }
}