package com.kitakkun.backintime.tooling.core.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.kitakkun.backintime.tooling.core.ui.theme.LocalIsIDEInDarkTheme
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme

@Composable
fun PreviewContainer(
    content: @Composable () -> Unit,
) {
    // Use IntUiTheme instead of SwingBridgeTheme for preview.
    IntUiTheme {
        CompositionLocalProvider(LocalIsIDEInDarkTheme provides true) {
            Box(Modifier.background(JewelTheme.globalColors.panelBackground)) {
                content()
            }
        }
    }
}
