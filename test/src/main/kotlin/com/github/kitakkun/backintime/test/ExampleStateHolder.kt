package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder

@DebuggableStateHolder
class ExampleStateHolder {
    private var mutableCounter = 0
    val counter get() = mutableCounter

    fun increment() {
        mutableCounter++
    }

    fun decrement() {
        mutableCounter--
    }

    fun reset() {
        mutableCounter = 0
    }
}
