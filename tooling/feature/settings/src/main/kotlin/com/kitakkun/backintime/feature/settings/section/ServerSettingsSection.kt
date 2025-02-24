package com.kitakkun.backintime.feature.settings.section

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.feature.settings.SettingsScreenUiState
import com.kitakkun.backintime.feature.settings.component.SessionListView
import com.kitakkun.backintime.feature.settings.component.SettingsHeadingItem
import com.kitakkun.backintime.feature.settings.component.SettingsItemRow
import com.kitakkun.backintime.tooling.core.ui.component.BackInTimeIconsKey
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.component.ActionButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun ServerSettingsSection(
    portNumber: Int,
    status: SettingsScreenUiState.ServerStatus,
    sessions: List<SettingsScreenUiState.SessionStatus>,
    needsServerRestart: Boolean,
    onUpdatePortNumber: (Int) -> Unit,
    onClickApply: () -> Unit,
    onClickShowInspectorForSession: (sessionId: String) -> Unit,
    onClickShowLogForSession: (sessionId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val portNumberTextFieldState = rememberTextFieldState(portNumber.toString())
    val invalidPortRange by rememberUpdatedState(portNumber < 0 || portNumber > 65535)

    LaunchedEffect(portNumber) {
        portNumberTextFieldState.setTextAndPlaceCursorAtEnd(portNumber.toString())
    }

    LaunchedEffect(portNumberTextFieldState) {
        snapshotFlow { portNumberTextFieldState.text.toString().toIntOrNull() }
            .filterNotNull()
            .distinctUntilChanged()
            .collect(onUpdatePortNumber)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SettingsHeadingItem(
            title = "Server",
            iconKey = BackInTimeIconsKey.WebSocket,
        )
        SettingsItemRow(
            label = { Text(text = "Status:") },
            settingComponent = {
                Text(
                    text = when (status) {
                        is SettingsScreenUiState.ServerStatus.Running -> "Running on port ${status.port} / ${status.activeConnectionCount} connections"
                        is SettingsScreenUiState.ServerStatus.Stopped -> "Stopped"
                    }
                )
            },
        )
        if (status is SettingsScreenUiState.ServerStatus.Running && sessions.isNotEmpty()) {
            SessionListView(
                sessions = sessions,
                onClickShowLog = { onClickShowLogForSession(it.id) },
                onClickShowInspector = { onClickShowInspectorForSession(it.id) },
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp)
            )
        }
        SettingsItemRow(
            label = { Text(text = "Server Port:") },
            settingComponent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (invalidPortRange) {
                        Text(
                            text = "Port must be in range 0 to 65535.",
                            color = JewelTheme.globalColors.text.error,
                        )
                    }
                    if (needsServerRestart && !invalidPortRange) {
                        ActionButton(
                            content = { Text(text = "Apply") },
                            onClick = onClickApply,
                        )
                    }
                    TextField(
                        state = portNumberTextFieldState,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                        inputTransformation = {
                            if (this.asCharSequence().any { !it.isDigit() }) {
                                revertAllChanges()
                            }
                        },
                    )
                }
            }
        )
    }
}

@Preview
@Composable
private fun ServerSettingsSectionPreview() {
    PreviewContainer {
        ServerSettingsSection(
            portNumber = 50023,
            status = SettingsScreenUiState.ServerStatus.Stopped,
            sessions = emptyList(),
            needsServerRestart = false,
            onClickShowLogForSession = {},
            onClickShowInspectorForSession = {},
            onClickApply = {},
            onUpdatePortNumber = {},
        )
    }
}
