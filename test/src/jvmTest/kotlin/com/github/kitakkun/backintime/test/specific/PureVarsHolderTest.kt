package com.github.kitakkun.backintime.test.specific

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PureVarsHolderTest {
    @DebuggableStateHolder
    private class PureVarsHolder {
        var string: String = "string value"
        var int: Int = 0
        var long: Long = 0L
        var float: Float = 0f
        var double: Double = 0.0
        var boolean: Boolean = false
        var char: Char = 'a'
        var short: Short = 0
        var byte: Byte = 0
        var nullableString: String? = null
        var nullableInt: Int? = null
        var nullableLong: Long? = null
        var nullableFloat: Float? = null
        var nullableDouble: Double? = null
        var nullableBoolean: Boolean? = null
        var nullableChar: Char? = null
        var nullableShort: Short? = null
        var nullableByte: Byte? = null
        var stringList: List<String> = listOf("string value")
        var intList: List<Int> = listOf(0)
        var longList: List<Long> = listOf(0L)
        var floatList: List<Float> = listOf(0f)
        var doubleList: List<Double> = listOf(0.0)
        var booleanList: List<Boolean> = listOf(false)
    }

    @Test
    fun serializeNonNullFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals("\"hogehoge\"", holder.serializeValue("string", "hogehoge"))
        assertEquals("1000", holder.serializeValue("int", 1000))
        assertEquals("10000000000", holder.serializeValue("long", 10000000000L))
        assertEquals("0.1", holder.serializeValue("float", 0.1f))
        assertEquals("0.001", holder.serializeValue("double", 0.001))
        assertEquals("true", holder.serializeValue("boolean", true))
        assertEquals("\"b\"", holder.serializeValue("char", 'b'))
        assertEquals("100", holder.serializeValue("short", 100.toShort()))
        assertEquals("100", holder.serializeValue("byte", 100.toByte()))
    }

    @Test
    fun serializeNonNullCollectionFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals("[\"string value\"]", holder.serializeValue("stringList", listOf("string value")))
        assertEquals("[0]", holder.serializeValue("intList", listOf(0)))
        assertEquals("[0]", holder.serializeValue("longList", listOf(0L)))
        assertEquals("[0.0]", holder.serializeValue("floatList", listOf(0f)))
        assertEquals("[0.0]", holder.serializeValue("doubleList", listOf(0.0)))
        assertEquals("[false]", holder.serializeValue("booleanList", listOf(false)))
    }

    @Test
    fun serializeNullableFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals("null", holder.serializeValue("nullableString", null))
        assertEquals("null", holder.serializeValue("nullableInt", null))
        assertEquals("null", holder.serializeValue("nullableLong", null))
        assertEquals("null", holder.serializeValue("nullableFloat", null))
        assertEquals("null", holder.serializeValue("nullableDouble", null))
        assertEquals("null", holder.serializeValue("nullableBoolean", null))
        assertEquals("null", holder.serializeValue("nullableChar", null))
        assertEquals("null", holder.serializeValue("nullableShort", null))
        assertEquals("null", holder.serializeValue("nullableByte", null))
    }

    @Test
    fun deserializeNonNullFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals("hogehoge", holder.deserializeValue("string", "\"hogehoge\""))
        assertEquals(0, holder.deserializeValue("int", "0"))
        assertEquals(0L, holder.deserializeValue("long", "0"))
        assertEquals(0f, holder.deserializeValue("float", "0.0"))
        assertEquals(0.0, holder.deserializeValue("double", "0.0"))
        assertEquals(false, holder.deserializeValue("boolean", "false"))
        assertEquals('a', holder.deserializeValue("char", "\"a\""))
        assertEquals(0.toShort(), holder.deserializeValue("short", "0"))
        assertEquals(0.toByte(), holder.deserializeValue("byte", "0"))
    }

    @Test
    fun deserializeNonNullCollectionFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)
        assertEquals(listOf("string value"), holder.deserializeValue("stringList", "[\"string value\"]"))
        assertEquals(listOf(0), holder.deserializeValue("intList", "[0]"))
        assertEquals(listOf(0L), holder.deserializeValue("longList", "[0]"))
        assertEquals(listOf(0f), holder.deserializeValue("floatList", "[0.0]"))
        assertEquals(listOf(0.0), holder.deserializeValue("doubleList", "[0.0]"))
        assertEquals(listOf(false), holder.deserializeValue("booleanList", "[false]"))
    }

    @Test
    fun deserializeNullableFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)
        assertEquals(null, holder.deserializeValue("nullableString", "null"))
        assertEquals(null, holder.deserializeValue("nullableInt", "null"))
        assertEquals(null, holder.deserializeValue("nullableLong", "null"))
        assertEquals(null, holder.deserializeValue("nullableFloat", "null"))
        assertEquals(null, holder.deserializeValue("nullableDouble", "null"))
        assertEquals(null, holder.deserializeValue("nullableBoolean", "null"))
        assertEquals(null, holder.deserializeValue("nullableChar", "null"))
        assertEquals(null, holder.deserializeValue("nullableShort", "null"))
        assertEquals(null, holder.deserializeValue("nullableByte", "null"))
    }

    @Test
    fun forceSet() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.forceSetValue("string", "hogehoge")
        assertEquals("hogehoge", holder.string)
        holder.forceSetValue("int", 0)
        assertEquals(0, holder.int)
        holder.forceSetValue("long", 0L)
        assertEquals(0L, holder.long)
        holder.forceSetValue("float", 0f)
        assertEquals(0f, holder.float)
        holder.forceSetValue("double", 0.0)
        assertEquals(0.0, holder.double)
        holder.forceSetValue("boolean", false)
        assertEquals(false, holder.boolean)
        holder.forceSetValue("char", 'a')
        assertEquals('a', holder.char)
        holder.forceSetValue("short", 0.toShort())
        assertEquals(0.toShort(), holder.short)
        holder.forceSetValue("byte", 0.toByte())
        assertEquals(0.toByte(), holder.byte)
        holder.forceSetValue("nullableString", null)
        assertEquals(null, holder.nullableString)
        holder.forceSetValue("nullableInt", null)
        assertEquals(null, holder.nullableInt)
        holder.forceSetValue("nullableLong", null)
        assertEquals(null, holder.nullableLong)
        holder.forceSetValue("nullableFloat", null)
        assertEquals(null, holder.nullableFloat)
        holder.forceSetValue("nullableDouble", null)
        assertEquals(null, holder.nullableDouble)
        holder.forceSetValue("nullableBoolean", null)
        assertEquals(null, holder.nullableBoolean)
        holder.forceSetValue("nullableChar", null)
        assertEquals(null, holder.nullableChar)
        holder.forceSetValue("nullableShort", null)
        assertEquals(null, holder.nullableShort)
        holder.forceSetValue("nullableByte", null)
        assertEquals(null, holder.nullableByte)
    }
}
