package io.github.kitakkun.backintime.debugger.feature.instance

import androidx.compose.runtime.Composable
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter

sealed interface InstanceSettingsScreenEvent {

}

@Composable
fun instanceSettingsPresenter(eventEmitter: EventEmitter<InstanceSettingsScreenEvent>): InstanceSettingsUiState {
    return InstanceSettingsUiState(
        false,
        false,
        false,
    )

}
