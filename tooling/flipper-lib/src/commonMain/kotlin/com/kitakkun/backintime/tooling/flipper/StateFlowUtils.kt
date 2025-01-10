package com.kitakkun.backintime.tooling.flipper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import react.useEffectWithCleanup
import react.useState

@JsExport
fun <T> useStateFlow(flow: StateFlow<T>): T {
    val (state, setState) = useState(flow.value)

    useEffectWithCleanup(listOf(flow)) {
        val coroutineScope = CoroutineScope(Dispatchers.Default)

        coroutineScope.launch {
            flow.collect {
                setState(it)
            }
        }

        onCleanup {
            coroutineScope.cancel()
        }
    }

    return state
}
