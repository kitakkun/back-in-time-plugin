package io.github.kitakkun.backintime.debugger.feature.settings

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
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import backintime.debug_tool.feature.settings.generated.resources.Res
import backintime.debug_tool.feature.settings.generated.resources.label_auto_erase_data
import backintime.debug_tool.feature.settings.generated.resources.label_websocket_port
import backintime.debug_tool.feature.settings.generated.resources.settings_tab_title
import io.github.kitakkun.backintime.debugger.feature.settings.component.SwitchSettingItem
import io.github.kitakkun.backintime.debugger.feature.settings.component.TextFieldSettingItem
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.rememberEventEmitter
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
object SettingsScreenRoute

fun NavGraphBuilder.settingsScreen() {
    composable<SettingsScreenRoute> {
        SettingsScreen()
    }
}

fun NavController.navigateToSettingsScreen() {
    navigate(SettingsScreenRoute) {
        restoreState = true
    }
}

@Composable
fun SettingsScreen(
    eventEmitter: EventEmitter<SettingsScreenEvent> = rememberEventEmitter(),
    uiState: SettingsScreenUiState = settingsScreenPresenter(eventEmitter),
) {
    SettingsScreen(
        uiState = uiState,
        onChangeWebSocketPort = { eventEmitter.tryEmit(SettingsScreenEvent.UpdateWebSocketPort(it)) },
        onChangeDeleteSessionDataOnDisconnect = { eventEmitter.tryEmit(SettingsScreenEvent.UpdateDeleteSessionDataOnDisconnect(it)) }
    )
}

data class SettingsScreenUiState(
    val webSocketPort: Int,
    val deleteSessionDataOnDisconnect: Boolean,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsScreenUiState,
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
                label = stringResource(Res.string.label_websocket_port),
                value = uiState.webSocketPort.toString(),
                onValueChange = { onChangeWebSocketPort(it.toIntOrNull() ?: 0) },
            )
            SwitchSettingItem(
                label = stringResource(Res.string.label_auto_erase_data),
                checked = uiState.deleteSessionDataOnDisconnect,
                onCheckedChange = onChangeDeleteSessionDataOnDisconnect,
            )
        }
    }
}

@Preview
@Composable
private fun LoadedViewPreview() {
    SettingsScreen(
        uiState = SettingsScreenUiState(
            webSocketPort = 8080,
            deleteSessionDataOnDisconnect = true,
        ),
        onChangeWebSocketPort = {},
        onChangeDeleteSessionDataOnDisconnect = {},
    )
}
