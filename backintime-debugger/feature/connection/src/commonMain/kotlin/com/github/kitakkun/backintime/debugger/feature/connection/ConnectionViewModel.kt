package com.github.kitakkun.backintime.debugger.feature.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kitakkun.backintime.debugger.data.server.BackInTimeDebuggerService
import com.github.kitakkun.backintime.debugger.data.server.BackInTimeDebuggerServiceState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinViewModel
class ConnectionViewModel : ViewModel(), KoinComponent {
    private val debuggerService: BackInTimeDebuggerService by inject()

    val bindModel = combine(
        debuggerService.serviceStateFlow,
        debuggerService.connectionSpecsFlow,
    ) { serviceState, connectionSpecs ->
        when (serviceState) {
            is BackInTimeDebuggerServiceState.Uninitialized -> ConnectionBindModel.ServerNotStarted
            is BackInTimeDebuggerServiceState.Running -> ConnectionBindModel.ServerRunning(
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

            is BackInTimeDebuggerServiceState.Error -> ConnectionBindModel.ServerError(
                error = serviceState.error,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ConnectionBindModel.Loading,
    )
}
