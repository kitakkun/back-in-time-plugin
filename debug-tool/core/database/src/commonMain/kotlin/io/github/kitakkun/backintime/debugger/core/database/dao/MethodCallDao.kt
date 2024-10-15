package io.github.kitakkun.backintime.debugger.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.kitakkun.backintime.debugger.core.database.model.MethodCallEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MethodCallDao {
    @Insert
    suspend fun insert(methodInvocationEntity: MethodCallEntity)

    @Query("SELECT * fROM method_call WHERE sessionId=:sessionId AND instanceId=:instanceId")
    fun selectAsFlow(sessionId: String, instanceId: String): Flow<List<MethodCallEntity>>

    @Query("SELECT * fROM method_call WHERE sessionId=:sessionId AND instanceId=:instanceId AND id=:callId")
    suspend fun select(sessionId: String, instanceId: String, callId: String): MethodCallEntity?
}
