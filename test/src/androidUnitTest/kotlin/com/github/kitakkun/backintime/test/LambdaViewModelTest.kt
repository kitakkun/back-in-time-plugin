package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import kotlin.test.Test

class LambdaViewModelTest {
    private val viewModel = LambdaViewModel()

    @Before
    fun setup() {
        mockkObject(BackInTimeDebugService)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun updateViaLocalLambdaTest() {
        viewModel.updateViaLocalLambda()

        verify(exactly = 1) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableState", any(), any())
        }
    }

    @Test
    fun updateViaComplicatedLocalLambdaTest() {
        viewModel.updateViaComplicatedLocalLambda()

        verify(exactly = 1) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableState", any(), any())
        }
    }

    @Test
    fun updateViaLocalLambdaReceiverTest() {
        viewModel.updateViaLocalLambdaReceiver()

        verify(exactly = 1) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableState", any(), any())
        }
    }

    @Test
    fun updateViaComplicatedLocalLambdaReceiverTest() {
        viewModel.updateViaComplicatedLocalLambdaReceiver()

        verify(exactly = 1) {
            BackInTimeDebugService.notifyPropertyChanged(any(), "mutableState", any(), any())
        }
    }
}
