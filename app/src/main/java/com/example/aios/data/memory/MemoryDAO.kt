package com.example.aios.data.memory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MemoryDao {

    @Insert
    suspend fun insert(memory: MemoryEntity)

    @Query("SELECT * FROM long_term_memory")
    suspend fun getAll(): List<MemoryEntity>

    @Query("DELETE FROM long_term_memory WHERE content LIKE :keyword")
    suspend fun deleteByKeyword(keyword: String)

    @Query("DELETE FROM long_term_memory")
    suspend fun clearAll()
}