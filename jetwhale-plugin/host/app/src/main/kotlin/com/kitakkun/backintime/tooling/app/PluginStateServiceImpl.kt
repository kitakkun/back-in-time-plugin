package com.kitakkun.backintime.tooling.app

import com.kitakkun.backintime.tooling.core.shared.PluginStateService
import com.kitakkun.backintime.tooling.model.PluginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Plain in-memory UI state holder. Persisting it is the host plugin's job — only the plugin has
 * access to JetWhale's per-plugin storage.
 */
class PluginStateServiceImpl : PluginStateService {
    private val mutableStateFlow: MutableStateFlow<PluginState> = MutableStateFlow(PluginState.Default)
    override val stateFlow: StateFlow<PluginState> = mutableStateFlow.asStateFlow()

    override fun getState(): PluginState = stateFlow.value
    override fun loadState(state: PluginState) = mutableStateFlow.update { state }
}
