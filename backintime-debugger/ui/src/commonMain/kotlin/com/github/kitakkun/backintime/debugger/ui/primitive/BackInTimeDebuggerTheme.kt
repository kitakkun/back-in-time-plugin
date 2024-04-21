package com.github.kitakkun.backintime.debugger.ui.primitive

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun BackInTimeDebuggerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content,
        typography = BackInTimeDebuggerTheme.typography,
        colorScheme = BackInTimeDebuggerTheme.colorScheme,
        shapes = BackInTimeDebuggerTheme.shapes,
    )
}

object BackInTimeDebuggerTheme {
    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()

    val typography: Typography = Typography()

    val shapes: Shapes = Shapes()

    val staticColors = StaticColors
}
