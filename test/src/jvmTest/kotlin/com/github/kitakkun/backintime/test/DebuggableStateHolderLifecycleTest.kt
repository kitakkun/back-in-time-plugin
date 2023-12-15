package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.After
import org.junit.Before
import org.junit.Test

class DebuggableStateHolderLifecycleTest {
    @Before
    fun setup() {
        mockkObject(BackInTimeDebugService)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun register() {
        val holder = ExampleStateHolder()
        verify(exactly = 1) { BackInTimeDebugService.register(holder, any()) }
    }

    @Test
    fun notifyValueChanged() {
        val holder = ExampleStateHolder()

        holder.increment()
        holder.reset()
        holder.decrement()

        verifyOrder {
            BackInTimeDebugService.notifyPropertyChanged(holder, "mutableCounter", 1, any())
            BackInTimeDebugService.notifyPropertyChanged(holder, "mutableCounter", 0, any())
            BackInTimeDebugService.notifyPropertyChanged(holder, "mutableCounter", -1, any())
        }
    }

    @Test
    fun notifyMethodCall() {
        val holder = ExampleStateHolder()
        holder.increment()
        holder.reset()
        holder.decrement()

        verifyOrder {
            BackInTimeDebugService.notifyMethodCall(holder, "increment", any())
            BackInTimeDebugService.notifyMethodCall(holder, "reset", any())
            BackInTimeDebugService.notifyMethodCall(holder, "decrement", any())
        }
    }
}
