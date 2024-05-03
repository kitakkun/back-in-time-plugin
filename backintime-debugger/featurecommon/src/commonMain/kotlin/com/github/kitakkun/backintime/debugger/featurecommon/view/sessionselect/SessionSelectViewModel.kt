package com.github.kitakkun.backintime.debugger.featurecommon.view.sessionselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kitakkun.backintime.debugger.data.repository.SessionInfoRepository
import com.github.kitakkun.backintime.debugger.featurecommon.view.sessionselect.component.OpenableSessionBindModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private data class SessionSelectViewModelState(
    val selectedSessionIds: List<String> = emptyList(),
)

class SessionSelectViewModel(
    private val openedSessionIds: List<String>,
) : ViewModel(), KoinComponent {
    private val sessionInfoRepository: SessionInfoRepository by inject()
    private val mutableViewModelState = MutableStateFlow(SessionSelectViewModelState())
    private val viewModelState = mutableViewModelState.asStateFlow()

    val bindModel = combine(
        sessionInfoRepository.allConnectedSessions,
        sessionInfoRepository.allDisconnectedSessions,
        viewModelState,
    ) { allConnectedSessions, allDisconnectedSessions, viewModelState ->
        val openableSessions = (allConnectedSessions + allDisconnectedSessions)
            .filter { it.id !in openedSessionIds }
            .map { sessionInfo ->
                OpenableSessionBindModel(
                    sessionId = sessionInfo.id,
                    sessionLabel = sessionInfo.label,
                    createdAt = sessionInfo.startedAt,
                    active = sessionInfo.isConnected,
                    selected = sessionInfo.id in viewModelState.selectedSessionIds,
                )
            }
        when {
            openableSessions.isEmpty() -> SessionSelectBindModel.Empty
            else -> SessionSelectBindModel.Loaded(openableSessions)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = SessionSelectBindModel.Loading,
    )

    fun toggleSessionSelection(session: OpenableSessionBindModel) {
        mutableViewModelState.update {
            it.copy(
                selectedSessionIds = if (session.selected) {
                    it.selectedSessionIds - session.sessionId
                } else {
                    it.selectedSessionIds + session.sessionId
                },
            )
        }
    }
}
