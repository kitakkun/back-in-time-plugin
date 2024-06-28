package com.github.kitakkun.backintime.debugger.feature.instance.view.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun HistoryPage(
    sessionId: String,
    instanceId: String,
    navController: NavController,
) {
    val viewModel: HistoryViewModel = koinViewModel { parametersOf(sessionId, instanceId) }
    val bindModel by viewModel.bindModel.collectAsState()

    HistoryView(
        bindModel = bindModel,
    )
}
