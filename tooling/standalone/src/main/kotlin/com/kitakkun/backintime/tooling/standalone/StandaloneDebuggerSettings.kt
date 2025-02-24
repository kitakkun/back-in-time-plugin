package com.kitakkun.backintime.tooling.standalone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerSettings

class StandaloneDebuggerSettings : BackInTimeDebuggerSettings {
    private var mutableState by mutableStateOf(BackInTimeDebuggerSettings.State())

    override fun getState(): BackInTimeDebuggerSettings.State = mutableState

    override fun loadState(state: BackInTimeDebuggerSettings.State) {
        mutableState = state
    }
} 