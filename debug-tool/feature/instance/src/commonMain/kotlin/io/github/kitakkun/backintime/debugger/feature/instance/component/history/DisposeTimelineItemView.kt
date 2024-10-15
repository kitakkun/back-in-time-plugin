package io.github.kitakkun.backintime.debugger.feature.instance.component.history

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class DisposeTimelineItemBindModel(
    override val id: String,
    override val timeMillis: Long,
    override val selected: Boolean,
) : TimelineItemBindModel()

@Composable
fun DisposeTimelineItemView(
    bindModel: DisposeTimelineItemBindModel,
    modifier: Modifier = Modifier,
) {
    Icon(
        Icons.Default.Close,
        contentDescription = null,
        modifier = modifier.size(32.dp),
    )
}

@Preview
@Composable
private fun DisposeTimelineItemViewPreview() {
    DisposeTimelineItemView(
        bindModel = DisposeTimelineItemBindModel(
            id = "1",
            timeMillis = 0,
            selected = false,
        ),
    )
}
