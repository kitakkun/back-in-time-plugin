package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwCountBadge
import com.kitakkun.jetwhale.host.ui.JwTag
import com.kitakkun.jetwhale.host.ui.JwText
import com.kitakkun.jetwhale.host.ui.JwTheme
import com.kitakkun.jetwhale.host.ui.JwTreeRow
import com.kitakkunl.backintime.feature.inspector.model.Signature
import com.kitakkunl.backintime.feature.inspector.model.toClassSignature
import com.kitakkunl.backintime.feature.inspector.model.toPropertySignature

data class InstanceItemUiState(
    val uuid: String,
    val classSignature: Signature.Class,
    val superClassSignature: Signature.Class,
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
        // Name first, package second: the simple name is what identifies the instance, so it is
        // never the part that gets truncated. Only the package gives up width as the pane narrows.
        JwTreeRow(
            text = uiState.classSignature.className,
            depth = 0,
            expandable = uiState.properties.isNotEmpty(),
            expanded = uiState.propertiesExpanded,
            selected = selected,
            onClick = onClick,
            onToggleExpanded = onTogglePropertyVisibility,
            trailingContent = {
                JwText(
                    text = uiState.classSignature.packageFqName,
                    style = JwTheme.textStyles.bodySmall,
                    color = JwTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis,
                    modifier = Modifier.weight(1f),
                )
                // Two instances of the same class are otherwise indistinguishable in this list.
                JwTag(text = uiState.uuid.take(8))
                JwCountBadge(count = uiState.totalEventsCount)
            },
        )
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
                superClassSignature = "androidx/lifecycle/ViewModel".toClassSignature(),
                propertiesExpanded = true,
                properties = List(10) {
                    PropertyItemUiState(
                        signature = "com/example/MyStateHolder.prop$it".toPropertySignature(),
                        type = "kotlin/String",
                        eventCount = it,
                        isSelected = false,
                        latestValue = if (it == 0) null else it.toString(),
                        isInherited = false,
                        debuggable = true,
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
