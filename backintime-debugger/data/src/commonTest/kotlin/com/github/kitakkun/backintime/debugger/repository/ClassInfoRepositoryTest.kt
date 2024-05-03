package com.github.kitakkun.backintime.debugger.repository

import com.github.kitakkun.backintime.debugger.data.repository.ClassInfoRepository
import com.github.kitakkun.backintime.debugger.database.ClassInfo
import kotlinx.coroutines.test.runTest
import org.koin.test.inject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ClassInfoRepositoryTest : BackInTimeDebuggerDataTest() {
    private val classInfoRepository: ClassInfoRepository by inject()

    @Test
    fun testInsert() = runTest {
        classInfoRepository.insert(
            sessionId = "session_id",
            className = "com.example.Class",
            superClassName = "com.exmaple.SuperClass",
            properties = listOf(),
        )
    }

    @Test
    fun testInsertAndSelect() = runTest {
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
                id = 1,
                className = "com.example.Class",
                superClassName = "com.exmaple.SuperClass",
                properties = listOf(),
                sessionId = "session_id",
            ),
        )
    }

    @Test
    fun testInsertTwice() = runTest {
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
        // class info is finalized at compile time, and it shouldn't be updated during the same session
        assertEquals(
            actual = data,
            expected = ClassInfo(
                id = 1,
                className = "com.example.Class",
                superClassName = "com.exmaple.SuperClass",
                properties = listOf(),
                sessionId = "session_id",
            ),
        )
    }
}
