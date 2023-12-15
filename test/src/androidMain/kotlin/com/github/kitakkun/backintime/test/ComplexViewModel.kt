package com.github.kitakkun.backintime.test

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

@DebuggableStateHolder
class ComplexViewModel {
    private val mutableLiveData = MutableLiveData("")
    private val mutableStateFlow = MutableStateFlow("")
    private val mutableState = mutableStateOf("")

    fun mutateLiveDataViaWith() {
        // mutate via with receiver
        with(mutableLiveData) {
            value = "Updated from <set-value>"
            setValue("Updated from setValue")
            postValue("Updated from postValue")
        }
    }

    fun mutateStateFlowViaWith() {
        // mutate via with receiver
        with(mutableStateFlow) {
            value = "Updated from <set-value>"
            update { "Updated from update" }
            updateAndGet { "Updated from updateAndGet" }
            getAndUpdate { "Updated from getAndUpdate" }
        }
    }

    fun mutateStateViaWith() {
        // mutate via with receiver
        with(mutableState) {
            value = "Updated from <set-value>"
        }
    }

    fun mutateLiveDataViaApply() {
        // mutate via apply
        mutableLiveData.apply {
            value = "Updated from <set-value>"
            setValue("Updated from setValue")
            postValue("Updated from postValue")
        }
    }

    fun mutateStateFlowViaApply() {
        // mutate via apply
        mutableStateFlow.apply {
            value = "Updated from <set-value>"
            update { "Updated from update" }
            updateAndGet { "Updated from updateAndGet" }
            getAndUpdate { "Updated from getAndUpdate" }
        }
    }

    fun mutateStateViaApply() {
        // mutate via apply
        mutableState.apply {
            value = "Updated from <set-value>"
        }
    }
}
