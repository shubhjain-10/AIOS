package com.example.aios.data.memory

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "long_term_memory")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)