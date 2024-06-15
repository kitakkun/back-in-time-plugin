package com.github.kitakkun.backintime.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kitakkun.backintime.annotations.BackInTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

@BackInTime
class StateFlowCaptureViewModel : ViewModel() {
    private val mutableStateFlow = MutableStateFlow("")

    fun updateValues() {
        mutableStateFlow.value = "Hoge"
        mutableStateFlow.tryEmit("Fuga")
        mutableStateFlow.update { "Piyo" }
        mutableStateFlow.updateAndGet { "Foo" }
        mutableStateFlow.getAndUpdate { "Bar" }
        viewModelScope.launch {
            mutableStateFlow.emit("Baz")
        }
    }
}
