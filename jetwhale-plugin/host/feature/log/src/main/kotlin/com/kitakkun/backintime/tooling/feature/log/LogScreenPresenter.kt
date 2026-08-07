package com.kitakkun.backintime.tooling.feature.log

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalPluginStateService
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalSessionId
import com.kitakkun.backintime.tooling.core.ui.logic.EventEffect
import com.kitakkun.backintime.tooling.core.ui.logic.EventEmitter
import com.kitakkun.backintime.tooling.core.usecase.allEvents

sealed interface LogScreenEvent {
    data class UpdateVerticalSplitDividerPosition(val position: Float) : LogScreenEvent
    data class SelectEvent(val eventId: String) : LogScreenEvent
}

@Composable
fun logScreenPresenter(eventEmitter: EventEmitter<LogScreenEvent>): LogScreenUiState {
    val pluginStateService = LocalPluginStateService.current
    val pluginState by pluginStateService.stateFlow.collectAsState()

    EventEffect(eventEmitter) { event ->
        when (event) {
            is LogScreenEvent.UpdateVerticalSplitDividerPosition -> pluginStateService.loadState(
                pluginState.copy(logState = pluginState.logState.copy(verticalSplitPanePosition = event.position))
            )

            is LogScreenEvent.SelectEvent -> pluginStateService.loadState(
                pluginState.copy(logState = pluginState.logState.copy(selectedEventId = event.eventId))
            )
        }
    }

    return LogScreenUiState(
        events = allEvents(LocalSessionId.current),
        selectedEventId = pluginState.logState.selectedEventId,
        verticalSplitDividerPosition = pluginState.logState.verticalSplitPanePosition,
    )
}
