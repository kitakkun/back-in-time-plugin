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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

object RootScreen : Screen {
    @Composable
    override fun Content() {
        val model = getScreenModel<RootScreenModel>()

        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            model.startServer()
        }

        LaunchedEffect(snackbarHostState) {
            snapshotFlow { model.state.value }
                .map { it.isServerRunning }
                .distinctUntilChanged()
                .collect { isServerRunning ->
                    if (isServerRunning) {
                        snackbarHostState.showSnackbar("WebSocket Server started", duration = SnackbarDuration.Short, actionLabel = "Dismiss")
                    } else {
                        snackbarHostState.showSnackbar("Starting WebSocket Server...", duration = SnackbarDuration.Indefinite)
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
