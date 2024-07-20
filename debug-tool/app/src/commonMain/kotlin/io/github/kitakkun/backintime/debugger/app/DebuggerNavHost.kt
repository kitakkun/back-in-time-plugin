package io.github.kitakkun.backintime.debugger.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.kitakkun.backintime.debugger.feature.connection.connectionScreen
import io.github.kitakkun.backintime.debugger.feature.instance.InstanceTabRoute
import io.github.kitakkun.backintime.debugger.feature.instance.instanceScreen
import io.github.kitakkun.backintime.debugger.feature.instance.navigateToHistoryScreen
import io.github.kitakkun.backintime.debugger.feature.instance.navigateToInstanceSettingsScreen
import io.github.kitakkun.backintime.debugger.feature.log.logScreen
import io.github.kitakkun.backintime.debugger.feature.settings.settingsScreen
import io.github.kitakkun.backintime.debugger.featurecommon.SessionTabScreenEvent
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.rememberEventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.sessionTabScreenPresenter

@Composable
fun DebuggerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val sharedTabEventEmitter = rememberEventEmitter<SessionTabScreenEvent>()
    val sharedTabPresenter = sessionTabScreenPresenter(sharedTabEventEmitter)

    NavHost(
        navController = navController,
        startDestination = InstanceTabRoute,
        modifier = modifier,
    ) {
        instanceScreen(
            sessionTabEventEmitter = sharedTabEventEmitter,
            sessionTabUiState = sharedTabPresenter,
            onClickHistory = { sessionId, instanceId ->
                navController.navigateToHistoryScreen(
                    sessionId = sessionId,
                    instanceId = instanceId,
                )
            },
            onPressBack = navController::navigateUp,
            onClickSettings = navController::navigateToInstanceSettingsScreen,
        )
        logScreen(
            sessionTabEventEmitter = sharedTabEventEmitter,
            sessionTabUiState = sharedTabPresenter,
            onClickSettings = { /* TODO */ },
        )
        connectionScreen()
        settingsScreen()
    }
}
