package com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kitakkun.backintime.debugger.data.repository.ClassInfoRepository
import com.github.kitakkun.backintime.debugger.data.repository.InstanceRepository
import com.github.kitakkun.backintime.debugger.data.repository.MethodCallInfoRepository
import com.github.kitakkun.backintime.debugger.data.repository.ValueChangeInfoRepository
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.component.ChangeInfoBindModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.component.InstanceInfoBindModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.component.PropertyInfoBindModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private data class PropertyInspectorViewModelState(
    val instanceId: String,
    val propertyName: String,
    val sortRule: SortRule = SortRule.CREATED_AT_ASC,
)

enum class SortRule {
    CREATED_AT_ASC,
    CREATED_AT_DESC,
    VALUE_ASC,
    VALUE_DESC,
}

@KoinViewModel
class PropertyInspectorViewModel(
    @InjectedParam params: PropertyInspectorArgs,
) : ViewModel(), KoinComponent {
    private val sessionId = params.sessionId
    private val instanceId = params.instanceId
    private val propertyName = params.propertyName
    private val propertyOwnerClassName = params.propertyOwnerClassName

    private val classInfoRepository: ClassInfoRepository by inject()
    private val instanceRepository: InstanceRepository by inject()
    private val valueChangeInfoRepository: ValueChangeInfoRepository by inject()
    private val methodCallInfoRepository: MethodCallInfoRepository by inject()

    private val mutableViewModelState = MutableStateFlow(
        PropertyInspectorViewModelState(
            instanceId = instanceId,
            propertyName = propertyName,
        ),
    )
    private val viewModelState = mutableViewModelState.asStateFlow()

    private val valueChanges = valueChangeInfoRepository.selectForPropertyAsFlow(
        sessionId = sessionId,
        instanceId = instanceId,
        propertyName = propertyName,
    ).map { changesInfo ->
        changesInfo.mapNotNull {
            val methodCallInfo = methodCallInfoRepository.select(
                sessionId = sessionId,
                instanceUUID = it.instanceId,
                callId = it.methodCallId,
            ) ?: return@mapNotNull null

            ChangeInfoBindModel(
                time = methodCallInfo.calledAt,
                methodCallId = it.methodCallId,
                newValue = it.propertyValue,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList(),
    )

    val bindModel = combine(
        viewModelState,
        valueChanges,
    ) { viewModelState, valueChanges ->
        val instanceInfo = instanceRepository.select(
            sessionId = sessionId,
            instanceId = viewModelState.instanceId,
        ) ?: return@combine PropertyInspectorBindModel.Error("Instance info not found")
        val classInfo = classInfoRepository.select(
            sessionId = sessionId,
            className = propertyOwnerClassName,
        ) ?: return@combine PropertyInspectorBindModel.Error("Class info not found")
        val propertyInfo = classInfo.properties.find { it.name == viewModelState.propertyName }
            ?: return@combine PropertyInspectorBindModel.Error("Property info not found")

        val sortedValueChanges = valueChanges.let { changes ->
            when (viewModelState.sortRule) {
                SortRule.CREATED_AT_ASC -> changes.sortedBy { it.time }
                SortRule.CREATED_AT_DESC -> changes.sortedByDescending { it.time }
                SortRule.VALUE_ASC -> changes.sortedBy { it.newValue }
                SortRule.VALUE_DESC -> changes.sortedByDescending { it.newValue }
            }
        }

        PropertyInspectorBindModel.Loaded(
            instanceInfo = InstanceInfoBindModel(
                instanceId = instanceInfo.id,
                instanceClassName = instanceInfo.className,
            ),
            propertyInfo = PropertyInfoBindModel(
                propertyName = propertyInfo.name,
                propertyValueType = propertyInfo.valueType,
                propertyType = propertyInfo.propertyType,
            ),
            changesInfo = sortedValueChanges,
            sortRule = viewModelState.sortRule,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = PropertyInspectorBindModel.Loading,
    )

    fun onToggleSortWithTime() {
        mutableViewModelState.update {
            it.copy(
                sortRule = when (it.sortRule) {
                    SortRule.CREATED_AT_ASC -> SortRule.CREATED_AT_DESC
                    SortRule.CREATED_AT_DESC -> SortRule.CREATED_AT_ASC
                    SortRule.VALUE_ASC -> SortRule.CREATED_AT_DESC
                    SortRule.VALUE_DESC -> SortRule.CREATED_AT_DESC
                },
            )
        }
    }

    fun onToggleSortWithValue() {
        mutableViewModelState.update {
            it.copy(
                sortRule = when (it.sortRule) {
                    SortRule.CREATED_AT_ASC -> SortRule.VALUE_DESC
                    SortRule.CREATED_AT_DESC -> SortRule.VALUE_DESC
                    SortRule.VALUE_ASC -> SortRule.VALUE_DESC
                    SortRule.VALUE_DESC -> SortRule.VALUE_ASC
                },
            )
        }
    }
}
