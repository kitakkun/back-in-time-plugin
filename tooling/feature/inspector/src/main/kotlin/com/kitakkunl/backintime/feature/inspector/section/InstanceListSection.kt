package com.kitakkunl.backintime.feature.inspector.section

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkunl.backintime.feature.inspector.components.EmptyInstanceView
import com.kitakkunl.backintime.feature.inspector.components.InstanceItemUiState
import com.kitakkunl.backintime.feature.inspector.components.InstanceItemView
import com.kitakkunl.backintime.feature.inspector.components.PropertyItemUiState
import com.kitakkunl.backintime.feature.inspector.model.toClassSignature

@Composable
fun InstanceListSection(
    instances: List<InstanceItemUiState>,
    onClickItem: (InstanceItemUiState) -> Unit,
    onClickProperty: (InstanceItemUiState, PropertyItemUiState) -> Unit,
    onTogglePropertyVisibility: (InstanceItemUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (instances.isEmpty()) {
        EmptyInstanceView(modifier)
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            items(
                items = instances,
                key = { it.uuid },
            ) { instance ->
                InstanceItemView(
                    uiState = instance,
                    onClick = { onClickItem(instance) },
                    onClickProperty = { onClickProperty(instance, it) },
                    onTogglePropertyVisibility = { onTogglePropertyVisibility(instance) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun InstanceListSectionPreview_Empty() {
    PreviewContainer {
        InstanceListSection(
            instances = emptyList(),
            onClickProperty = { _, _ -> },
            onClickItem = {},
            onTogglePropertyVisibility = {},
        )
    }
}

@Preview
@Composable
private fun InstanceListSectionPreview() {
    PreviewContainer {
        InstanceListSection(
            instances = List(10) {
                InstanceItemUiState(
                    uuid = "$it",
                    classSignature = "com/example/A$it".toClassSignature(),
                    properties = listOf(),
                    propertiesExpanded = it == 0,
                    totalEventsCount = it,
                )
            },
            onClickProperty = { _, _ -> },
            onClickItem = {},
            onTogglePropertyVisibility = {},
        )
    }
}