package com.example.aios.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.aios.storage.AimRepository

class ProgressScreen(
    private val context: Context,
    private val onBack: () -> Unit,
    private val openDetail: (View) -> Unit
) {

    fun createView(): View {

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(60, 50, 60, 50)
            setBackgroundColor(Color.BLACK)
        }

        // ===== Title =====
        val title = TextView(context).apply {
            text = "Your Aims"
            textSize = 24f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 60)
        }

        root.addView(title)

        // ===== Aims List =====
        for (aim in AimRepository.aims) {

            val aimButton = Button(context).apply {
                text = aim.title.uppercase()
                textSize = 16f
                setTextColor(Color.WHITE)
                setTypeface(null, Typeface.BOLD)

                background = GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(
                        Color.parseColor("#2F6BFF"),
                        Color.parseColor("#3D7BFF")
                    )
                ).apply {
                    cornerRadius = 80f
                }

                setPadding(40, 40, 40, 40)
            }

            aimButton.setOnClickListener {
                val detailScreen = ProgressDetailScreen(context, aim) {
                    openDetail(createView())
                }
                openDetail(detailScreen.createView())
            }

            root.addView(aimButton)

            val space = Space(context)
            space.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                40
            )
            root.addView(space)
        }

        // ===== Back Button =====
        val backButton = Button(context).apply {
            text = "BACK"
            textSize = 16f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)

            background = GradientDrawable().apply {
                cornerRadius = 80f
                setColor(Color.parseColor("#4A4A4A"))
            }

            setPadding(40, 40, 40, 40)

            setOnClickListener { onBack() }
        }

        root.addView(backButton)

        return root
    }
}