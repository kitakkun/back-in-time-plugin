package com.github.kitakkun.backintime.debugger.feature.connection

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.kitakkun.backintime.debugger.data.server.BackInTimeDebuggerService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent

class ConnectionTabScreenModel(
    debuggerService: BackInTimeDebuggerService,
) : ScreenModel, KoinComponent {
    val bindModel = combine(
        debuggerService.serverSpecFlow,
        debuggerService.connectionSpecsFlow,
    ) { serverSpec, connectionSpecs ->
        when (serverSpec) {
            null -> ConnectionTabBindModel.ServerNotStarted
            else -> ConnectionTabBindModel.ServerRunning(
                host = serverSpec.host,
                port = serverSpec.port,
                sessionBindModels = connectionSpecs.map {
                    SessionBindModel(
                        host = it.host,
                        port = it.port,
                        sessionId = it.id,
                    )
                },
            )
        }
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Lazily,
        initialValue = ConnectionTabBindModel.Loading,
    )
}
