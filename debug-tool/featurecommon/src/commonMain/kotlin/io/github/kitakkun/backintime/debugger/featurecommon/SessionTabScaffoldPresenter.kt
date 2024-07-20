package io.github.kitakkun.backintime.debugger.featurecommon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import io.github.kitakkun.backintime.debugger.core.usecase.sessionInfoList
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEffect
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.component.SessionItem
import io.github.takahirom.rin.rememberRetained

sealed interface SessionTabScreenEvent {
    data object OpenSelectDialog : SessionTabScreenEvent
    data object CloseSelectDialog : SessionTabScreenEvent
    data class OpenSelectedSessions(val sessionIds: List<String>) : SessionTabScreenEvent
    data class SelectTab(val sessionId: String) : SessionTabScreenEvent
    data class CloseTab(val sessionId: String) : SessionTabScreenEvent
}

@Composable
fun sessionTabScreenPresenter(eventEmitter: EventEmitter<SessionTabScreenEvent>): SessionTabScreenUiState {
    val manuallyOpenedSessionIds = rememberRetained { mutableStateListOf<String>() }

    val allSessionInfoList = sessionInfoList()
    val filteredSessionInfoList by rememberUpdatedState(allSessionInfoList.filter { it.isActive || it.id in manuallyOpenedSessionIds })

    var selectedSessionId by rememberRetained { mutableStateOf<String?>(null) }
    var showSessionSelectDialog by rememberRetained { mutableStateOf(false) }

    LaunchedEffect(filteredSessionInfoList) {
        if (selectedSessionId == null) {
            selectedSessionId = filteredSessionInfoList.firstOrNull()?.id
        }
    }

    EventEffect(eventEmitter) { event ->
        when (event) {
            is SessionTabScreenEvent.OpenSelectedSessions -> {
                manuallyOpenedSessionIds.addAll(event.sessionIds)
            }

            is SessionTabScreenEvent.CloseSelectDialog -> {
                showSessionSelectDialog = false
            }

            is SessionTabScreenEvent.OpenSelectDialog -> {
                showSessionSelectDialog = true
            }

            is SessionTabScreenEvent.SelectTab -> {
                selectedSessionId = event.sessionId
            }

            is SessionTabScreenEvent.CloseTab -> {
                val index = filteredSessionInfoList.indexOfFirst { it.id == event.sessionId }
                manuallyOpenedSessionIds.remove(event.sessionId)
                selectedSessionId = filteredSessionInfoList.getOrNull(index - 1)?.id ?: filteredSessionInfoList.firstOrNull()?.id
            }
        }
    }

    return SessionTabScreenUiState(
        sessions = filteredSessionInfoList.map {
            SessionItem(
                id = it.id,
                label = it.label ?: it.id,
                hasActiveConnection = it.isActive,
                selected = it.id == selectedSessionId,
            )
        },
        showSessionSelectDialog = showSessionSelectDialog,
    )
}
