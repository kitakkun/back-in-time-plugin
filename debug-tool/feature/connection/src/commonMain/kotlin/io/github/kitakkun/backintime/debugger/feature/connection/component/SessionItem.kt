package io.github.kitakkun.backintime.debugger.feature.connection.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import backintime.debug_tool.feature.connection.generated.resources.Res
import backintime.debug_tool.feature.connection.generated.resources.host
import backintime.debug_tool.feature.connection.generated.resources.port
import org.jetbrains.compose.resources.stringResource

data class SessionItemUiState(
    val host: String,
    val port: Int,
    val sessionId: String,
)

@Composable
fun SessionItem(
    uiState: SessionItemUiState,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        Text(text = stringResource(Res.string.host, uiState.host))
        Text(text = stringResource(Res.string.port, uiState.port))
        Text(text = "(ID: ${uiState.sessionId})")
    }
}
