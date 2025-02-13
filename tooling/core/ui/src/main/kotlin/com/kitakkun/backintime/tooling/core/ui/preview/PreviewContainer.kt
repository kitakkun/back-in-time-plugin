package com.kitakkun.backintime.tooling.core.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.kitakkun.backintime.tooling.core.ui.theme.LocalIsIDEInDarkTheme
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme

// FIXME: Can't preview due to kotlinx/coroutines/Dispatchers missing
//  We have to add kotlinx-coroutines dependency by implementation but it causes the runtime error on IDEA Plugin.
//  See intellij-common.gradle.kts for details.
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
