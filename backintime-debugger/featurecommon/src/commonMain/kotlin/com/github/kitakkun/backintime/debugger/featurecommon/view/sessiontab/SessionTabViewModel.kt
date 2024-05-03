package com.github.kitakkun.backintime.debugger.featurecommon.view.sessiontab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kitakkun.backintime.debugger.data.repository.SessionInfoRepository
import com.github.kitakkun.backintime.debugger.database.SessionInfo
import com.github.kitakkun.backintime.debugger.featurecommon.view.sessiontab.components.SessionTabItemBindModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private data class SessionTabViewModelState(
    val selectedSessionId: String = "",
    val openedSessionIds: List<String> = emptyList(),
    val showSelectSessionDialog: Boolean = false,
)

class SessionTabViewModel : ViewModel(), KoinComponent {
    private val sessionInfoRepository: SessionInfoRepository by inject()
    private val mutableViewModelState = MutableStateFlow(SessionTabViewModelState())
    private val viewModelState = mutableViewModelState.asStateFlow()

    val bindModel = combine(
        sessionInfoRepository.allConnectedSessions,
        sessionInfoRepository.allDisconnectedSessions,
        viewModelState,
    ) { connectedSessions, disconnectedSessions, viewModelState ->
        when {
            connectedSessions.isEmpty() && viewModelState.openedSessionIds.isEmpty() -> SessionTabBindModel.NoConnections(viewModelState.showSelectSessionDialog)
            else -> {
                val sessions = (connectedSessions + disconnectedSessions)
                    .map { convert(it, viewModelState.selectedSessionId) }
                    .filter { it.hasActiveConnection || viewModelState.openedSessionIds.contains(it.sessionId) }
                SessionTabBindModel.WithSessions(openedSessions = sessions, showSelectSessionDialog = viewModelState.showSelectSessionDialog)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = SessionTabBindModel.Loading(showSelectSessionDialog = false),
    )

    fun showSelectSessionDialog() {
        mutableViewModelState.update {
            it.copy(showSelectSessionDialog = true)
        }
    }

    fun dismissSelectSessionDialog() {
        mutableViewModelState.update {
            it.copy(showSelectSessionDialog = false)
        }
    }

    fun selectSession(sessionId: String) {
        mutableViewModelState.update {
            it.copy(selectedSessionId = sessionId)
        }
    }

    fun openSessions(sessionIds: List<String>) {
        mutableViewModelState.update {
            it.copy(openedSessionIds = it.openedSessionIds + sessionIds)
        }
    }

    private fun convert(sessionInfo: SessionInfo, selectedSessionId: String): SessionTabItemBindModel {
        return SessionTabItemBindModel(
            sessionId = sessionInfo.id,
            label = sessionInfo.id,
            hasActiveConnection = sessionInfo.isConnected,
            selected = sessionInfo.id == selectedSessionId,
        )
    }

    fun closeSessionTab(session: SessionTabItemBindModel) {
        if (viewModelState.value.selectedSessionId == session.sessionId) {
            val firstSessionId = (bindModel.value as? SessionTabBindModel.WithSessions)?.openedSessions?.first()?.sessionId ?: ""
            // FIXME: need this before update selectedSessionId to update view, but it's quite complicated...
            mutableViewModelState.update {
                it.copy(selectedSessionId = "")
            }
            mutableViewModelState.update {
                it.copy(selectedSessionId = firstSessionId)
            }
        }
        mutableViewModelState.update {
            it.copy(openedSessionIds = it.openedSessionIds - session.sessionId)
        }
    }
}
