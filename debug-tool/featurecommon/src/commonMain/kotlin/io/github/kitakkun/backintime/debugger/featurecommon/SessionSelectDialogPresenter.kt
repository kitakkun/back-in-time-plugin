package io.github.kitakkun.backintime.debugger.featurecommon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.kitakkun.backintime.debugger.core.usecase.sessionInfoList
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEffect
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.component.OpenableSessionUiState
import io.github.takahirom.rin.rememberRetained

sealed interface SessionSelectDialogEvent {
    data class ToggleSelection(val sessionId: String) : SessionSelectDialogEvent
}

@Composable
fun sessionSelectDialogPresenter(
    eventEmitter: EventEmitter<SessionSelectDialogEvent>,
    route: SessionSelectDialogRoute,
): SessionSelectUiState {
    val allSessions = sessionInfoList()
    var selectedSessionIds by rememberRetained { mutableStateOf<List<String>>(emptyList()) }

    val sessionUiStates: List<OpenableSessionUiState> = allSessions
        .filter { it.id !in route.openedSessionIds }
        .map { sessionInfo ->
            OpenableSessionUiState(
                sessionId = sessionInfo.id,
                sessionLabel = sessionInfo.label ?: "",
                createdAt = sessionInfo.createdAt,
                active = sessionInfo.isActive,
                selected = sessionInfo.id in selectedSessionIds,
            )
        }

    EventEffect(eventEmitter) { event ->
        when (event) {
            is SessionSelectDialogEvent.ToggleSelection -> {
                if (selectedSessionIds.contains(event.sessionId)) {
                    selectedSessionIds = selectedSessionIds.filter { it != event.sessionId }
                } else {
                    selectedSessionIds = selectedSessionIds + event.sessionId
                }
            }
        }
    }

    return SessionSelectUiState(sessionUiStates)
}
