package com.kitakkunl.backintime.feature.inspector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalPluginStateService
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalServer
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalSettings
import com.kitakkun.backintime.tooling.core.ui.logic.EventEffect
import com.kitakkun.backintime.tooling.core.ui.logic.EventEmitter
import com.kitakkun.backintime.tooling.core.usecase.rememberInstances
import com.kitakkun.backintime.tooling.model.EventEntity
import com.kitakkunl.backintime.feature.inspector.components.EventItemUiState
import com.kitakkunl.backintime.feature.inspector.components.InstanceItemUiState
import com.kitakkunl.backintime.feature.inspector.components.PropertyItemUiState
import com.kitakkunl.backintime.feature.inspector.model.toClassSignature
import com.kitakkunl.backintime.feature.inspector.model.toFunctionSignature
import com.kitakkunl.backintime.feature.inspector.model.toPropertySignature
import com.kitakkunl.backintime.feature.inspector.section.HistorySectionUiState

sealed interface InspectorScreenEvent {
    data class TogglePropertyVisibility(val instanceId: String) : InspectorScreenEvent
    data class SelectSession(val id: String) : InspectorScreenEvent
    data class SelectInstance(val id: String) : InspectorScreenEvent
    data class SelectProperty(val instanceId: String, val name: String) : InspectorScreenEvent
    data class UpdateVerticalDividerPosition(val position: Float) : InspectorScreenEvent
    data class UpdateHorizontalDividerPosition(val position: Float) : InspectorScreenEvent
    data class SelectEvent(val event: EventItemUiState) : InspectorScreenEvent
    data class BackInTime(val sessionId: String, val instanceId: String, val eventId: String) : InspectorScreenEvent
    data class UpdateNonDebuggablePropertiesVisibility(val visible: Boolean) : InspectorScreenEvent
}

