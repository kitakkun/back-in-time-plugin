package io.github.kitakkun.backintime.debugger.feature.instance

import androidx.compose.runtime.Composable
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter

sealed interface HistoryScreenEvent {
}

@Composable
fun historyScreenPresenter(
    eventEmitter: EventEmitter<HistoryScreenEvent>,
    sessionId: String,
    instanceId: String,
): HistoryScreenUiState {
//    val instanceRepository: InstanceRepository = koinInject()
//    val instances = instanceRepository.selectInstanceAsFlow(sessionId, instanceId)

    return HistoryScreenUiState.Loading
}
