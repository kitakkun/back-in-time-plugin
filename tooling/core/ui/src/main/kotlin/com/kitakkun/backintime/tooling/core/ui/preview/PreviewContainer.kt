package com.kitakkun.backintime.tooling.core.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kitakkun.backintime.tooling.core.ui.theme.BackInTimeTheme
import org.jetbrains.jewel.foundation.theme.JewelTheme

@Composable
fun PreviewContainer(
    content: @Composable () -> Unit,
) {
    BackInTimeTheme {
        Box(Modifier.background(JewelTheme.globalColors.panelBackground)) {
            content()
        }
    }
}
