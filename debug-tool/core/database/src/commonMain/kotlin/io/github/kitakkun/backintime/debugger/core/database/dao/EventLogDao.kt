package io.github.kitakkun.backintime.debugger.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.kitakkun.backintime.debugger.core.database.model.EventLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventLogDao {
    @Insert
    suspend fun insert(eventLogEntity: EventLogEntity)

    @Query("SELECT * FROM event_log WHERE sessionId=:sessionId")
    fun selectAsFlow(sessionId: String): Flow<List<EventLogEntity>>

    @Query("DELETE FROM event_log WHERE sessionId=:sessionId")
    suspend fun deleteAll(sessionId: String)
}
