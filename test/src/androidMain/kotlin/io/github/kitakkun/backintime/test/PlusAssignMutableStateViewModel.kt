package io.github.kitakkun.backintime.test

import androidx.compose.runtime.mutableStateOf
import io.github.kitakkun.backintime.annotations.BackInTime

/**
 * MutableState<List<*>> なプロパティを宣言し，
 * mutableState.value += "hoge" のような書き方をすると
 * <set-value> の receiver が間接的にプロパティを参照する別の一時変数の参照 (IrGetValue）となり，
 * キャプチャを逃れてしまう事象があった
 *
 * この問題の修正確認用
 */
@BackInTime
class PlusAssignMutableStateViewModel {
    private val mutableStrings = mutableStateOf(emptyList<String>())

    fun test() {
        mutableStrings.value += "test"
    }
}
