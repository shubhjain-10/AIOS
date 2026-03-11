package com.example.aios.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aims")
data class Aim(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val description: String,
    val deadline: String,

    var progress: Int = 0,

    var solvedCount: Int = 0,

    val createdDate: Long = System.currentTimeMillis()
)
