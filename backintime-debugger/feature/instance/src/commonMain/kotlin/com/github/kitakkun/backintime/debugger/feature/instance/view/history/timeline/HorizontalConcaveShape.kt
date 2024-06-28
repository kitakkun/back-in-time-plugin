package com.github.kitakkun.backintime.debugger.feature.instance.view.history.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

// shape like ) (
class HorizontalConcaveShape(
    private val startCornerSize: CornerSize,
    private val endCornerSize: CornerSize,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val endPx = endCornerSize.toPx(size, density)
        val startPx = startCornerSize.toPx(size, density)
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            arcTo(
                rect = Rect(
                    offset = Offset(x = size.width - endPx, y = 0f),
                    size = Size(endPx * 2, size.height),
                ),
                startAngleDegrees = -90f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false,
            )
            lineTo(0f, size.height)
            arcTo(
                rect = Rect(
                    offset = Offset(x = -startPx, y = 0f),
                    size = Size(startPx * 2, size.height),
                ),
                startAngleDegrees = 90f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false,
            )
        }
        return Outline.Generic(path)
    }
}

fun HorizontalConcaveShape(startPercent: Float, endPercent: Float): HorizontalConcaveShape {
    return HorizontalConcaveShape(
        startCornerSize = CornerSize(startPercent),
        endCornerSize = CornerSize(endPercent),
    )
}

fun HorizontalConcaveShape(
    start: Dp,
    end: Dp,
): HorizontalConcaveShape {
    return HorizontalConcaveShape(
        startCornerSize = CornerSize(start),
        endCornerSize = CornerSize(end),
    )
}

@Preview
@Composable
private fun HorizontalConcaveShapePreview() {
    Box(
        modifier = Modifier
            .padding(50.dp)
            .width(200.dp)
            .height(50.dp)
            .background(
                color = Color.Gray,
                shape = HorizontalConcaveShape(
                    startPercent = 50f,
                    endPercent = 50f,
                ),
            ),
    )
}
