package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.component.CountBadge
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkunl.backintime.feature.inspector.model.Signature
import com.kitakkunl.backintime.feature.inspector.model.toClassSignature
import com.kitakkunl.backintime.feature.inspector.model.toPropertySignature

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
    selected: Boolean,
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
                .background(
                    if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    else MaterialTheme.colorScheme.surface
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 8.dp, vertical = 6.dp),
        ) {
            // A plain Icon rather than an IconButton: the button's 48dp touch target is sized for
            // fingers and makes every row in a dense list twice as tall as its content needs.
            Icon(
                imageVector = if (uiState.propertiesExpanded) Icons.Default.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = if (uiState.propertiesExpanded) "Collapse properties" else "Expand properties",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = onTogglePropertyVisibility)
                    .size(20.dp),
            )
            Text(
                text = uiState.classSignature.asString(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                // The class name is the identity here, and the tail (`…ViewModel`) carries more of it
                // than the package prefix does, so drop from the middle rather than the end.
                overflow = TextOverflow.MiddleEllipsis,
                modifier = Modifier.weight(1f),
            )
            // Two instances of the same class are otherwise indistinguishable in this list.
            InstanceIdTag(uuid = uiState.uuid)
            CountBadge(count = uiState.totalEventsCount)
        }
        AnimatedVisibility(visible = uiState.propertiesExpanded) {
            Column {
                uiState.properties.forEach { property ->
                    PropertyItemView(
                        uiState = property,
                        onClick = { onClickProperty(property) },
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

/** The instance UUID, shown as an identifier to compare at a glance rather than prose to read. */
@Composable
private fun InstanceIdTag(
    uuid: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = uuid.take(8),
        style = MaterialTheme.typography.labelSmall,
        fontFamily = FontFamily.Monospace,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 1.dp),
    )
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
            selected = true,
            onClick = {},
            onTogglePropertyVisibility = {},
            onClickProperty = {},
        )
    }
}
