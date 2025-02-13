package com.kitakkun.backintime.tooling.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

val LocalIsIDEInDarkTheme = compositionLocalOf<Boolean> { error("No LocalIsIDEInDarkTheme is provided!") }

@Composable
fun isIDEInDarkTheme(): Boolean {
    return LocalIsIDEInDarkTheme.current
}
