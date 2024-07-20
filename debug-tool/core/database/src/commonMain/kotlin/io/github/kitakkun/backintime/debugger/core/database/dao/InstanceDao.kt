package io.github.kitakkun.backintime.debugger.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.kitakkun.backintime.debugger.core.database.model.InstanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstanceDao {
    @Insert
    suspend fun insert(instanceEntity: InstanceEntity)

    @Query("SELECT * FROM instanceEntity WHERE sessionId=:sessionId AND id=:id")
    suspend fun select(sessionId: String, id: String): InstanceEntity?

    @Query("UPDATE instanceEntity SET isAlive=:isAlive WHERE sessionId=:sessionId AND id=:id")
    suspend fun updateAlive(sessionId: String, id: String, isAlive: Boolean)

    @Query("UPDATE instanceEntity SET className=:newClassName WHERE sessionId=:sessionId AND id=:id")
    suspend fun updateClassName(sessionId: String, id: String, newClassName: String)

    @Query("DELETE FROM instanceEntity WHERE sessionId=:sessionId")
    suspend fun deleteAll(sessionId: String)

    @Query("UPDATE instanceEntity SET referencingInstanceIds=:newReferencingInstanceIds WHERE sessionId=:sessionId AND id=:id")
    suspend fun updateReferencingInstanceIds(sessionId: String, id: String, newReferencingInstanceIds: List<String>)

    @Query("SELECT * FROM instanceEntity WHERE sessionId=:sessionId")
    fun getInstancesAsFlow(sessionId: String): Flow<List<InstanceEntity>>
}
