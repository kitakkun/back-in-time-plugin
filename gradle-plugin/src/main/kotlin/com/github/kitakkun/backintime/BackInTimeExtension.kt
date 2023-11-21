package com.github.kitakkun.backintime

val defaultCapturedCalls = listOf(
    "kotlinx.coroutines.flow.MutableStateFlow.update",
    "kotlinx.coroutines.flow.MutableStateFlow.<set-value>",
    "androidx.lifecycle.MutableLiveData.postValue",
    "androidx.lifecycle.MutableLiveData.setValue",
    "androidx.compose.runtime.MutableState.<set-value>",
)

val defaultValueGetters = listOf(
    "androidx.lifecycle.MutableLiveData:<get-value>",
    "kotlinx.coroutines.flow.MutableStateFlow:<get-value>",
    "androidx.compose.runtime.MutableState:<get-value>",
)

open class BackInTimeExtension(
    var enabled: Boolean = true,
    var capturedCalls: List<String> = defaultCapturedCalls,
    var valueGetters: List<String> = defaultValueGetters,
)
