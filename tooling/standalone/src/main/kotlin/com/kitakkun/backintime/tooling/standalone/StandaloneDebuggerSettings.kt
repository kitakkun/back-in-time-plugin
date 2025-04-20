package com.kitakkun.backintime.tooling.standalone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerSettings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class StandaloneDebuggerSettings : BackInTimeDebuggerSettings {
    private val settingsFile = File(System.getProperty("user.home"), ".backintime/settings.json")
    private var mutableState by mutableStateOf(loadPersistedState())

    private fun loadPersistedState(): BackInTimeDebuggerSettings.State {
        return try {
            Json.decodeFromString(settingsFile.readText())
        } catch (e: Exception) {
            BackInTimeDebuggerSettings.State()
        }
    }

    override fun getState(): BackInTimeDebuggerSettings.State = mutableState

    override fun loadState(state: BackInTimeDebuggerSettings.State) {
        mutableState = state
        persistState()
    }

    private fun persistState() {
        settingsFile.parentFile.mkdirs()
        settingsFile.writeText(Json.encodeToString(mutableState))
    }
} 