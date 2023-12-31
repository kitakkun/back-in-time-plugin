package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder

@DebuggableStateHolder
class ValueContainerHolder {
    val intContainer = AnnotationConfiguredValueContainer(10)
    val stringContainer = AnnotationConfiguredValueContainer("Hello")
}
