package com.github.kitakkun.backintime.debugger.repository

import com.github.kitakkun.backintime.debugger.data.repository.ClassInfoRepository
import com.github.kitakkun.backintime.debugger.database.ClassInfo
import org.koin.test.inject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ClassInfoRepositoryTest : BackInTimeDebuggerDataTest() {
    private val classInfoRepository: ClassInfoRepository by inject()

    @Test
    fun testInsert() {
        classInfoRepository.insert(
            sessionId = "session_id",
            className = "com.example.Class",
            superClassName = "com.exmaple.SuperClass",
            properties = listOf(),
        )
    }

    @Test
    fun testInsertAndSelect() {
        classInfoRepository.insert(
            sessionId = "session_id",
            className = "com.example.Class",
            superClassName = "com.exmaple.SuperClass",
            properties = listOf(),
        )
        val data = classInfoRepository.select(
            sessionId = "session_id",
            className = "com.example.Class",
        )
        assertNotNull(data)
        assertEquals(
            actual = data,
            expected = ClassInfo(
                id = "session_id/com.example.Class",
                className = "com.example.Class",
                superClassName = "com.exmaple.SuperClass",
                properties = listOf(),
                sessionId = "session_id",
            ),
        )
    }

    @Test
    fun testInsertTwice() {
        classInfoRepository.insert(
            sessionId = "session_id",
            className = "com.example.Class",
            superClassName = "com.exmaple.SuperClass",
            properties = listOf(),
        )
        classInfoRepository.insert(
            sessionId = "session_id",
            className = "com.example.Class",
            superClassName = "com.exmaple.SuperClass.Updated",
            properties = listOf(),
        )
        val data = classInfoRepository.select(
            sessionId = "session_id",
            className = "com.example.Class",
        )
        assertNotNull(data)
        assertEquals(
            actual = data,
            expected = ClassInfo(
                id = "session_id/com.example.Class",
                className = "com.example.Class",
                superClassName = "com.exmaple.SuperClass.Updated",
                properties = listOf(),
                sessionId = "session_id",
            ),
        )
    }
}
