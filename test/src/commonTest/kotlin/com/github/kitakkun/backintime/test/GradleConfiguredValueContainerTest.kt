package com.github.kitakkun.backintime.test

import junit.framework.TestCase.assertEquals
import kotlin.test.Test

class GradleConfiguredValueContainerTest {
    private val holder = GradleConfiguredValueContainerHolder()

    @Test
    fun forceSet() {
        holder.forceSetValue("intContainer", 2525)
        assertEquals(2525, holder.intContainer.value)
        holder.forceSetValue("stringContainer", "Hoge")
        assertEquals("Hoge", holder.stringContainer.value)
    }
}
