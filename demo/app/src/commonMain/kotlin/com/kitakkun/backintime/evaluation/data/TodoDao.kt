package com.kitakkun.backintime.evaluation.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo")
    suspend fun getAll(): List<Todo>

    @Insert
    suspend fun insertAll(vararg todos: Todo)

    @Query("UPDATE todo SET done = :done WHERE uuid = :uuid")
    suspend fun updateStatus(uuid: String, done: Boolean)

    @Query("UPDATE todo SET label = :label WHERE uuid = :uuid")
    suspend fun updateLabel(uuid: String, label: String)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("DELETE FROM todo WHERE uuid = :uuid")
    suspend fun deleteByUuid(uuid: String)
}
