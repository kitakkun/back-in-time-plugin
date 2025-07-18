package com.kitakkunl.backintime.feature.inspector.section

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.shared.IDENavigator
import com.kitakkun.backintime.tooling.core.ui.component.BackInTimeIconActionButton
import com.kitakkun.backintime.tooling.core.ui.component.BackInTimeIconsKey
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalIDENavigator
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkunl.backintime.feature.inspector.components.InstanceItemUiState
import com.kitakkunl.backintime.feature.inspector.components.KeyValueRow
import com.kitakkunl.backintime.feature.inspector.model.Signature
import org.jetbrains.jewel.ui.component.Text

@Composable
fun PropertyInspectorSection(
    uiState: InstanceItemUiState?,
    propertySignature: Signature.Property?,
    modifier: Modifier = Modifier,
) {
    val localNavigator = LocalIDENavigator.current

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
                BackInTimeIconActionButton(
                    iconKey = BackInTimeIconsKey.EditSource,
                    onClick = {
                        localNavigator.navigateToClass(uiState.classSignature.asString())
                    },
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
        CompositionLocalProvider(LocalIDENavigator provides IDENavigator.Noop) {
            PropertyInspectorSection(
                uiState = null,
                propertySignature = null,
            )
        }
    }
}
