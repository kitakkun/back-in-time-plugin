package com.kitakkun.backintime.feature.settings.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icon.IconKey
import org.jetbrains.jewel.ui.icons.AllIconsKeys

@Composable
fun SettingsHeadingItem(
    title: String,
    iconKey: IconKey,
    modifier: Modifier = Modifier,
) {
    val fontSize = LocalTextStyle.current.fontSize * 1.2
    val fontSizeDp = with(LocalDensity.current) { fontSize.toDp() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        Text(text = title, fontSize = fontSize)
        Icon(
            key = iconKey,
            contentDescription = null,
            modifier = Modifier.size(fontSizeDp),
        )
    }
}

@Preview
@Composable
private fun SettingsHeaderItemPreview() {
    PreviewContainer {
        SettingsHeadingItem(
            title = "Title",
            iconKey = AllIconsKeys.Toolwindows.InfoEvents,
        )
    }
}
