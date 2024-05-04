package com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.SessionLogViewModel
import com.github.kitakkun.backintime.debugger.featurecommon.lifecycle.GlobalViewModelStoreOwner

@Composable
fun SessionLogContentPage(sessionId: String) {
    val sharedViewModel: SessionLogViewModel = viewModel(GlobalViewModelStoreOwner)
    val viewModel = viewModel(key = sessionId) { SessionLogContentViewModel(sessionId) }
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
