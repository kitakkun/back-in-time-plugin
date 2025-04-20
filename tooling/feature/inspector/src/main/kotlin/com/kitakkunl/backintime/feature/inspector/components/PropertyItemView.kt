package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.component.Badge
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkunl.backintime.feature.inspector.model.Signature
import com.kitakkunl.backintime.feature.inspector.model.toPropertySignature
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text

data class PropertyItemUiState(
    val signature: Signature.Property,
    val type: String,
    val eventCount: Int,
    val isSelected: Boolean,
)

@Composable
fun PropertyItemView(
    uiState: PropertyItemUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .then(
                if (uiState.isSelected) {
                    Modifier.background(Color.White.copy(alpha = 0.2f))
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        Text(
            text = uiState.signature.propertyName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = uiState.type,
            maxLines = 1,
            textAlign = TextAlign.End,
            overflow = TextOverflow.Ellipsis,
            color = JewelTheme.globalColors.text.normal.copy(alpha = 0.7f),
            modifier = Modifier
                .padding(start = 20.dp)
                .weight(1f)
        )
        Box(
            modifier = Modifier
                .width(with(LocalDensity.current) { JewelTheme.defaultTextStyle.fontSize.toDp() * 2 })
                .height(with(LocalDensity.current) { JewelTheme.defaultTextStyle.fontSize.toDp() })
        ) {
            if (uiState.eventCount != 0) {
                Badge(
                    containerColor = Color.Red,
                    modifier = Modifier.matchParentSize()
                ) {
                    Text(
                        text = if (uiState.eventCount >= 100) "99+" else uiState.eventCount.toString(),
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PropertyItemViewPreview() {
    PreviewContainer {
        PropertyItemView(
            uiState = PropertyItemUiState(
                signature = "com/example/MyClass.prop1".toPropertySignature(),
                type = "kotlin/Int",
                eventCount = 10,
                isSelected = false,
            ),
            onClick = {},
        )
    }
}