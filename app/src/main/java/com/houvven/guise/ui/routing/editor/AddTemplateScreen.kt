package com.houvven.guise.ui.routing.editor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.houvven.guise.R
import com.houvven.guise.ui.components.SaveTemplate
import com.houvven.guise.ui.components.simplify.SimplifyIcon
import com.houvven.guise.ui.routing.LocalNavController
import com.houvven.guise.xposed.config.ModuleConfigManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTemplateScreen() {
    val navHostController = LocalNavController.current
    val moduleConfigManager = ModuleConfigManager.empty()
    val isSaveRequest = remember { mutableStateOf(false) }

    SaveTemplate(dialogState = isSaveRequest, moduleConfig = moduleConfigManager.config)

    ConfigEditorView(moduleConfigManager.state) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.add_template),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            navigationIcon = {
                IconButton(onClick = { navHostController.popBackStack() }) {
                    SimplifyIcon(Icons.Default.ArrowBack)
                }
            },
            actions = {
                IconButton(onClick = { moduleConfigManager.clear() }) {
                    SimplifyIcon(Icons.Default.Delete)
                }
                IconButton(onClick = {
                    moduleConfigManager.updateConfigFromState()
                    isSaveRequest.value = true
                }) {
                    SimplifyIcon(Icons.Default.Save)
                }
            }
        )
    }
}