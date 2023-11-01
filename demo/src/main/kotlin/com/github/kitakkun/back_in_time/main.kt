package com.github.kitakkun.back_in_time

fun main() {
    val hoge = HogeViewModel()
//    hoge.forceSetParameterForBackInTimeDebug("hoge", "20000")
    hoge.hoge = "2000"
//    hoge.fuga = 100
//    hoge.foo = 200.0
    hoge.set()
}
