package com.github.kitakkun.back_in_time

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val hoge = HogeViewModel()

suspend fun main() {
    coroutineScope {
        launch {
            BackInTimeDebugService.valueChangeFlow.collect {
                println("value changed: $it")
            }
        }
        launch {
            while (true) {
                delay(1000)
                println(hoge.valueContainer.value)
                hoge.forceSetPropertyValueForBackInTimeDebug("valueContainer", "hogehoge")
//                hoge.set()
//                hoge.hogehoge()
            }
        }
    }
}
