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
class ProgressDetailScreen(
    private val context: Context,
    private val aim: Aim,
    private val onBack: () -> Unit
) {

    fun createView(): View {

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 50, 60, 50)
        }

        val title = TextView(context).apply {
            text = aim.title
            textSize = 22f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
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

        container.addView(percentText)

        val input = EditText(context).apply {
            hint = "Enter progress... "
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
        }

        container.addView(input)

        val updateBtn = Button(context).apply {
            text = "Update"

            setOnClickListener {

                CoroutineScope(Dispatchers.Main).launch {

                    val text = input.text.toString()

                    if (text.isBlank()) {
                        Toast.makeText(context, "Describe your progress", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val percent = withContext(Dispatchers.IO) {
                        analyzeProgress(text,aim)
                    }

                    aim.progress = percent
                    progressBar.progress = percent
                    percentText.text = "Progress: $percent%"
                }
            }
        }

        container.addView(updateBtn)

        val backBtn = Button(context).apply {
            text = "Back"
            setOnClickListener { onBack() }
        }

        container.addView(backBtn)

        val summary = TextView(context).apply {
            text = "Aim: ${aim.description}\nDeadline: ${aim.deadline}"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 20, 0, 30)
        }

        container.addView(summary)

        return container
    }
}