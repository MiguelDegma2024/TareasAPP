package com.example.tareasapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY created_at DESC")
    fun getAllTasksNewestFirst(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY created_at ASC")
    fun getAllTasksOldestFirst(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY title ASC")
    fun getAllTasksTitleAZ(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY title DESC")
    fun getAllTasksTitleZA(): Flow<List<TaskEntity>>

    @Insert
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("""
    SELECT * FROM tasks
    WHERE title LIKE '%' || :query || '%'
    ORDER BY created_at DESC
    """)
    fun searchTasks(query: String): Flow<List<TaskEntity>>

    @Query("""
    SELECT * FROM tasks
    WHERE title LIKE '%' || :query || '%'
    ORDER BY created_at ASC
    """)
    fun searchTasksOldestFirst(query: String): Flow<List<TaskEntity>>

    @Query("""
    SELECT * FROM tasks
    WHERE title LIKE '%' || :query || '%'
    ORDER BY title ASC
    """)
    fun searchTasksTitleAZ(query: String): Flow<List<TaskEntity>>

    @Query("""
    SELECT * FROM tasks
    WHERE title LIKE '%' || :query || '%'
    ORDER BY title DESC
    """)
    fun searchTasksTitleZA(query: String): Flow<List<TaskEntity>>
}