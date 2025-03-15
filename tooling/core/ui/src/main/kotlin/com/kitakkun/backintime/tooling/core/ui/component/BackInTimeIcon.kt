package com.kitakkun.backintime.tooling.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.LocalContentColor
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.theme.iconButtonStyle

enum class BackInTimeIconsKey {
    Settings,
    ToolWindowHierarchy,
    DataSchema,
    ArrowDown,
    ArrowRight,
    WebSocket,
    EditSource,
    UiForm,
}

interface BackInTimeIconPainterResolver {
    @Composable
    fun resolveIconPainter(iconKey: BackInTimeIconsKey): Painter
}

val LocalIconPainterResolver = compositionLocalOf<BackInTimeIconPainterResolver> {
    error("No IconPainterResolver provided")
}

@Composable
fun BackInTimeIcon(
    iconKey: BackInTimeIconsKey,
    modifier: Modifier = Modifier,
) {
    val resolver = LocalIconPainterResolver.current
    val painter = resolver.resolveIconPainter(iconKey)
    Icon(
        painter = painter,
        contentDescription = null,
        tint = LocalContentColor.current,
        modifier = modifier,
    )
}

@Composable
fun BackInTimeSelectableIconActionButton(
    selected: Boolean,
    iconKey: BackInTimeIconsKey,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = JewelTheme.iconButtonStyle.metrics.minSize.width,
                minHeight = JewelTheme.iconButtonStyle.metrics.minSize.height,
            )
            .clickable(onClick = onClick)
            .background(
                color = if (selected) {
                    JewelTheme.iconButtonStyle.colors.backgroundSelected
                } else {
                    Color.Transparent
                },
            )
            .padding(JewelTheme.iconButtonStyle.metrics.padding),
        contentAlignment = Alignment.Center,
    ) {
        BackInTimeIcon(
            iconKey = iconKey,
        )
    }
}

@Composable
fun BackInTimeIconActionButton(
    iconKey: BackInTimeIconsKey,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = JewelTheme.iconButtonStyle.metrics.minSize.width,
                minHeight = JewelTheme.iconButtonStyle.metrics.minSize.height,
            )
            .padding(JewelTheme.iconButtonStyle.metrics.padding)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        BackInTimeIcon(
            iconKey = iconKey,
        )
    }
}
