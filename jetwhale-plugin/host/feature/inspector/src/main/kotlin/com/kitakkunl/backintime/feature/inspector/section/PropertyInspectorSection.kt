package com.kitakkunl.backintime.feature.inspector.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.component.EmptyState
import com.kitakkun.backintime.tooling.core.ui.component.SectionLabel
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
    if (uiState == null) {
        EmptyState(text = "Select an instance to inspect it.", modifier = modifier)
        return
    }

    // A signature can be arbitrarily long, so the pane scrolls rather than letting a value push the
    // layout past its own edge.
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        SectionLabel(text = "INSTANCE")
        KeyValueRow(key = "uuid", value = uiState.uuid)
        KeyValueRow(key = "class", value = uiState.classSignature.asString())

        uiState.properties.find { it.signature == propertySignature }?.let { property ->
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(vertical = 6.dp),
            )
            SectionLabel(text = "PROPERTY")
            KeyValueRow(key = "name", value = property.signature.propertyName)
            KeyValueRow(key = "type", value = property.type)
            KeyValueRow(key = "changes", value = property.eventCount.toString())
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
