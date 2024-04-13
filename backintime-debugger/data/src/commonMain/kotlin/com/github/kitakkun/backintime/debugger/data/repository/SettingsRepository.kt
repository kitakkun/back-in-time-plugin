package com.github.kitakkun.backintime.debugger.data.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.boolean
import com.russhwolf.settings.int

interface SettingsRepository {
    var webSocketPort: Int
    var deleteSessionDataOnDisconnect: Boolean
}

class SettingsRepositoryImpl : SettingsRepository {
    private val settings: Settings = Settings()

    override var webSocketPort: Int by settings.int("port", 8080)
    override var deleteSessionDataOnDisconnect: Boolean by settings.boolean("deleteSessionDataOnDisconnect", true)
}
