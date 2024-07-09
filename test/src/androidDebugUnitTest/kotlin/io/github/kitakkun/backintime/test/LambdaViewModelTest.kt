package io.github.kitakkun.backintime.test

import kotlin.test.Test
import kotlin.test.assertEquals

class LambdaViewModelTest : BackInTimeDebugServiceTest() {

    @Test
    fun updateViaLocalLambdaTest() {
        val viewModel = LambdaViewModel()
        viewModel.updateViaLocalLambda()

        assertEquals(1, propertyValueChangeEvents.size)
    }

    @Test
    fun updateViaComplicatedLocalLambdaTest() {
        val viewModel = LambdaViewModel()
        viewModel.updateViaComplicatedLocalLambda()

        assertEquals(1, propertyValueChangeEvents.size)
    }

    @Test
    fun updateViaLocalLambdaReceiverTest() {
        val viewModel = LambdaViewModel()
        viewModel.updateViaLocalLambdaReceiver()

        assertEquals(1, propertyValueChangeEvents.size)
    }

    @Test
    fun updateViaComplicatedLocalLambdaReceiverTest() {
        val viewModel = LambdaViewModel()
        viewModel.updateViaComplicatedLocalLambdaReceiver()

        assertEquals(1, propertyValueChangeEvents.size)
    }
}
