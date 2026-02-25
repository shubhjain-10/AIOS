package com.example.aios

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.aios.ai.AIService
import com.example.aios.data.memory.MemoryDao
import com.example.aios.ui.HomeScreen
import com.example.aios.data.memory.MemoryDatabase

class MainActivity : ComponentActivity() {

    private val requestMic = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fullscreen immersive
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val database = MemoryDatabase.getDatabase(this)
        val memoryDao = database.memoryDao()

        val homeScreen = HomeScreen(
            context = this,
            memoryDao = memoryDao
        ) {
            startListening()
        }

        setContentView(homeScreen.createView())

        checkPermissions()
    }

    private fun startListening() {
        val intent = Intent(this, AIService::class.java)
        intent.action = "START_LISTENING"
        startService(intent)
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                requestMic
            )
        } else {
            startAIService()
        }
    }

    private fun startAIService() {
        val serviceIntent = Intent(this, AIService::class.java)
        startForegroundService(serviceIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestMic) {
            startAIService()
        }
    }
}