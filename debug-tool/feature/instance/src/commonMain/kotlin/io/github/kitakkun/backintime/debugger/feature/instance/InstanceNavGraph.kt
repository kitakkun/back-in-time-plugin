package io.github.kitakkun.backintime.debugger.feature.instance

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import io.github.kitakkun.backintime.debugger.featurecommon.SessionTabScreenEvent
import io.github.kitakkun.backintime.debugger.featurecommon.SessionTabScreenUiState
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import kotlinx.serialization.Serializable

@Serializable
data object InstanceTabRoute

fun NavGraphBuilder.instanceScreen(
    sessionTabEventEmitter: EventEmitter<SessionTabScreenEvent>,
    sessionTabUiState: SessionTabScreenUiState,
    onClickHistory: (sessionId: String, instanceId: String) -> Unit,
    onPressBack: () -> Unit,
    onClickSettings: () -> Unit,
) {
    navigation<InstanceTabRoute>(startDestination = InstanceListScreenRoute) {
        instanceListScreen(
            sessionTabEventEmitter = sessionTabEventEmitter,
            sessionTabUiState = sessionTabUiState,
            onClickHistory = onClickHistory,
            onClickSettings = onClickSettings,
        )
        historyScreen(onPressBack = onPressBack)
        instanceSettingsScreen(onPressBack = onPressBack)
    }
}

fun NavController.navigateToInstanceTab() {
    navigate(InstanceTabRoute) {
        restoreState = true
    }
}
