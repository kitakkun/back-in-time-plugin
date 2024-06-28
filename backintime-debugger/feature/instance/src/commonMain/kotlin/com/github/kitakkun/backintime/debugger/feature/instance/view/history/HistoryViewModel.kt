package com.github.kitakkun.backintime.debugger.feature.instance.view.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kitakkun.backintime.debugger.data.repository.EventLogRepository
import com.github.kitakkun.backintime.debugger.data.repository.InstanceRepository
import com.github.kitakkun.backintime.debugger.feature.instance.view.history.timeline.BackInTimeTimelineItemBindModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.history.timeline.DisposeTimelineItemBindModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.history.timeline.MethodInvocationTimelineItemBindModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.history.timeline.RegisterTimelineItemBindModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class HistoryViewModel(
    @InjectedParam private val sessionId: String,
    @InjectedParam private val instanceId: String,
    instanceRepository: InstanceRepository,
    private val eventLogRepository: EventLogRepository,
) : ViewModel() {
    val bindModel = combine(
        instanceRepository.selectInstanceAsFlow(sessionId, instanceId),
    ) {
        HistoryBindModel.Loaded(
            timelines = listOf(
                RegisterTimelineItemBindModel(
                    timeMillis = System.currentTimeMillis(),
                    id = "",
                    selected = true,
                ),
                MethodInvocationTimelineItemBindModel(
                    timeMillis = System.currentTimeMillis(),
                    id = "",
                    selected = true,
                    updatedPropertyCount = 0,
                ),
                MethodInvocationTimelineItemBindModel(
                    timeMillis = System.currentTimeMillis(),
                    id = "",
                    selected = true,
                    updatedPropertyCount = 0,
                ),
                MethodInvocationTimelineItemBindModel(
                    timeMillis = System.currentTimeMillis(),
                    id = "",
                    selected = true,
                    updatedPropertyCount = 0,
                ),
                MethodInvocationTimelineItemBindModel(
                    timeMillis = System.currentTimeMillis(),
                    id = "",
                    selected = true,
                    updatedPropertyCount = 0,
                ),
                MethodInvocationTimelineItemBindModel(
                    timeMillis = System.currentTimeMillis(),
                    id = "",
                    selected = true,
                    updatedPropertyCount = 0,
                ),
                MethodInvocationTimelineItemBindModel(
                    timeMillis = System.currentTimeMillis(),
                    id = "",
                    selected = true,
                    updatedPropertyCount = 0,
                ),
                MethodInvocationTimelineItemBindModel(
                    timeMillis = System.currentTimeMillis(),
                    id = "",
                    selected = true,
                    updatedPropertyCount = 0,
                ),
                MethodInvocationTimelineItemBindModel(
                    timeMillis = System.currentTimeMillis(),
                    id = "",
                    selected = true,
                    updatedPropertyCount = 0,
                ),
                MethodInvocationTimelineItemBindModel(
                    timeMillis = System.currentTimeMillis(),
                    id = "",
                    selected = true,
                    updatedPropertyCount = 0,
                ),
                BackInTimeTimelineItemBindModel(
                    timeMillis = System.currentTimeMillis(),
                    id = "",
                    selected = true,
                    rollbackDestinationId = "",
                ),
                DisposeTimelineItemBindModel(
                    timeMillis = System.currentTimeMillis(),
                    id = "",
                    selected = true,
                ),
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HistoryBindModel.Loading,
    )
}
