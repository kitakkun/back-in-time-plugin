package com.kitakkun.backintime.tooling.core.shared

import com.kitakkun.backintime.tooling.model.InspectorState
import com.kitakkun.backintime.tooling.model.PluginState
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

    fun togglePropertyExpansion(instanceId: String) {
        val prevState = getState()
        val newState = prevState.copy(
            inspectorState = prevState.inspectorState.copy(
                expandedInstanceIds = if (instanceId in prevState.inspectorState.expandedInstanceIds) {
                    prevState.inspectorState.expandedInstanceIds - instanceId
                } else {
                    prevState.inspectorState.expandedInstanceIds + instanceId
                },
            )
        )
        loadState(newState)
    }

    fun updateTab(tab: Tab) {
        val prevState = getState()
        loadState(prevState.copy(globalState = prevState.globalState.copy(activeTab = tab)))
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
