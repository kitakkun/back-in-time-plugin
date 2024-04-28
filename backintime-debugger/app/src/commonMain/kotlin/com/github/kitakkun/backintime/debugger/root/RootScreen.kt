package com.github.kitakkun.backintime.debugger.root

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.github.kitakkun.backintime.app.generated.resources.Res
import com.github.kitakkun.backintime.app.generated.resources.dismiss
import com.github.kitakkun.backintime.app.generated.resources.restart_server
import com.github.kitakkun.backintime.app.generated.resources.server_error
import com.github.kitakkun.backintime.app.generated.resources.starting_websocket_server
import com.github.kitakkun.backintime.app.generated.resources.unknown_error
import com.github.kitakkun.backintime.app.generated.resources.websocket_server_started
import com.github.kitakkun.backintime.debugger.data.server.BackInTimeDebuggerServiceState
import org.jetbrains.compose.resources.getString

object RootScreen : Screen {
    @Composable
    override fun Content() {
        val model = getScreenModel<RootScreenModel>()

        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            model.startServer()
        }

        LaunchedEffect(snackbarHostState) {
            model.serverState.collect {
                when (it) {
                    is BackInTimeDebuggerServiceState.Uninitialized -> {
                        snackbarHostState.showSnackbar(
                            message = getString(Res.string.starting_websocket_server),
                            duration = SnackbarDuration.Indefinite,
                        )
                    }

                    is BackInTimeDebuggerServiceState.Running -> {
                        snackbarHostState.showSnackbar(
                            message = getString(Res.string.websocket_server_started),
                            duration = SnackbarDuration.Short,
                            actionLabel = getString(Res.string.dismiss),
                        )
                    }

                    is BackInTimeDebuggerServiceState.Error -> {
                        val errorMessage = it.error.cause?.message ?: it.error.message ?: getString(Res.string.unknown_error)
                        val result = snackbarHostState.showSnackbar(
                            message = getString(Res.string.server_error, errorMessage),
                            duration = SnackbarDuration.Indefinite,
                            actionLabel = getString(Res.string.restart_server),
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            model.startServer()
                        }
                    }
                }
            }
        }

        RootView(
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
