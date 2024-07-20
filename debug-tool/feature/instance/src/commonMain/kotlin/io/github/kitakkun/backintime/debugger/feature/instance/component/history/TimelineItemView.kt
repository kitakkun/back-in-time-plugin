package io.github.kitakkun.backintime.debugger.feature.instance.component.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

sealed class TimelineItemBindModel {
    abstract val id: String
    abstract val timeMillis: Long
    abstract val selected: Boolean
}

@Composable
fun TimelineItemView(
    bindModel: TimelineItemBindModel,
    modifier: Modifier = Modifier,
) {
    when (bindModel) {
        is RegisterTimelineItemBindModel -> {
            RegisterTimelineItemView(
                bindModel = bindModel,
                onClick = {},
                modifier = modifier,
            )
        }

        is MethodInvocationTimelineItemBindModel -> {
            MethodInvocationTimelineItemView(
                bindModel = bindModel,
                onClick = {},
                modifier = modifier,
            )
        }

        is DisposeTimelineItemBindModel -> {
            DisposeTimelineItemView(
                bindModel = bindModel,
                modifier = modifier,
            )
        }

        is BackInTimeTimelineItemBindModel -> {
            BackInTimeTimelineItemView(
                bindModel = bindModel,
                onClick = {},
                modifier = modifier,
            )
        }
    }
}
