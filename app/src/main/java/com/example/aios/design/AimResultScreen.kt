package com.example.aios.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.coroutines.*
import com.example.aios.ai.analyzeGoal
import com.example.aios.storage.Aim
import com.example.aios.data.memory.MemoryDatabase



class AimResultScreen(
    private val context: Context,
    private val goal: String,
    private val timeline: String,
    private val onBack: () -> Unit,
    private val openScreen: (View) -> Unit   // 👈 NEW
) {

    fun createView(): View {

        val scrollView = ScrollView(context)
        scrollView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 40, 60, 40)
        }

        scrollView.addView(container)

        // ===== Title =====
        val title = TextView(context).apply {
            text = "Feasibility Result"
            textSize = 22f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 40)
        }

        container.addView(title)

        // ===== AI Result Text =====
        val resultText = TextView(context).apply {
            textSize = 16f
            setTextColor(Color.WHITE)
            text = "Analyzing with AI..."
        }

        container.addView(resultText)

        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                analyzeGoal(goal, timeline)
            }
            resultText.text = result
        }

        addSpace(container, 60)

        // ===== Save Aim Button =====
        val saveButton = Button(context).apply {
            text = "SAVE AIM"
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            background = GradientDrawable().apply {
                cornerRadius = 60f
                setColor(Color.parseColor("#2F6BFF"))
            }
            setPadding(40, 30, 40, 30)
        }

        container.addView(saveButton)

        addSpace(container, 30)

        // ===== Modify Button =====
        val backButton = Button(context).apply {
            text = "MODIFY GOAL"
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                cornerRadius = 60f
                setColor(Color.parseColor("#444444"))
            }
            setPadding(40, 30, 40, 30)
            setOnClickListener { onBack() }
        }

        container.addView(backButton)

        // ===== Save Logic =====
        saveButton.setOnClickListener {

            val titleText = goal.lines().firstOrNull() ?: goal

            val newAim = Aim(
                title = titleText,
                description = goal,
                deadline = timeline
            )

            val db = MemoryDatabase.getDatabase(context)
            val aimDao = db.aimDao()

            CoroutineScope(Dispatchers.IO).launch {

                aimDao.insert(newAim)

                withContext(Dispatchers.Main) {

                    Toast.makeText(context, "Aim Saved!", Toast.LENGTH_SHORT).show()

                    val progressScreen = ProgressScreen(
                        context,
                        onBack = { onBack() },
                        openDetail = { openScreen(it) }
                    )

                    openScreen(progressScreen.createView())
                }
            }
        }

        return scrollView
    }

    private fun addSpace(layout: LinearLayout, height: Int) {
        val space = Space(context)
        space.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
        )
        layout.addView(space)
    }
}