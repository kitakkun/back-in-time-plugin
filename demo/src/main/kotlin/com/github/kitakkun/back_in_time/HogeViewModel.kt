package com.github.kitakkun.back_in_time

import com.github.kitakkun.back_in_time.annotations.BackInTimeDebugService
import com.github.kitakkun.back_in_time.annotations.DebuggableStateHolder
import com.github.kitakkun.back_in_time.annotations.DebuggableStateHolderManipulator
import java.io.Serial

interface HOHO {
    fun HOHO()
}

@DebuggableStateHolder
@Suppress("unused")
class HogeViewModel : HOHO {
    var hoge = ""
    var fuga = ""
    var foo = ""

    override fun HOHO() {
        TODO("Not yet implemented")
    }

    fun test(parameterType: String) {
        when (parameterType) {
            "0" -> {
                hoge = "10"
            }

            "1" -> {
                fuga = "11"
            }

            "2" -> {
                foo = "12"
            }
        }
    }
}


class Hoge : DebuggableStateHolderManipulator {
    fun set() {
        BackInTimeDebugService.manipulate(System.identityHashCode(this), "hoge", "value")
    }

    override fun forceSetParameterForBackInTimeDebug(paramKey: String, value: String) {
        println("forceSetParameterForBackInTimeDebug: $paramKey, $value")
    }
}
