package com.example.aios.data.memory

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aios.storage.Aim
import com.example.aios.storage.AimDao
@Database(
    entities = [MemoryEntity::class, Aim::class],
    version = 2
)
abstract class MemoryDatabase : RoomDatabase() {

    abstract fun memoryDao(): MemoryDao

    abstract fun aimDao(): AimDao

    companion object {

        @Volatile
        private var INSTANCE: MemoryDatabase? = null

        fun getDatabase(context: Context): MemoryDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemoryDatabase::class.java,
                    "ai_memory_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}