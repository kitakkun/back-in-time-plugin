package com.github.kitakkun.backintime.debugger.feature.instance.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kitakkun.backintime.debugger.data.repository.InstanceRepository
import com.github.kitakkun.backintime.debugger.data.repository.ValueChangeInfoRepository
import com.github.kitakkun.backintime.debugger.feature.instance.usecase.ConvertToInstanceBindModelUseCase
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.component.InstanceBindModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.component.PropertyBindModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class SessionInstanceScreenModelState(
    val expandedInstanceIds: Set<String> = emptySet(),
)

@KoinViewModel
class SessionInstanceViewModel(
    @InjectedParam sessionId: String
) : ViewModel(), KoinComponent {
    private val instanceRepository: InstanceRepository by inject()
    private val valueChangeInfoRepository: ValueChangeInfoRepository by inject()
    private val convertToInstanceBindModelUseCase: ConvertToInstanceBindModelUseCase by inject()

    private val mutableScreenModelState = MutableStateFlow(SessionInstanceScreenModelState())
    private val screenModelState = mutableScreenModelState.asStateFlow()
    private val activeInstances = instanceRepository.selectActiveInstances(sessionId)

    val bindModel = combine(
        screenModelState,
        activeInstances,
        valueChangeInfoRepository.selectForSessionAsFlow(sessionId),
    ) { screenModelState, activeInstances, valueChangeInfoList ->
        val instanceBindModels = activeInstances
            .mapNotNull { convertToInstanceBindModelUseCase(it, valueChangeInfoList) }
            .map { bindModel ->
                val propertiesExpanded = screenModelState.expandedInstanceIds.contains(bindModel.uuid)
                bindModel.copy(propertiesExpanded = propertiesExpanded)
            }
        when {
            instanceBindModels.isEmpty() -> SessionInstanceBindModel.NoInstanceRegistered
            else -> SessionInstanceBindModel.InstancesAvailable(instanceBindModels = instanceBindModels)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SessionInstanceBindModel.Loading,
    )

    fun onTogglePropertiesExpanded(instanceBindModel: InstanceBindModel) {
        mutableScreenModelState.update {
            val expandedInstanceIds = it.expandedInstanceIds.toMutableSet()
            if (instanceBindModel.propertiesExpanded) {
                expandedInstanceIds.remove(instanceBindModel.uuid)
            } else {
                expandedInstanceIds.add(instanceBindModel.uuid)
            }
            it.copy(expandedInstanceIds = expandedInstanceIds)
        }
    }

    fun onClickProperty(instanceBindModel: InstanceBindModel, propertyBindModel: PropertyBindModel) {
        propertyBindModel.name
    }

    fun onClickHistory(instanceBindModel: InstanceBindModel) {
        TODO()
    }
}
