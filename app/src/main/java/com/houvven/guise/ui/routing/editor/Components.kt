package com.houvven.guise.ui.routing.editor


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Circle
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.houvven.guise.ui.components.ElevatedTextField
import com.houvven.guise.ui.isHooked
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
internal fun Title(text: String, topPadding: Dp = 22.dp) {
    Text(
        text = text,
        modifier = Modifier.padding(top = topPadding, start = 25.dp, bottom = 5.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

/**
 * 自定义容器 默认为水平排列
 */
@Composable
internal fun Container(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 3.dp)
            .fillMaxSize(),
        shape = RoundedCornerShape(15.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 3.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            content()
        }
    }
}

@Composable
internal fun ContainerSwitch(state: MutableState<Boolean>, label: String) {
    Container {
        val color =
            if (isHooked) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        Text(text = label, fontSize = 16.sp, color = color)
        Switch(checked = state.value, onCheckedChange = { state.value = it }, enabled = isHooked)
    }
}

@Composable
private fun IconButton(icon: ImageVector, clickable: () -> Unit) {
    Row {
        FilledIconButton(onClick = clickable, modifier = Modifier.requiredSize(22.dp)) {
            Icon(icon, contentDescription = null, Modifier.padding(2.dp))
        }
    }
}

@Composable
private fun BasicInputBox(
    state: MutableState<String>,
    label: String,
    validate: (String) -> Boolean = { true },
    trailingIcon: @Composable () -> Unit = {},
) {
    val modifier = Modifier
        .padding(horizontal = 15.dp, vertical = 3.dp)
        .fillMaxWidth()
    ElevatedTextField(
        value = state.value,
        onValueChange = { state.value = if (validate(it)) it else state.value },
        modifier = modifier,
        singleLine = true,
        label = { Text(text = label) },
        trailingIcon = { if (isHooked) trailingIcon() },
        enabled = isHooked
    )
}


@Composable
internal fun InputBox(
    state: MutableState<String>,
    label: String,
    validate: (String) -> Boolean = { true },
) {
    BasicInputBox(state, label, validate) {
        state.value.isNotBlank().let {
            if (it) IconButton(Icons.TwoTone.Delete) { state.value = "" }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class, DelicateCoroutinesApi::class)
@Composable
internal fun OperateInputBox(
    state: MutableState<String>,
    label: String,
    showOperateIcon: Boolean = true,
    validate: (String) -> Boolean = { true },
    clickable: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val onClick = {
        focusManager.clearFocus()
        GlobalScope.launch { keyboardController?.hide() }.start()
        clickable()
    }

    BasicInputBox(state, label, validate) {
        Row {
            if (showOperateIcon) IconButton(Icons.TwoTone.Circle, onClick)
            if (state.value.isNotBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(Icons.TwoTone.Delete) { state.value = "" }
                Spacer(modifier = Modifier.width(13.dp))
            }
        }
    }
}

@Composable
internal fun RandomInputBox(
    state: MutableState<String>,
    label: String,
    validate: (String) -> Boolean = { true },
    randomGenerator: () -> String,
) {
    OperateInputBox(
        state = state,
        label = label,
        validate = validate,
        clickable = { state.value = randomGenerator() }
    )
}

