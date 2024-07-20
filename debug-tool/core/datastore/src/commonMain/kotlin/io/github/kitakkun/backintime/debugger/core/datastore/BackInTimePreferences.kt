package io.github.kitakkun.backintime.debugger.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.kitakkun.backintime.debugger.core.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface BackInTimePreferences {
    // general
    val languageFlow: Flow<Language>
    val webSocketPortFlow: Flow<Int>
    val clearDataOnDisconnectEnabledFlow: Flow<Boolean>

    suspend fun updateWebSocketPort(port: Int)
    suspend fun updateClearDataOnDisconnectedEnabled(enabled: Boolean)
    suspend fun updateLanguage(language: Language)
}

class BackInTimePreferencesImpl(
    private val dataStore: DataStore<Preferences>,
) : BackInTimePreferences {
    companion object {
        private val KEY_LANGUAGE = stringPreferencesKey("KEY_LANGUAGE")
        private val KEY_WEBSOCKET_PORT = intPreferencesKey("KEY_WEBSOCKET_PORT")
        private val KEY_CLEAR_DATA_ON_DISCONNECT_ENABLED = booleanPreferencesKey("KEY_CLEAR_DATA_ON_DISCONNECT_ENABLED")

        private val DEFAULT_LANGUAGE = Language.SYSTEM.preferencesKey
        private const val DEFAULT_WEBSOCKET_PORT = 8080
        private const val DEFAULT_CLEAR_DATA_ON_DISCONNECT_ENABLED = false
    }

    override val languageFlow: Flow<Language> = dataStore.getFlow(KEY_LANGUAGE, DEFAULT_LANGUAGE).map { Language.convert(it) }
    override val webSocketPortFlow: Flow<Int> = dataStore.getFlow(KEY_WEBSOCKET_PORT, DEFAULT_WEBSOCKET_PORT)
    override val clearDataOnDisconnectEnabledFlow: Flow<Boolean> = dataStore.getFlow(KEY_CLEAR_DATA_ON_DISCONNECT_ENABLED, DEFAULT_CLEAR_DATA_ON_DISCONNECT_ENABLED)

    override suspend fun updateLanguage(language: Language) {
        dataStore.edit { preferences -> preferences[KEY_LANGUAGE] = language.preferencesKey }
    }

    override suspend fun updateWebSocketPort(port: Int) {
        dataStore.edit { preferences -> preferences[KEY_WEBSOCKET_PORT] = port }
    }

    override suspend fun updateClearDataOnDisconnectedEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[KEY_CLEAR_DATA_ON_DISCONNECT_ENABLED] = enabled }
    }
}

private inline fun <reified T> DataStore<Preferences>.getFlow(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
    return data.map { it[key] ?: defaultValue }
}
