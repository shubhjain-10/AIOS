package com.example.aios.ai

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.aios.voice.SpeechEngine
import android.util.Log
import com.example.aios.data.memory.MemoryDatabase
import com.example.aios.data.memory.MemoryDao
import com.example.aios.data.memory.MemoryEntity
import kotlinx.coroutines.*
import com.example.aios.ai.MemoryIntentParser
class AIService : Service() {

    private lateinit var speechEngine: SpeechEngine
    private lateinit var database: MemoryDatabase
    private lateinit var memoryDao: MemoryDao

    override fun onCreate() {
        super.onCreate()
        Log.d("AI_OS","AIService Created")
        startForegroundService()
        database = MemoryDatabase.getDatabase(this)
        memoryDao = database.memoryDao()

        // Initialize speech engine
        speechEngine = SpeechEngine(this) { spokenText ->

            Log.d("AI_OS", "User said: $spokenText")

            when {
                MemoryIntentParser.shouldRemember(spokenText) -> {

                    val memory = MemoryIntentParser.extractMemory(spokenText)

                    CoroutineScope(Dispatchers.IO).launch {
                        memoryDao.insert(
                            MemoryEntity(content = memory)
                        )
                        Log.d("AI_OS", "Stored memory: $memory")
                    }
                }

                MemoryIntentParser.shouldForget(spokenText) -> {

                    val keyword = MemoryIntentParser.extractForgetKeyword(spokenText)

                    CoroutineScope(Dispatchers.IO).launch {
                        memoryDao.deleteByKeyword("%$keyword%")
                        Log.d("AI_OS", "Deleted memory containing: $keyword")
                    }
                }

                else -> {
                    Log.d("AI_OS", "No memory action triggered")
                }
            }
        }

        // Start listening
        //speechEngine.startListening()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.action == "START_LISTENING") {
            Log.d("AI_OS", "Listening triggered")
            speechEngine.startListening()
        }

        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "AIOS_CHANNEL"

        val channel = NotificationChannel(
            channelId,
            "AIOS Service",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("AI OS Running")
            .setContentText("Your AI is active")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}