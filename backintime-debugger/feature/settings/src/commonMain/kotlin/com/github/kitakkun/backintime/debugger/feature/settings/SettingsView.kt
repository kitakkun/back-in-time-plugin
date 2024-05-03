package com.github.kitakkun.backintime.debugger.feature.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.debugger.ui.customview.CommonLoadingView
import com.github.kitakkun.backintime.settings.generated.resources.Res
import com.github.kitakkun.backintime.settings.generated.resources.settings_tab_title
import com.github.kitakkun.backintime.settings.generated.resources.text_loading_settings
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsView(
    bindModel: SettingsBindModel,
    onChangeWebSocketPort: (Int) -> Unit,
    onChangeDeleteSessionDataOnDisconnect: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (bindModel) {
        is SettingsBindModel.Loading -> CommonLoadingView(modifier = modifier, stringResource(Res.string.text_loading_settings))
        is SettingsBindModel.Loaded -> LoadedView(
            bindModel = bindModel,
            onChangeWebSocketPort = onChangeWebSocketPort,
            onChangeDeleteSessionDataOnDisconnect = onChangeDeleteSessionDataOnDisconnect,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadedView(
    bindModel: SettingsBindModel.Loaded,
    onChangeWebSocketPort: (Int) -> Unit,
    onChangeDeleteSessionDataOnDisconnect: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.settings_tab_title))
                },
            )
        },
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextFieldSettingItem(
                label = "WebSocket Port",
                value = bindModel.webSocketPort.toString(),
                onValueChange = { onChangeWebSocketPort(it.toIntOrNull() ?: 0) },
            )
            SwitchSettingItem(
                label = "Erase Session Data on Disconnect",
                checked = bindModel.deleteSessionDataOnDisconnect,
                onCheckedChange = onChangeDeleteSessionDataOnDisconnect,
            )
        }
    }
}

@Preview
@Composable
private fun LoadedViewPreview() {
    LoadedView(
        bindModel = SettingsBindModel.Loaded(
            webSocketPort = 8080,
            deleteSessionDataOnDisconnect = true,
        ),
        onChangeWebSocketPort = {},
        onChangeDeleteSessionDataOnDisconnect = {},
    )
}
