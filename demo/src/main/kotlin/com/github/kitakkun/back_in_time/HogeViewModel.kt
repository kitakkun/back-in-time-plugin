package com.github.kitakkun.back_in_time

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder

@DebuggableStateHolder
class HogeViewModel(
    private val aa: String,
) {
    var hoge = ""
    var fuga = 0
    var foo = 20.0
    val valueContainer = ValueContainer("hoge")

    constructor() : this("hogehoge")

    fun set() {
//        val a = java.util.UUID.randomUUID().toString()
//        hoge = "hoge"
//        fuga = 100
//        foo = 200.0
        valueContainer.value = "fuga"
        valueContainer.postValue("HOHO")
    }

    fun hogehoge(a: Int = 10): Int {
//        hoge += hoge
        return a
    }
}


class ValueContainer<T>(var value: T) {
    fun set(value: T) {
        this.value = value
    }

    fun postValue(value: T) {
        this.value = value
    }
}
