package io.github.kitakkun.backintime.debugger.feature.connection.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import backintime.debug_tool.feature.connection.generated.resources.Res
import backintime.debug_tool.feature.connection.generated.resources.active_sessions
import backintime.debug_tool.feature.connection.generated.resources.host
import backintime.debug_tool.feature.connection.generated.resources.ic_server_line
import backintime.debug_tool.feature.connection.generated.resources.port
import backintime.debug_tool.feature.connection.generated.resources.server_is_running
import backintime.debug_tool.feature.connection.generated.resources.waiting_for_connection
import io.github.kitakkun.backintime.debugger.feature.connection.ConnectionScreenUiState
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ServerRunningView(
    bindModel: ConnectionScreenUiState.ServerRunning,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_server_line),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Badge(containerColor = DebuggerTheme.staticColors.activeGreen)
            Text(stringResource(Res.string.server_is_running))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = stringResource(Res.string.host, bindModel.host))
            Text(text = stringResource(Res.string.port, bindModel.port))
        }
        when {
            bindModel.sessionItems.isEmpty() -> Text(stringResource(Res.string.waiting_for_connection))
            else -> {
                Column {
                    Text(stringResource(Res.string.active_sessions))
                    bindModel.sessionItems.forEach { itemUiState ->
                        SessionItem(itemUiState)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ServerRunningViewPreview_EmptySessions() {
    ServerRunningView(
        bindModel = ConnectionScreenUiState.ServerRunning(
            host = "localhost",
            port = 8080,
            sessionItems = emptyList(),
        ),
    )
}
