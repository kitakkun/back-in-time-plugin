package com.github.kitakkun.back_in_time

fun main() {
    val hoge = HogeViewModel()

    println(hoge.hoge)

//    hoge.javaClass.declaredMethods.find { it.name == "forceSetParameterForBackInTimeDebug" }!!.invoke(hoge, "hoge", "20000")
    println(hoge.hoge)
}
