package com.kitakkun.backintime.test

import kotlin.test.Test
import kotlin.test.assertEquals

class LambdaViewModelTest : BackInTimeDebugServiceTest() {
    @Test
    fun updateViaLocalLambdaTest() {
        val viewModel = LambdaViewModel()
        viewModel.updateViaLocalLambda()

        assertEquals(2, propertyValueChangeEvents.size)
    }

    @Test
    fun updateViaComplicatedLocalLambdaTest() {
        val viewModel = LambdaViewModel()
        viewModel.updateViaComplicatedLocalLambda()

        assertEquals(2, propertyValueChangeEvents.size)
    }

    @Test
    fun updateViaLocalLambdaReceiverTest() {
        val viewModel = LambdaViewModel()
        viewModel.updateViaLocalLambdaReceiver()

        assertEquals(2, propertyValueChangeEvents.size)
    }

    @Test
    fun updateViaComplicatedLocalLambdaReceiverTest() {
        val viewModel = LambdaViewModel()
        viewModel.updateViaComplicatedLocalLambdaReceiver()

        assertEquals(2, propertyValueChangeEvents.size)
    }
}
