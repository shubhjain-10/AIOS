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
                    You are an AI productivity coach.

                    The user previously generated a structured milestone plan.
                    
                    Each phase represents a percentage range of the total goal.
                    
                    Example structure:
                    
                    Phase 1 (0–30%)
                    - milestone
                    - milestone
                    
                    Phase 2 (30–70%)
                    - milestone
                    - milestone
                    
                    Phase 3 (70–100%)
                    - milestone
                    - milestone
                    
                    Your job:
                    
                    1. Read the learning plan.
                    2. Identify which milestones are completed based on the user update.
                    3. Estimate the correct percentage within the phase range.
                    
                    Rules:
                    - Do not return the same progress unless no new milestone is achieved.
                    - Progress must always move forward when milestones are completed.
                    - Be generous when the user describes major improvements.
                    
                    Goal:
                    ${aim.description}
                    
                    Learning Plan:
                    ${aim.analysis}
                    
                    User Progress Update:
                    $note
                    
                    Return STRICT JSON ONLY:
                    
                    {
                     "progress": number 0-100,
                     "feedback": "detailed coaching message"
                    }
                    
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

    val progress = Regex("\"progress\"\\s*:\\s*(\\d+)")
        .find(reply)
        ?.groupValues?.get(1)
        ?.toIntOrNull() ?: aim.progress
    val feedback = Regex("\"feedback\":\\s*\"(.*?)\"").find(reply)?.groupValues?.get(1) ?: ""
    val finalProgress = maxOf(progress, aim.progress)

    return ProgressResult(finalProgress.coerceIn(0,100), feedback)

}