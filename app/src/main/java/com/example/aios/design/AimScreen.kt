package com.example.aios.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*


class AimScreen(
    private val context: Context,
    private val onAnalyzeComplete: (View) -> Unit
) {

    fun createView(): View {

        val container = LinearLayout(context)
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(60, 40, 60, 40)
        container.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // ----- Title -----
        val title = TextView(context)
        title.text = "Set Your Aim"
        title.setTextColor(Color.WHITE)
        title.textSize = 20f
        title.setTypeface(null, Typeface.BOLD)
        title.setPadding(0, 0, 0, 40)

        container.addView(title)

        // ----- Aim Input -----
        val aimInput = EditText(context)
        aimInput.hint = "Enter your goal (e.g. Solve 150 DSA problems)"
        aimInput.setHintTextColor(Color.GRAY)
        aimInput.setTextColor(Color.WHITE)
        aimInput.setPadding(40, 30, 40, 30)

        val aimBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor("#111111"))
            cornerRadius = 40f
            setStroke(3, Color.parseColor("#1F6BFF"))
        }

        aimInput.background = aimBackground

        container.addView(aimInput)

        // Spacing
        val space = Space(context)
        space.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            40
        )
        container.addView(space)

        // ----- Time Period Input -----
        val timeInput = EditText(context)
        timeInput.hint = "Enter time period (e.g. 30 days or 1 month)"
        timeInput.setHintTextColor(Color.GRAY)
        timeInput.setTextColor(Color.WHITE)
        timeInput.setPadding(40, 30, 40, 30)

        val timeBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor("#111111"))
            cornerRadius = 40f
            setStroke(3, Color.parseColor("#1F6BFF"))
        }

        timeInput.background = timeBackground

        container.addView(timeInput)

        // Spacing
        val space2 = Space(context)
        space2.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            60
        )
        container.addView(space2)

        // ----- Analyze Button -----
        val analyzeButton = Button(context)
        analyzeButton.text = "Analyze Feasibility"
        analyzeButton.setTextColor(Color.WHITE)

        val buttonBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 40f
            setColor(Color.parseColor("#1F6BFF"))
        }

        analyzeButton.background = buttonBackground
        analyzeButton.setPadding(40, 25, 40, 25)

        container.addView(analyzeButton)

        analyzeButton.setOnClickListener {

            val goalText = aimInput.text.toString().trim()
            val timeText = timeInput.text.toString().trim()

            if (goalText.isEmpty() || timeText.isEmpty()) {
                Toast.makeText(context, "Please enter both goal and time period", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultScreen = AimResultScreen(
                context,
                goalText,
                timeText
            ) {
                // When back is pressed → return to Aim input screen
                onAnalyzeComplete(createView())
            }

            onAnalyzeComplete(resultScreen.createView())
        }

        return container
    }
}