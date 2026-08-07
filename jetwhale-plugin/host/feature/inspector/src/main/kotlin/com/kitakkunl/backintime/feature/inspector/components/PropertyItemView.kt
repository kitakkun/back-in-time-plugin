package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.component.CountBadge
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkunl.backintime.feature.inspector.model.Signature
import com.kitakkunl.backintime.feature.inspector.model.toPropertySignature

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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                if (uiState.isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.surfaceContainerLow
            )
            .clickable(onClick = onClick)
            // Indented past the instance row's disclosure arrow, so the nesting reads without a rule.
            .padding(start = 36.dp, end = 8.dp, top = 5.dp, bottom = 5.dp),
    ) {
        Text(
            text = uiState.signature.propertyName,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = uiState.type,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            textAlign = TextAlign.End,
            overflow = TextOverflow.MiddleEllipsis,
            modifier = Modifier.weight(1f),
        )
        // A property with no recorded changes gets blank space the width of a badge rather than a
        // "0", so the badge column stays aligned down the list without adding noise.
        if (uiState.eventCount > 0) {
            CountBadge(count = uiState.eventCount)
        } else {
            Spacer(Modifier.width(20.dp))
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
