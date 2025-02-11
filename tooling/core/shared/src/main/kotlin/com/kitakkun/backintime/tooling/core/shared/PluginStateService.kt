package com.kitakkun.backintime.tooling.core.shared

import com.kitakkun.backintime.tooling.model.GlobalState
import com.kitakkun.backintime.tooling.model.InspectorState
import com.kitakkun.backintime.tooling.model.LogState
import com.kitakkun.backintime.tooling.model.PluginState
import com.kitakkun.backintime.tooling.model.SettingsState
import com.kitakkun.backintime.tooling.model.Tab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface PluginStateService {
    fun getState(): PluginState
    fun loadState(state: PluginState)

    fun updateSessionId(sessionId: String) {
        val prevState = getState()
        loadState(
            prevState.copy(
                globalState = prevState.globalState.copy(selectedSessionId = sessionId),
                inspectorState = if (prevState.globalState.selectedSessionId != sessionId) InspectorState.Default else prevState.inspectorState
            )
        )
    }

    fun updateTab(tab: Tab) {
        val prevState = getState()
        loadState(prevState.copy(globalState = prevState.globalState.copy(activeTab = tab)))
    }

    fun updateGlobalState(function: (GlobalState) -> GlobalState) {
        val prevState = getState()
        val newState = prevState.copy(globalState = function(prevState.globalState))
        loadState(newState)
    }

    fun updateInspectorState(function: (InspectorState) -> InspectorState) {
        val prevState = getState()
        val newState = prevState.copy(inspectorState = function(prevState.inspectorState))
        loadState(newState)
    }

    fun updateLogState(function: (LogState) -> LogState) {
        val prevState = getState()
        val newState = prevState.copy(logState = function(prevState.logState))
        loadState(newState)
    }

    fun updateSettingsState(function: (SettingsState) -> SettingsState) {
        val prevState = getState()
        val newState = prevState.copy(settingsState = function(prevState.settingsState))
        loadState(newState)
    }

    val stateFlow: StateFlow<PluginState>

    companion object {
        val Dummy = object : PluginStateService {
            private val mutableStateFlow = MutableStateFlow(PluginState.Default)
            override val stateFlow: StateFlow<PluginState> = mutableStateFlow.asStateFlow()

            override fun loadState(state: PluginState) {
                mutableStateFlow.update { state }
            }

            override fun getState(): PluginState {
                return stateFlow.value
            }
        }
    }
}
