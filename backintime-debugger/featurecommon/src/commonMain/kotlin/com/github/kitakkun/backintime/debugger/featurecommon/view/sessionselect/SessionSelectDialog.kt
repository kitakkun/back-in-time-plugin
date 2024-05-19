package com.github.kitakkun.backintime.debugger.featurecommon.view.sessionselect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Dialog
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SessionSelectDialogPage(
    openedSessionIds: List<String>,
    onDismissRequest: () -> Unit,
    onClickConfirm: (selectedSessionIds: List<String>) -> Unit,
) {
    val viewModel: SessionSelectViewModel = koinViewModel { parametersOf(openedSessionIds) }
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
