package com.kitakkun.backintime.tooling.idea.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerSettings

@State(
    name = "com.kitakkun.backintime.ideaplugin.service.BackInTimeDebuggerSettings",
    storages = [Storage("BackInTimeDebuggerPlugin.xml")]
)
class BackInTimeDebuggerSettingsImpl : PersistentStateComponent<BackInTimeDebuggerSettings.State>, BackInTimeDebuggerSettings {
    companion object {
        fun getInstance(): BackInTimeDebuggerSettings = ApplicationManager.getApplication().service<BackInTimeDebuggerSettingsImpl>()
    }

    private var mutableState by mutableStateOf(BackInTimeDebuggerSettings.State())

    override fun getState(): BackInTimeDebuggerSettings.State {
        return mutableState
    }

    override fun loadState(state: BackInTimeDebuggerSettings.State) {
        this.mutableState = state
    }
}
