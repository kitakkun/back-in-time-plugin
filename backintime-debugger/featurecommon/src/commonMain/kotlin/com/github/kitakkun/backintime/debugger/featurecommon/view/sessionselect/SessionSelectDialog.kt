package com.github.kitakkun.backintime.debugger.featurecommon.view.sessionselect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.kitakkun.backintime.debugger.featurecommon.lifecycle.LocalViewModelStoreOwnerProvider

@Composable
fun SessionSelectDialogPage(
    openedSessionIds: List<String>,
    onDismissRequest: () -> Unit,
    onClickConfirm: (selectedSessionIds: List<String>) -> Unit,
) {
    LocalViewModelStoreOwnerProvider {
        val viewModel: SessionSelectViewModel = viewModel {
            SessionSelectViewModel(openedSessionIds = openedSessionIds)
        }
        val bindModel by viewModel.bindModel.collectAsState()

        Dialog(onDismissRequest = onDismissRequest) {
            SessionSelectView(
                bindModel = bindModel,
                onToggleSessionSelection = { viewModel.toggleSessionSelection(it) },
                onClickOpen = { bindModel ->
                    val selectedSessionIds = bindModel.openableSessions.filter { it.selected }.map { it.sessionId }
                    onClickConfirm(selectedSessionIds)
                },
                onClickCancel = onDismissRequest,
            )
        }
    }
}
