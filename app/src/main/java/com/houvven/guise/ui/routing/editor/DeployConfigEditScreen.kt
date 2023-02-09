package com.houvven.guise.ui.routing.editor

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.houvven.guise.R
import com.houvven.guise.ui.GlobalSnackbarHost
import com.houvven.guise.ui.components.SaveTemplate
import com.houvven.guise.ui.components.simplify.SimplifyDropdownMenuItem
import com.houvven.guise.ui.components.simplify.SimplifyIcon
import com.houvven.guise.ui.routing.LocalNavController
import com.houvven.guise.xposed.config.ModuleConfig
import com.houvven.guise.xposed.config.ModuleConfigManager

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DeployConfigEditScreen(name: String, packageName: String) {
    val navHostController = LocalNavController.current
    val moduleConfigManager = ModuleConfigManager.of(ModuleConfig.get(packageName))
    val isSaveRequest = remember { mutableStateOf(false) }

    SaveTemplate(isSaveRequest, moduleConfigManager.config)

    ConfigEditorView(moduleConfigManager.state) {
        TopAppBar(
            title = { Text(name, style = MaterialTheme.typography.titleMedium) },

            navigationIcon = {
                IconButton(onClick = { navHostController.popBackStack() }) {
                    SimplifyIcon(Icons.Default.ArrowBack)
                }
            },

            actions = {
                IconButton({
                    moduleConfigManager.clear()
                    GlobalSnackbarHost.showSuccess()
                }) { SimplifyIcon(Icons.Outlined.Delete) }

                IconButton({
                    moduleConfigManager.save()
                    GlobalSnackbarHost.showSuccess()
                }) { SimplifyIcon(Icons.Outlined.Save) }

                var expanded by remember { mutableStateOf(false) }
                IconButton({ expanded = true }) {
                    SimplifyIcon(Icons.Rounded.MoreVert)
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        SimplifyDropdownMenuItem(
                            text = stringResource(R.string.stop_app),
                            onClick = {
                                val isUseRootSucceed = moduleConfigManager.stopApp()
                                if (isUseRootSucceed) GlobalSnackbarHost.showSuccess()
                            }
                        )
                        SimplifyDropdownMenuItem(
                            text = stringResource(R.string.restart_app),
                            onClick = {
                                moduleConfigManager.restartApp().let {
                                    if (it.isFailure) {
                                        it.exceptionOrNull()?.message?.let { message ->
                                            GlobalSnackbarHost.showOnErrorByDismissPrevious(message)
                                        }
                                    }
                                }
                            }
                        )
                        SimplifyDropdownMenuItem(
                            text = stringResource(R.string.save_as_template),
                            onClick = { isSaveRequest.value = true }
                        )
                    }
                }
            }
        )
    }
}