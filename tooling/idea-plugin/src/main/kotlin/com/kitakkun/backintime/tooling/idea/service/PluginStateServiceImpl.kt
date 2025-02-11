package com.kitakkun.backintime.tooling.idea.service

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.kitakkun.backintime.tooling.core.shared.PluginStateService
import com.kitakkun.backintime.tooling.model.PluginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@State(
    name = "com.kitakkun.backintime.ideaplugin.service.PluginUiStateProvider",
    storages = [Storage("BackInTimeUiState.xml")]
)
class PluginStateServiceImpl : PluginStateService, PersistentStateComponent<PluginState> {
    companion object {
        fun getInstance(): PluginStateService = ApplicationManager.getApplication().service<PluginStateServiceImpl>()
    }

    private var mutableStateFlow: MutableStateFlow<PluginState> = MutableStateFlow(PluginState.Default)
    override val stateFlow: StateFlow<PluginState> = mutableStateFlow.asStateFlow()

    override fun getState(): PluginState = stateFlow.value
    override fun loadState(state: PluginState) = mutableStateFlow.update { state }
}
