package com.github.kitakkun.backintime.debugger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object DynamicColors {
    val registerColor: Color
        @Composable get() = when {
            isSystemInDarkTheme() -> Color(0xFF00FF00)
            else -> Color(0xFF0000FF)
        }
}
