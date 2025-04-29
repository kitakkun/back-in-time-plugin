package com.kitakkun.backintime.tooling.idea.service

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.util.xmlb.annotations.Tag
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Almost same as [BackInTimeDebuggerSettings.State]
 * Just add Tag annotations to persist fields
 */
data class BackInTimeSavableSettingsState(
    @Tag val serverPort: Int = 50023,
    @Tag val showNonDebuggableProperties: Boolean = true,
    @Tag val persistSessionData: Boolean = false,
    @Tag val databasePath: String? = null,
)

@State(
    name = "BackInTimeDebuggerSettings",
    storages = [Storage("BackInTimeDebuggerPlugin.xml")]
)
class BackInTimeDebuggerSettingsImpl : PersistentStateComponent<BackInTimeSavableSettingsState>, BackInTimeDebuggerSettings {
    companion object {
        fun getInstance(): BackInTimeDebuggerSettings = ApplicationManager.getApplication().service<BackInTimeDebuggerSettingsImpl>()
    }

    private val mutableStateFlow = MutableStateFlow(BackInTimeDebuggerSettings.State())
    override val stateFlow = mutableStateFlow.asStateFlow()

    override fun update(block: (BackInTimeDebuggerSettings.State) -> BackInTimeDebuggerSettings.State) {
        mutableStateFlow.update(block)
    }

    override fun getState(): BackInTimeSavableSettingsState {
        return BackInTimeSavableSettingsState(
            serverPort = stateFlow.value.serverPort,
            showNonDebuggableProperties = stateFlow.value.showNonDebuggableProperties,
            persistSessionData = stateFlow.value.persistSessionData,
            databasePath = stateFlow.value.databasePath,
        )
    }

    override fun loadState(state: BackInTimeSavableSettingsState) {
        mutableStateFlow.update {
            BackInTimeDebuggerSettings.State(
                serverPort = state.serverPort,
                showNonDebuggableProperties = state.showNonDebuggableProperties,
                persistSessionData = state.persistSessionData,
                databasePath = state.databasePath,
            )
        }
    }
}
