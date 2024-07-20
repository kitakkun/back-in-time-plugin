package io.github.kitakkun.backintime.debugger.feature.instance.component.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class InstanceUiState(
    val id: String,
    val className: String,
    val properties: List<PropertyUiState>,
    val propertiesExpanded: Boolean,
)

@Composable
fun InstanceItemView(
    uiState: InstanceUiState,
    onClickExpand: () -> Unit,
    onClickHistory: () -> Unit,
    onClickProperty: (PropertyUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        InstanceInfoView(
            uuid = uiState.id,
            className = uiState.className,
            propertiesExpanded = uiState.propertiesExpanded,
            onClickExpand = onClickExpand,
            onClickHistory = onClickHistory,
        )
        AnimatedVisibility(visible = uiState.propertiesExpanded) {
            PropertyListView(
                properties = uiState.properties,
                onClickProperty = onClickProperty,
            )
        }
    }
}

@Preview
@Composable
private fun InstanceItemViewPreview() {
    InstanceItemView(
        uiState = InstanceUiState(
            id = "123",
            className = "com.example.MyClass",
            properties = listOf(
                PropertyUiState(
                    name = "superProp",
                    type = "kotlin/String",
                    eventCount = 10,
                    ownerClassName = "com/example/SuperClass",
                    isDerivedProperty = true,
                    isBackInTimeDebuggable = false,
                    isSelected = false,
                ),
                PropertyUiState(
                    name = "debuggableProp",
                    type = "com/example/DebuggableClass",
                    eventCount = 10,
                    ownerClassName = "",
                    isDerivedProperty = false,
                    isBackInTimeDebuggable = true,
                    isSelected = false,
                ),
                PropertyUiState(
                    name = "normalProp",
                    type = "kotlin/String",
                    eventCount = 10,
                    ownerClassName = "",
                    isDerivedProperty = false,
                    isBackInTimeDebuggable = false,
                    isSelected = false,
                ),
            ),
            propertiesExpanded = true,

            ),
        onClickExpand = {},
        onClickHistory = {},
        onClickProperty = {},
    )
}
