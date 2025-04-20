package com.kitakkun.backintime.tooling.idea.service

import com.intellij.openapi.components.Service
import com.kitakkun.backintime.tooling.core.shared.PluginStateService
import com.kitakkun.backintime.tooling.model.PluginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Service(Service.Level.PROJECT)
class PluginStateServiceImpl : PluginStateService {
    private var mutableStateFlow: MutableStateFlow<PluginState> = MutableStateFlow(PluginState.Default)
    override val stateFlow: StateFlow<PluginState> = mutableStateFlow.asStateFlow()

    override fun getState(): PluginState = stateFlow.value
    override fun loadState(state: PluginState) = mutableStateFlow.update { state }
}
