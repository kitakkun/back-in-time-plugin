package com.github.kitakkun.backintime.debugger.featurecommon.view.sessionselect

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.debugger.featurecommon.view.sessionselect.component.OpenableSessionBindModel
import com.github.kitakkun.backintime.debugger.featurecommon.view.sessionselect.component.OpenableSessionItemView
import com.github.kitakkun.backintime.debugger.ui.customview.CommonLoadingView
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import com.github.kitakkun.backintime.featurecommon.generated.resources.Res
import com.github.kitakkun.backintime.featurecommon.generated.resources.cancel
import com.github.kitakkun.backintime.featurecommon.generated.resources.msg_no_sessions_available
import com.github.kitakkun.backintime.featurecommon.generated.resources.open
import org.jetbrains.compose.resources.stringResource

@Composable
fun SessionSelectView(
    bindModel: SessionSelectBindModel,
    onToggleSessionSelection: (session: OpenableSessionBindModel) -> Unit,
    onClickOpen: (bindModel: SessionSelectBindModel.Loaded) -> Unit,
    onClickCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (bindModel) {
        is SessionSelectBindModel.Loading -> CommonLoadingView(modifier)
        is SessionSelectBindModel.Loaded -> SessionSelectLoadedView(
            sessions = bindModel.openableSessions,
            onToggleSessionSelection = onToggleSessionSelection,
            onClickOpen = { onClickOpen(bindModel) },
            onClickCancel = onClickCancel,
            modifier = modifier,
        )

        is SessionSelectBindModel.Empty -> SessionSelectEmptyView(
            onClickClose = onClickCancel,
            modifier = modifier,
        )
    }
}

@Composable
fun SessionSelectLoadedView(
    sessions: List<OpenableSessionBindModel>,
    onToggleSessionSelection: (session: OpenableSessionBindModel) -> Unit,
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

@Composable
fun SessionSelectEmptyView(
    onClickClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(500.dp)
            .height(300.dp)
            .background(
                shape = DebuggerTheme.shapes.medium,
                color = DebuggerTheme.colorScheme.surface,
            ),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(
            onClick = onClickClose,
            modifier = Modifier.align(Alignment.TopEnd),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
            )
        }
        Text(text = stringResource(Res.string.msg_no_sessions_available))
    }
}
