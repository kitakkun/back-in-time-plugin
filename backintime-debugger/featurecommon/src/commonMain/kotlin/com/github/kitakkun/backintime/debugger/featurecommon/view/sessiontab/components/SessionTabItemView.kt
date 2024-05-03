package com.github.kitakkun.backintime.debugger.featurecommon.view.sessiontab.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.LocalSystemTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.SystemTheme
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme

data class SessionTabItemBindModel(
    val sessionId: String,
    val label: String,
    val hasActiveConnection: Boolean,
    val selected: Boolean,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SessionTabItemView(
    selected: Boolean,
    bindModel: SessionTabItemBindModel,
    onSelectSession: (SessionTabItemBindModel) -> Unit,
    onClickCloseSession: (SessionTabItemBindModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isVisibleCloseButton by remember { mutableStateOf(false) }

    Tab(
        selected = selected,
        onClick = { onSelectSession(bindModel) },
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp),
        ) {
            if (bindModel.hasActiveConnection) {
                Badge(containerColor = DebuggerTheme.staticColors.activeGreen)
            }
            Text(text = bindModel.label, style = DebuggerTheme.typography.labelMedium)
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .onPointerEvent(PointerEventType.Enter) {
                        isVisibleCloseButton = true
                    }
                    .onPointerEvent(PointerEventType.Exit) {
                        isVisibleCloseButton = false
                    }
                    .then(
                        if (isVisibleCloseButton) {
                            Modifier.clickable(onClick = { onClickCloseSession(bindModel) })
                        } else {
                            Modifier.alpha(0f)
                        },
                    ),
                tint = LocalContentColor.current.copy(alpha = 0.5f),
            )
        }
    }
}

@OptIn(InternalComposeApi::class)
@Preview
@Composable
private fun SessionTabItemViewPreview_HasActiveConnection_NotSelected() {
    CompositionLocalProvider(LocalSystemTheme provides SystemTheme.Light) {
        SessionTabItemView(
            selected = false,
            bindModel = SessionTabItemBindModel(
                sessionId = "1",
                label = "Session 1",
                hasActiveConnection = true,
                selected = false,
            ),
            onSelectSession = {},
            onClickCloseSession = {},
        )
    }
}

@OptIn(InternalComposeApi::class)
@Preview
@Composable
private fun SessionTabItemViewPreview_HasActiveConnection_Selected() {
    CompositionLocalProvider(LocalSystemTheme provides SystemTheme.Light) {
        SessionTabItemView(
            selected = true,
            bindModel = SessionTabItemBindModel(
                sessionId = "1",
                label = "Session 1",
                hasActiveConnection = true,
                selected = true,
            ),
            onSelectSession = {},
            onClickCloseSession = {},
        )
    }
}

@Preview
@Composable
private fun SessionTabItemViewPreview_NoActiveConnection_NotSelected() {
    SessionTabItemView(
        selected = false,
        bindModel = SessionTabItemBindModel(
            sessionId = "1",
            label = "Session 1",
            hasActiveConnection = false,
            selected = false,
        ),
        onSelectSession = {},
        onClickCloseSession = {},
    )
}

@Preview
@Composable
private fun SessionTabItemViewPreview_NoActiveConnection_Selected() {
    SessionTabItemView(
        selected = true,
        bindModel = SessionTabItemBindModel(
            sessionId = "1",
            label = "Session 1",
            hasActiveConnection = false,
            selected = true,
        ),
        onSelectSession = {},
        onClickCloseSession = {},
    )
}
