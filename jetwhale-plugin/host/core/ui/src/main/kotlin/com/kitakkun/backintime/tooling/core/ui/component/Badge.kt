package com.kitakkun.backintime.tooling.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer

/**
 * A count pill.
 *
 * It sizes itself to its label — a badge constrained from the outside is how digits end up clipped —
 * and only grows past [BadgeMinSize] when the number needs the room, so a column of them stays
 * aligned whether they read 1 or 99+.
 */
@Composable
fun CountBadge(
    count: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = BadgeMinSize, minHeight = BadgeMinSize)
            .background(containerColor, CircleShape)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (count >= 100) "99+" else count.toString(),
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

private val BadgeMinSize = 20.dp

@Preview
@Composable
private fun CountBadgePreview() {
    PreviewContainer {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CountBadge(count = 7)
            CountBadge(count = 128)
        }
    }
}
