package com.example.aios.storage

import androidx.room.*

@Dao
interface AimDao {

    @Insert
    suspend fun insert(aim: Aim)

    @Query("SELECT * FROM aims")
    suspend fun getAll(): List<Aim>

    @Update
    suspend fun update(aim: Aim)

    @Delete
    suspend fun delete(aim: Aim)
}