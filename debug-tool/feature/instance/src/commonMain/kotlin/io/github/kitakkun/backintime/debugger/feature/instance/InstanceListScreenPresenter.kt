package io.github.kitakkun.backintime.debugger.feature.instance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import io.github.kitakkun.backintime.debugger.core.usecase.instances
import io.github.kitakkun.backintime.debugger.core.usecase.valueChangesByProperty
import io.github.kitakkun.backintime.debugger.feature.instance.component.history.ChangeInfo
import io.github.kitakkun.backintime.debugger.feature.instance.component.inspector.InstanceDetail
import io.github.kitakkun.backintime.debugger.feature.instance.component.inspector.PropertyDetail
import io.github.kitakkun.backintime.debugger.feature.instance.component.list.InstanceUiState
import io.github.kitakkun.backintime.debugger.feature.instance.component.list.PropertyUiState
import io.github.kitakkun.backintime.debugger.feature.instance.model.SortRule
import io.github.kitakkun.backintime.debugger.feature.instance.section.InstanceListUiState
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEffect
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import io.github.takahirom.rin.rememberRetained

sealed interface InstanceListEvent {
    data class SelectProperty(val instanceId: String, val propertyId: String) : InstanceListEvent
    data class TogglePropertyVisibility(val instanceId: String) : InstanceListEvent
}

@Composable
fun instanceListScreenPresenter(events: EventEmitter<InstanceListEvent>, sessionId: String): InstanceListScreenUiState {
    val instances = instances(sessionId)
    val propertiesExpanded = rememberRetained { mutableStateMapOf<String, Boolean>() }

    var selectedInstanceId by rememberRetained { mutableStateOf<String?>(null) }
    var selectedPropertyId by rememberRetained { mutableStateOf<String?>(null) }

    EventEffect(events) { event ->
        when (event) {
            is InstanceListEvent.SelectProperty -> {
                selectedInstanceId = event.instanceId
                selectedPropertyId = event.propertyId
            }

            is InstanceListEvent.TogglePropertyVisibility -> {
                val expanded = propertiesExpanded[event.instanceId] ?: false
                propertiesExpanded[event.instanceId] = !expanded
            }
        }
    }

    val instanceUiStateList by rememberUpdatedState(
        instances.map { instance ->
            InstanceUiState(
                id = instance.id,
                className = instance.classInfo.name,
                properties = instance.classInfo.allPropertiesWithOwnerClassName.map { (ownerClassName, propertyInfo) ->
                    PropertyUiState(
                        name = propertyInfo.name,
                        type = propertyInfo.type,
                        ownerClassName = ownerClassName,
                        isDerivedProperty = ownerClassName != instance.classInfo.name,
                        isBackInTimeDebuggable = propertyInfo.backInTimeDebuggable,
                        eventCount = 0,
                        isSelected = propertyInfo.name == selectedPropertyId && instance.id == selectedInstanceId,
                    )
                },
                propertiesExpanded = propertiesExpanded[instance.id] ?: false,
            )
        }
    )

    val inspectorState by rememberUpdatedState(
        propertyInspectorState(
            sessionId = sessionId,
            instanceId = selectedInstanceId,
            propertyId = selectedPropertyId,
        )
    )

    return if (sessionId.isEmpty()) {
        InstanceListScreenUiState.NoSessionOpened
    } else {
        InstanceListScreenUiState.Opened(
            sessionInstanceListUiState = if (instanceUiStateList.isEmpty()) {
                InstanceListUiState.Empty
            } else {
                InstanceListUiState.Loaded(items = instanceUiStateList)
            },
            propertyInspectorUiState = inspectorState,
        )
    }
}

@Composable
private fun propertyInspectorState(
    sessionId: String,
    instanceId: String?,
    propertyId: String?,
): PropertyInspectorScreenUiState {
    if (instanceId == null || propertyId == null) return PropertyInspectorScreenUiState.NoneSelected
    val valueChanges = valueChangesByProperty(
        sessionId = sessionId,
        instanceId = instanceId,
        propertyId = propertyId,
    )

    return PropertyInspectorScreenUiState.Loaded(
        instanceInfo = InstanceDetail(instanceId = instanceId, instanceClassName = ""),
        propertyInfo = PropertyDetail(propertyName = propertyId, propertyValueType = "", propertyType = ""),
        changesInfo = valueChanges.map {
            ChangeInfo(
                time = 0,
                newValue = it.newValue,
            )
        },
        sortRule = SortRule.VALUE_ASC,
    )
}
