package com.github.kitakkun.backintime

import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyName
import com.github.kitakkun.backintime.compiler.backend.utils.isGetterName
import com.github.kitakkun.backintime.compiler.backend.utils.isSetterName
import org.jetbrains.kotlin.name.Name
import org.junit.Test
import kotlin.test.assertEquals

class NameUtilsTest {
    @Test
    fun getterNameTest() {
        val name = Name.guessByFirstCharacter("<get-value>")
        assertEquals("value", name.getPropertyName())
    }

    @Test
    fun setterNameTest() {
        val name = Name.guessByFirstCharacter("<set-value>")
        assertEquals("value", name.getPropertyName())
    }

    @Test
    fun normalNameTest() {
        val name = Name.guessByFirstCharacter("hogeMethod")
        assertEquals("hogeMethod", name.getPropertyName())
    }

    @Test
    fun isSetterNameTest() {
        val name = Name.guessByFirstCharacter("<set-value>")
        assertEquals(true, name.isSetterName())
    }

    @Test
    fun isGetterNameTest() {
        val name = Name.guessByFirstCharacter("<get-value>")
        assertEquals(true, name.isGetterName())
    }

    @Test
    fun isNotSetterNameTest() {
        val name = Name.guessByFirstCharacter("hogeMethod")
        assertEquals(false, name.isSetterName())
    }

    @Test
    fun isNotGetterNameTest() {
        val name = Name.guessByFirstCharacter("hogeMethod")
        assertEquals(false, name.isGetterName())
    }
}
