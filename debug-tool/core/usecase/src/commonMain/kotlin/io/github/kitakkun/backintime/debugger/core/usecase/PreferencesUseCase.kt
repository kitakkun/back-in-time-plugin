package io.github.kitakkun.backintime.debugger.core.usecase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.kitakkun.backintime.debugger.core.datastore.BackInTimePreferences
import io.github.kitakkun.backintime.debugger.core.model.Preferences
import org.koin.compose.koinInject

@Composable
fun preferences(): Preferences {
    val backInTimePreferences: BackInTimePreferences = koinInject()
    val webSocketPort by backInTimePreferences.webSocketPortFlow.collectAsState(8080)
    val clearDataOnDisconnectEnabled by backInTimePreferences.clearDataOnDisconnectEnabledFlow.collectAsState(false)

    return Preferences(
        webSocketPort = webSocketPort,
        clearDataOnDisconnectEnabled = clearDataOnDisconnectEnabled,
    )
}
