package com.houvven.guise.ui.routing.editor

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.clipScrollableContainer
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.houvven.guise.ContextAmbient
import com.houvven.guise.R
import com.houvven.guise.db.DeviceDBHelper
import com.houvven.guise.module.PresetAdapter
import com.houvven.guise.module.preset.LanguagePreset
import com.houvven.guise.module.preset.NetworkPreset
import com.houvven.guise.module.preset.ReleasePreset
import com.houvven.guise.module.preset.SimPreset
import com.houvven.guise.ui.components.SearchBox
import com.houvven.guise.util.android.Randoms
import com.houvven.guise.xposed.config.ModuleConfigState
import kotlinx.coroutines.launch


private val localSetValue = mutableStateOf({ _: String -> })
private val localPreset = mutableStateOf(emptyList<PresetAdapter>())

private val allBrands = DeviceDBHelper(ContextAmbient.current).use { it.getAllBrand() }

@Composable
private fun ConfigEditorItems(state: ModuleConfigState, launch: () -> Unit) {
    @Composable
    fun PresetInputBox(
        state: MutableState<String>,
        label: String,
        preset: List<PresetAdapter>,
        showOperateIcon: Boolean = true,
        validate: (String) -> Boolean = { true },
        setValue: (String) -> Unit = { value -> state.value = value },
    ) = OperateInputBox(state, label, showOperateIcon, validate) {
        localPreset.value = preset
        localSetValue.value = setValue
        launch()
    }


    val context = LocalContext.current


    Title(text = stringResource(R.string.title_device_parameter), topPadding = 1.dp)
    PresetInputBox(
        state = state.brand,
        label = stringResource(R.string.device_brand),
        preset = allBrands.map {
            object : PresetAdapter {
                override val label: String = it.value
                override val value: String = it.key
            }
        }
    )
    PresetInputBox(
        state = state.model,
        label = stringResource(R.string.device_model),
        preset = DeviceDBHelper(context).use { dbHelper ->
            dbHelper.getDevicesByBrand(state.brand.value)
                .filterNot { it.modelName.isNullOrBlank() || it.model.isNullOrBlank() }
                .map {
                    val label = if (it.verName == "#" || it.verName == null) it.modelName!!
                    else "${it.modelName!!} (${it.verName.removePrefix("#")})"
                    object : PresetAdapter {
                        override val label: String = label
                        override val value: String = "${it.model!!}:${it.codeAlias ?: ""}"
                    }
                }
        },
        showOperateIcon = allBrands.containsKey(state.brand.value),
        setValue = { value ->
            val (model, codeAlias) = value.split(":")
            state.model.value = model
            state.device.value = codeAlias
        }
    )
    InputBox(state.product, stringResource(R.string.device_product))
    InputBox(state.device, stringResource(R.string.device_device))
    InputBox(state.board, stringResource(R.string.device_board))
    InputBox(state.hardware, stringResource(R.string.device_cpu))
    PresetInputBox(
        state.androidVersion,
        stringResource(R.string.device_system_android_version),
        preset = ReleasePreset.values().toList().reversed()
    )
    PresetInputBox(
        state = state.sdkInt,
        label = stringResource(R.string.device_system_api_level),
        preset = Build.VERSION_CODES::class.java.fields.map {
            object : PresetAdapter {
                override val label: String = it.name
                override val value: String = it.getInt(null).toString()
            }
        }.sortedBy { it.label }.reversed()
    )
    InputBox(state.fingerPrint, stringResource(R.string.device_system_finger_print))


    Title(text = stringResource(R.string.title_net_info))
    PresetInputBox(
        state = state.networkType,
        label = stringResource(R.string.net_type),
        preset = NetworkPreset.values().toList()
    )
    InputBox(state.wifiSSID, stringResource(R.string.net_wifi_ssid))
    InputBox(state.wifiBSSID, stringResource(R.string.net_wifi_bssid))
    InputBox(state.wifiMacAddress, stringResource(R.string.net_wifi_mac))


    Title(text = stringResource(R.string.title_sim))
    PresetInputBox(
        state.simOperator,
        stringResource(R.string.net_sim_code),
        SimPreset.values().toList()
    ) {
        val (name, code, country) = it.split(":")
        state.simOperatorName.value = name
        state.simOperator.value = code
        state.simCountry.value = country
    }
    InputBox(state.simOperatorName, stringResource(R.string.net_sim_name))
    InputBox(state.simCountry, stringResource(R.string.net_sim_iso))


    Title(text = stringResource(R.string.title_unique_id))
    RandomInputBox(state.imei, stringResource(R.string.id_imei)) { Randoms.randomIMEI() }
    RandomInputBox(state.androidId, stringResource(R.string.id_ssaid)) { Randoms.randomIMEI() }


    Title(text = stringResource(R.string.title_cell_location))
    InputBox(state.lac, stringResource(R.string.gsm_lac))
    InputBox(state.cid, stringResource(R.string.gsm_cid))


    Title(text = stringResource(R.string.title_location_info))
    InputBox(state.longitude, stringResource(R.string.location_lng))
    InputBox(state.latitude, stringResource(R.string.location_lat))
    ContainerSwitch(state.randomOffset, stringResource(R.string.location_offset))
    ContainerSwitch(state.makeWifiLocationFail, stringResource(R.string.location_wifi_fail))
    ContainerSwitch(state.makeCellLocationFail, label = "使基站位置信息失效") // TODO:  国际化


    Title(text = stringResource(R.string.title_other))
    InputBox(state.batteryLevel, stringResource(R.string.other_battery_level))
    PresetInputBox(
        state.language,
        stringResource(R.string.other_language),
        LanguagePreset.values().toList()
    )
    InputBox(state.screenshotsFlag, stringResource(R.string.other_screenshot))
    ContainerSwitch(state.hookSuccessHint, stringResource(R.string.other_hook_hint))


    Title(text = stringResource(R.string.title_blank_pass))
    ContainerSwitch(state.passContacts, stringResource(R.string.pass_contacts))
    ContainerSwitch(state.passPhoto, stringResource(R.string.pass_photo))
    ContainerSwitch(state.passVideo, stringResource(R.string.pass_video))
    ContainerSwitch(state.passAudio, stringResource(R.string.pass_audio))


    // bottom blank 底部留白
    Spacer(modifier = Modifier.height(50.dp))
}


