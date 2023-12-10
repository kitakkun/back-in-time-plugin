package com.github.kitakkun.backintime.test

class GradleConfiguredValueContainer<T>(var value: T) {
    fun update(newValue: T) {
        value = newValue
    }
}
