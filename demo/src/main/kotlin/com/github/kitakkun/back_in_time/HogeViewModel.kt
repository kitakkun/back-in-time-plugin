package com.github.kitakkun.back_in_time

import com.github.kitakkun.back_in_time.annotations.DebuggableStateHolder
import java.io.Serial

interface HOHO {
    fun HOHO()
}

//@Suppress("unused")
@DebuggableStateHolder
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
