package io.github.kitakkun.backintime.debugger.featurecommon

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.rememberEventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.component.OpenableSessionUiState
import io.github.kitakkun.backintime.debugger.featurecommon.component.SessionSelectDialogView
import kotlinx.serialization.Serializable

@Serializable
data class SessionSelectDialogRoute(
    val openedSessionIds: List<String>,
)
//
//fun NavGraphBuilder.sessionSelectDialog(
//    onClickConfirm: (selectedSessionIds: List<String>) -> Unit,
//    onDismissRequest: () -> Unit,
//) {
//    composable<SessionSelectDialogRoute> {
//        val route: SessionSelectDialogRoute = it.toRoute()
//        SessionSelectDialog(
//            route = route,
//            onClickConfirm = onClickConfirm,
//            onDismissRequest = onDismissRequest,
//        )
//    }
//}
//
//fun NavController.navigateToSessionSelectDialog(openedSessionIds: List<String>) {
//    navigate(SessionSelectDialogRoute(openedSessionIds))
//}

@Composable
fun SessionSelectDialog(
    route: SessionSelectDialogRoute,
    eventEmitter: EventEmitter<SessionSelectDialogEvent> = rememberEventEmitter(),
    uiState: SessionSelectUiState = sessionSelectDialogPresenter(eventEmitter, route),
    onDismissRequest: () -> Unit,
    onClickConfirm: (selectedSessionIds: List<String>) -> Unit,
) {
    SessionSelectDialog(
        uiState = uiState,
        onClickConfirm = onClickConfirm,
        onToggleSessionSelection = { eventEmitter.tryEmit(SessionSelectDialogEvent.ToggleSelection(it)) },
        onDismissRequest = onDismissRequest,
    )
}

data class SessionSelectUiState(
    val openableSessions: List<OpenableSessionUiState>
) {
    val selectedSessionIds = openableSessions.filter { it.selected }.map { it.sessionId }
}

@Composable
fun SessionSelectDialog(
    uiState: SessionSelectUiState,
    onDismissRequest: () -> Unit,
    onToggleSessionSelection: (sessionId: String) -> Unit,
    onClickConfirm: (selectedSessionIds: List<String>) -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        SessionSelectDialogView(
            sessions = uiState.openableSessions,
            onToggleSessionSelection = { onToggleSessionSelection(it.sessionId) },
            onClickCancel = onDismissRequest,
            onClickOpen = { onClickConfirm(uiState.selectedSessionIds) },
        )
    }
}
