package com.example.aios.ai

import com.example.aios.storage.Aim
import java.text.SimpleDateFormat
import java.util.*

suspend fun analyzeProgress(aim: Aim, note: String): ProgressResult {

    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(aim.createdDate))

    val request = ChatRequest(
        model = "gpt-4o-mini",
        messages = listOf(
            Message(
                "system",
                """
                    Message(
                        "system",
                        ""${'"'}
                    You are an AI productivity coach.
                    
                    Analyze the user's goal progress.
                    
                    Calculate:
                    
                    1. Days passed since start
                    2. Total days available
                    3. Expected problems solved by now
                    4. Predicted completion date
                    5. Recommended daily pace
                    
                    Return JSON ONLY:
                    
                    {
                     "progress": number 0-100,
                     "feedback": detailed coaching message
                    }
                    
                    Example feedback style:
                    
                    Great start! You solved 10 problems in 1 day.
                    
                    At this pace you will finish the goal in about 10 days.
                    
                    Your original timeline allows ~3 problems per day, so you can slow down your pace if you want.
                    
                    Goal: ${aim.description}
                    
                    Total solved so far: ${aim.solvedCount}
                    
                    User update note: $note
                    
                    Start Date: $startDate
                    Deadline: ${aim.deadline}
                    Current Date: $currentDate
                    """
            )
        )
    )

    val response = RetrofitClient.api.chatCompletion(
        "Bearer ${Secrets.OPENAI_API_KEY}",
        request
    )

    val reply = response.body()
        ?.choices
        ?.firstOrNull()
        ?.message
        ?.content ?: """{"progress":0,"feedback":"No response"}"""

    println("GPT RESPONSE: $reply")

    val progress = Regex("\"progress\":\\s*(\\d+)").find(reply)?.groupValues?.get(1)?.toInt() ?: 0
    val feedback = Regex("\"feedback\":\\s*\"(.*?)\"").find(reply)?.groupValues?.get(1) ?: ""
    val goalNumber = Regex("(\\d+)").find(aim.description)?.groupValues?.get(1)?.toInt() ?: 100
    val percent = ((aim.solvedCount.toDouble() / goalNumber) * 100).toInt().coerceIn(0,100)

    return ProgressResult(percent, feedback)
}