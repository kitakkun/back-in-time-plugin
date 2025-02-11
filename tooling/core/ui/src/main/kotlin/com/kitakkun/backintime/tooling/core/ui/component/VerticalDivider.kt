package com.kitakkun.backintime.tooling.core.ui.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = JewelTheme.globalColors.borders.normal,
    thickness: Dp = 1.dp,
) {
    Box(
        modifier = modifier
            .background(color)
            .fillMaxHeight()
            .width(thickness)
    )
}

@Preview
@Composable
private fun VerticalDividerPreview() {
    PreviewContainer {
        Row {
            Text("Left")
            VerticalDivider(
                Modifier.height(20.dp),
                color = Color.Red,
            )
            Text("Right")
        }
    }
}
