package com.github.kitakkun.backintime.debugger

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.github.kitakkun.backintime.app.generated.resources.Res
import com.github.kitakkun.backintime.app.generated.resources.dismiss
import com.github.kitakkun.backintime.app.generated.resources.restart_server
import com.github.kitakkun.backintime.app.generated.resources.server_error
import com.github.kitakkun.backintime.app.generated.resources.starting_websocket_server
import com.github.kitakkun.backintime.app.generated.resources.unknown_error
import com.github.kitakkun.backintime.app.generated.resources.websocket_server_started
import com.github.kitakkun.backintime.debugger.data.server.BackInTimeDebuggerServiceState
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import org.jetbrains.compose.resources.getString

@Composable
fun App() {
    val rootViewModel: RootViewModel = viewModel()
    val navHostController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        rootViewModel.startServer()
    }

    LaunchedEffect(snackbarHostState) {
        rootViewModel.serverState.collect {
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
                        rootViewModel.startServer()
                    }
                }
            }
        }
    }

    DebuggerTheme {
        Row {
            BackInTimeDebuggerNavigationRail(navController = navHostController)
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
            ) { innerPadding ->
                BackInTimeDebuggerNavHost(
                    navController = navHostController,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                )
            }
        }
    }
}
