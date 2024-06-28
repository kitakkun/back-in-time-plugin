package com.github.kitakkun.backintime.debugger.feature.instance.view

import androidx.lifecycle.ViewModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.PropertyInspectorArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class InstanceSharedViewModel : ViewModel() {
    private val mutablePropertyArgFlow = MutableStateFlow<PropertyInspectorArgs?>(null)
    val propertyArgFlow = mutablePropertyArgFlow.asStateFlow()

    fun selectProperty(sessionId: String, instanceId: String, propertyName: String, propertyOwnerClassName: String) {
        mutablePropertyArgFlow.value = PropertyInspectorArgs(
                sessionId = sessionId,
                instanceId = instanceId,
                propertyName = propertyName,
                propertyOwnerClassName = propertyOwnerClassName,
        )
    }
}
