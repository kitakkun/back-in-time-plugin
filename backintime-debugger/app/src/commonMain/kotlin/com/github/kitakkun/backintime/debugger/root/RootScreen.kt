package com.github.kitakkun.backintime.debugger.root

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.github.kitakkun.backintime.app.generated.resources.Res
import com.github.kitakkun.backintime.app.generated.resources.dismiss
import com.github.kitakkun.backintime.app.generated.resources.starting_websocket_server
import com.github.kitakkun.backintime.app.generated.resources.websocket_server_started
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.stringResource

object RootScreen : Screen {
    @Composable
    override fun Content() {
        val model = getScreenModel<RootScreenModel>()

        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            model.startServer()
        }

        val messageWebSocketServerStarted = stringResource(Res.string.websocket_server_started)
        val messageWebSocketServerStarting = stringResource(Res.string.starting_websocket_server)
        val dismissText = stringResource(Res.string.dismiss)

        LaunchedEffect(snackbarHostState) {
            snapshotFlow { model.state.value }
                .map { it.isServerRunning }
                .distinctUntilChanged()
                .collect { isServerRunning ->
                    if (isServerRunning) {
                        snackbarHostState.showSnackbar(
                            message = messageWebSocketServerStarted,
                            duration = SnackbarDuration.Short,
                            actionLabel = dismissText,
                        )
                    } else {
                        snackbarHostState.showSnackbar(
                            message = messageWebSocketServerStarting,
                            duration = SnackbarDuration.Indefinite,
                        )
                    }
                }
        }

        RootView(
            state = model.state.value,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { data ->
                        Snackbar(data)
                    },
                )
            },
        )
    }
}
