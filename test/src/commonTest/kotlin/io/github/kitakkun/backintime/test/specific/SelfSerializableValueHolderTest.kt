package io.github.kitakkun.backintime.test.specific

import io.github.kitakkun.backintime.annotations.BackInTime
import io.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SelfSerializableValueHolderTest {
    companion object {
        private const val CLASS_FQ_NAME = "io.github.kitakkun.backintime.test.specific.SelfSerializableValueHolderTest.TestStateHolder"
        private const val MUTABLE_LIST_PROPERTY_NAME = "mutableList"
    }

    @BackInTime
    private class TestStateHolder {
        val mutableList = mutableListOf<String>()
    }

    @Test
    fun serializeTest() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)

        val input = listOf("Hello")
        val serializedValue = holder.serializeValue(CLASS_FQ_NAME, MUTABLE_LIST_PROPERTY_NAME, input)
        assertEquals("[\"Hello\"]", serializedValue)
    }

    @Test
    fun deserializeTest() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)

        val input = "[\"Hello\"]"
        val deserializedValue = holder.deserializeValue(CLASS_FQ_NAME, MUTABLE_LIST_PROPERTY_NAME, input)
        assertEquals(listOf("Hello"), deserializedValue)
    }

    @Test
    fun forceSetTest() {
        val holder = TestStateHolder()
        assertIs<BackInTimeDebuggable>(holder)

        val input = listOf("Hello")
        holder.forceSetValue(CLASS_FQ_NAME, MUTABLE_LIST_PROPERTY_NAME, input)
        assertEquals(listOf("Hello"), holder.mutableList)
    }
}
