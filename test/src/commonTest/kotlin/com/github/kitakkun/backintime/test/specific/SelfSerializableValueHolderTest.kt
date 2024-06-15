package com.github.kitakkun.backintime.test.specific

import com.github.kitakkun.backintime.annotations.BackInTime
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SelfSerializableValueHolderTest {
    @BackInTime
    private class TestStateHolder {
        val mutableList = mutableListOf<String>()
    }

    @Test
    fun serializeTest() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)

        val input = listOf("Hello")
        val serializedValue = holder.serializeValue("mutableList", input)
        assertEquals("[\"Hello\"]", serializedValue)
    }

    @Test
    fun deserializeTest() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)

        val input = "[\"Hello\"]"
        val deserializedValue = holder.deserializeValue("mutableList", input)
        assertEquals(listOf("Hello"), deserializedValue)
    }

    @Test
    fun forceSetTest() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)

        val input = listOf("Hello")
        holder.forceSetValue("mutableList", input)
        assertEquals(listOf("Hello"), holder.mutableList)
    }
}
