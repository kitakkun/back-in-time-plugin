package com.kitakkunl.backintime.feature.inspector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.component.horizontalSplitter
import com.kitakkun.backintime.tooling.core.ui.component.verticalSplitter
import com.kitakkun.backintime.tooling.core.ui.logic.EventEmitter
import com.kitakkun.backintime.tooling.core.ui.logic.rememberEventEmitter
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkunl.backintime.feature.inspector.components.EventItemUiState
import com.kitakkunl.backintime.feature.inspector.components.InstanceItemUiState
import com.kitakkunl.backintime.feature.inspector.components.PropertyItemUiState
import com.kitakkunl.backintime.feature.inspector.model.Signature
import com.kitakkunl.backintime.feature.inspector.section.HistorySection
import com.kitakkunl.backintime.feature.inspector.section.HistorySectionUiState
import com.kitakkunl.backintime.feature.inspector.section.InstanceListSection
import com.kitakkunl.backintime.feature.inspector.section.PropertyInspectorSection
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@Composable
fun InspectorScreen(
    eventEmitter: EventEmitter<InspectorScreenEvent> = rememberEventEmitter(),
    uiState: InspectorScreenUiState = inspectorScreenPresenter(eventEmitter),
) {
    InspectorScreen(
        uiState = uiState,
        onClickProperty = { instance, property -> eventEmitter.tryEmit(InspectorScreenEvent.SelectProperty(instance.uuid, property.signature)) },
        onClickItem = { eventEmitter.tryEmit(InspectorScreenEvent.SelectInstance(it.uuid)) },
        onTogglePropertyVisibility = { eventEmitter.tryEmit(InspectorScreenEvent.TogglePropertyVisibility(it.uuid)) },
        onUpdateVerticalSplitDividerPosition = { eventEmitter.tryEmit(InspectorScreenEvent.UpdateVerticalDividerPosition(it)) },
        onUpdateHorizontalSplitDividerPosition = { eventEmitter.tryEmit(InspectorScreenEvent.UpdateHorizontalDividerPosition(it)) },
        onClickEvent = { eventEmitter.tryEmit(InspectorScreenEvent.SelectEvent(it)) },
        onPerformBackInTime = { instanceId, eventId -> eventEmitter.tryEmit(InspectorScreenEvent.BackInTime(instanceId, eventId)) },
        onToggleShowNonDebuggableProperties = { eventEmitter.tryEmit(InspectorScreenEvent.UpdateNonDebuggablePropertiesVisibility(it)) }
    )
}

data class InspectorScreenUiState(
    val selectedInstanceId: String?,
    val selectedPropertySignature: Signature.Property?,
    val instances: List<InstanceItemUiState>,
    val horizontalDividerPosition: Float,
    val verticalDividerPosition: Float,
    val history: HistorySectionUiState?,
    val showNonDebuggableProperties: Boolean,
) {
    val selectedInstance: InstanceItemUiState? get() = instances.find { it.uuid == selectedInstanceId }
}

@Composable
fun InspectorScreen(
    uiState: InspectorScreenUiState,
    onClickItem: (InstanceItemUiState) -> Unit,
    onClickProperty: (InstanceItemUiState, PropertyItemUiState) -> Unit,
    onTogglePropertyVisibility: (InstanceItemUiState) -> Unit,
    onUpdateVerticalSplitDividerPosition: (Float) -> Unit,
    onUpdateHorizontalSplitDividerPosition: (Float) -> Unit,
    onClickEvent: (event: EventItemUiState) -> Unit,
    onPerformBackInTime: (instanceId: String, eventId: String) -> Unit,
    onToggleShowNonDebuggableProperties: (Boolean) -> Unit,
) {
    val verticalSplitLayoutState = rememberSplitPaneState(uiState.verticalDividerPosition)
    val horizontalSplitLayoutState = rememberSplitPaneState(uiState.horizontalDividerPosition)

    LaunchedEffect(verticalSplitLayoutState) {
        snapshotFlow { verticalSplitLayoutState.positionPercentage }
            .distinctUntilChanged()
            .collect(onUpdateVerticalSplitDividerPosition)
    }

    LaunchedEffect(horizontalSplitLayoutState) {
        snapshotFlow { horizontalSplitLayoutState.positionPercentage }
            .distinctUntilChanged()
            .collect(onUpdateHorizontalSplitDividerPosition)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Show non-debuggable properties",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Switch(
                checked = uiState.showNonDebuggableProperties,
                onCheckedChange = onToggleShowNonDebuggableProperties,
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        VerticalSplitPane(
            splitPaneState = verticalSplitLayoutState,
        ) {
            first(minSize = 200.dp) {
                HorizontalSplitPane(
                    splitPaneState = horizontalSplitLayoutState,
                ) {
                    first(minSize = 240.dp) {
                        InstanceListSection(
                            instances = uiState.instances,
                            selectedInstanceId = uiState.selectedInstanceId,
                            onClickItem = onClickItem,
                            onClickProperty = onClickProperty,
                            onTogglePropertyVisibility = onTogglePropertyVisibility,
                        )
                    }
                    second(minSize = 240.dp) {
                        PropertyInspectorSection(
                            uiState = uiState.selectedInstance,
                            propertySignature = uiState.selectedPropertySignature,
                        )
                    }
                    horizontalSplitter()
                }
            }
            second(minSize = 200.dp) {
                HistorySection(
                    uiState = uiState.history,
                    onClickEvent = onClickEvent,
                    onPerformBackInTime = { onPerformBackInTime(uiState.selectedInstanceId!!, it.id) }
                )
            }
            verticalSplitter()
        }
    }
}

@Preview
@Composable
private fun InspectorScreenPreview() {
    PreviewContainer {
        InspectorScreen(
            uiState = InspectorScreenUiState(
                selectedInstanceId = null,
                selectedPropertySignature = null,
                instances = listOf(),
                horizontalDividerPosition = 0.5f,
                verticalDividerPosition = 0.5f,
                history = null,
                showNonDebuggableProperties = true,
            ),
            onClickEvent = {},
            onClickItem = {},
            onClickProperty = { _, _ -> },
            onUpdateVerticalSplitDividerPosition = {},
            onUpdateHorizontalSplitDividerPosition = {},
            onTogglePropertyVisibility = {},
            onPerformBackInTime = { _, _ -> },
            onToggleShowNonDebuggableProperties = {},
        )
    }
}