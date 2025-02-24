package com.kitakkun.backintime.feature.settings.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.feature.settings.SettingsScreenUiState
import com.kitakkun.backintime.tooling.core.ui.component.BackInTimeIconActionButton
import com.kitakkun.backintime.tooling.core.ui.component.BackInTimeIconsKey
import com.kitakkun.backintime.tooling.core.ui.component.Badge
import com.kitakkun.backintime.tooling.core.ui.component.HorizontalDivider
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.component.Text

@Composable
fun SessionListView(
    sessions: List<SettingsScreenUiState.SessionStatus>,
    onClickShowInspector: (SettingsScreenUiState.SessionStatus) -> Unit,
    onClickShowLog: (SettingsScreenUiState.SessionStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        sessions.forEachIndexed { index, session ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = session.id)
                Spacer(Modifier.width(4.dp))
                Badge(
                    containerColor = if (session.isActive) Color.Green else Color.Gray,
                    modifier = Modifier.size(
                        with(LocalDensity.current) {
                            LocalTextStyle.current.fontSize.toDp()
                        }
                    )
                )
                Spacer(Modifier.weight(1f))
                BackInTimeIconActionButton(
                    iconKey = BackInTimeIconsKey.ToolWindowHierarchy,
                    onClick = { onClickShowInspector(session) },
                )
                BackInTimeIconActionButton(
                    iconKey = BackInTimeIconsKey.DataSchema,
                    onClick = { onClickShowLog(session) },
                )
            }
            if (index != sessions.size - 1) {
                HorizontalDivider()
            }
        }
    }
}

@Preview
@Composable
private fun SessionListViewPreview() {
    PreviewContainer {
        SessionListView(
            sessions = List(10) {
                SettingsScreenUiState.SessionStatus(
                    id = "session$it",
                    isActive = true,
                )
            },
            onClickShowInspector = {},
            onClickShowLog = {},
        )
    }
}
