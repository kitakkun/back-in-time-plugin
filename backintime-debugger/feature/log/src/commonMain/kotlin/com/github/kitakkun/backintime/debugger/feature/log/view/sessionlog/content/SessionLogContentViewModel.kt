package com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kitakkun.backintime.debugger.data.repository.EventLogRepository
import com.github.kitakkun.backintime.debugger.database.EventLog
import com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.content.model.EventKind
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private data class SessionLogContentViewModelState(
    val sortRule: SortRule = SortRule.CREATED_AT_ASC,
    val visibleKinds: Set<EventKind> = EventKind.entries.toSet(),
)

enum class SortRule {
    CREATED_AT_ASC,
    CREATED_AT_DESC,
    KIND_ASC,
    KIND_DESC,
}

@KoinViewModel
class SessionLogContentViewModel(
    @InjectedParam sessionId: String,
) : ViewModel(), KoinComponent {
    private val eventLogRepository: EventLogRepository by inject()

    private val mutableViewModelState = MutableStateFlow(SessionLogContentViewModelState())
    private val viewModelState = mutableViewModelState.asStateFlow()

    val bindModel = combine(
        eventLogRepository.logFlow(sessionId),
        viewModelState,
    ) { logs, viewModelState ->
        val logBindModels = logs.map { eventLog: EventLog ->
            LogItemBindModel(
                createdAt = eventLog.createdAt,
                payload = eventLog.payload,
            )
        }

        val sortedBindModels = when (viewModelState.sortRule) {
            SortRule.CREATED_AT_ASC -> logBindModels.sortedBy { it.createdAt }
            SortRule.CREATED_AT_DESC -> logBindModels.sortedByDescending { it.createdAt }
            SortRule.KIND_ASC -> logBindModels.sortedBy { it.kind }
            SortRule.KIND_DESC -> logBindModels.sortedByDescending { it.kind }
        }

        val kindFilteredBindModels = sortedBindModels.filter { viewModelState.visibleKinds.contains(it.kind) }

        SessionLogContentBindModel.Loaded(
            logs = kindFilteredBindModels,
            sortRule = viewModelState.sortRule,
            visibleKinds = viewModelState.visibleKinds,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = SessionLogContentBindModel.Loading,
    )

    fun onToggleSortWithTime() {
        mutableViewModelState.update {
            it.copy(
                sortRule = when (it.sortRule) {
                    SortRule.CREATED_AT_ASC -> SortRule.CREATED_AT_DESC
                    SortRule.CREATED_AT_DESC -> SortRule.CREATED_AT_ASC
                    SortRule.KIND_ASC -> SortRule.CREATED_AT_DESC
                    SortRule.KIND_DESC -> SortRule.CREATED_AT_DESC
                },
            )
        }
    }

    fun onToggleSortWithKind() {
        mutableViewModelState.update {
            it.copy(
                sortRule = when (it.sortRule) {
                    SortRule.CREATED_AT_ASC -> SortRule.KIND_DESC
                    SortRule.CREATED_AT_DESC -> SortRule.KIND_DESC
                    SortRule.KIND_ASC -> SortRule.KIND_DESC
                    SortRule.KIND_DESC -> SortRule.KIND_ASC
                },
            )
        }
    }

    fun updateVisibleKinds(visibleKinds: Set<EventKind>) {
        mutableViewModelState.update {
            it.copy(visibleKinds = visibleKinds)
        }
    }
}
