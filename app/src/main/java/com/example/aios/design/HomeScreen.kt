package com.example.aios.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import com.example.aios.ai.*
import com.example.aios.ai.Secrets
import com.google.gson.internal.GsonBuildConfig
import com.example.aios.R
import android.view.KeyEvent

class HomeScreen(
    private val context: Context,
    private val memoryDao: com.example.aios.data.memory.MemoryDao,
    private val onMicClick: () -> Unit
) {

    private lateinit var micButton: ImageView
    private var isListening = false
    private var glowAnimator: ValueAnimator? = null

    private lateinit var aiContainer: LinearLayout
    private lateinit var memoryContainer: LinearLayout

    private lateinit var aimContainer: LinearLayout
    private val uiScope = CoroutineScope(Dispatchers.Main + Job())

    private lateinit var chatContainer: LinearLayout
    private lateinit var inputField: EditText


    fun createView(): View {

        val root = FrameLayout(context)
        root.setBackgroundColor(Color.BLACK)

        val mainLayout = LinearLayout(context)
        mainLayout.orientation = LinearLayout.VERTICAL
        mainLayout.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        root.addView(mainLayout)

        // ----- Top Header -----
        val header = LinearLayout(context)
        header.orientation = LinearLayout.HORIZONTAL
        header.gravity = Gravity.CENTER_VERTICAL
        header.setPadding(40, 60, 40, 40)
        header.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Menu Icon
        val menuIcon = ImageView(context)
        menuIcon.setImageResource(android.R.drawable.ic_menu_sort_by_size)
        menuIcon.setColorFilter(Color.parseColor("#1F6BFF"))

        val iconParams = LinearLayout.LayoutParams(80, 80)
        menuIcon.layoutParams = iconParams

        header.addView(menuIcon)

        // Title
        val headerTitle = TextView(context)
        headerTitle.text = "AI OS"
        headerTitle.setTextColor(Color.WHITE)
        headerTitle.textSize = 20f
        headerTitle.setTypeface(null, Typeface.BOLD)

        val titleParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        titleParams.setMargins(40, 0, 0, 0)
        headerTitle.layoutParams = titleParams

        header.addView(headerTitle)

        mainLayout.addView(header)

        // ----- AI CONTAINER -----
        aiContainer = createAIContainer()

        // ----- MEMORY CONTAINER -----
        memoryContainer = LinearLayout(context)
        memoryContainer.orientation = LinearLayout.VERTICAL
        memoryContainer.setPadding(60, 40, 60, 0)
        memoryContainer.visibility = View.GONE

        aimContainer = LinearLayout(context)
        aimContainer.orientation = LinearLayout.VERTICAL

        val aimScreen = AimScreen(context) { newView ->

            aimContainer.removeAllViews()
            aimContainer.addView(newView)
        }

        aimContainer.addView(aimScreen.createView())
        aimContainer.visibility = View.GONE
        mainLayout.addView(aiContainer)
        mainLayout.addView(memoryContainer)
        mainLayout.addView(aimContainer)

        // ----- Sidebar -----
        val sidebarWidth = 600

        val sidebar = LinearLayout(context)
        sidebar.orientation = LinearLayout.VERTICAL
        sidebar.setBackgroundColor(Color.parseColor("#111111"))

        val sidebarParams = FrameLayout.LayoutParams(
            sidebarWidth,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        sidebar.layoutParams = sidebarParams
        sidebar.translationX = -sidebarWidth.toFloat()

        // Sidebar items
        fun createMenuItem(text: String): TextView {
            val item = TextView(context)
            item.text = text
            item.setTextColor(Color.WHITE)
            item.textSize = 18f
            item.setPadding(60, 60, 60, 60)
            return item
        }

        val aiItem = createMenuItem("AI OS")
        val memoryItem = createMenuItem("Memory")
        val aimItem = createMenuItem("Aim")

        sidebar.addView(aiItem)
        sidebar.addView(memoryItem)
        sidebar.addView(aimItem)

        root.addView(sidebar)

        var isSidebarOpen = false

        fun openSidebar() {
            sidebar.animate()
                .translationX(0f)
                .setDuration(300)
                .start()
            isSidebarOpen = true
        }

        fun closeSidebar() {
            sidebar.animate()
                .translationX(-sidebarWidth.toFloat())
                .setDuration(300)
                .start()
            isSidebarOpen = false
        }

        menuIcon.setOnClickListener {
            if (isSidebarOpen) closeSidebar()
            else openSidebar()
        }

        aiItem.setOnClickListener {
            aiContainer.visibility = View.VISIBLE
            memoryContainer.visibility = View.GONE
            aimContainer.visibility = View.GONE
            headerTitle.text = "AI OS"
            closeSidebar()
        }

        memoryItem.setOnClickListener {
            aiContainer.visibility = View.GONE
            memoryContainer.visibility = View.VISIBLE
            aimContainer.visibility = View.GONE
            headerTitle.text = "Memory"
            loadMemories()
            closeSidebar()
        }

        aimItem.setOnClickListener {
            aiContainer.visibility = View.GONE
            memoryContainer.visibility = View.GONE
            aimContainer.visibility = View.VISIBLE
            headerTitle.text = "Aim"
            closeSidebar()
        }

        return root
    }

    private fun createTab(title: String): TextView {
        val tab = TextView(context)
        tab.text = title
        tab.setTextColor(Color.parseColor("#1F6BFF"))
        tab.textSize = 18f
        tab.setPadding(60, 10, 60, 10)
        tab.setTypeface(null, Typeface.BOLD)
        return tab
    }

    private fun createAIContainer(): LinearLayout {

        val container = LinearLayout(context)
        container.orientation = LinearLayout.VERTICAL
        container.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f
        )

        // Chat Scroll Area
        val scrollView = ScrollView(context)
        scrollView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f
        )

        chatContainer = LinearLayout(context)
        chatContainer.orientation = LinearLayout.VERTICAL
        chatContainer.setPadding(40, 40, 40, 40)

        scrollView.addView(chatContainer)

        // Input Container
        val inputContainer = LinearLayout(context)
        inputContainer.orientation = LinearLayout.HORIZONTAL
        inputContainer.setPadding(40, 20, 40, 40)

        val inputParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        )

        inputField = EditText(context)
        inputField.hint = "Ask anything..."
        inputField.setHintTextColor(Color.GRAY)
        inputField.setTextColor(Color.WHITE)
        //inputField.setBackgroundColor(Color.parseColor("#111111"))
        inputField.layoutParams = inputParams

        // Rounded background with blue border
        val roundedBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor("#111111")) // inside color
            cornerRadius = 60f                     // roundness
            setStroke(3, Color.parseColor("#1F6BFF")) // blue border
        }
        inputField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                roundedBackground.setStroke(4, Color.parseColor("#1F6BFF"))
            } else {
                roundedBackground.setStroke(3, Color.parseColor("#1F6BFF"))
            }
        }

        inputField.background = roundedBackground
        inputField.setPadding(40, 25, 40, 25)
        inputField.isSingleLine = false
        inputField.maxLines = 6

        inputField.setOnKeyListener { _, keyCode, event ->

            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                // If ALT is pressed → allow new line
                if (event.isAltPressed) {
                    return@setOnKeyListener false
                }

                // Otherwise send message
                val text = inputField.text.toString().trim()
                if (text.isNotBlank()) {
                    addMessage("You", text)
                    inputField.text.clear()
                    fetchAI(text)
                }

                true // consume event (prevent new line)
            } else {
                false
            }
        }

        // Send Arrow Button
        val sendButton = ImageView(context)

        val size = 110
        val sendParams = LinearLayout.LayoutParams(size, size)
        sendParams.setMargins(20, 0, 20, 0)
        sendButton.layoutParams = sendParams

        // Blue circular background
        val sendBackground = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor("#1F6BFF"))
        }
        sendButton.background = sendBackground

        // Arrow icon
        sendButton.setImageResource(R.drawable.ic_send)
        sendButton.scaleType = ImageView.ScaleType.CENTER_INSIDE
        sendButton.setPadding(28, 28, 28, 28)
        sendButton.scaleType = ImageView.ScaleType.CENTER_INSIDE
        sendButton.setPadding(30, 30, 30, 30)

        sendButton.setOnClickListener {
            val text = inputField.text.toString()
            if (text.isNotBlank()) {
                addMessage("You", text)
                inputField.text.clear()
                fetchAI(text)
            }
        }

        // Mic Button (circular, aligned)
        micButton = ImageView(context)

        val micParams = LinearLayout.LayoutParams(size, size)
        micButton.layoutParams = micParams

        val micBackground = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor("#111111"))
            setStroke(3, Color.parseColor("#1F6BFF"))
        }

        micButton.background = micBackground
        micButton.setImageResource(android.R.drawable.ic_btn_speak_now)
        micButton.setColorFilter(Color.parseColor("#1F6BFF"))
        micButton.scaleType = ImageView.ScaleType.CENTER

        micButton.setOnClickListener {
            toggleListening()
            onMicClick()
        }

        inputContainer.addView(inputField)
        inputContainer.addView(sendButton)
        inputContainer.addView(micButton)
        inputContainer.gravity = Gravity.CENTER_VERTICAL

        container.addView(scrollView)
        container.addView(inputContainer)

        return container
    }

    private fun fetchAI(userMessage: String) {

        uiScope.launch {

            addMessage("AI OS", "Thinking...")

            val lower = userMessage.lowercase()

            // -------- HANDLE MEMORY STORE --------
            if (MemoryIntentParser.shouldRemember(lower)) {

                val memory = MemoryIntentParser.extractMemory(userMessage)

                withContext(Dispatchers.IO) {
                    memoryDao.insert(
                        com.example.aios.data.memory.MemoryEntity(content = memory)
                    )
                }

                chatContainer.removeViewAt(chatContainer.childCount - 1)
                addMessage("AI OS", "Got it. I'll remember that.")
                return@launch
            }

            // -------- HANDLE MEMORY DELETE --------
            if (MemoryIntentParser.shouldForget(lower)) {

                val keyword = MemoryIntentParser.extractForgetKeyword(userMessage)

                withContext(Dispatchers.IO) {
                    memoryDao.deleteByKeyword("%$keyword%")
                }

                chatContainer.removeViewAt(chatContainer.childCount - 1)
                addMessage("AI OS", "Done. I forgot that.")
                return@launch
            }

            // -------- NORMAL AI RESPONSE --------

            val memories = withContext(Dispatchers.IO) {
                memoryDao.getAll()
            }

            val memoryContext = memories.joinToString("\n") { memory ->
                memory.content
            }

            val request = ChatRequest(
                model = "gpt-4o-mini",
                messages = listOf(
                    Message("system", "You are AI OS, futuristic assistant."),
                    Message("system", "User facts:\n$memoryContext"),
                    Message("user", userMessage)
                )
            )

            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.chatCompletion(
                    "Bearer ${Secrets.OPENAI_API_KEY}",
                    request
                )
            }

            val reply = response.body()
                ?.choices
                ?.firstOrNull()
                ?.message
                ?.content ?: "No response."

            chatContainer.removeViewAt(chatContainer.childCount - 1)
            addMessage("AI OS", reply)
        }
    }

    private fun addMessage(sender: String, message: String) {

        val textView = TextView(context)
        textView.text = "$sender: $message"
        textView.setTextColor(Color.WHITE)
        textView.textSize = 16f
        textView.setPadding(0, 10, 0, 10)

        chatContainer.addView(textView)
    }

    private fun loadMemories() {

        uiScope.launch {

            val memories = withContext(Dispatchers.IO) {
                memoryDao.getAll()
            }

            memoryContainer.removeAllViews()

            if (memories.isEmpty()) {
                val empty = TextView(context)
                empty.text = "No memories stored."
                empty.setTextColor(Color.GRAY)
                memoryContainer.addView(empty)
                return@launch
            }

            memories.forEach {
                val text = TextView(context)
                text.text = "• ${it.content}"
                text.setTextColor(Color.WHITE)
                text.textSize = 16f
                text.setPadding(0, 20, 0, 20)
                memoryContainer.addView(text)
            }
        }
    }

    private fun toggleListening() {
        isListening = !isListening

        if (isListening) {
            startGlow()
        } else {
            stopGlow()
        }
    }

    private fun startGlow() {
        glowAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1200
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()

            addUpdateListener {
                val value = it.animatedValue as Float
                val alpha = (100 + (155 * value)).toInt()
                micButton.setColorFilter(Color.argb(alpha, 31, 107, 255))
            }

            start()
        }
    }

    private fun stopGlow() {
        glowAnimator?.cancel()
        micButton.setColorFilter(Color.parseColor("#1F6BFF"))
    }
}