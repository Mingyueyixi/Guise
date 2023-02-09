package com.houvven.guise.ui.components.simplify

import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun <T> SimplifyRadioButton(value: T, state: MutableState<T>) {
    RadioButton(state.value == value, { state.value = value })
}

@Composable
fun SimplifyCheckBox(state: MutableState<Boolean>) {
    Checkbox(state.value, { state.value = !state.value })
}