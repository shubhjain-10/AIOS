package com.example.aios.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.aios.storage.AimRepository

class ProgressMenuScreen(
    private val context: Context,
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
            text = "Your Aims"
            textSize = 22f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 40)
        }

        container.addView(title)

        // Show all aims
        for (aim in AimRepository.aims) {

            val aimButton = Button(context).apply {
                text = aim.title
                setTextColor(Color.WHITE)
                background = GradientDrawable().apply {
                    cornerRadius = 40f
                    setColor(Color.parseColor("#1F6BFF"))
                }
                setPadding(40, 30, 40, 30)
            }

            aimButton.setOnClickListener {
                val detail = ProgressDetailScreen(context, aim) {
                    onBack()
                }
                onBack.invoke() // not needed if using screen controller
            }

            container.addView(aimButton)

            val space = Space(context)
            space.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                30
            )
            container.addView(space)
        }

        val backButton = Button(context).apply {
            text = "Back"
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                cornerRadius = 40f
                setColor(Color.parseColor("#444444"))
            }
            setOnClickListener { onBack() }
        }

        container.addView(backButton)

        return scrollView
    }
}