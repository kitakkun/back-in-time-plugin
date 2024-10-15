package io.github.kitakkun.backintime.debugger.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.kitakkun.backintime.debugger.core.database.model.ClassInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassInfoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(classInfoEntity: ClassInfoEntity)

    @Query("SELECT * FROM class_info WHERE name=:name AND sessionId=:sessionId LIMIT 1")
    suspend fun select(sessionId: String, name: String): ClassInfoEntity?

    @Query("SELECT * FROM class_info WHERE name=:name AND sessionId=:sessionId LIMIT 1")
    fun selectAsFlow(sessionId: String, name: String): Flow<ClassInfoEntity?>
}
