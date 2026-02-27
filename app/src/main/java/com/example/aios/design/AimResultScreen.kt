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


class AimResultScreen(
    private val context: Context,
    private val goal: String,
    private val timeline: String,
    private val onBack: () -> Unit
) {

    fun createView(): View {

        // Root ScrollView
        val scrollView = ScrollView(context)
        scrollView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Inner container
        val container = LinearLayout(context)
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(60, 40, 60, 40)
        container.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        scrollView.addView(container)

        // Title
        val title = TextView(context)
        title.text = "Feasibility Result"
        title.setTextColor(Color.WHITE)
        title.textSize = 22f
        title.setTypeface(null, Typeface.BOLD)
        title.setPadding(0, 0, 0, 40)

        container.addView(title)

        // Result Text
        val resultText = TextView(context)
        resultText.setTextColor(Color.WHITE)
        resultText.textSize = 16f
        resultText.text = "Analyzing with AI..."

        container.addView(resultText)

        CoroutineScope(Dispatchers.Main).launch {

            val result = withContext(Dispatchers.IO) {
                analyzeGoal(goal, timeline)
            }

            resultText.text = result
        }

        // Spacing
        val space = Space(context)
        space.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            60
        )
        container.addView(space)

        // Back Button
        val backButton = Button(context)
        backButton.text = "Modify Goal"
        backButton.setTextColor(Color.WHITE)

        val bg = GradientDrawable().apply {
            cornerRadius = 40f
            setColor(Color.parseColor("#1F6BFF"))
        }

        backButton.background = bg
        backButton.setPadding(40, 25, 40, 25)

        backButton.setOnClickListener {
            onBack()
        }

        container.addView(backButton)

        return scrollView
    }
}