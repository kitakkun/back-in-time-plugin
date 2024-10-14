package io.github.kitakkun.backintime.test

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import io.github.kitakkun.backintime.core.annotations.BackInTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * しないとは思うが，すごく変なコードの書き方をした場合でも正しくキャプチャされるかのテスト
 */
@BackInTime
class WeirdCodeStyleViewModel {
    private val mutableLiveData1 = MutableLiveData("")
    private val mutableLiveData2 = MutableLiveData("")
    private val mutableStateFlow = MutableStateFlow("")
    private val mutableState = mutableStateOf("")

    fun mutateLiveData() {
        println((mutableLiveData1.setValue("Updated from setValue")).toString())
        val lambda = {
            ((mutableLiveData1.setValue("Updated from setValue inside block")).equals(null)).toString()
        }
        lambda.invoke()
        doubleMutateByBlock(mutableLiveData1, mutableLiveData2) {
            value = "Updated from <set-value>"
        }
        mutableLiveData1.setValueAndGet("Updated from setValueAndGet1") + mutableLiveData1.setValueAndGet("Updated from setValueAndGet2")
        mutableLiveData1.setValueAndGetInline("Updated from setValueAndGetInline1") + mutableLiveData1.setValueAndGetInline("Updated from setValueAndGetInline2")
    }

    fun mutateStateFlow() {
        println((mutableStateFlow.update { "Updated from update" }).toString())
        val lambda = {
            ((mutableStateFlow.update { "Updated from update inside block" }).equals(null)).toString()
        }
        lambda.invoke()
    }

    fun mutateState() {
        println((mutableState.setValue("Updated from setValue")).toString())
    }
}

private fun <T> MutableState<T>.setValue(value: T) {
    this.value = value
}

private fun <T> MutableLiveData<T>.setValueAndGet(value: T): T {
    this.value = value
    return value
}

@Suppress("NOTHING_TO_INLINE")
private inline fun <T> MutableLiveData<T>.setValueAndGetInline(value: T): T {
    this.value = value
    return value
}

private fun <T> doubleMutateByBlock(receiver1: MutableLiveData<T>, receiver2: MutableLiveData<T>, block: MutableLiveData<T>.() -> Unit) {
    receiver1.block()
    receiver2.block()
}
