package com.example.aios.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.setPadding

class AimScreen(
    private val context: Context,
    private val onAnalyzeComplete: (View) -> Unit
) {

    fun createView(): View {

        // ===== Root Layout (NO SCROLL) =====
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(60, 50, 60, 40)
            background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    Color.parseColor("#03121F"),
                    Color.parseColor("#072B4F")
                )
            )
        }

        // ===== Big Title Input =====
        val titleInput = EditText(context).apply {
            hint = "TITLE..."
            setHintTextColor(Color.parseColor("#5A6E8A"))
            setTextColor(Color.parseColor("#8FA9C4"))
            textSize = 32f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = null
        }

        root.addView(titleInput)

        addSpace(root, 40)

        // ===== Goal Description =====
        val goalLabel = createLabel("Goal Description")
        root.addView(goalLabel)

        addSpace(root, 15)

        val goalInput = createRoundedInputBox(
            "Describe what you want to achieve...",
            240   // 👈 reduced from 350
        )
        root.addView(goalInput)

        addSpace(root, 35)

        // ===== Target Deadline =====
        val deadlineLabel = createLabel("Target Deadline")
        root.addView(deadlineLabel)

        addSpace(root, 15)

        val deadlineInput = createRoundedInputBox(
            "Set your deadline...",
            110
        )
        root.addView(deadlineInput)

        addSpace(root, 45)

        // ===== Analyze Button =====
        val analyzeButton = Button(context).apply {
            text = "ANALYZE FEASIBILITY"
            textSize = 16f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#2F6BFF"),
                    Color.parseColor("#5AA9FF")
                )
            ).apply {
                cornerRadius = 80f
            }
            setPadding(50, 35, 50, 35)
        }

        root.addView(analyzeButton)

        analyzeButton.setOnClickListener {

            val title = titleInput.text.toString().trim()
            val goal = goalInput.text.toString().trim()
            val deadline = deadlineInput.text.toString().trim()

            if (title.isEmpty() || goal.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultScreen = AimResultScreen(
                context,
                "$title\n$goal",
                deadline,
                onBack = { onAnalyzeComplete(createView()) },
                openScreen = { onAnalyzeComplete(it) }
            )

            onAnalyzeComplete(resultScreen.createView())
        }

        return root
    }

    // ===== Reusable Label =====
    private fun createLabel(textValue: String): TextView {
        return TextView(context).apply {
            text = textValue
            textSize = 18f
            setTextColor(Color.parseColor("#4DA3FF"))
            setTypeface(null, Typeface.BOLD)
        }
    }

    // ===== Reusable Rounded Box =====
    private fun createRoundedInputBox(hintText: String, height: Int): EditText {
        return EditText(context).apply {

            hint = hintText
            setHintTextColor(Color.parseColor("#5A6E8A"))
            setTextColor(Color.WHITE)
            textSize = 16f
            minHeight = height
            gravity = Gravity.TOP
            setPadding(40)

            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#0A1A2F"),
                    Color.parseColor("#112B4A")
                )
            ).apply {
                cornerRadius = 50f
                setStroke(2, Color.parseColor("#1F6BFF"))
            }
        }
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