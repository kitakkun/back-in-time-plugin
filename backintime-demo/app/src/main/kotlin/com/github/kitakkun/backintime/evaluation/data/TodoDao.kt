package com.github.kitakkun.backintime.evaluation.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo")
    fun getAll(): List<Todo>

    @Insert
    fun insertAll(vararg todos: Todo)

    @Query("UPDATE todo SET done = :done WHERE uuid = :uuid")
    fun updateStatus(uuid: String, done: Boolean)

    @Query("UPDATE todo SET label = :label WHERE uuid = :uuid")
    fun updateLabel(uuid: String, label: String)

    @Delete
    fun delete(todo: Todo)

    @Query("DELETE FROM todo WHERE uuid = :uuid")
    fun deleteByUuid(uuid: String)
}
