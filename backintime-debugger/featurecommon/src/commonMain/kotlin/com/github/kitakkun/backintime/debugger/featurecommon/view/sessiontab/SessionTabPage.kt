package com.github.kitakkun.backintime.debugger.featurecommon.view.sessiontab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.kitakkun.backintime.debugger.featurecommon.lifecycle.GlobalViewModelStoreOwner
import com.github.kitakkun.backintime.debugger.featurecommon.view.sessionselect.SessionSelectDialogPage

@Composable
fun SessionTabPage(
    modifier: Modifier = Modifier,
    tabTrailingContent: @Composable () -> Unit = {},
    content: @Composable (sessionId: String) -> Unit,
) {
    val viewModel: SessionTabViewModel = viewModel(GlobalViewModelStoreOwner)
    val bindModel by viewModel.bindModel.collectAsState()

    if (bindModel.showSelectSessionDialog) {
        SessionSelectDialogPage(
            openedSessionIds = (bindModel as? SessionTabBindModel.WithSessions)?.openedSessions.orEmpty().map { it.sessionId },
            onDismissRequest = { viewModel.dismissSelectSessionDialog() },
            onClickConfirm = { selectedSessionIds ->
                viewModel.openSessions(selectedSessionIds)
                viewModel.dismissSelectSessionDialog()
            },
        )
    }

    SessionTabView(
        bindModel = bindModel,
        content = content,
        modifier = modifier,
        onSelectSession = { viewModel.selectSession(it.sessionId) },
        onClickOpenSession = { viewModel.showSelectSessionDialog() },
        onCloseSessionTab = { viewModel.closeSessionTab(it) },
        tabTrailingContent = tabTrailingContent,
    )
}
