package com.example.aios.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.aios.storage.Aim
import kotlinx.coroutines.*
import com.example.aios.ai.analyzeProgress
import com.example.aios.data.memory.MemoryDatabase
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
class ProgressDetailScreen(
    private val context: Context,
    private val aim: Aim,
    private val onBack: () -> Unit
) {

    fun createView(): View {

        val scrollView = ScrollView(context)

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 50, 60, 50)
        }
        scrollView.addView(container)


        val title = TextView(context).apply {
            text = aim.title
            textSize = 22f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
        }

        val goalNumber = Regex("(\\d+)").find(aim.description)?.groupValues?.get(1)?.toInt() ?: 100

        val deadlineDays = Regex("(\\d+)").find(aim.deadline)?.groupValues?.get(1)?.toInt() ?: 30

        val daysPassed = ((System.currentTimeMillis() - aim.createdDate) / (1000 * 60 * 60 * 24)).toInt()

        val daysRemaining = (deadlineDays - daysPassed).coerceAtLeast(0)

        val expectedSolved = (goalNumber.toFloat() / deadlineDays * daysPassed).toInt()

        val paceStatus = when {
            aim.solvedCount > expectedSolved -> "Ahead of schedule 🟢"
            aim.solvedCount == expectedSolved -> "On track 🟡"
            else -> "Behind schedule 🔴"
        }

        container.addView(title)

        val progressBar = ProgressBar(
            context,
            null,
            android.R.attr.progressBarStyleHorizontal
        ).apply {
            max = 100
            progress = aim.progress
        }

        container.addView(progressBar)

        val percentText = TextView(context).apply {
            text = "Progress: ${aim.progress}%"
            setTextColor(Color.WHITE)
        }

        val solvedText = TextView(context).apply {
            text = "Solved: ${aim.solvedCount}"
            setTextColor(Color.LTGRAY)
        }

        container.addView(solvedText)

        container.addView(percentText)

        val remainingText = TextView(context).apply {
            text = "$daysRemaining days remaining"
            setTextColor(Color.LTGRAY)
        }

        container.addView(remainingText)

        val feedbackText = TextView(context).apply {
            textSize = 16f
            setTextColor(Color.CYAN)
            setPadding(0,20,0,20)
        }

        container.addView(feedbackText)

        val progressInput = EditText(context).apply {
            hint = "Update progress..."
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
        }

        val paceIndicator = TextView(context).apply {
            text = "Status: $paceStatus"
            setTextColor(Color.YELLOW)
            textSize = 16f
            setPadding(0, 20, 0, 20)
        }

        container.addView(paceIndicator)

        container.addView(progressInput)

        val updateBtn = Button(context).apply {
            text = "Update"

            setOnClickListener {

                CoroutineScope(Dispatchers.Main).launch {

                    val userInput = progressInput.text.toString()

                    if (userInput.isBlank()) {
                        Toast.makeText(context, "Enter progress update", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val solvedNumber = Regex("(\\d+)").find(userInput)?.groupValues?.get(1)?.toInt()

                    if (solvedNumber != null) {
                        aim.solvedCount = solvedNumber
                    }

                    val note = userInput

                    val result = withContext(Dispatchers.IO) {
                        analyzeProgress(aim,note)
                    }

                    val percent = result.progress

                    val message = result.feedback

                    aim.progress = percent

                    val db = MemoryDatabase.getDatabase(context)
                    val aimDao = db.aimDao()

                    CoroutineScope(Dispatchers.IO).launch {
                        aimDao.update(aim)
                    }
                    progressBar.progress = percent
                    percentText.text = "Progress: $percent%"
                    feedbackText.text = message

                    val newDaysPassed = ((System.currentTimeMillis() - aim.createdDate) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)
                    val newDaysRemaining = (deadlineDays - newDaysPassed).coerceAtLeast(0)

                    remainingText.text = "$newDaysRemaining days remaining"

                }
            }
        }

        container.addView(updateBtn)

        val backBtn = Button(context).apply {
            text = "Back"
            setOnClickListener { onBack() }
        }

        container.addView(backBtn)

        val timelineTitle = TextView(context).apply {
            text = "Progress Timeline"
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(0,30,0,10)
        }

        container.addView(timelineTitle)

        val timelineText = TextView(context).apply {
            text =
                "Start: Day 0\n" +
                        "Today: Day $daysPassed\n" +
                        "Solved: ${aim.solvedCount} / $goalNumber\n"
            setTextColor(Color.LTGRAY)
        }

        container.addView(timelineText)


        val summary = TextView(context).apply {
            text =
                "Goal: ${aim.description}\n" +
                        "Deadline: ${aim.deadline}"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 20, 0, 30)
        }

        container.addView(summary)


        return scrollView
    }
}