package io.github.kitakkun.backintime.debugger.featurecommon.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import backintime.debug_tool.featurecommon.generated.resources.Res
import backintime.debug_tool.featurecommon.generated.resources.msg_no_active_connections
import backintime.debug_tool.featurecommon.generated.resources.open_session
import org.jetbrains.compose.resources.stringResource

@Composable
fun SessionTabEmptyView(
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
