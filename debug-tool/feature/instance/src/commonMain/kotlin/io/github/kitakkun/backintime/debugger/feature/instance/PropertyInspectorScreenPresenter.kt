package io.github.kitakkun.backintime.debugger.feature.instance

import androidx.compose.runtime.Composable
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter

sealed interface PropertyInspectorScreenEvent {

}

@Composable
fun propertyInspectorScreenPresenter(
    eventEmitter: EventEmitter<PropertyInspectorScreenEvent>,
    route: PropertyInspectorScreenRoute,
): PropertyInspectorScreenUiState {
//    val instanceRepository: InstanceRepository = koinInject()
//    val classInfoRepository: ClassInfoRepository = koinInject()
//    val methodCallInfoRepository: MethodCallInfoRepository = koinInject()
//    val valueChangeInfoRepository: ValueChangeInfoRepository = koinInject()
//
//    val (sessionId, instanceId, propertyName, propertyOwnerClassName) = route
//
//    val instanceInfo: Instance? by produceState<Instance?>(null) {
//        this.value = instanceRepository.select(
//            sessionId = sessionId,
//            instanceId = instanceId,
//        )
//    }
//
//    val classInfo: ClassInfo? by produceState<ClassInfo?>(null) {
//        value = classInfoRepository.select(
//            sessionId = sessionId,
//            className = route.propertyOwnerClassName,
//        )
//    }
//
//    val propertyInfo by rememberUpdatedState(classInfo?.properties?.find { it.name == propertyName })
//
//    var sortRule: SortRule = rememberRetained { SortRule.CREATED_AT_DESC }
//
//    val valueChanges: List<ChangeInfoBindModel> by produceRetainedState(emptyList()) {
//        valueChangeInfoRepository.selectForPropertyAsFlow(
//            sessionId = sessionId,
//            instanceId = instanceId,
//            propertyName = propertyName,
//        )
//            .map { changesInfo ->
//                changesInfo.mapNotNull {
//                    val methodCallInfo = methodCallInfoRepository.select(
//                        sessionId = sessionId,
//                        instanceUUID = it.instanceId,
//                        callId = it.methodCallId,
//                    ) ?: return@mapNotNull null
//
//                    ChangeInfoBindModel(
//                        time = methodCallInfo.calledAt,
//                        methodCallId = it.methodCallId,
//                        newValue = it.propertyValue,
//                    )
//                }
//            }.collect {
//                this.value = it
//            }
//    }
//
//    val sortedValueChanges = valueChanges.let { changes ->
//        when (sortRule) {
//            SortRule.CREATED_AT_ASC -> changes.sortedBy { it.time }
//            SortRule.CREATED_AT_DESC -> changes.sortedByDescending { it.time }
//            SortRule.VALUE_ASC -> changes.sortedBy { it.newValue }
//            SortRule.VALUE_DESC -> changes.sortedByDescending { it.newValue }
//        }
//    }
//
//    return PropertyInspectorScreenUiState.Loaded(
//        instanceInfo = InstanceInfoBindModel(
//            instanceId = instanceInfo?.id ?: return PropertyInspectorScreenUiState.Error("InstanceInfo not found"),
//            instanceClassName = instanceInfo?.className ?: return PropertyInspectorScreenUiState.Error("InstanceInfo not found"),
//        ),
//        propertyInfo = PropertyInfoBindModel(
//            propertyName = propertyInfo?.name ?: return PropertyInspectorScreenUiState.Error("InstanceInfo not found"),
//            propertyValueType = propertyInfo?.valueType ?: return PropertyInspectorScreenUiState.Error("InstanceInfo not found"),
//            propertyType = propertyInfo?.propertyType ?: return PropertyInspectorScreenUiState.Error("InstanceInfo not found"),
//        ),
//        changesInfo = sortedValueChanges,
//        sortRule = sortRule,
//    )
    return TODO()
}
