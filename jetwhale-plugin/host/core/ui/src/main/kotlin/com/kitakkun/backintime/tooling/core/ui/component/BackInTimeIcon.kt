package com.kitakkun.backintime.tooling.core.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter

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

// TODO: determine whether to migrate to material3 component or not
//@Composable
//fun BackInTimeSelectableIconActionButton(
//    selected: Boolean,
//    iconKey: BackInTimeIconsKey,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    Box(
//        modifier = modifier
//            .defaultMinSize(
//                minWidth = JewelTheme.iconButtonStyle.metrics.minSize.width,
//                minHeight = JewelTheme.iconButtonStyle.metrics.minSize.height,
//            )
//            .clickable(onClick = onClick)
//            .background(
//                color = if (selected) {
//                    JewelTheme.iconButtonStyle.colors.backgroundSelected
//                } else {
//                    Color.Transparent
//                },
//            )
//            .padding(JewelTheme.iconButtonStyle.metrics.padding),
//        contentAlignment = Alignment.Center,
//    ) {
//        BackInTimeIcon(
//            iconKey = iconKey,
//        )
//    }
//}
//
//@Composable
//fun BackInTimeIconActionButton(
//    iconKey: BackInTimeIconsKey,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    Box(
//        modifier = modifier
//            .defaultMinSize(
//                minWidth = JewelTheme.iconButtonStyle.metrics.minSize.width,
//                minHeight = JewelTheme.iconButtonStyle.metrics.minSize.height,
//            )
//            .padding(JewelTheme.iconButtonStyle.metrics.padding)
//            .clickable(onClick = onClick),
//        contentAlignment = Alignment.Center,
//    ) {
//        BackInTimeIcon(
//            iconKey = iconKey,
//        )
//    }
//}
