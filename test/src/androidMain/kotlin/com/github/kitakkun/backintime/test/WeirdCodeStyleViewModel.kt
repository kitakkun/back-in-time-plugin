package com.github.kitakkun.backintime.test

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * しないとは思うが，すごく変なコードの書き方をした場合でも正しくキャプチャされるかのテスト
 */
@DebuggableStateHolder
class WeirdCodeStyleViewModel {
    private val mutableLiveData = MutableLiveData("")
    private val mutableStateFlow = MutableStateFlow("")
    private val mutableState = mutableStateOf("")

    fun mutateLiveData() {
        println((mutableLiveData.setValue("Updated from setValue")).toString())
    }

    fun mutateStateFlow() {
        println((mutableStateFlow.update { "Updated from update" }).toString())
    }

    fun mutateState() {
        println((mutableState.setValue("Updated from setValue")).toString())
    }
}

private fun <T> MutableState<T>.setValue(value: T) {
    this.value = value
}
