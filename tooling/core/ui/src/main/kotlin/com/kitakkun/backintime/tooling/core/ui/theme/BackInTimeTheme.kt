package com.kitakkun.backintime.tooling.core.ui.theme

import androidx.compose.runtime.Composable
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme

@Composable
fun BackInTimeTheme(
    isDark: Boolean = isIDEInDarkTheme(),
    content: @Composable () -> Unit,
) {
    IntUiTheme(isDark = isDark) {
        content()
    }
}
