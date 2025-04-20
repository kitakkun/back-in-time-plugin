package com.kitakkun.backintime.feature.settings

import kotlin.io.path.createFile
import kotlin.io.path.createTempDirectory
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertEquals

class DatabasePathValidatorTest {
    @Test
    fun testValidPath() {
        val validator = DatabasePathValidator()

        val tempDir = createTempDirectory()
        val testFile = tempDir.resolve("test.db")

        assertEquals(PathValidationResult.Valid, validator.validate(testFile.pathString))
    }

    @Test
    fun testInvalidPath() {
        val validator = DatabasePathValidator()
        assertEquals(PathValidationResult.Invalid, validator.validate("/"))
    }

    @Test
    fun testAlreadyExistsPath() {
        val validator = DatabasePathValidator()

        val tempDir = createTempDirectory()
        val testFile = tempDir.resolve("test.db")
        testFile.createFile()

        assertEquals(PathValidationResult.AlreadyExists, validator.validate(testFile.pathString))
    }
}