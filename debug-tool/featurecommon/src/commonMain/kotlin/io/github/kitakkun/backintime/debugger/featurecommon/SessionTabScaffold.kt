package io.github.kitakkun.backintime.debugger.featurecommon

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.rememberEventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.component.SessionItem
import io.github.kitakkun.backintime.debugger.featurecommon.component.SessionTabRow

@Composable
fun SessionTabScaffold(
    eventEmitter: EventEmitter<SessionTabScreenEvent> = rememberEventEmitter(),
    uiState: SessionTabScreenUiState = sessionTabScreenPresenter(eventEmitter),
    tabActions: @Composable RowScope.() -> Unit,
    content: @Composable (sessionId: String) -> Unit,
) {
    SessionTabScaffold(
        uiState = uiState,
        onClickTabItem = { eventEmitter.tryEmit(SessionTabScreenEvent.SelectTab(it.id)) },
        onCloseTabItem = { eventEmitter.tryEmit(SessionTabScreenEvent.CloseTab(it.id)) },
        onClickAdd = { eventEmitter.tryEmit(SessionTabScreenEvent.OpenSelectDialog) },
        onOpenSelectedSessions = {
            eventEmitter.tryEmit(SessionTabScreenEvent.OpenSelectedSessions(it))
            eventEmitter.tryEmit(SessionTabScreenEvent.CloseSelectDialog)
        },
        onDismissRequestSessionSelectDialog = { eventEmitter.tryEmit(SessionTabScreenEvent.CloseSelectDialog) },
        content = content,
        tabActions = tabActions,
    )
}

data class SessionTabScreenUiState(
    val sessions: List<SessionItem>,
    val showSessionSelectDialog: Boolean,
) {
    val sessionIds = sessions.map { it.id }
    val selectedSessionId: String get() = sessions.firstOrNull { it.selected }?.id ?: sessions.firstOrNull()?.id ?: ""
}

@Composable
fun SessionTabScaffold(
    uiState: SessionTabScreenUiState,
    onClickTabItem: (SessionItem) -> Unit,
    onCloseTabItem: (SessionItem) -> Unit,
    onClickAdd: () -> Unit,
    onDismissRequestSessionSelectDialog: () -> Unit,
    onOpenSelectedSessions: (sessionIds: List<String>) -> Unit,
    content: @Composable (sessionId: String) -> Unit,
    tabActions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.showSessionSelectDialog) {
        SessionSelectDialog(
            route = SessionSelectDialogRoute(uiState.sessionIds),
            onDismissRequest = onDismissRequestSessionSelectDialog,
            onClickConfirm = onOpenSelectedSessions,
        )
    }

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SessionTabRow(
                sessions = uiState.sessions,
                onClickTabItem = onClickTabItem,
                onCloseTabItem = onCloseTabItem,
                onClickAdd = onClickAdd,
            )
            Spacer(Modifier.weight(1f))
            tabActions()
        }
        content(uiState.selectedSessionId)
    }
}
