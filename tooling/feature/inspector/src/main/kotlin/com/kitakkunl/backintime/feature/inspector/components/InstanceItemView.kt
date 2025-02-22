package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.component.Badge
import com.kitakkun.backintime.tooling.core.ui.component.HorizontalDivider
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkunl.backintime.feature.inspector.model.Signature
import com.kitakkunl.backintime.feature.inspector.model.toClassSignature
import com.kitakkunl.backintime.feature.inspector.model.toPropertySignature
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icons.AllIconsKeys

data class InstanceItemUiState(
    val uuid: String,
    val classSignature: Signature.Class,
    val properties: List<PropertyItemUiState>,
    val propertiesExpanded: Boolean,
    val totalEventsCount: Int,
)

@Composable
fun InstanceItemView(
    uiState: InstanceItemUiState,
    onClick: () -> Unit,
    onClickProperty: (PropertyItemUiState) -> Unit,
    onTogglePropertyVisibility: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(8.dp),
        ) {
            IconButton(onClick = onTogglePropertyVisibility) {
                Icon(
                    key = if (uiState.propertiesExpanded) AllIconsKeys.General.ArrowDown else AllIconsKeys.General.ArrowRight,
                    contentDescription = null,
                )
            }
            Text(text = uiState.uuid.take(5))
            Text(text = uiState.classSignature.asString(), modifier = Modifier.weight(1f))
            Badge(containerColor = Color.Red) {
                Text(text = uiState.totalEventsCount.toString())
            }
        }
        AnimatedVisibility(
            visible = uiState.propertiesExpanded,
        ) {
            Column {
                uiState.properties.forEach { property ->
                    HorizontalDivider()
                    PropertyItemView(
                        uiState = property,
                        onClick = { onClickProperty(property) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview
@Composable
private fun InstanceItemViewPreview() {
    PreviewContainer {
        InstanceItemView(
            uiState = InstanceItemUiState(
                uuid = "c9ed94d9-1c1f-493d-b982-db34db076ffe",
                classSignature = "com/example/MyStateHolder".toClassSignature(),
                propertiesExpanded = true,
                properties = List(10) {
                    PropertyItemUiState(
                        signature = "com/example/MyStateHolder.prop$it".toPropertySignature(),
                        type = "kotlin/String",
                        eventCount = it,
                        isSelected = false,
                    )
                },
                totalEventsCount = 10,
            ),
            onClick = {},
            onTogglePropertyVisibility = {},
            onClickProperty = {},
        )
    }
}
