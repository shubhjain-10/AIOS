package com.example.aios.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*

class RoutineScreen(
    private val context: Context,
    private val onBack: () -> Unit,
    private val onViewHistory: () -> Unit
) {

    fun createView(): View {

        val scroll = ScrollView(context)

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 60, 60, 60)
        }

        scroll.addView(container)

        val title = TextView(context).apply {
            text = "Routine"
            textSize = 28f
            setTextColor(Color.parseColor("#4DA3FF"))
            setTypeface(null, Typeface.BOLD)
        }

        val subtitle = TextView(context).apply {
            text = "Track your daily activities"
            textSize = 16f
            setTextColor(Color.LTGRAY)
            setPadding(0,10,0,40)
        }

        container.addView(title)
        container.addView(subtitle)

        val historyBtn = Button(context).apply {
            text = "View Past Saves"
            setOnClickListener { onViewHistory() }
        }

        val historyParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        historyParams.setMargins(0,0,0,40)

        container.addView(historyBtn, historyParams)

        val section = TextView(context).apply {
            text = "Daily Routine"
            textSize = 22f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(0,0,0,30)
        }

        container.addView(section)

        container.addView(createRoutineItem(1,"Gym"))
        container.addView(createRoutineItem(2,"Office"))
        container.addView(createRoutineItem(3,"Worked after office"))

        // Hours worked card
        val hoursCard = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40,40,40,40)

            background = GradientDrawable().apply {
                cornerRadius = 30f
                setStroke(2, Color.parseColor("#2A2A2A"))
                setColor(Color.parseColor("#0E1117"))
            }
        }

        val hoursTitle = TextView(context).apply {
            text = "Hours Worked Today"
            textSize = 16f
            setTextColor(Color.LTGRAY)
        }

        val hoursInput = EditText(context).apply {
            hint = "0"
            textSize = 26f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        hoursCard.addView(hoursTitle)
        hoursCard.addView(hoursInput)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0,30,0,40)

        container.addView(hoursCard, params)

        val saveBtn = Button(context).apply {
            text = "Save"
        }

        container.addView(saveBtn)

        val backBtn = Button(context).apply {
            text = "Back"
            setOnClickListener { onBack() }
        }

        container.addView(backBtn)

        return scroll
    }


    private fun createRoutineItem(number: Int, text: String): View {

        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(40,40,40,40)

            background = GradientDrawable().apply {
                cornerRadius = 30f
                setStroke(2, Color.parseColor("#2A2A2A"))
                setColor(Color.parseColor("#0E1117"))
            }
        }

        val numCircle = TextView(context).apply {
            this.text = number.toString()
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER

            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#132A4A"))
            }

            val size = 80
            layoutParams = LinearLayout.LayoutParams(size,size)
        }

        val label = TextView(context).apply {
            this.text = text
            textSize = 18f
            setTextColor(Color.WHITE)
            setPadding(40,0,0,0)
        }

        val tick = ImageView(context).apply {
            setImageResource(android.R.drawable.checkbox_on_background)
        }

        val spacer = Space(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        var checked = false

        row.setOnClickListener {
            checked = !checked
            tick.visibility = if (checked) View.VISIBLE else View.INVISIBLE
        }

        tick.visibility = View.INVISIBLE

        row.addView(numCircle)
        row.addView(label)
        row.addView(spacer)
        row.addView(tick)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0,0,0,25)

        row.layoutParams = params

        return row
    }
}