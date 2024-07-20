package io.github.kitakkun.backintime.debugger.feature.connection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.kitakkun.backintime.debugger.core.server.BackInTimeDebuggerService
import io.github.kitakkun.backintime.debugger.core.server.BackInTimeDebuggerServiceState
import io.github.kitakkun.backintime.debugger.feature.connection.component.SessionItemUiState
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEffect
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import io.github.takahirom.rin.rememberRetained
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

sealed interface ConnectionScreenEvent

@Composable
fun connectionScreenPresenter(eventEmitter: EventEmitter<ConnectionScreenEvent>): ConnectionScreenUiState {
    val debuggerService: BackInTimeDebuggerService = koinInject()
    val serviceState by debuggerService.serviceStateFlow.collectAsState()
    val sessions = rememberRetained { mutableListOf<SessionItemUiState>() }
//
    LaunchedEffect(Unit) {
        launch {
            debuggerService.newConnectionFlow.collect {
                sessions += SessionItemUiState(
                    host = it.host,
                    port = it.port,
                    sessionId = it.id,
                )
            }
        }
    }

    EventEffect(eventEmitter) { event ->
    }

    return when (val state = serviceState) {
        is BackInTimeDebuggerServiceState.Uninitialized -> ConnectionScreenUiState.ServerNotStarted
        is BackInTimeDebuggerServiceState.Error -> ConnectionScreenUiState.ServerError(error = state.error)
        is BackInTimeDebuggerServiceState.Running -> ConnectionScreenUiState.ServerRunning(
            host = state.host,
            port = state.port,
            sessionItems = sessions
        )
    }
}
