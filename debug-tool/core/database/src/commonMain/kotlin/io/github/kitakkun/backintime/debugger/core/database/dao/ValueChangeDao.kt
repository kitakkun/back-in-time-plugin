package io.github.kitakkun.backintime.debugger.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.kitakkun.backintime.debugger.core.database.model.ValueChangeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ValueChangeDao {
    @Insert
    suspend fun insert(valueChangeEntity: ValueChangeEntity)

    @Query("SELECT * FROM value_change WHERE methodCallId = :callId AND sessionId = :sessionId AND instanceId = :instanceId")
    fun selectAsFlow(callId: String, instanceId: String, sessionId: String): Flow<List<ValueChangeEntity>>

    @Query(
        """
       SELECT * FROM value_change WHERE 
       propertyName = :propertyId AND 
       sessionId = :sessionId AND 
       instanceId = :instanceId
    """
    )
    fun selectByPropertyAsFlow(sessionId: String, instanceId: String, propertyId: String): Flow<List<ValueChangeEntity>>
}
