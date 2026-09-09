package com.kitakkunl.backintime.feature.inspector.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwEmptyState
import com.kitakkun.jetwhale.host.ui.JwHorizontalDivider
import com.kitakkun.jetwhale.host.ui.JwKeyValueRow
import com.kitakkun.jetwhale.host.ui.JwSectionHeader
import com.kitakkun.jetwhale.host.ui.JwSpacing
import com.kitakkun.jetwhale.host.ui.JwText
import com.kitakkun.jetwhale.host.ui.JwTheme
import com.kitakkunl.backintime.feature.inspector.components.InstanceItemUiState
import com.kitakkunl.backintime.feature.inspector.model.Signature

@Composable
fun PropertyInspectorSection(
    uiState: InstanceItemUiState?,
    propertySignature: Signature.Property?,
    modifier: Modifier = Modifier,
) {
    if (uiState == null) {
        JwEmptyState(title = "Select an instance to inspect it.", modifier = modifier)
        return
    }

    // A signature can be arbitrarily long, so the pane scrolls rather than letting a value push the
    // layout past its own edge.
    Column(
        verticalArrangement = Arrangement.spacedBy(JwSpacing.small),
        modifier = modifier
            .fillMaxSize()
            .background(JwTheme.colors.surface)
            .verticalScroll(rememberScrollState())
            .padding(JwSpacing.extraLarge),
    ) {
        JwSectionHeader(title = "Instance")
        JwKeyValueRow(monospace = true, key = "uuid", value = uiState.uuid)
        JwKeyValueRow(monospace = true, key = "class", value = uiState.classSignature.asString())
        JwKeyValueRow(monospace = true, key = "extends", value = uiState.superClassSignature.asString())
        JwKeyValueRow(key = "properties", value = uiState.properties.size.toString())
        JwKeyValueRow(key = "events", value = uiState.totalEventsCount.toString())

        val property = uiState.properties.find { it.signature == propertySignature }
        Divider()
        if (property == null) {
            // The pane would otherwise stop dead after the instance rows with no hint that half of
            // what it can show is one click away in the list on the left.
            JwText(
                text = "Select a property to see its type and current value.",
                style = JwTheme.textStyles.bodySmall,
                color = JwTheme.colors.textSecondary,
            )
        } else {
            JwSectionHeader(title = "Property")
            JwKeyValueRow(monospace = true, key = "name", value = property.signature.propertyName)
            JwKeyValueRow(monospace = true, key = "type", value = property.type)
            JwKeyValueRow(key = "debuggable", value = property.debuggable.toString())
            JwKeyValueRow(key = "inherited", value = property.isInherited.toString())
            JwKeyValueRow(key = "changes", value = property.eventCount.toString())
            JwKeyValueRow(
                monospace = true,
                key = "current",
                // A property the app never assigned has no recorded value; saying so beats an empty
                // row that reads like a rendering bug.
                value = property.latestValue ?: "(never changed)",
            )
        }
    }
}

@Composable
private fun Divider() {
    JwHorizontalDivider(modifier = Modifier.padding(vertical = JwSpacing.small))
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
