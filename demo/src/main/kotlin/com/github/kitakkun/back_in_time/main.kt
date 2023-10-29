package com.github.kitakkun.back_in_time

import com.github.kitakkun.back_in_time.annotations.BackInTimeDebugService

fun main() {
//    val hoge = HogeViewModel()
//
//    println(hoge.hoge)
//
////    hoge.javaClass.declaredMethods.find { it.name == "forceSetParameterForBackInTimeDebug" }!!.invoke(hoge, "hoge", "20000")
//    println(hoge.hoge)
//
    val hoge = HogeViewModel()
    hoge.forceSetParameterForBackInTimeDebug("hoge", "20000")
    println(hoge.hoge)
}
