package com.kitakkun.backintime.tooling.standalone

import com.kitakkun.backintime.tooling.core.shared.PluginStateService
import com.kitakkun.backintime.tooling.model.PluginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StandalonePluginStateService : PluginStateService {
    private val mutableStateFlow = MutableStateFlow(PluginState.Default)
    override val stateFlow: StateFlow<PluginState> = mutableStateFlow.asStateFlow()

    override fun getState(): PluginState = mutableStateFlow.value

    override fun loadState(state: PluginState) {
        mutableStateFlow.update { state }
    }
}
