package com.github.kitakkun.backintime.debugger.featurecommon.view.sessiontab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.debugger.featurecommon.view.sessiontab.components.SessionTabItemBindModel
import com.github.kitakkun.backintime.debugger.featurecommon.view.sessiontab.components.SessionTabRow
import com.github.kitakkun.backintime.debugger.ui.customview.CommonLoadingView
import com.github.kitakkun.backintime.featurecommon.generated.resources.Res
import com.github.kitakkun.backintime.featurecommon.generated.resources.msg_no_active_connections
import com.github.kitakkun.backintime.featurecommon.generated.resources.open_session
import org.jetbrains.compose.resources.stringResource

sealed class SessionTabBindModel {
    abstract val showSelectSessionDialog: Boolean

    data class Loading(override val showSelectSessionDialog: Boolean) : SessionTabBindModel()
    data class NoConnections(override val showSelectSessionDialog: Boolean) : SessionTabBindModel()
    data class WithSessions(
        val openedSessions: List<SessionTabItemBindModel>,
        override val showSelectSessionDialog: Boolean,
    ) : SessionTabBindModel() {
        val selectedSessionIndex: Int get() = openedSessions.indexOfFirst { it.selected }.coerceAtLeast(0)
        val selectedSessionId: String get() = openedSessions.getOrNull(selectedSessionIndex)?.sessionId ?: ""
    }
}

@Composable
fun SessionTabView(
    bindModel: SessionTabBindModel,
    onSelectSession: (SessionTabItemBindModel) -> Unit,
    onClickOpenSession: () -> Unit,
    onCloseSessionTab: (SessionTabItemBindModel) -> Unit,
    tabTrailingContent: @Composable () -> Unit,
    content: @Composable (sessionId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (bindModel) {
        is SessionTabBindModel.Loading -> CommonLoadingView(modifier)
        is SessionTabBindModel.NoConnections -> SessionTabNoSessionsView(onClickOpenSession = onClickOpenSession, modifier = modifier)
        is SessionTabBindModel.WithSessions -> SessionTabWithContentView(
            bindModel = bindModel,
            onSelectSession = onSelectSession,
            onClickCloseSession = onCloseSessionTab,
            onClickAddSession = onClickOpenSession,
            tabTrailingContent = tabTrailingContent,
            content = content,
            modifier = modifier,
        )
    }
}

@Composable
private fun SessionTabNoSessionsView(
    onClickOpenSession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(Res.string.msg_no_active_connections))
        FilledTonalButton(onClick = onClickOpenSession) {
            Text(stringResource(Res.string.open_session))
        }
    }
}

@Composable
fun SessionTabWithContentView(
    bindModel: SessionTabBindModel.WithSessions,
    onSelectSession: (SessionTabItemBindModel) -> Unit,
    onClickCloseSession: (SessionTabItemBindModel) -> Unit,
    onClickAddSession: () -> Unit,
    tabTrailingContent: @Composable () -> Unit,
    content: @Composable (sessionId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SessionTabRow(
                selectedTabIndex = bindModel.selectedSessionIndex,
                openedTabItems = bindModel.openedSessions,
                onSelectSession = onSelectSession,
                onClickCloseSession = onClickCloseSession,
                onClickAddSession = onClickAddSession,
                modifier = Modifier.weight(1f),
            )
            tabTrailingContent()
        }
        content(bindModel.selectedSessionId)
    }
}
