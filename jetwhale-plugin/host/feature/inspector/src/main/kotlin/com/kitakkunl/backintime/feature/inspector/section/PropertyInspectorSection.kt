package com.kitakkunl.backintime.feature.inspector.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkunl.backintime.feature.inspector.components.InstanceItemUiState
import com.kitakkunl.backintime.feature.inspector.components.KeyValueRow
import com.kitakkunl.backintime.feature.inspector.model.Signature

@Composable
fun PropertyInspectorSection(
    uiState: InstanceItemUiState?,
    propertySignature: Signature.Property?,
    modifier: Modifier = Modifier,
) {
    if (uiState != null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier,
        ) {
            Text(text = "Instance")
            KeyValueRow(
                "uuid",
                uiState.uuid,
            )
            Row {
                KeyValueRow(
                    "class",
                    uiState.classSignature.asString(),
                    modifier = Modifier.weight(1f),
                )
            }
            uiState.properties.find { it.signature == propertySignature }?.let {
                Text(text = "Property")
                KeyValueRow(
                    "name",
                    it.signature.propertyName,
                )
                KeyValueRow(
                    "type",
                    it.type,
                )
            }
        }
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "No instance is selected")
        }
    }
}


@Preview
@Composable
private fun PropertyInspectorSectionPreview() {
    PreviewContainer {
        PropertyInspectorSection(
            uiState = null,
            propertySignature = null,
        )
    }
}
