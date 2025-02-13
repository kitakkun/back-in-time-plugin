package com.kitakkun.backintime.tooling.feature.log

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalPluginStateService
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalServer
import com.kitakkun.backintime.tooling.core.ui.logic.EventEffect
import com.kitakkun.backintime.tooling.core.ui.logic.EventEmitter
import com.kitakkun.backintime.tooling.core.usecase.allEvents

sealed interface LogScreenEvent {
    data class UpdateVerticalSplitDividerPosition(val position: Float) : LogScreenEvent
    data class SelectSession(val sessionId: String) : LogScreenEvent
    data class SelectEvent(val eventId: String) : LogScreenEvent
}

@Composable
fun logScreenPresenter(eventEmitter: EventEmitter<LogScreenEvent>): LogScreenUiState {
    val pluginStateService = LocalPluginStateService.current
    val pluginState by pluginStateService.stateFlow.collectAsState()
    val server = LocalServer.current

    EventEffect(eventEmitter) { event ->
        when (event) {
            is LogScreenEvent.UpdateVerticalSplitDividerPosition -> pluginStateService.loadState(
                pluginState.copy(logState = pluginState.logState.copy(verticalSplitPanePosition = event.position))
            )

            is LogScreenEvent.SelectSession -> pluginStateService.loadState(
                pluginState.copy(globalState = pluginState.globalState.copy(selectedSessionId = event.sessionId))
            )

            is LogScreenEvent.SelectEvent -> pluginStateService.loadState(
                pluginState.copy(logState = pluginState.logState.copy(selectedEventId = event.eventId))
            )
        }
    }

    return LogScreenUiState(
        events = allEvents(pluginState.globalState.selectedSessionId),
        selectedSessionId = pluginStateService.getState().globalState.selectedSessionId,
        sessionIdCandidates = server.state.connections.map { it.id },
        selectedEventId = pluginState.logState.selectedEventId,
        verticalSplitDividerPosition = pluginState.logState.verticalSplitPanePosition,
    )
}
