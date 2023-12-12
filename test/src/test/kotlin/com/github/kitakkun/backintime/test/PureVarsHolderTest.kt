package com.github.kitakkun.backintime.test

import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

class PureVarsHolderTest {
    private val pureVarsHolder = PureVarsHolder()

    @Test
    fun serializeNonNullFields() {
        assertEquals("\"hogehoge\"", pureVarsHolder.serializeValue("string", "hogehoge"))
        assertEquals("1000", pureVarsHolder.serializeValue("int", 1000))
        assertEquals("10000000000", pureVarsHolder.serializeValue("long", 10000000000L))
        assertEquals("0.1", pureVarsHolder.serializeValue("float", 0.1f))
        assertEquals("0.001", pureVarsHolder.serializeValue("double", 0.001))
        assertEquals("true", pureVarsHolder.serializeValue("boolean", true))
        assertEquals("\"b\"", pureVarsHolder.serializeValue("char", 'b'))
        assertEquals("100", pureVarsHolder.serializeValue("short", 100.toShort()))
        assertEquals("100", pureVarsHolder.serializeValue("byte", 100.toByte()))
    }

    @Test
    fun serializeNonNullCollectionFields() {
        assertEquals("[\"string value\"]", pureVarsHolder.serializeValue("stringList", listOf("string value")))
        assertEquals("[0]", pureVarsHolder.serializeValue("intList", listOf(0)))
        assertEquals("[0]", pureVarsHolder.serializeValue("longList", listOf(0L)))
        assertEquals("[0.0]", pureVarsHolder.serializeValue("floatList", listOf(0f)))
        assertEquals("[0.0]", pureVarsHolder.serializeValue("doubleList", listOf(0.0)))
        assertEquals("[false]", pureVarsHolder.serializeValue("booleanList", listOf(false)))
    }

    @Test
    fun serializeNullableFields() {
        assertEquals("null", pureVarsHolder.serializeValue("nullableString", null))
        assertEquals("null", pureVarsHolder.serializeValue("nullableInt", null))
        assertEquals("null", pureVarsHolder.serializeValue("nullableLong", null))
        assertEquals("null", pureVarsHolder.serializeValue("nullableFloat", null))
        assertEquals("null", pureVarsHolder.serializeValue("nullableDouble", null))
        assertEquals("null", pureVarsHolder.serializeValue("nullableBoolean", null))
        assertEquals("null", pureVarsHolder.serializeValue("nullableChar", null))
        assertEquals("null", pureVarsHolder.serializeValue("nullableShort", null))
        assertEquals("null", pureVarsHolder.serializeValue("nullableByte", null))
    }

    @Test
    fun deserializeNonNullFields() {
        assertEquals("hogehoge", pureVarsHolder.deserializeValue("string", "\"hogehoge\""))
        assertEquals(0, pureVarsHolder.deserializeValue("int", "0"))
        assertEquals(0L, pureVarsHolder.deserializeValue("long", "0"))
        assertEquals(0f, pureVarsHolder.deserializeValue("float", "0.0"))
        assertEquals(0.0, pureVarsHolder.deserializeValue("double", "0.0"))
        assertEquals(false, pureVarsHolder.deserializeValue("boolean", "false"))
        assertEquals('a', pureVarsHolder.deserializeValue("char", "\"a\""))
        assertEquals(0.toShort(), pureVarsHolder.deserializeValue("short", "0"))
        assertEquals(0.toByte(), pureVarsHolder.deserializeValue("byte", "0"))
    }

    @Test
    fun deserializeNonNullCollectionFields() {
        assertEquals(listOf("string value"), pureVarsHolder.deserializeValue("stringList", "[\"string value\"]"))
        assertEquals(listOf(0), pureVarsHolder.deserializeValue("intList", "[0]"))
        assertEquals(listOf(0L), pureVarsHolder.deserializeValue("longList", "[0]"))
        assertEquals(listOf(0f), pureVarsHolder.deserializeValue("floatList", "[0.0]"))
        assertEquals(listOf(0.0), pureVarsHolder.deserializeValue("doubleList", "[0.0]"))
        assertEquals(listOf(false), pureVarsHolder.deserializeValue("booleanList", "[false]"))
    }

    @Test
    fun deserializeNullableFields() {
        assertEquals(null, pureVarsHolder.deserializeValue("nullableString", "null"))
        assertEquals(null, pureVarsHolder.deserializeValue("nullableInt", "null"))
        assertEquals(null, pureVarsHolder.deserializeValue("nullableLong", "null"))
        assertEquals(null, pureVarsHolder.deserializeValue("nullableFloat", "null"))
        assertEquals(null, pureVarsHolder.deserializeValue("nullableDouble", "null"))
        assertEquals(null, pureVarsHolder.deserializeValue("nullableBoolean", "null"))
        assertEquals(null, pureVarsHolder.deserializeValue("nullableChar", "null"))
        assertEquals(null, pureVarsHolder.deserializeValue("nullableShort", "null"))
        assertEquals(null, pureVarsHolder.deserializeValue("nullableByte", "null"))
    }

    @Test
    fun forceSet() {
        pureVarsHolder.forceSetValue("string", "hogehoge")
        assertEquals("hogehoge", pureVarsHolder.string)
        pureVarsHolder.forceSetValue("int", 0)
        assertEquals(0, pureVarsHolder.int)
        pureVarsHolder.forceSetValue("long", 0L)
        assertEquals(0L, pureVarsHolder.long)
        pureVarsHolder.forceSetValue("float", 0f)
        assertEquals(0f, pureVarsHolder.float)
        pureVarsHolder.forceSetValue("double", 0.0)
        assertEquals(0.0, pureVarsHolder.double)
        pureVarsHolder.forceSetValue("boolean", false)
        assertEquals(false, pureVarsHolder.boolean)
        pureVarsHolder.forceSetValue("char", 'a')
        assertEquals('a', pureVarsHolder.char)
        pureVarsHolder.forceSetValue("short", 0.toShort())
        assertEquals(0.toShort(), pureVarsHolder.short)
        pureVarsHolder.forceSetValue("byte", 0.toByte())
        assertEquals(0.toByte(), pureVarsHolder.byte)
        pureVarsHolder.forceSetValue("nullableString", null)
        assertEquals(null, pureVarsHolder.nullableString)
        pureVarsHolder.forceSetValue("nullableInt", null)
        assertEquals(null, pureVarsHolder.nullableInt)
        pureVarsHolder.forceSetValue("nullableLong", null)
        assertEquals(null, pureVarsHolder.nullableLong)
        pureVarsHolder.forceSetValue("nullableFloat", null)
        assertEquals(null, pureVarsHolder.nullableFloat)
        pureVarsHolder.forceSetValue("nullableDouble", null)
        assertEquals(null, pureVarsHolder.nullableDouble)
        pureVarsHolder.forceSetValue("nullableBoolean", null)
        assertEquals(null, pureVarsHolder.nullableBoolean)
        pureVarsHolder.forceSetValue("nullableChar", null)
        assertEquals(null, pureVarsHolder.nullableChar)
        pureVarsHolder.forceSetValue("nullableShort", null)
        assertEquals(null, pureVarsHolder.nullableShort)
        pureVarsHolder.forceSetValue("nullableByte", null)
        assertEquals(null, pureVarsHolder.nullableByte)
    }
}
