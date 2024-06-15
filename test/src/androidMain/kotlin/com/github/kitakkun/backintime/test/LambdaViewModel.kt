package com.github.kitakkun.backintime.test

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.github.kitakkun.backintime.annotations.BackInTime

@BackInTime
class LambdaViewModel {
    private val mutableState = mutableStateOf(0)

    fun updateViaLocalLambda() {
        val lambda = {
            mutableState.value = 1
        }
        lambda.invoke()
    }

    fun updateViaComplicatedLocalLambda() {
        val lambda = {
            val lambda2 = {
                mutableState.value = 2
            }
            lambda2.invoke()
        }
        lambda.invoke()
    }

    fun updateViaLocalLambdaReceiver() {
        val lambda = { target: MutableState<Int> ->
            target.value = 3
        }
        lambda.invoke(mutableState)
    }

    fun updateViaComplicatedLocalLambdaReceiver() {
        val lambda = { target: MutableState<Int> ->
            val lambda2 = {
                target.value = 4
            }
            lambda2.invoke()
        }
        lambda.invoke(mutableState)
    }
}
