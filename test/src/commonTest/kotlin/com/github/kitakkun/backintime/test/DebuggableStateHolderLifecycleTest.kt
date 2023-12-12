package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DebuggableStateHolderLifecycleTest {
    val service = BackInTimeDebugService

    @Test
    fun register() {
        val holder = ExampleStateHolder()
        assertNotNull(service.instances[holder])
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun notifyValueChanged() = runTest {
        val holder = ExampleStateHolder()
        val values = mutableListOf<String>()

        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val job = launch(testDispatcher) {
            service.notifyValueChangeFlow.map { it.value }.collect { values.add(it) }
        }

        holder.increment()
        runCurrent()
        holder.reset()
        runCurrent()
        holder.decrement()
        runCurrent()

        assertEquals(listOf("1", "0", "-1"), values)
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun notifyMethodCall() = runTest {
        val holder = ExampleStateHolder()
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)

        val methodNames = mutableListOf<String>()
        val job = launch(testDispatcher) {
            service.notifyMethodCallFlow.collect {
                methodNames.add(it.methodName)
            }
        }

        holder.increment()
        runCurrent()
        holder.reset()
        runCurrent()
        holder.decrement()
        runCurrent()

        assertEquals(listOf("increment", "reset", "decrement"), methodNames)
        job.cancel()
    }
}
