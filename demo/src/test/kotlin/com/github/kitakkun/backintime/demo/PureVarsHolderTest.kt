package com.github.kitakkun.backintime.demo

import org.junit.Test
import kotlin.test.assertEquals

class PureVarsHolderTest {
    private val pureVarsHolder = PureVarsHolder()

    @Test
    fun serialize() {
        assertEquals("\"hogehoge\"", pureVarsHolder.serializeValue("string", "hogehoge"))
        assertEquals("0", pureVarsHolder.serializeValue("int", 0))
        assertEquals("0", pureVarsHolder.serializeValue("long", 0L))
        assertEquals("0.0", pureVarsHolder.serializeValue("float", 0f))
        assertEquals("0.0", pureVarsHolder.serializeValue("double", 0.0))
        assertEquals("false", pureVarsHolder.serializeValue("boolean", false))
        assertEquals("\"a\"", pureVarsHolder.serializeValue("char", 'a'))
        assertEquals("0", pureVarsHolder.serializeValue("short", 0.toShort()))
        assertEquals("0", pureVarsHolder.serializeValue("byte", 0.toByte()))
    }

    @Test
    fun deserialize() {
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
    }
}
