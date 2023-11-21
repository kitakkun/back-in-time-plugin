package com.github.kitakkun.back_in_time

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder

@DebuggableStateHolder
class HogeViewModel {
    val valueContainer = ValueContainer("hoge")

    fun set() {
        valueContainer.value = "foo"
        valueContainer.postValue("bar")
    }
}


class ValueContainer<T>(var value: T) {
    fun postValue(value: T) {
        this.value = value
    }
}
