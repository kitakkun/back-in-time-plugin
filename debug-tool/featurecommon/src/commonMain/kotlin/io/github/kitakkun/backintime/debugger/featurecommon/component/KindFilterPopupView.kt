package io.github.kitakkun.backintime.debugger.featurecommon.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import backintime.debug_tool.featurecommon.generated.resources.Res
import backintime.debug_tool.featurecommon.generated.resources.kind_filter
import backintime.debug_tool.featurecommon.generated.resources.select_all
import backintime.debug_tool.featurecommon.generated.resources.unselect_all
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun KindFilterPopupView(
    selectedKinds: Set<EventKind>,
    onClickClose: () -> Unit,
    onSelectedKindsUpdate: (selectedKinds: Set<EventKind>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(300.dp)
            .background(
                color = DebuggerTheme.colorScheme.surfaceBright,
                shape = DebuggerTheme.shapes.small,
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(Res.string.kind_filter),
                style = DebuggerTheme.typography.titleMedium,
                color = DebuggerTheme.colorScheme.onSurface,
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = DebuggerTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable {
                        onClickClose()
                    },
            )
        }
        Column {
            EventKind.entries.forEach { kind ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = selectedKinds.contains(kind),
                        onCheckedChange = {
                            if (kind in selectedKinds) {
                                onSelectedKindsUpdate(selectedKinds - kind)
                            } else {
                                onSelectedKindsUpdate(selectedKinds + kind)
                            }
                        },
                    )
                    Text(
                        text = kind.label,
                        style = DebuggerTheme.typography.labelMedium,
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.select_all),
                style = DebuggerTheme.typography.labelMedium,
                color = DebuggerTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onSelectedKindsUpdate(EventKind.entries.toSet())
                },
            )
            Text(
                text = stringResource(Res.string.unselect_all),
                style = DebuggerTheme.typography.labelMedium,
                color = DebuggerTheme.colorScheme.secondary,
                modifier = Modifier.clickable {
                    onSelectedKindsUpdate(emptySet())
                },
            )
        }
    }
}
