package com.example.aios.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.aios.data.memory.MemoryDatabase
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        CoroutineScope(Dispatchers.Main).launch {

            val db = MemoryDatabase.getDatabase(context)
            val aimDao = db.aimDao()

            val aims = withContext(Dispatchers.IO) {
                aimDao.getAll()
            }

            aims.forEach { aim ->

                val row = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }

                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val created = dateFormat.format(Date(aim.createdDate))
                val aimButton = Button(context).apply {
                    text = "${aim.title.uppercase()}\nCreated: $created"
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

                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )

                    setPadding(40, 40, 40, 40)
                }

                aimButton.setOnClickListener {

                    val detailScreen = ProgressDetailScreen(
                        context,
                        aim
                    ) {
                        // go back to progress list
                        openDetail(createView())
                    }

                    val view = detailScreen.createView()

                    openDetail(view)
                }

                val deleteButton = Button(context).apply {
                    text = "DELETE"
                    textSize = 14f
                    setTextColor(Color.WHITE)

                    background = GradientDrawable().apply {
                        cornerRadius = 80f
                        setColor(Color.parseColor("#D32F2F"))
                    }

                    setPadding(30, 40, 30, 40)
                }

                deleteButton.setOnClickListener {

                    CoroutineScope(Dispatchers.IO).launch {
                        aimDao.delete(aim)

                        withContext(Dispatchers.Main) {
                            openDetail(createView()) // refresh screen
                        }
                    }
                }

                row.addView(aimButton)
                row.addView(deleteButton)

                root.addView(row)

                val space = Space(context)
                space.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    40
                )

                root.addView(space)
            }
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