@Composable
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
internal fun ConfigEditorView(
    moduleConfigState: ModuleConfigState,
    topBar: @Composable () -> Unit,
) {
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var search by remember { mutableStateOf(false) }
    var key by remember { mutableStateOf("") }


    if (!state.isVisible) {
        search = false
        key = ""
    }

    val content = @Composable {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Divider(
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 5.dp,
                    modifier = Modifier
                        .width(50.dp)
                        .padding(vertical = 20.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .clickable {
                            if (!search) scope.launch {
                                state.animateTo(ModalBottomSheetValue.Expanded, spring())
                            }
                            search = !search
                        }
                )

                val presets =
                    if (key.isNotBlank() && localPreset.value.any { it.label.contains(key, true) })
                        localPreset.value
                            .sortedBy { it.label.contains(key, true) }
                            .reversed()
                    else localPreset.value

                if (search) SearchBox(value = key, onValueChange = { key = it })
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 15.dp),
                ) {
                    items(presets) {
                        ElevatedAssistChip(
                            onClick = { localSetValue.value(it.value); scope.launch { state.hide() } },
                            label = { Row(Modifier.padding(vertical = 15.dp)) { Text(it.label) } },
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(50.dp))
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = state,
        sheetShape = RoundedCornerShape(15.dp),
        sheetContent = { content() }
    ) {
        Scaffold(topBar = topBar) {
            Surface(
                modifier = Modifier
                    .padding(top = it.calculateTopPadding())
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .clipScrollableContainer(Orientation.Vertical)
                ) {
                    ConfigEditorItems(moduleConfigState) { scope.launch { state.show() } }
                }
            }
        }
    }

    BackHandler(
        enabled = state.isVisible,
        onBack = { scope.launch { state.hide() } }
    )

}


