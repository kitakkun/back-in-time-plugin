package com.kitakkun.backintime.tooling.core.ui.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.theme.popupContainerStyle

enum class TrianglePosition {
    Top,
    Left,
    Right,
    Bottom,
}

class BalloonShape(
    val position: TrianglePosition,
    val triangleSizeDp: Dp,
    val radius: Dp,
    val triangleOffset: Density.(size: Size) -> Float,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val diameterPx = with(density) { radius.toPx() }
        val radiusPx = diameterPx / 2
        val triangleSizePx = with(density) { triangleSizeDp.toPx() }
        val offset = with(density) { triangleOffset(size) }

        val path = Path().apply {
            // Start at top-left corner
            moveTo(radiusPx, 0f)

            // Top line
            if (position == TrianglePosition.Top) {
                lineTo(offset - triangleSizePx / 2, 0f)
                lineTo(offset, -triangleSizePx)
                lineTo(offset + triangleSizePx / 2, 0f)
            }
            lineTo(size.width - radiusPx, 0f)

            // Top-right arc
            arcTo(
                rect = Rect(
                    offset = Offset(size.width - diameterPx, 0f),
                    size = Size(diameterPx, diameterPx)
                ),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Right line
            if (position == TrianglePosition.Right) {
                lineTo(size.width, offset - triangleSizePx / 2)
                lineTo(size.width + triangleSizePx, offset)
                lineTo(size.width, offset + triangleSizePx / 2)
            }
            lineTo(size.width, size.height - radiusPx)

            // Bottom-right arc
            arcTo(
                rect = Rect(
                    offset = Offset(size.width - diameterPx, size.height - diameterPx),
                    size = Size(diameterPx, diameterPx)
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Bottom line
            if (position == TrianglePosition.Bottom) {
                lineTo(size.width - offset - triangleSizePx / 2, size.height)
                lineTo(size.width - offset, size.height + triangleSizePx)
                lineTo(size.width - offset + triangleSizePx / 2, size.height)
            }
            lineTo(radiusPx, size.height)

            // Bottom-left arc
            arcTo(
                rect = Rect(
                    offset = Offset(0f, size.height - diameterPx),
                    size = Size(diameterPx, diameterPx)
                ),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Left line
            if (position == TrianglePosition.Left) {
                lineTo(0f, size.height - offset + triangleSizePx / 2)
                lineTo(-triangleSizePx, size.height - offset)
                lineTo(0f, size.height - offset - triangleSizePx / 2)
            }
            lineTo(0f, radiusPx)

            // Top-left arc
            arcTo(
                rect = Rect(
                    offset = Offset(0f, 0f),
                    size = Size(diameterPx, diameterPx)
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
        }
        return Outline.Generic(path = path)
    }
}

@Composable
fun BalloonView(
    position: TrianglePosition,
    triangleSizeDp: Dp,
    radius: Dp,
    elevation: Dp = 1.dp,
    borderWidth: Dp = 1.dp,
    containerColor: Color = JewelTheme.popupContainerStyle.colors.background,
    borderColor: Color = JewelTheme.popupContainerStyle.colors.border,
    modifier: Modifier = Modifier,
    triangleOffset: Density.(size: Size) -> Float = { size ->
        when (position) {
            TrianglePosition.Top,
            TrianglePosition.Bottom,
                -> (size.width - triangleSizeDp.toPx()) / 2

            TrianglePosition.Left,
            TrianglePosition.Right
                -> (size.height - triangleSizeDp.toPx()) / 2
        }
    },
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val triangleShape = remember(position, radius, triangleSizeDp, triangleOffset) {
        BalloonShape(
            position = position,
            triangleSizeDp = triangleSizeDp,
            radius = radius,
            triangleOffset = triangleOffset,
        )
    }

    Column(
        modifier = modifier
            .background(color = containerColor, shape = triangleShape)
            .border(width = borderWidth, color = borderColor, shape = triangleShape)
            .shadow(elevation = elevation, shape = triangleShape)
            .padding(contentPadding),
    ) {
        content()
    }
}

@Preview
@Composable
fun Preview() {
    PreviewContainer {
        BalloonView(
            elevation = 1.dp,
            position = TrianglePosition.Bottom,
            triangleSizeDp = 10.dp,
            radius = 10.dp,
            containerColor = Color.Red,
            modifier = Modifier.size(100.dp),
        ) {
            Text("Balloon")
        }
    }
}
