package com.github.kitakkun.backintime.debugger.feature.instance.view.list.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import com.github.kitakkun.backintime.instance.generated.resources.Res
import com.github.kitakkun.backintime.instance.generated.resources.history
import com.github.kitakkun.backintime.instance.generated.resources.instance_fill
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

data class InstanceBindModel(
    val uuid: String,
    val className: String,
    val properties: List<PropertyBindModel>,
    val propertiesExpanded: Boolean,
)

@Composable
fun InstanceItemView(
    bindModel: InstanceBindModel,
    onClickExpand: () -> Unit,
    onClickHistory: () -> Unit,
    onClickProperty: (PropertyBindModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        InstanceInfoRow(
            bindModel = bindModel,
            onClickExpand = onClickExpand,
            onClickHistory = onClickHistory,
        )
        AnimatedVisibility(visible = bindModel.propertiesExpanded) {
            PropertiesView(
                properties = bindModel.properties,
                onClickProperty = onClickProperty,
            )
        }
    }
}

@Composable
private fun InstanceInfoRow(
    bindModel: InstanceBindModel,
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
                imageVector = if (bindModel.propertiesExpanded) {
                    Icons.Default.ArrowDropDown
                } else {
                    Icons.AutoMirrored.Default.ArrowRight
                },
                contentDescription = null,
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
                    text = bindModel.uuid,
                    style = DebuggerTheme.typography.labelSmall,
                    color = DebuggerTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = bindModel.className,
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

@Composable
private fun PropertiesView(
    properties: List<PropertyBindModel>,
    onClickProperty: (PropertyBindModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        properties.forEachIndexed { index, property ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 32.dp),
            ) {
                VerticalDivider(
                    modifier = Modifier
                        .height(
                            if (index == properties.size - 1) {
                                25.dp
                            } else {
                                50.dp
                            },
                        )
                        .align(Alignment.Top),
                )
                HorizontalDivider(Modifier.width(20.dp))
                PropertyItemView(
                    bindModel = property,
                    modifier = Modifier
                        .height(50.dp)
                        .clickable(onClick = { onClickProperty(property) }),
                )
            }
        }
    }
}

@Preview
@Composable
private fun InstanceRowPreview() {
    InstanceItemView(
        bindModel = InstanceBindModel(
            uuid = "123",
            className = "com.example.MyClass",
            properties = listOf(
                PropertyBindModel.Super("superProp", "kotlin/String", "com/example/SuperClass", 10),
                PropertyBindModel.DebuggableStateHolder("debuggableProp", "com/example/DebuggableClass"),
                PropertyBindModel.Normal("normalProp", "kotlin/String", 5),
            ),
            propertiesExpanded = true,
        ),
        onClickExpand = {},
        onClickHistory = {},
        onClickProperty = {},
    )
}
