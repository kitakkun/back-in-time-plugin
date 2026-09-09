package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwCountBadge
import com.kitakkun.jetwhale.host.ui.JwCountBadgeDefaults
import com.kitakkun.jetwhale.host.ui.JwText
import com.kitakkun.jetwhale.host.ui.JwTheme
import com.kitakkun.jetwhale.host.ui.JwTreeRow
import com.kitakkunl.backintime.feature.inspector.model.Signature
import com.kitakkunl.backintime.feature.inspector.model.toPropertySignature

data class PropertyItemUiState(
    val signature: Signature.Property,
    val type: String,
    val eventCount: Int,
    val isSelected: Boolean,
    /** The value the most recent state change assigned, or `null` if it never changed. */
    val latestValue: String?,
    val isInherited: Boolean,
    val debuggable: Boolean,
)

@Composable
fun PropertyItemView(
    uiState: PropertyItemUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // A leaf one level under its instance: the indent and the empty chevron slot carry the nesting,
    // so the row needs no rule or tint of its own to read as "inside that instance".
    JwTreeRow(
        text = uiState.signature.propertyName,
        depth = 1,
        expandable = false,
        expanded = false,
        selected = uiState.isSelected,
        onClick = onClick,
        onToggleExpanded = {},
        // A property the app never declared debuggable is present but not what the pane is for.
        muted = !uiState.debuggable,
        modifier = modifier,
        trailingContent = {
            // Just the simple name: the row is scanned, and a fully-qualified type eats the width
            // the property name needs. The whole signature is one click away in the pane on the right.
            JwText(
                text = uiState.type.substringAfterLast('/'),
                style = JwTheme.textStyles.bodySmall,
                color = JwTheme.colors.textSecondary,
                maxLines = 1,
                textAlign = TextAlign.End,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            // A property with no recorded changes gets blank space the width of a badge rather
            // than a "0", so the badge column stays aligned down the list without adding noise.
            if (uiState.eventCount > 0) {
                JwCountBadge(count = uiState.eventCount)
            } else {
                Spacer(Modifier.width(JwCountBadgeDefaults.minWidth))
            }
        },
    )
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
                latestValue = "42",
                isInherited = false,
                debuggable = true,
            ),
            onClick = {},
        )
    }
}
