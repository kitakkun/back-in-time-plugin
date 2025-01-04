package com.kitakkun.backintime.test.specific

import com.kitakkun.backintime.core.annotations.BackInTime
import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SelfSerializableValueHolderTest {
    @BackInTime
    private class TestStateHolder {
        val mutableList = mutableListOf<String>()
    }

    @Test
    fun forceSetTest() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.forceSetValue("com/kitakkun/backintime/test/specific/SelfSerializableValueHolderTest.TestStateHolder.mutableList", "[\"Hello\"]")
        assertEquals(listOf("Hello"), holder.mutableList)
    }
}
