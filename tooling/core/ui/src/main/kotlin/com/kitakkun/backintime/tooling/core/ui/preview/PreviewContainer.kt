package com.kitakkun.backintime.tooling.core.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.kitakkun.backintime.tooling.core.ui.theme.BackInTimeTheme
import com.kitakkun.backintime.tooling.core.ui.theme.LocalIsIDEInDarkTheme
import org.jetbrains.jewel.foundation.theme.JewelTheme

@Composable
fun PreviewContainer(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalIsIDEInDarkTheme provides true) {
        BackInTimeTheme {
            Box(Modifier.background(JewelTheme.globalColors.panelBackground)) {
                content()
            }
        }
    }
}
