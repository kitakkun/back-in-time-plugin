package io.github.kitakkun.backintime.debugger.feature.instance.component.list

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SessionInstanceLoadedView(
    instances: List<InstanceUiState>,
    onTogglePropertiesExpanded: (InstanceUiState) -> Unit,
    onClickProperty: (InstanceUiState, PropertyUiState) -> Unit,
    onClickHistory: (InstanceUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier.fillMaxSize()) {
        items(
            items = instances,
            key = { it.id },
        ) { bindModel ->
            InstanceItemView(
                uiState = bindModel,
                onClickExpand = { onTogglePropertiesExpanded(bindModel) },
                onClickProperty = { propertyBindModel ->
                    onClickProperty(bindModel, propertyBindModel)
                },
                onClickHistory = { onClickHistory(bindModel) },
            )
        }
    }
}

@Preview
@Composable
private fun SessionInstanceLoadedViewPreview() {
    SessionInstanceLoadedView(
        instances = List(10) { instanceId ->
            InstanceUiState(
                id = "instance $instanceId",
                className = "com.example.Class$instanceId",
                propertiesExpanded = instanceId == 0,
                properties = List(10) {
                    PropertyUiState(
                        name = "prop$it",
                        type = "kotlin/Int",
                        eventCount = it,
                        isDerivedProperty = false,
                        isBackInTimeDebuggable = false,
                        ownerClassName = "",
                        isSelected = false,
                    )
                }
            )
        },
        onTogglePropertiesExpanded = {},
        onClickProperty = { _, _ -> },
        onClickHistory = {},
    )
}
