package com.github.kitakkun.backintime

val defaultCapturedCalls = mutableListOf(
    "kotlinx.coroutines.flow.MutableStateFlow:update",
    "kotlinx.coroutines.flow.MutableStateFlow:<set-value>",
    "androidx.lifecycle.MutableLiveData:postValue",
    "androidx.lifecycle.MutableLiveData:setValue",
    "androidx.compose.runtime.MutableState:<set-value>",
)

val defaultValueGetters = mutableListOf(
    "androidx.lifecycle.MutableLiveData:getValue",
    "kotlinx.coroutines.flow.MutableStateFlow:<get-value>",
    "androidx.compose.runtime.MutableState:<get-value>",
)

val defaultValueSetters = mutableListOf(
    "androidx.lifecycle.MutableLiveData:setValue",
    "kotlinx.coroutines.flow.MutableStateFlow:<set-value>",
    "androidx.compose.runtime.MutableState:<set-value>",
)

open class BackInTimeExtension(
    var enabled: Boolean = true,
    val capturedCalls: MutableList<String> = defaultCapturedCalls,
    val valueGetters: MutableList<String> = defaultValueGetters,
    val valueSetters: MutableList<String> = defaultValueSetters,
)
