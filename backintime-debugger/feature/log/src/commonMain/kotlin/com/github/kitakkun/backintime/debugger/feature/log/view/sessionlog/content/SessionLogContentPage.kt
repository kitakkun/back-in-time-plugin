package com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.SessionLogViewModel
import org.koin.compose.viewmodel.koinNavViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SessionLogContentPage(sessionId: String) {
    val sharedViewModel: SessionLogViewModel = koinNavViewModel()
    val viewModel = koinViewModel<SessionLogContentViewModel>(key = sessionId) { parametersOf(sessionId) }
    val bindModel by viewModel.bindModel.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            sharedViewModel.clearSelectedLogItem()
        }
    }

    SessionLogContentView(
        bindModel = bindModel,
        onToggleSortWithTime = viewModel::onToggleSortWithTime,
        onToggleSortWithKind = viewModel::onToggleSortWithKind,
        onUpdateVisibleKinds = viewModel::updateVisibleKinds,
        onSelectLogItem = { sharedViewModel.selectLogItem(it) },
    )
}
