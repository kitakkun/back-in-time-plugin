package io.github.kitakkun.backintime.debugger.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.kitakkun.backintime.debugger.core.database.model.SessionInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionInfoDao {
    @Insert
    suspend fun insert(info: SessionInfoEntity)

    @Query("SELECT * FROM session WHERE id=:id LIMIT 1")
    suspend fun select(id: String): SessionInfoEntity?

    @Query("SELECT * FROM session")
    fun selectAllAsFlow(): Flow<List<SessionInfoEntity>>

    @Query("SELECT * FROM session WHERE isActive = false")
    fun selectAllInactiveAsFlow(): Flow<List<SessionInfoEntity>>

    @Query("SELECT * FROM session WHERE isActive = true")
    fun selectAllActiveAsFlow(): Flow<List<SessionInfoEntity>>

    @Query("UPDATE session SET isActive=:isActive WHERE id=:sessionId")
    suspend fun updateIsActive(sessionId: String, isActive: Boolean)

    @Query("UPDATE session SET isActive = false")
    suspend fun markAllAsInactive()
}
