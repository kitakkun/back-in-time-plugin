package com.github.kitakkun.backintime.test

import org.junit.Test
import kotlin.test.assertEquals

@Suppress("UNRESOLVED_REFERENCE")
class InheritanceTest {
    @Test
    fun test() {
        val subClass = SubClass()
        subClass.forceSetValue("superProperty", "super(modified)")
        assertEquals("super(modified)", subClass.superProperty)
    }
}
