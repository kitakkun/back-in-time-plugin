package com.kitakkunl.backintime.feature.inspector

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.shared.IDENavigator
import com.kitakkun.backintime.tooling.core.ui.component.SessionSelectorView
import com.kitakkun.backintime.tooling.core.ui.component.Switch
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalIDENavigator
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
import org.jetbrains.jewel.ui.component.HorizontalSplitLayout
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.VerticalSplitLayout
import org.jetbrains.jewel.ui.component.rememberSplitLayoutState

@Composable
fun InspectorScreen(
    eventEmitter: EventEmitter<InspectorScreenEvent> = rememberEventEmitter(),
    uiState: InspectorScreenUiState = inspectorScreenPresenter(eventEmitter),
) {
    InspectorScreen(
        uiState = uiState,
        onClickProperty = { instance, property -> eventEmitter.tryEmit(InspectorScreenEvent.SelectProperty(instance.uuid, property.signature)) },
        onSelectSessionId = { eventEmitter.tryEmit(InspectorScreenEvent.SelectSession(it)) },
        onClickItem = { eventEmitter.tryEmit(InspectorScreenEvent.SelectInstance(it.uuid)) },
        onTogglePropertyVisibility = { eventEmitter.tryEmit(InspectorScreenEvent.TogglePropertyVisibility(it.uuid)) },
        onUpdateVerticalSplitDividerPosition = { eventEmitter.tryEmit(InspectorScreenEvent.UpdateVerticalDividerPosition(it)) },
        onUpdateHorizontalSplitDividerPosition = { eventEmitter.tryEmit(InspectorScreenEvent.UpdateHorizontalDividerPosition(it)) },
        onClickEvent = { eventEmitter.tryEmit(InspectorScreenEvent.SelectEvent(it)) },
        onPerformBackInTime = { sessionId, instanceId, eventId -> eventEmitter.tryEmit(InspectorScreenEvent.BackInTime(sessionId, instanceId, eventId)) },
        onToggleShowNonDebuggableProperties = { eventEmitter.tryEmit(InspectorScreenEvent.UpdateNonDebuggablePropertiesVisibility(it)) }
    )
}

data class InspectorScreenUiState(
    val selectedSessionId: String?,
    val selectedInstanceId: String?,
    val selectedPropertySignature: Signature.Property?,
    val availableSessionIds: List<String>,
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
    onSelectSessionId: (String) -> Unit,
    onClickItem: (InstanceItemUiState) -> Unit,
    onClickProperty: (InstanceItemUiState, PropertyItemUiState) -> Unit,
    onTogglePropertyVisibility: (InstanceItemUiState) -> Unit,
    onUpdateVerticalSplitDividerPosition: (Float) -> Unit,
    onUpdateHorizontalSplitDividerPosition: (Float) -> Unit,
    onClickEvent: (event: EventItemUiState) -> Unit,
    onPerformBackInTime: (sessionId: String, instanceId: String, eventId: String) -> Unit,
    onToggleShowNonDebuggableProperties: (Boolean) -> Unit,
) {
    val verticalSplitLayoutState = rememberSplitLayoutState(uiState.verticalDividerPosition)
    val horizontalSplitLayoutState = rememberSplitLayoutState(uiState.horizontalDividerPosition)

    LaunchedEffect(verticalSplitLayoutState) {
        snapshotFlow { verticalSplitLayoutState.dividerPosition }
            .distinctUntilChanged()
            .collect(onUpdateVerticalSplitDividerPosition)
    }

    LaunchedEffect(horizontalSplitLayoutState) {
        snapshotFlow { horizontalSplitLayoutState.dividerPosition }
            .distinctUntilChanged()
            .collect(onUpdateHorizontalSplitDividerPosition)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SessionSelectorView(
                sessionIdCandidates = uiState.availableSessionIds,
                selectedSessionId = uiState.selectedSessionId,
                onSelectItem = onSelectSessionId,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Show non-debuggable properties: "
                )
                Switch(
                    checked = uiState.showNonDebuggableProperties,
                    onCheckedChange = onToggleShowNonDebuggableProperties,
                )
            }
        }
        VerticalSplitLayout(
            first = {
                HorizontalSplitLayout(
                    first = {
                        InstanceListSection(
                            instances = uiState.instances,
                            onClickItem = onClickItem,
                            onClickProperty = onClickProperty,
                            onTogglePropertyVisibility = onTogglePropertyVisibility,
                            modifier = Modifier.padding(8.dp),
                        )
                    },
                    second = {
                        PropertyInspectorSection(
                            uiState = uiState.selectedInstance,
                            propertySignature = uiState.selectedPropertySignature,
                            modifier = Modifier.padding(8.dp),
                        )
                    },
                    firstPaneMinWidth = 200.dp,
                    secondPaneMinWidth = 200.dp,
                    state = horizontalSplitLayoutState,
                )
            },
            second = {
                HistorySection(
                    uiState = uiState.history,
                    onClickEvent = onClickEvent,
                    onPerformBackInTime = { onPerformBackInTime(uiState.selectedSessionId!!, uiState.selectedInstanceId!!, it.id) }
                )
            },
            firstPaneMinWidth = 200.dp,
            secondPaneMinWidth = 200.dp,
            state = verticalSplitLayoutState,
        )
    }
}

@Preview
@Composable
private fun InspectorScreenPreview() {
    PreviewContainer {
        CompositionLocalProvider(LocalIDENavigator provides IDENavigator.Noop) {
            InspectorScreen(
                uiState = InspectorScreenUiState(
                    selectedSessionId = null,
                    selectedInstanceId = null,
                    selectedPropertySignature = null,
                    availableSessionIds = listOf(),
                    instances = listOf(),
                    horizontalDividerPosition = 0.5f,
                    verticalDividerPosition = 0.5f,
                    history = null,
                    showNonDebuggableProperties = true,
                ),
                onClickEvent = {},
                onSelectSessionId = {},
                onClickItem = {},
                onClickProperty = { _, _ -> },
                onUpdateVerticalSplitDividerPosition = {},
                onUpdateHorizontalSplitDividerPosition = {},
                onTogglePropertyVisibility = {},
                onPerformBackInTime = { _, _, _ -> },
                onToggleShowNonDebuggableProperties = {},
            )
        }
    }
}