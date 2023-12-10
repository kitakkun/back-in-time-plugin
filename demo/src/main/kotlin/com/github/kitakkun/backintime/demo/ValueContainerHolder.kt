package com.github.kitakkun.backintime.demo

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder

@DebuggableStateHolder
class ValueContainerHolder {
    val intContainer = ValueContainer(10)
    val stringContainer = ValueContainer("Hello")
}
