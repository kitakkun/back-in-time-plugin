package com.github.kitakkun.backintime.debugger

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.kitakkun.backintime.app.generated.resources.Res
import com.github.kitakkun.backintime.app.generated.resources.app_name
import com.github.kitakkun.backintime.debugger.data.di.dataModule
import com.github.kitakkun.backintime.debugger.feature.connection.connectionFeatureModule
import com.github.kitakkun.backintime.debugger.feature.settings.settingsFeatureModule
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(
            appModule,
            dataModule,
            connectionFeatureModule,
            settingsFeatureModule,
        )
    }

    application {
        Window(
            title = stringResource(Res.string.app_name),
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(
                width = 1280.dp,
                height = 720.dp,
                position = WindowPosition.Aligned(Alignment.Center),
            ),
        ) {
            App()
        }
    }
}
