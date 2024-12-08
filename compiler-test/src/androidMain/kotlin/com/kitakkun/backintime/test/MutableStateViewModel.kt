package com.kitakkun.backintime.test

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import com.kitakkun.backintime.core.annotations.BackInTime

@BackInTime
class MutableStateViewModel {
    private val mutableIntState = mutableIntStateOf(0)
    private val mutableDoubleState = mutableDoubleStateOf(0.0)
    private val mutableFloatState = mutableFloatStateOf(0.0f)
    private val mutableLongState = mutableLongStateOf(0L)
    private val mutableStateList = mutableStateListOf<String>()
    private val mutableStateMap = mutableStateMapOf<String, String>()

    fun mutateMutableIntState() {
        mutableIntState.value = 1
        mutableIntState.intValue = 1
    }

    fun mutateMutableDoubleState() {
        mutableDoubleState.value = 1.0
        mutableDoubleState.doubleValue = 1.0
    }

    fun mutateMutableFloatState() {
        mutableFloatState.value = 1.0f
        mutableFloatState.floatValue = 1.0f
    }

    fun mutateMutableLongState() {
        mutableLongState.value = 1L
        mutableLongState.longValue = 1L
    }

    fun mutateMutableStateList() {
        mutableStateList.add("Hello")
    }

    fun mutateMutableStateMap() {
        mutableStateMap["Hello"] = "World"
    }
}
