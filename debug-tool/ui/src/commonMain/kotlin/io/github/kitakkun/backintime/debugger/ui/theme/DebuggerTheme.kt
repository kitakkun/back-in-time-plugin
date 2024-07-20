package io.github.kitakkun.backintime.debugger.ui.theme

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
fun DebuggerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content,
        typography = DebuggerTheme.typography,
        colorScheme = DebuggerTheme.colorScheme,
        shapes = DebuggerTheme.shapes,
    )
}

object DebuggerTheme {
    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()

    val typography: Typography = Typography()

    val shapes: Shapes = Shapes()

    val staticColors = StaticColors

    val dynamicColors = DynamicColors
}
