package com.kitakkunl.backintime.feature.inspector.section

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwEmptyState
import com.kitakkunl.backintime.feature.inspector.components.InstanceItemUiState
import com.kitakkunl.backintime.feature.inspector.components.InstanceItemView
import com.kitakkunl.backintime.feature.inspector.components.PropertyItemUiState
import com.kitakkunl.backintime.feature.inspector.model.toClassSignature

@Composable
fun InstanceListSection(
    instances: List<InstanceItemUiState>,
    selectedInstanceId: String?,
    onClickItem: (InstanceItemUiState) -> Unit,
    onClickProperty: (InstanceItemUiState, PropertyItemUiState) -> Unit,
    onTogglePropertyVisibility: (InstanceItemUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (instances.isEmpty()) {
        JwEmptyState(
            title = "No instance is registered yet.",
            description = "Interact with the app to see its debuggable objects here.",
            modifier = modifier,
        )
    } else {
        val listState = rememberLazyListState()
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(state = listState) {
                items(
                    items = instances,
                    key = { it.uuid },
                ) { instance ->
                    InstanceItemView(
                        uiState = instance,
                        selected = instance.uuid == selectedInstanceId,
                        onClick = { onClickItem(instance) },
                        onClickProperty = { onClickProperty(instance, it) },
                        onTogglePropertyVisibility = { onTogglePropertyVisibility(instance) },
                    )
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(listState),
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
    }
}

@Preview
@Composable
private fun InstanceListSectionPreview_Empty() {
    PreviewContainer {
        InstanceListSection(
            instances = emptyList(),
            selectedInstanceId = null,
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
                    superClassSignature = "kotlin/Any".toClassSignature(),
                    properties = listOf(),
                    propertiesExpanded = it == 0,
                    totalEventsCount = it,
                )
            },
            selectedInstanceId = "0",
            onClickProperty = { _, _ -> },
            onClickItem = {},
            onTogglePropertyVisibility = {},
        )
    }
}