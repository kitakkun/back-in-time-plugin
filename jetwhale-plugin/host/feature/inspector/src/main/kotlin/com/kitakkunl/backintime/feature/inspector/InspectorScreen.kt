package com.kitakkunl.backintime.feature.inspector

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.logic.EventEmitter
import com.kitakkun.backintime.tooling.core.ui.logic.rememberEventEmitter
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwSplitPane
import com.kitakkun.jetwhale.host.ui.JwSwitch
import com.kitakkun.jetwhale.host.ui.JwText
import com.kitakkun.jetwhale.host.ui.JwTheme
import com.kitakkun.jetwhale.host.ui.JwToolbar
import com.kitakkun.jetwhale.host.ui.rememberJwSplitPaneState
import com.kitakkunl.backintime.feature.inspector.components.EventItemUiState
import com.kitakkunl.backintime.feature.inspector.components.InstanceItemUiState
import com.kitakkunl.backintime.feature.inspector.components.PropertyItemUiState
import com.kitakkunl.backintime.feature.inspector.model.Signature
import com.kitakkunl.backintime.feature.inspector.section.HistorySection
import com.kitakkunl.backintime.feature.inspector.section.HistorySectionUiState
import com.kitakkunl.backintime.feature.inspector.section.InstanceListSection
import com.kitakkunl.backintime.feature.inspector.section.PropertyInspectorSection
import kotlinx.coroutines.flow.distinctUntilChanged

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
        onUpdateHistorySplitDividerPosition = { eventEmitter.tryEmit(InspectorScreenEvent.UpdateHistoryDividerPosition(it)) },
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
    val historyDividerPosition: Float,
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
    onUpdateHistorySplitDividerPosition: (Float) -> Unit,
    onClickEvent: (event: EventItemUiState) -> Unit,
    onPerformBackInTime: (instanceId: String, eventId: String) -> Unit,
    onToggleShowNonDebuggableProperties: (Boolean) -> Unit,
) {
    val verticalSplitLayoutState = rememberJwSplitPaneState(uiState.verticalDividerPosition)
    val horizontalSplitLayoutState = rememberJwSplitPaneState(uiState.horizontalDividerPosition)

    LaunchedEffect(verticalSplitLayoutState) {
        snapshotFlow { verticalSplitLayoutState.fraction }
            .distinctUntilChanged()
            .collect(onUpdateVerticalSplitDividerPosition)
    }

    LaunchedEffect(horizontalSplitLayoutState) {
        snapshotFlow { horizontalSplitLayoutState.fraction }
            .distinctUntilChanged()
            .collect(onUpdateHorizontalSplitDividerPosition)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        // The pane's one global control sits in the toolbar, where the host puts a pane's controls.
        JwToolbar(
            actions = {
                JwText(
                    text = "Show non-debuggable properties",
                    style = JwTheme.textStyles.label,
                    color = JwTheme.colors.textSecondary,
                )
                JwSwitch(
                    checked = uiState.showNonDebuggableProperties,
                    onCheckedChange = onToggleShowNonDebuggableProperties,
                    contentDescription = "Show non-debuggable properties",
                )
            },
        )
        JwSplitPane(
            orientation = Orientation.Vertical,
            state = verticalSplitLayoutState,
            firstMinSize = 200.dp,
            secondMinSize = 200.dp,
            first = {
                JwSplitPane(
                    orientation = Orientation.Horizontal,
                    state = horizontalSplitLayoutState,
                    firstMinSize = 240.dp,
                    secondMinSize = 240.dp,
                    first = {
                        InstanceListSection(
                            instances = uiState.instances,
                            selectedInstanceId = uiState.selectedInstanceId,
                            onClickItem = onClickItem,
                            onClickProperty = onClickProperty,
                            onTogglePropertyVisibility = onTogglePropertyVisibility,
                        )
                    },
                    second = {
                        PropertyInspectorSection(
                            uiState = uiState.selectedInstance,
                            propertySignature = uiState.selectedPropertySignature,
                        )
                    },
                )
            },
            second = {
                HistorySection(
                    uiState = uiState.history,
                    dividerPosition = uiState.historyDividerPosition,
                    onUpdateDividerPosition = onUpdateHistorySplitDividerPosition,
                    onClickEvent = onClickEvent,
                    onPerformBackInTime = { onPerformBackInTime(uiState.selectedInstanceId!!, it.id) },
                )
            },
        )
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
                historyDividerPosition = 0.35f,
                history = null,
                showNonDebuggableProperties = true,
            ),
            onClickEvent = {},
            onClickItem = {},
            onClickProperty = { _, _ -> },
            onUpdateVerticalSplitDividerPosition = {},
            onUpdateHorizontalSplitDividerPosition = {},
            onUpdateHistorySplitDividerPosition = {},
            onTogglePropertyVisibility = {},
            onPerformBackInTime = { _, _ -> },
            onToggleShowNonDebuggableProperties = {},
        )
    }
}