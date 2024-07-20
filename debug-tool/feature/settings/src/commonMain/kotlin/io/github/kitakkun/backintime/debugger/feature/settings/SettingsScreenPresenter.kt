package io.github.kitakkun.backintime.debugger.feature.settings

import androidx.compose.runtime.Composable
import io.github.kitakkun.backintime.debugger.core.usecase.preferences
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEffect
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter

sealed interface SettingsScreenEvent {
    data class UpdateWebSocketPort(val port: Int) : SettingsScreenEvent
    data class UpdateDeleteSessionDataOnDisconnect(val deleteSessionDataOnDisconnect: Boolean) : SettingsScreenEvent
}

@Composable
fun settingsScreenPresenter(
    eventEmitter: EventEmitter<SettingsScreenEvent>,
): SettingsScreenUiState {
    val preferences = preferences()

    EventEffect(eventEmitter) { event ->
        when (event) {
            is SettingsScreenEvent.UpdateWebSocketPort -> {
//                settingsRepository.webSocketPort = event.port
            }

            is SettingsScreenEvent.UpdateDeleteSessionDataOnDisconnect -> {
//                settingsRepository.deleteSessionDataOnDisconnect = event.deleteSessionDataOnDisconnect
            }
        }
    }

    return SettingsScreenUiState(
        webSocketPort = preferences.webSocketPort,
        deleteSessionDataOnDisconnect = preferences.clearDataOnDisconnectEnabled,
    )
}
