package io.github.kitakkun.backintime.debugger.feature.instance.component.inspector

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme

data class InstanceDetail(
    val instanceId: String,
    val instanceClassName: String,
)

@Composable
fun InstanceDetailView(
    bindModel: InstanceDetail,
    modifier: Modifier = Modifier,
) {
    val firstColumnWidth = 100.dp
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "Instance Info",
            style = DebuggerTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Spacer(Modifier.height(12.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CompositionLocalProvider(LocalTextStyle provides DebuggerTheme.typography.labelMedium) {
                Row {
                    Text(
                        text = "ID: ",
                        modifier = Modifier.width(firstColumnWidth),
                    )
                    Text(text = bindModel.instanceId)
                }
                Row {
                    Text(
                        text = "Class: ",
                        modifier = Modifier.width(firstColumnWidth),
                    )
                    Text(text = bindModel.instanceClassName)
                }
            }
        }
    }
}

@Preview
@Composable
private fun InstanceInfoViewPreview() {
    InstanceDetailView(
        bindModel = InstanceDetail(
            instanceId = "1234567890",
            instanceClassName = "com.example.MyClass",
        ),
    )
}
