package com.kitakkun.backintime.test.specific

import com.kitakkun.backintime.core.annotations.BackInTime
import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PureVarsHolderTest {
    companion object {
        private const val HOLDER_CLASS_FQ_NAME = "com.kitakkun.backintime.test.specific.PureVarsHolderTest.PureVarsHolder"
    }

    @BackInTime
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

        assertEquals("\"hogehoge\"", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "string", "hogehoge"))
        assertEquals("1000", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "int", 1000))
        assertEquals("10000000000", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "long", 10000000000L))
        assertEquals("0.1", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "float", 0.1f))
        assertEquals("0.001", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "double", 0.001))
        assertEquals("true", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "boolean", true))
        assertEquals("\"b\"", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "char", 'b'))
        assertEquals("100", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "short", 100.toShort()))
        assertEquals("100", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "byte", 100.toByte()))
    }

    @Test
    fun serializeNonNullCollectionFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals("[\"string value\"]", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "stringList", listOf("string value")))
        assertEquals("[0]", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "intList", listOf(0)))
        assertEquals("[0]", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "longList", listOf(0L)))
        assertEquals("[0.0]", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "floatList", listOf(0f)))
        assertEquals("[0.0]", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "doubleList", listOf(0.0)))
        assertEquals("[false]", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "booleanList", listOf(false)))
    }

    @Test
    fun serializeNullableFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals("null", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "nullableString", null))
        assertEquals("null", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "nullableInt", null))
        assertEquals("null", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "nullableLong", null))
        assertEquals("null", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "nullableFloat", null))
        assertEquals("null", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "nullableDouble", null))
        assertEquals("null", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "nullableBoolean", null))
        assertEquals("null", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "nullableChar", null))
        assertEquals("null", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "nullableShort", null))
        assertEquals("null", holder.serializeValue(HOLDER_CLASS_FQ_NAME, "nullableByte", null))
    }

    @Test
    fun deserializeNonNullFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        assertEquals("hogehoge", holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "string", "\"hogehoge\""))
        assertEquals(0, holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "int", "0"))
        assertEquals(0L, holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "long", "0"))
        assertEquals(0f, holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "float", "0.0"))
        assertEquals(0.0, holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "double", "0.0"))
        assertEquals(false, holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "boolean", "false"))
        assertEquals('a', holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "char", "\"a\""))
        assertEquals(0.toShort(), holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "short", "0"))
        assertEquals(0.toByte(), holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "byte", "0"))
    }

    @Test
    fun deserializeNonNullCollectionFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)
        assertEquals(listOf("string value"), holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "stringList", "[\"string value\"]"))
        assertEquals(listOf(0), holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "intList", "[0]"))
        assertEquals(listOf(0L), holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "longList", "[0]"))
        assertEquals(listOf(0f), holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "floatList", "[0.0]"))
        assertEquals(listOf(0.0), holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "doubleList", "[0.0]"))
        assertEquals(listOf(false), holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "booleanList", "[false]"))
    }

    @Test
    fun deserializeNullableFields() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)
        assertEquals(null, holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "nullableString", "null"))
        assertEquals(null, holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "nullableInt", "null"))
        assertEquals(null, holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "nullableLong", "null"))
        assertEquals(null, holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "nullableFloat", "null"))
        assertEquals(null, holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "nullableDouble", "null"))
        assertEquals(null, holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "nullableBoolean", "null"))
        assertEquals(null, holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "nullableChar", "null"))
        assertEquals(null, holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "nullableShort", "null"))
        assertEquals(null, holder.deserializeValue(HOLDER_CLASS_FQ_NAME, "nullableByte", "null"))
    }

    @Test
    fun forceSet() {
        val holder = PureVarsHolder()
        assertIs<BackInTimeDebuggable>(holder)

        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "string", "hogehoge")
        assertEquals("hogehoge", holder.string)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "int", 0)
        assertEquals(0, holder.int)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "long", 0L)
        assertEquals(0L, holder.long)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "float", 0f)
        assertEquals(0f, holder.float)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "double", 0.0)
        assertEquals(0.0, holder.double)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "boolean", false)
        assertEquals(false, holder.boolean)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "char", 'a')
        assertEquals('a', holder.char)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "short", 0.toShort())
        assertEquals(0.toShort(), holder.short)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "byte", 0.toByte())
        assertEquals(0.toByte(), holder.byte)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "nullableString", null)
        assertEquals(null, holder.nullableString)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "nullableInt", null)
        assertEquals(null, holder.nullableInt)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "nullableLong", null)
        assertEquals(null, holder.nullableLong)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "nullableFloat", null)
        assertEquals(null, holder.nullableFloat)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "nullableDouble", null)
        assertEquals(null, holder.nullableDouble)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "nullableBoolean", null)
        assertEquals(null, holder.nullableBoolean)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "nullableChar", null)
        assertEquals(null, holder.nullableChar)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "nullableShort", null)
        assertEquals(null, holder.nullableShort)
        holder.forceSetValue(HOLDER_CLASS_FQ_NAME, "nullableByte", null)
        assertEquals(null, holder.nullableByte)
    }
}
