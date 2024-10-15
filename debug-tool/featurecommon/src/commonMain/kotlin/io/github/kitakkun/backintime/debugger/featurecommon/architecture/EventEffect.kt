package io.github.kitakkun.backintime.debugger.featurecommon.architecture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@Composable
fun <E> EventEffect(
    eventEmitter: EventEmitter<E>,
    onEvent: suspend CoroutineScope.(event: E) -> Unit,
) {
    LaunchedEffect(eventEmitter) {
        supervisorScope {
            eventEmitter.collect { event ->
                launch {
                    onEvent(event)
                }
            }
        }
    }
}
