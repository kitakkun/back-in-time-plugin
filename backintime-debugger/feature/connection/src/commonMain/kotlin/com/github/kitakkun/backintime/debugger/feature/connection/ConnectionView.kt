package com.github.kitakkun.backintime.debugger.feature.connection

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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.connection.generated.resources.Res
import com.github.kitakkun.backintime.connection.generated.resources.active_sessions
import com.github.kitakkun.backintime.connection.generated.resources.failed_to_start_server
import com.github.kitakkun.backintime.connection.generated.resources.host
import com.github.kitakkun.backintime.connection.generated.resources.ic_server_line
import com.github.kitakkun.backintime.connection.generated.resources.port
import com.github.kitakkun.backintime.connection.generated.resources.server_is_running
import com.github.kitakkun.backintime.connection.generated.resources.server_not_started
import com.github.kitakkun.backintime.connection.generated.resources.text_loading_server_status
import com.github.kitakkun.backintime.connection.generated.resources.waiting_for_connection
import com.github.kitakkun.backintime.debugger.ui.customview.CommonLoadingView
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConnectionView(
    bindModel: ConnectionBindModel,
) {
    when (bindModel) {
        is ConnectionBindModel.Loading -> CommonLoadingView(message = stringResource(Res.string.text_loading_server_status))
        is ConnectionBindModel.ServerNotStarted -> ServerNotStartedView()
        is ConnectionBindModel.ServerRunning -> ServerRunningView(bindModel)
        is ConnectionBindModel.ServerError -> ServerErrorView()
    }
}

@Composable
private fun ServerNotStartedView() {
    val errorColor = DebuggerTheme.colorScheme.error
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_server_line),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .drawWithContent {
                    drawContent()
                    drawLine(
                        color = errorColor,
                        start = Offset.Zero,
                        end = Offset(size.width, size.height),
                        strokeWidth = 4.dp.toPx(),
                    )
                },
        )
        Text(stringResource(Res.string.server_not_started))
    }
}

@Composable
private fun ServerRunningView(
    bindModel: ConnectionBindModel.ServerRunning,
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
            bindModel.sessionBindModels.isEmpty() -> Text(stringResource(Res.string.waiting_for_connection))
            else -> {
                Column {
                    Text(stringResource(Res.string.active_sessions))
                    bindModel.sessionBindModels.forEach { sessionBindModel ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(text = stringResource(Res.string.host, sessionBindModel.host))
                            Text(text = stringResource(Res.string.port, sessionBindModel.port))
                            Text(text = "(ID: ${sessionBindModel.sessionId})")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServerErrorView() {
    val errorColor = DebuggerTheme.colorScheme.error
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_server_line),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .drawWithContent {
                    drawContent()
                    drawLine(
                        color = errorColor,
                        start = Offset.Zero,
                        end = Offset(size.width, size.height),
                        strokeWidth = 4.dp.toPx(),
                    )
                },
        )
        Text(stringResource(Res.string.failed_to_start_server))
    }
}

@Preview
@Composable
private fun ServerNotStartedViewPreview() {
    ServerNotStartedView()
}

@Preview
@Composable
private fun ServerRunningViewPreview_EmptySessions() {
    ServerRunningView(
        bindModel = ConnectionBindModel.ServerRunning(
            host = "localhost",
            port = 8080,
            sessionBindModels = emptyList(),
        ),
    )
}

@Preview
@Composable
private fun ServerRunningViewPreview_WithSessions() {
    ServerRunningView(
        bindModel = ConnectionBindModel.ServerRunning(
            host = "localhost",
            port = 8080,
            sessionBindModels = listOf(
                SessionBindModel(
                    host = "192.168.1.1",
                    port = 20200,
                    sessionId = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                ),
                SessionBindModel(
                    host = "192.168.1.2",
                    port = 5990,
                    sessionId = "f47ac10b-58cc-4372-a567-0e02b2c3d480",
                ),
            ),
        ),
    )
}