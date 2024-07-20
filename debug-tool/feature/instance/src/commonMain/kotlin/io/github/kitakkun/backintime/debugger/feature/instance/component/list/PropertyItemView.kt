package io.github.kitakkun.backintime.debugger.feature.instance.component.list

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme

data class PropertyUiState(
    val name: String,
    val type: String,
    val eventCount: Int,
    val ownerClassName: String,
    val isDerivedProperty: Boolean,
    val isBackInTimeDebuggable: Boolean,
    val isSelected: Boolean,
)

@Composable
fun PropertyItemView(
    uiState: PropertyUiState,
    modifier: Modifier = Modifier,
) {
    val name = when {
        uiState.isBackInTimeDebuggable -> buildAnnotatedString {
            append(uiState.name)
            withStyle(SpanStyle(color = DebuggerTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))) {
                append(" (debuggable)")
            }
        }

        uiState.isDerivedProperty -> buildAnnotatedString {
            append(uiState.name)
            withStyle(SpanStyle(color = DebuggerTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))) {
                append(" (super: ${uiState.ownerClassName})")
            }
        }

        else -> AnnotatedString(uiState.name)
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = name,
            style = DebuggerTheme.typography.labelMedium,
            color = DebuggerTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = uiState.type,
            style = DebuggerTheme.typography.labelMedium,
            color = DebuggerTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Badge(
            contentColor = DebuggerTheme.colorScheme.onPrimaryContainer,
            containerColor = DebuggerTheme.colorScheme.primaryContainer,
            modifier = Modifier.alpha(if (uiState.eventCount > 0) 1f else 0f),
        ) {
            Text(
                text = uiState.eventCount.toString(),
                style = DebuggerTheme.typography.labelMedium,
                modifier = Modifier.padding(4.dp),
            )
        }
    }
}

@Preview
@Composable
private fun PropertyViewPreview_NormalWithBadge() {
    PropertyItemView(
        uiState = PropertyUiState(
            name = "message",
            type = "kotlin/String",
            eventCount = 5,
            ownerClassName = "",
            isDerivedProperty = false,
            isBackInTimeDebuggable = false,
            isSelected = false,
        )
    )
}

@Preview
@Composable
private fun PropertyViewPreview_NormalWithoutBadge() {
    PropertyItemView(
        uiState = PropertyUiState(
            name = "message",
            type = "kotlin/String",
            eventCount = 0,
            ownerClassName = "",
            isDerivedProperty = false,
            isBackInTimeDebuggable = false,
            isSelected = false,
        )
    )
}

@Preview
@Composable
private fun PropertyViewPreview_SuperWithBadge() {
    PropertyItemView(
        uiState = PropertyUiState(
            name = "message",
            type = "kotlin/String",
            eventCount = 5,
            ownerClassName = "com/example/Super",
            isDerivedProperty = true,
            isBackInTimeDebuggable = false,
            isSelected = false,
        )
    )
}

@Preview
@Composable
private fun PropertyViewPreview_SuperWithoutBadge() {
    PropertyItemView(
        uiState = PropertyUiState(
            name = "message",
            type = "kotlin/String",
            eventCount = 0,
            ownerClassName = "com/example/Super",
            isDerivedProperty = true,
            isBackInTimeDebuggable = false,
            isSelected = false,
        )
    )
}

@Preview
@Composable
private fun PropertyViewPreview_DebuggableStateHolder() {
    PropertyItemView(
        uiState = PropertyUiState(
            name = "message",
            type = "kotlin/String",
            eventCount = 0,
            ownerClassName = "",
            isDerivedProperty = false,
            isBackInTimeDebuggable = true,
            isSelected = false,
        )
    )
}
