package com.kitakkun.backintime.compiler.backend.valuecontainer

sealed class CaptureStrategy {
    data object AfterCall : CaptureStrategy()
    data class ValueArgument(val index: Int = 0) : CaptureStrategy()
}
