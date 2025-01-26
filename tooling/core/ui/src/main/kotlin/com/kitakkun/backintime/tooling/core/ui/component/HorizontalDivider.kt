package com.kitakkun.backintime.tooling.core.ui.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text

/**
 * [org.jetbrains.jewel.ui.component.Divider] seems to be broken.
 * This is our own implementation for HorizontalDivider
 */
@Composable
fun HorizontalDivider(
    color: Color = JewelTheme.globalColors.borders.normal,
    thickness: Dp = 1.dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(color)
            .fillMaxWidth()
            .height(thickness)
    )
}

@Preview
@Composable
private fun HorizontalDividerPreview() {
    PreviewContainer {
        Column {
            Text("Top of divider")
            HorizontalDivider()
            Text("Bottom of divider")
        }
    }
}
