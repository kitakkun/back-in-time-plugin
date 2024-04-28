package com.github.kitakkun.backintime.debugger.feature.connection

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.kitakkun.backintime.debugger.data.server.BackInTimeDebuggerService
import com.github.kitakkun.backintime.debugger.data.server.BackInTimeDebuggerServiceState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent

class ConnectionTabScreenModel(
    debuggerService: BackInTimeDebuggerService,
) : ScreenModel, KoinComponent {
    val bindModel = combine(
        debuggerService.serviceStateFlow,
        debuggerService.connectionSpecsFlow,
    ) { serviceState, connectionSpecs ->
        when (serviceState) {
            is BackInTimeDebuggerServiceState.Uninitialized -> ConnectionTabBindModel.ServerNotStarted
            is BackInTimeDebuggerServiceState.Running -> ConnectionTabBindModel.ServerRunning(
                host = serviceState.host,
                port = serviceState.port,
                sessionBindModels = connectionSpecs.map {
                    SessionBindModel(
                        host = it.host,
                        port = it.port,
                        sessionId = it.id,
                    )
                },
            )

            is BackInTimeDebuggerServiceState.Error -> ConnectionTabBindModel.ServerError(
                error = serviceState.error,
            )
        }
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Lazily,
        initialValue = ConnectionTabBindModel.Loading,
    )
}
