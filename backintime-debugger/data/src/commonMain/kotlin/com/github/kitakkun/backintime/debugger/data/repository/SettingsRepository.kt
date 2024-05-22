package com.github.kitakkun.backintime.debugger.data.repository

import com.github.kitakkun.backintime.debugger.data.settings.createObservableSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.boolean
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getIntFlow
import com.russhwolf.settings.int
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Singleton

interface SettingsRepository {
    var webSocketPort: Int
    val websocketPortFlow: Flow<Int>
    var deleteSessionDataOnDisconnect: Boolean
    val deleteSessionDataOnDisconnectFlow: Flow<Boolean>
}

@OptIn(ExperimentalSettingsApi::class)
@Singleton(binds = [SettingsRepository::class])
class SettingsRepositoryImpl : SettingsRepository {
    private val observableSettings: ObservableSettings = createObservableSettings()

    override var webSocketPort: Int by observableSettings.int("port", 8080)
    override val websocketPortFlow: Flow<Int> = observableSettings.getIntFlow("port", 8080)

    override var deleteSessionDataOnDisconnect: Boolean by observableSettings.boolean("deleteSessionDataOnDisconnect", true)
    override val deleteSessionDataOnDisconnectFlow: Flow<Boolean> = observableSettings.getBooleanFlow("deleteSessionDataOnDisconnect", true)
}
