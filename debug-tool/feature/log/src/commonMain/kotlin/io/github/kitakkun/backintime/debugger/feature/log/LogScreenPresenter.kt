package io.github.kitakkun.backintime.debugger.feature.log

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import io.github.kitakkun.backintime.debugger.core.usecase.eventLogs
import io.github.kitakkun.backintime.debugger.feature.log.section.LogItemUiState
import io.github.kitakkun.backintime.debugger.feature.log.section.SessionLogTableUiState
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEffect
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.component.EventKind
import io.github.takahirom.rin.rememberRetained

sealed interface LogScreenEvent {
    data object ToggleSortWithTime : LogScreenEvent
    data object ToggleSortWithKind : LogScreenEvent
    data class VisibleKindsUpdated(val kinds: Set<EventKind>) : LogScreenEvent
    data class SelectLogItem(val id: String) : LogScreenEvent
}

@Composable
fun logScreenPresenter(
    eventEmitter: EventEmitter<LogScreenEvent>,
    sessionId: String,
): LogScreenUiState {
    val eventLogs = eventLogs(sessionId)
    var selectedLogId by rememberRetained { mutableStateOf<String?>(null) }

    val logItemUiStates by rememberUpdatedState(
        eventLogs.map {
            LogItemUiState(
                id = it.id,
                payload = it.payload,
                createdAt = it.createdAt,
                selected = it.id == selectedLogId,
            )
        }
    )

    EventEffect(eventEmitter) { event ->
        when (event) {
            LogScreenEvent.ToggleSortWithKind -> {}
            LogScreenEvent.ToggleSortWithTime -> {}
            is LogScreenEvent.VisibleKindsUpdated -> {}
            is LogScreenEvent.SelectLogItem -> {
                selectedLogId = event.id
            }
        }
    }

    return LogScreenUiState(
        tableUiState = SessionLogTableUiState(
            logs = logItemUiStates,
            sortRule = SessionLogTableUiState.SortRule.KIND_ASC,
            visibleKinds = setOf(),
        )
    )
}
