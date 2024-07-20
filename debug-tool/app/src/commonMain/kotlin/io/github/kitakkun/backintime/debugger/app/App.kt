package io.github.kitakkun.backintime.debugger.app

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import io.github.kitakkun.backintime.debugger.core.data.SessionInfoRepository
import io.github.kitakkun.backintime.debugger.core.datastore.BackInTimePreferences
import io.github.kitakkun.backintime.debugger.core.server.BackInTimeDebuggerService
import io.github.kitakkun.backintime.debugger.core.usecase.compositionlocal.ProvideLocalRepositories
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import org.koin.compose.koinInject

@Composable
fun App() {
//    val rootViewModel: RootViewModel = koinViewModel()
    val sessionInfoRepository: SessionInfoRepository = koinInject()
    val service: BackInTimeDebuggerService = koinInject()

    val preferences: BackInTimePreferences = koinInject()
    val serverPort: Int by preferences.webSocketPortFlow.collectAsState(8080)

    val navHostController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(service) {
        sessionInfoRepository.markAllAsDisconnected()
        service.start("localhost", serverPort)
    }
//
//    LaunchedEffect(Unit) {
//        rootViewModel.startServer()
//    }
//
//    LaunchedEffect(snackbarHostState) {
//        rootViewModel.serverState.collect {
//            when (it) {
//                is BackInTimeDebuggerServiceState.Uninitialized -> {
//                    snackbarHostState.showSnackbar(
//                        message = getString(Res.string.starting_websocket_server),
//                        duration = SnackbarDuration.Indefinite,
//                    )
//                }
//
//                is BackInTimeDebuggerServiceState.Running -> {
//                    snackbarHostState.showSnackbar(
//                        message = getString(Res.string.websocket_server_started),
//                        duration = SnackbarDuration.Short,
//                        actionLabel = getString(Res.string.dismiss),
//                    )
//                }
//
//                is BackInTimeDebuggerServiceState.Error -> {
//                    val errorMessage = it.error.cause?.message ?: it.error.message ?: getString(Res.string.unknown_error)
//                    val result = snackbarHostState.showSnackbar(
//                        message = getString(Res.string.server_error, errorMessage),
//                        duration = SnackbarDuration.Indefinite,
//                        actionLabel = getString(Res.string.restart_server),
//                    )
//                    if (result == SnackbarResult.ActionPerformed) {
//                        rootViewModel.startServer()
//                    }
//                }
//            }
//        }
//    }

    ProvideLocalRepositories {
        DebuggerTheme {
            Row {
                DebuggerNavigationRail(navController = navHostController)
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) { innerPadding ->
                    DebuggerNavHost(
                        navController = navHostController,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                    )
                }
            }
        }
    }
}