@Composable
fun inspectorScreenPresenter(eventEmitter: EventEmitter<InspectorScreenEvent>): InspectorScreenUiState {
    val pluginStateService = LocalPluginStateService.current
    val pluginState by pluginStateService.stateFlow.collectAsState()

    val server = LocalServer.current
    val serverState by rememberUpdatedState(server.state)

    val settings = LocalSettings.current
    val settingsState by rememberUpdatedState(settings.getState())

    val instances by rememberInstances(pluginState.globalState.selectedSessionId)

    val instanceUiStates by remember {
        derivedStateOf {
            instances.map { instance ->
                InstanceItemUiState(
                    uuid = instance.id,
                    classSignature = instance.className.toClassSignature(),
                    properties = instance.properties
                        .filter { settingsState.showNonDebuggableProperties || it.debuggable }
                        .map { property ->
                            PropertyItemUiState(
                                name = property.name,
                                type = property.type,
                                eventCount = property.totalEvents,
                                isSelected = pluginState.inspectorState.selectedInstanceId == instance.id && pluginState.inspectorState.selectedPropertyKey == property.name,
                            )
                        },
                    propertiesExpanded = instance.id in pluginState.inspectorState.expandedInstanceIds,
                    totalEventsCount = instance.totalEvents,
                )
            }
        }
    }

    val history by remember {
        derivedStateOf {
            instances.find { it.id == pluginState.inspectorState.selectedInstanceId }?.let { instance ->
                val events = instance.events.mapNotNull { event ->
                    val eventIsSelected = event.eventId == pluginState.inspectorState.selectedEventId
                    when (event) {
                        is EventEntity.Instance.MethodInvocation -> EventItemUiState.MethodInvocation(
                            expandedDetails = true,
                            stateChanges = instance.events.filterIsInstance<EventEntity.Instance.StateChange>()
                                .filter { it.callId == event.callId }
                                .groupBy { it.propertySignature }
                                .map { (propertyFqName, stateChanges) ->
                                    EventItemUiState.MethodInvocation.UpdatedProperty(
                                        signature = propertyFqName.toPropertySignature(),
                                        stateUpdates = stateChanges.map { it.newValueAsJson },
                                    )
                                },
                            invokedMethodSignature = event.methodSignature.toFunctionSignature(),
                            id = event.eventId,
                            selected = eventIsSelected,
                            time = event.time,
                        )

                        is EventEntity.Instance.Register -> EventItemUiState.Register(
                            id = event.eventId,
                            selected = eventIsSelected,
                            expandedDetails = false,
                            time = event.time,
                        )

                        is EventEntity.Instance.StateChange -> null // collected as a part of MethodInvocation
                        is EventEntity.Instance.Unregister -> EventItemUiState.Unregister(
                            id = event.eventId,
                            selected = event.eventId == pluginState.inspectorState.selectedEventId,
                            expandedDetails = false,
                            time = event.time,
                        )

                        is EventEntity.Instance.BackInTime -> TODO()
                        is EventEntity.Instance.NewDependency -> TODO()
                        is EventEntity.System.AppError -> TODO()
                        is EventEntity.System.CheckInstanceAlive -> TODO()
                        is EventEntity.System.CheckInstanceAliveResult -> TODO()
                        is EventEntity.System.DebuggerError -> TODO()
                    }
                }

                HistorySectionUiState(
                    events = events,
                    selectedEventData = events.find { it.id == pluginState.inspectorState.selectedEventId },
                )
            }
        }
    }

    EventEffect(eventEmitter) { event ->
        when (event) {
            is InspectorScreenEvent.TogglePropertyVisibility -> {
                pluginStateService.updateInspectorState {
                    it.copy(
                        expandedInstanceIds = if (event.instanceId in it.expandedInstanceIds) {
                            it.expandedInstanceIds - event.instanceId
                        } else {
                            it.expandedInstanceIds + event.instanceId
                        },
                    )
                }
            }

            is InspectorScreenEvent.SelectSession -> {
                pluginStateService.updateSessionId(event.id)
            }

            is InspectorScreenEvent.SelectInstance -> {
                if (pluginState.inspectorState.selectedInstanceId != event.id) {
                    pluginStateService.loadState(
                        pluginState.copy(inspectorState = pluginState.inspectorState.copy(selectedInstanceId = event.id, selectedPropertyKey = null))
                    )
                } else {
                    pluginStateService.loadState(
                        pluginState.copy(inspectorState = pluginState.inspectorState.copy(selectedInstanceId = event.id))
                    )
                }
            }

            is InspectorScreenEvent.SelectProperty -> {
                pluginStateService.updateInspectorState {
                    it.copy(selectedInstanceId = event.instanceId, selectedPropertyKey = event.name)
                }
            }

            is InspectorScreenEvent.UpdateHorizontalDividerPosition -> {
                pluginStateService.updateInspectorState {
                    it.copy(horizontalSplitPanePosition = event.position)
                }
            }

            is InspectorScreenEvent.UpdateVerticalDividerPosition -> {
                pluginStateService.updateInspectorState {
                    it.copy(verticalSplitPanePosition = event.position)
                }
            }

            is InspectorScreenEvent.SelectEvent -> {
                pluginStateService.updateInspectorState {
                    it.copy(selectedEventId = event.event.id)
                }
            }

            is InspectorScreenEvent.BackInTime -> {
                val instance = instances.find { it.id == event.instanceId } ?: return@EventEffect
                val allEventsBeforeBackInTimePoint = (instance.events.takeWhile { it.eventId != event.eventId } + instance.events.find { it.eventId == event.eventId }).filterNotNull()
                val values = allEventsBeforeBackInTimePoint
                    .filterIsInstance<EventEntity.Instance.StateChange>()
                    .reversed()
                    .map { it.propertySignature to it.newValueAsJson }
                    .distinctBy { it.first }
                    .toMap()
                server.backInTime(
                    sessionId = event.sessionId,
                    instanceId = event.instanceId,
                    values = values,
                )
            }

            is InspectorScreenEvent.UpdateNonDebuggablePropertiesVisibility -> {
                settings.update {
                    it.copy(showNonDebuggableProperties = event.visible)
                }
            }
        }
    }

    return InspectorScreenUiState(
        selectedInstanceId = pluginState.inspectorState.selectedInstanceId,
        selectedPropertyName = pluginState.inspectorState.selectedPropertyKey,
        selectedSessionId = pluginState.globalState.selectedSessionId,
        availableSessionIds = serverState.connections.map { it.id },
        instances = instanceUiStates,
        horizontalDividerPosition = pluginState.inspectorState.horizontalSplitPanePosition,
        verticalDividerPosition = pluginState.inspectorState.verticalSplitPanePosition,
        history = history,
        showNonDebuggableProperties = settingsState.showNonDebuggableProperties,
    )
}