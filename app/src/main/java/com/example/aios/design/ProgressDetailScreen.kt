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

    fun createCard(): LinearLayout {

        return LinearLayout(context).apply {

            orientation = LinearLayout.VERTICAL
            setPadding(40,40,40,40)

            val bg = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = 40f
                setColor(Color.parseColor("#08162E"))
                setStroke(2, Color.parseColor("#1E3357"))
            }

            background = bg

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(0,30,0,0)
            layoutParams = params
        }
    }

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

        val today = Calendar.getInstance()

        val created = Calendar.getInstance().apply {
            timeInMillis = aim.createdDate
        }

        val daysPassed = (
                today.get(Calendar.DAY_OF_YEAR) - created.get(Calendar.DAY_OF_YEAR) +
                        (today.get(Calendar.YEAR) - created.get(Calendar.YEAR)) * 365
                ).coerceAtLeast(0)

        val daysRemaining = (deadlineDays - daysPassed).coerceAtLeast(0)

        val expectedSolved = (goalNumber.toFloat() / deadlineDays * daysPassed).toInt()

        val paceStatus = when {
            aim.solvedCount > expectedSolved -> "Ahead of schedule 🟢"
            aim.solvedCount == expectedSolved -> "On track 🟡"
            else -> "Behind schedule 🔴"
        }

        container.addView(title)

        val toggleLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0,30,0,30)
        }

        val aimTab = Button(context).apply {

            text = "AIM"
            setTextColor(Color.WHITE)

            background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = 40f
                setColor(Color.parseColor("#1F2E45"))
            }

            setPadding(40,20,40,20)
        }

        val progressTab = Button(context).apply {

            text = "PROGRESS"
            setTextColor(Color.BLACK)

            background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = 40f
                setColor(Color.parseColor("#E5E5E5"))
            }

            setPadding(40,20,40,20)
        }

        toggleLayout.addView(aimTab)
        toggleLayout.addView(progressTab)

        container.addView(toggleLayout)

        // Default state: Progress selected
        progressTab.setTextColor(Color.BLACK)
        progressTab.background = android.graphics.drawable.GradientDrawable().apply {
            cornerRadius = 40f
            setColor(Color.WHITE)
        }

        aimTab.setTextColor(Color.WHITE)
        aimTab.background = android.graphics.drawable.GradientDrawable().apply {
            cornerRadius = 40f
            setColor(Color.parseColor("#1F2E45"))
        }

        val aimView = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        val aimTitle = TextView(context).apply {
            text = "AI Goal Plan"
            textSize = 20f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
        }

        val analysisText = TextView(context).apply {
            text = aim.analysis.ifBlank { "No analysis available." }
            setTextColor(Color.LTGRAY)
            textSize = 16f
            setPadding(0,20,0,20)
        }

        aimView.addView(aimTitle)
        aimView.addView(analysisText)

        val progressView = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        container.addView(aimView)
        container.addView(progressView)

        aimTab.setOnClickListener {

            aimView.visibility = View.VISIBLE
            progressView.visibility = View.GONE

            // AIM selected (white)
            aimTab.setTextColor(Color.BLACK)
            aimTab.background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = 40f
                setColor(Color.WHITE)
            }

            // PROGRESS unselected (dark)
            progressTab.setTextColor(Color.WHITE)
            progressTab.background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = 40f
                setColor(Color.parseColor("#1F2E45"))
            }
        }

        progressTab.setOnClickListener {

            aimView.visibility = View.GONE
            progressView.visibility = View.VISIBLE

            // PROGRESS selected (white)
            progressTab.setTextColor(Color.BLACK)
            progressTab.background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = 40f
                setColor(Color.WHITE)
            }

            // AIM unselected (dark)
            aimTab.setTextColor(Color.WHITE)
            aimTab.background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = 40f
                setColor(Color.parseColor("#1F2E45"))
            }
        }

        progressView.visibility = View.VISIBLE
        aimView.visibility = View.GONE

        val statsCard = createCard()

        val statsRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 3f
        }

        fun stat(label:String,value:String):LinearLayout{

            return LinearLayout(context).apply {

                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1f)

                addView(TextView(context).apply{
                    text = label
                    textSize = 14f
                    setTextColor(Color.GRAY)
                })

                addView(TextView(context).apply{
                    text = value
                    textSize = 28f
                    setTypeface(null,Typeface.BOLD)
                    setTextColor(Color.WHITE)
                })
            }
        }

        val progressValue = TextView(context).apply {
            text = "${aim.progress}%"
            textSize = 28f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
        }

        val progressColumn = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1f)

            addView(TextView(context).apply{
                text = "Progress"
                textSize = 14f
                setTextColor(Color.GRAY)
            })

            addView(progressValue)
        }

        statsRow.addView(stat("Solved","${aim.solvedCount}"))
        statsRow.addView(progressColumn)
        statsRow.addView(stat("Remaining","${daysRemaining}d"))

        statsCard.addView(statsRow)

        val progressBar = ProgressBar(
            context,
            null,
            android.R.attr.progressBarStyleHorizontal
        )

        progressBar.progress = aim.progress
        progressBar.max = 100

        progressBar.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            20
        ).apply{
            setMargins(0,40,0,30)
        }

        statsCard.addView(progressBar)

        val statusBox = TextView(context).apply{

            text = "● Status: ${paceStatus.replace("🟢","").replace("🔴","").replace("🟡","")}"

            setTextColor(Color.parseColor("#FFC107"))
            textSize = 16f
            setPadding(30,25,30,25)

            background = android.graphics.drawable.GradientDrawable().apply{
                cornerRadius = 30f
                setColor(Color.parseColor("#2A2615"))
                setStroke(2,Color.parseColor("#8A6D00"))
            }
        }

        statsCard.addView(statusBox)

        progressView.addView(statsCard)

        val updateCard = createCard()

        val updateTitle = TextView(context).apply{
            text = "Update progress"
            textSize = 18f
            setTextColor(Color.LTGRAY)
        }

        val progressInput = EditText(context).apply{

            hint = "Enter number solved..."
            setHintTextColor(Color.GRAY)
            setTextColor(Color.WHITE)

            background = android.graphics.drawable.GradientDrawable().apply{
                cornerRadius = 30f
                setColor(Color.parseColor("#1F2E45"))
                setStroke(2,Color.parseColor("#3A4C66"))
            }

            setPadding(30,25,30,25)
        }
        val feedbackText = TextView(context).apply {

            textSize = 16f
            setTextColor(Color.CYAN)
            setPadding(0,20,0,10)
        }

        val updateBtn = Button(context).apply {

            text = "UPDATE"
            setTextColor(Color.WHITE)

            background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = 40f
                setColor(Color.parseColor("#2F6FED"))
            }

            setPadding(40,30,40,30)

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

                    val expectedProgress = (daysPassed.toFloat() / deadlineDays) * 100f

                    val newStatus = when {
                        percent > expectedProgress -> "Ahead of schedule"
                        percent >= expectedProgress * 0.9 -> "On track"
                        else -> "Behind schedule"
                    }

                    statusBox.text = "● Status: $newStatus"

                    val db = MemoryDatabase.getDatabase(context)
                    val aimDao = db.aimDao()

                    CoroutineScope(Dispatchers.IO).launch {
                        aimDao.update(aim)
                    }

                    progressBar.progress = percent
                    progressValue.text = "$percent%"

                    // SHOW AI FEEDBACK
                    feedbackText.text = message
                }
            }
        }

        updateCard.addView(updateTitle)
        updateCard.addView(progressInput)
        updateCard.addView(updateBtn)
        updateCard.addView(feedbackText)

        progressView.addView(updateCard)


        val backBtn = Button(context).apply {

            text = "← BACK"
            setTextColor(Color.WHITE)

            background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = 40f
                setColor(Color.parseColor("#1F2E45"))
            }

            setPadding(40,30,40,30)

            setOnClickListener { onBack() }
        }

        val timelineCard = createCard()

        timelineCard.addView(TextView(context).apply{
            text = "Progress Timeline"
            textSize = 22f
            setTypeface(null,Typeface.BOLD)
            setTextColor(Color.WHITE)
        })

        timelineCard.addView(TextView(context).apply{

            text =
                "Start        Day 0\n\n"+
                        "Today        Day $daysPassed\n\n"+
                        "Solved       ${aim.solvedCount} / $goalNumber"

            textSize = 16f
            setTextColor(Color.LTGRAY)
        })

        progressView.addView(timelineCard)

        val summary = TextView(context).apply {
            text =
                "Goal: ${aim.description}\n" +
                        "Deadline: ${aim.deadline}"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 20, 0, 30)
        }

        container.addView(summary)
        container.addView(backBtn)

        return scrollView
    }

}