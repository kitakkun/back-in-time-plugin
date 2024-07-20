package io.github.kitakkun.backintime.debugger.feature.instance.component.list

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import backintime.debug_tool.feature.instance.generated.resources.Res
import backintime.debug_tool.feature.instance.generated.resources.history
import backintime.debug_tool.feature.instance.generated.resources.instance_fill
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun InstanceInfoView(
    uuid: String,
    propertiesExpanded: Boolean,
    className: String,
    onClickExpand: () -> Unit,
    onClickHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = DebuggerTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(4.dp),
            )
            .padding(8.dp),
    ) {
        IconButton(onClick = onClickExpand) {
            Icon(
                imageVector = if (propertiesExpanded) {
                    Icons.Default.ArrowDropDown
                } else {
                    Icons.AutoMirrored.Default.ArrowRight
                },
                contentDescription = null,
                tint = DebuggerTheme.colorScheme.onPrimaryContainer,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.instance_fill),
                    tint = DebuggerTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = uuid,
                    style = DebuggerTheme.typography.labelSmall,
                    color = DebuggerTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = className,
                style = DebuggerTheme.typography.labelLarge,
                color = DebuggerTheme.colorScheme.onSurface,
            )
        }
        OutlinedButton(onClick = onClickHistory) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = stringResource(Res.string.history),
                style = DebuggerTheme.typography.labelMedium,
                color = DebuggerTheme.colorScheme.primary,
            )
        }
    }
}

@Preview
@Composable
private fun InstanceInfoViewPreview_PropertiesExpanded() {
    InstanceInfoView(
        uuid = "123",
        className = "com.example.MyClass",
        propertiesExpanded = true,
        onClickExpand = {},
        onClickHistory = {},
    )
}

@Preview
@Composable
private fun InstanceInfoViewPreview_PropertiesNotExpanded() {
    InstanceInfoView(
        uuid = "123",
        className = "com.example.MyClass",
        propertiesExpanded = false,
        onClickExpand = {},
        onClickHistory = {},
    )
}
