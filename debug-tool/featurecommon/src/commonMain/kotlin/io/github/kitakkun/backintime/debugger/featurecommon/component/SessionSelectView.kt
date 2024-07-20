package io.github.kitakkun.backintime.debugger.featurecommon.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import backintime.debug_tool.featurecommon.generated.resources.Res
import backintime.debug_tool.featurecommon.generated.resources.cancel
import backintime.debug_tool.featurecommon.generated.resources.open
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SessionSelectDialogView(
    sessions: List<OpenableSessionUiState>,
    onToggleSessionSelection: (session: OpenableSessionUiState) -> Unit,
    onClickOpen: () -> Unit,
    onClickCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // FIXME: Buttons don't appear at the bottom of the dialog because of growing LazyColumn
    //  This might be a bug, but temporary workaround is to use a fixed height for the bottom button area
    val bottomButtonAreaHeight = 55.dp

    Box(
        modifier = modifier
            .width(700.dp)
            .height(500.dp)
            .background(
                shape = DebuggerTheme.shapes.medium,
                color = DebuggerTheme.colorScheme.surface,
            ),
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = bottomButtonAreaHeight),
        ) {
            items(sessions) { session ->
                OpenableSessionItemView(
                    bindModel = session,
                    onToggleSessionSelection = { onToggleSessionSelection(session) },
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomButtonAreaHeight)
                .background(DebuggerTheme.colorScheme.surface)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        ) {
            OutlinedButton(onClick = onClickCancel) {
                Text(text = stringResource(Res.string.cancel))
            }
            Button(onClick = onClickOpen) {
                Text(text = stringResource(Res.string.open))
            }
        }
    }
}
