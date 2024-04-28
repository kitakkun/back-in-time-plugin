package com.github.kitakkun.backintime.debugger.data.settings

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings

actual fun createObservableSettings(): ObservableSettings {
    return PreferencesSettings.Factory().create()
}
