package com.kitakkun.backintime.tooling.core.ui.logic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

typealias EventEmitter<T> = MutableSharedFlow<T>

@Composable
fun <T> rememberEventEmitter(): EventEmitter<T> {
    return remember {
        MutableSharedFlow(extraBufferCapacity = 20)
    }
}

@Composable
fun <T> EventEffect(
    eventEmitter: EventEmitter<T>,
    block: suspend CoroutineScope.(event: T) -> Unit,
) {
    LaunchedEffect(eventEmitter) {
        eventEmitter.collect {
            launch {
                try {
                    block(it)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }
}
