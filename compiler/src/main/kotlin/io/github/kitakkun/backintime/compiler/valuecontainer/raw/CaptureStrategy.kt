package io.github.kitakkun.backintime.compiler.valuecontainer.raw

sealed class CaptureStrategy {
    data object AfterCall : CaptureStrategy()
    data class ValueArgument(val index: Int = 0) : CaptureStrategy()
}
