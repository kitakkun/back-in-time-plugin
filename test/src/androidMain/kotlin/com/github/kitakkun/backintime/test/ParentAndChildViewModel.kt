package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder

@DebuggableStateHolder
class Parent {
    val child = Child()
    val lazyChild by lazy { Child() }

    fun accessLazyChild() {
        lazyChild.mutateHoge()
    }
}

@DebuggableStateHolder
class Child {
    private var mutableHoge = 0
    val hoge get() = mutableHoge

    fun mutateHoge() {
        mutableHoge++
    }
}
