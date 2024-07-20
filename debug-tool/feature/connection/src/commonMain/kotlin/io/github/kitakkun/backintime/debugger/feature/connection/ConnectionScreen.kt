package io.github.kitakkun.backintime.debugger.feature.connection

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import backintime.debug_tool.feature.connection.generated.resources.Res
import backintime.debug_tool.feature.connection.generated.resources.text_loading_server_status
import io.github.kitakkun.backintime.debugger.feature.connection.component.ServerErrorView
import io.github.kitakkun.backintime.debugger.feature.connection.component.ServerNotStartedView
import io.github.kitakkun.backintime.debugger.feature.connection.component.ServerRunningView
import io.github.kitakkun.backintime.debugger.feature.connection.component.SessionItemUiState
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.rememberEventEmitter
import io.github.kitakkun.backintime.debugger.ui.customview.CommonLoadingView
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
object ConnectionScreenRoute

fun NavGraphBuilder.connectionScreen() {
    composable<ConnectionScreenRoute> {
        ConnectionScreen()
    }
}

fun NavController.navigateToConnectionScreen() {
    navigate(ConnectionScreenRoute) {
        restoreState = true
    }
}

@Composable
fun ConnectionScreen(
    eventEmitter: EventEmitter<ConnectionScreenEvent> = rememberEventEmitter(),
    uiState: ConnectionScreenUiState = connectionScreenPresenter(eventEmitter),
) {
    ConnectionScreen(uiState = uiState)
}

sealed interface ConnectionScreenUiState {
    data object Loading : ConnectionScreenUiState
    data object ServerNotStarted : ConnectionScreenUiState
    data class ServerRunning(
        val host: String,
        val port: Int,
        val sessionItems: List<SessionItemUiState>,
    ) : ConnectionScreenUiState

    data class ServerError(private val error: Throwable) : ConnectionScreenUiState
}

@Composable
fun ConnectionScreen(
    uiState: ConnectionScreenUiState,
) {
    when (uiState) {
        is ConnectionScreenUiState.Loading -> CommonLoadingView(message = stringResource(Res.string.text_loading_server_status))
        is ConnectionScreenUiState.ServerNotStarted -> ServerNotStartedView()
        is ConnectionScreenUiState.ServerRunning -> ServerRunningView(uiState)
        is ConnectionScreenUiState.ServerError -> ServerErrorView()
    }
}
