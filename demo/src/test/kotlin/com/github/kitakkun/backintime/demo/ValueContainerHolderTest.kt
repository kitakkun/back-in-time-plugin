package com.github.kitakkun.backintime.demo

import org.junit.Test
import kotlin.test.assertEquals

class ValueContainerHolderTest {
    val valueContainerHolder = ValueContainerHolder()

    @Test
    fun forceSet() {
        valueContainerHolder.forceSetValue("intContainer", 2525)
        assertEquals(2525, valueContainerHolder.intContainer.value)
        valueContainerHolder.forceSetValue("stringContainer", "Hoge")
        assertEquals("Hoge", valueContainerHolder.stringContainer.value)
    }
}
