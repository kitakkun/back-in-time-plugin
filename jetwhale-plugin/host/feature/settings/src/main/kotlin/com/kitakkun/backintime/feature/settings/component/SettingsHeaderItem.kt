package com.kitakkun.backintime.feature.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer

@Composable
fun SettingsHeadingItem(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        Text(text = title)
        Icon(
            imageVector = icon,
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun SettingsHeaderItemPreview() {
    PreviewContainer {
        SettingsHeadingItem(
            title = "Title",
            icon = Icons.Default.Settings,
        )
    }
}
