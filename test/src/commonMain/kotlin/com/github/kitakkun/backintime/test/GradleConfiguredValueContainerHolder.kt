package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder

@DebuggableStateHolder
class GradleConfiguredValueContainerHolder {
    val intContainer = GradleConfiguredValueContainer(10)
    val stringContainer = GradleConfiguredValueContainer("Hello")
}
