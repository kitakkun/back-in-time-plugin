package com.kitakkun.backintime.tooling.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.items

@Composable
fun SessionSelectorView(
    sessionIdCandidates: List<String>,
    selectedSessionId: String?,
    onSelectItem: (sessionId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("session:")
        Dropdown(
            menuContent = {
                items(
                    items = sessionIdCandidates,
                    isSelected = { it == selectedSessionId },
                    onItemClick = { onSelectItem(it) },
                ) {
                    Text(text = it)
                }
            },
            content = {
                Text(selectedSessionId ?: "no session selected")
            },
        )
    }
}
