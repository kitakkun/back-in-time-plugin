package com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import com.github.kitakkun.backintime.instance.generated.resources.Res
import com.github.kitakkun.backintime.instance.generated.resources.name
import com.github.kitakkun.backintime.instance.generated.resources.origin_class
import com.github.kitakkun.backintime.instance.generated.resources.type
import com.github.kitakkun.backintime.instance.generated.resources.value_type
import org.jetbrains.compose.resources.stringResource

data class PropertyInfoBindModel(
    val propertyName: String,
    val propertyValueType: String,
    val propertyType: String,
)

@Composable
fun PropertyInfoView(
    bindModel: PropertyInfoBindModel,
    modifier: Modifier = Modifier,
) {
    val firstColumnWidth = 100.dp

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Property Info",
            style = DebuggerTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CompositionLocalProvider(LocalTextStyle provides DebuggerTheme.typography.labelMedium) {
                Row {
                    Text(
                        text = stringResource(Res.string.name),
                        modifier = Modifier.width(firstColumnWidth),
                    )
                    Text(bindModel.propertyName)
                }
                Row {
                    Text(
                        text = stringResource(Res.string.type),
                        modifier = Modifier.width(firstColumnWidth),
                    )
                    Text(bindModel.propertyType)
                }
                Row {
                    Text(
                        text = stringResource(Res.string.value_type),
                        modifier = Modifier.width(firstColumnWidth),
                    )
                    Text(bindModel.propertyValueType)
                }
                Row {
                    Text(
                        text = stringResource(Res.string.origin_class),
                        modifier = Modifier.width(firstColumnWidth),
                    )
                    Text("Not implemented yet!")
                }
            }
        }
    }
}

@Preview
@Composable
private fun PropertyInfoViewPreview() {
    PropertyInfoView(
        bindModel = PropertyInfoBindModel(
            propertyName = "message",
            propertyValueType = "kotlin/String",
            propertyType = "kotlin/String",
        ),
    )
}
