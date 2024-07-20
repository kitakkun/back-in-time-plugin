package io.github.kitakkun.backintime.debugger.app

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import backintime.debug_tool.app.generated.resources.Res
import backintime.debug_tool.app.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

fun main() {
    initKoin()

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
