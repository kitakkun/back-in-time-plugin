package com.github.kitakkun.back_in_time

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder

@DebuggableStateHolder
@Suppress("unused")
class HogeViewModel(
    private val aa: String,
) {
    var hoge = ""
    var fuga = 0
    var foo = 20.0

    constructor() : this("hogehoge")

    fun set() {
        hoge = "hoge"
        fuga = 100
        foo = 200.0
    }
}
