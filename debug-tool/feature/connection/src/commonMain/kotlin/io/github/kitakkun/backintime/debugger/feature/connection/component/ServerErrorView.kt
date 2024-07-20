package io.github.kitakkun.backintime.debugger.feature.connection.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import backintime.debug_tool.feature.connection.generated.resources.Res
import backintime.debug_tool.feature.connection.generated.resources.failed_to_start_server
import backintime.debug_tool.feature.connection.generated.resources.ic_server_line
import io.github.kitakkun.backintime.debugger.feature.connection.ConnectionScreenUiState
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun ServerErrorView() {
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
private fun ServerRunningViewPreview_WithSessions() {
    ServerRunningView(
        bindModel = ConnectionScreenUiState.ServerRunning(
            host = "localhost",
            port = 8080,
            sessionItems = listOf(
                SessionItemUiState(
                    host = "192.168.1.1",
                    port = 20200,
                    sessionId = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                ),
                SessionItemUiState(
                    host = "192.168.1.2",
                    port = 5990,
                    sessionId = "f47ac10b-58cc-4372-a567-0e02b2c3d480",
                ),
            ),
        ),
    )
}
