package com.github.kitakkun.backintime.debugger.feature.instance.view.list.component

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
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme

sealed interface PropertyBindModel {
    val name: String
    val type: String

    data class Normal(
        override val name: String,
        override val type: String,
        val eventCount: Int,
    ) : PropertyBindModel

    data class Super(
        override val name: String,
        override val type: String,
        val parentClassName: String,
        val eventCount: Int,
    ) : PropertyBindModel

    data class DebuggableStateHolder(
        override val name: String,
        override val type: String,
    ) : PropertyBindModel
}

@Composable
fun PropertyItemView(
    bindModel: PropertyBindModel,
    modifier: Modifier = Modifier,
) {
    val name = when (bindModel) {
        is PropertyBindModel.Normal -> AnnotatedString(bindModel.name)
        is PropertyBindModel.Super -> buildAnnotatedString {
            append(bindModel.name)
            withStyle(SpanStyle(color = DebuggerTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))) {
                append(" (super: ${bindModel.parentClassName})")
            }
        }

        is PropertyBindModel.DebuggableStateHolder -> buildAnnotatedString {
            append(bindModel.name)
            withStyle(SpanStyle(color = DebuggerTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))) {
                append(" (debuggable)")
            }
        }
    }
    val eventCount = when (bindModel) {
        is PropertyBindModel.Normal -> bindModel.eventCount
        is PropertyBindModel.Super -> bindModel.eventCount
        is PropertyBindModel.DebuggableStateHolder -> null
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
            text = bindModel.type,
            style = DebuggerTheme.typography.labelMedium,
            color = DebuggerTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (eventCount != null) {
            Badge(
                contentColor = DebuggerTheme.colorScheme.onPrimaryContainer,
                containerColor = DebuggerTheme.colorScheme.primaryContainer,
                modifier = Modifier.alpha(if (eventCount > 0) 1f else 0f),
            ) {
                Text(
                    text = eventCount.toString(),
                    style = DebuggerTheme.typography.labelMedium,
                    modifier = Modifier.padding(4.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PropertyViewPreview_NormalWithBadge() {
    PropertyItemView(
        bindModel = PropertyBindModel.Normal("message", "kotlin/String", 5),
    )
}

@Preview
@Composable
private fun PropertyViewPreview_NormalWithoutBadge() {
    PropertyItemView(
        bindModel = PropertyBindModel.Normal("message", "kotlin/String", 0),
    )
}

@Preview
@Composable
private fun PropertyViewPreview_SuperWithBadge() {
    PropertyItemView(
        bindModel = PropertyBindModel.Super("message", "kotlin/String", "com/example/Super", 5),
    )
}

@Preview
@Composable
private fun PropertyViewPreview_SuperWithoutBadge() {
    PropertyItemView(
        bindModel = PropertyBindModel.Super("message", "kotlin/String", "com/example/Super", 0),
    )
}

@Preview
@Composable
private fun PropertyViewPreview_DebuggableStateHolder() {
    PropertyItemView(
        bindModel = PropertyBindModel.DebuggableStateHolder("prop", "com/example/DebuggableClass"),
    )
}
