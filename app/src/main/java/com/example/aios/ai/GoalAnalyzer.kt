package com.example.aios.ai

import com.example.aios.ai.ChatRequest
import com.example.aios.ai.Message
import com.example.aios.ai.RetrofitClient
import com.example.aios.ai.Secrets

suspend fun analyzeGoal(goal: String, timeline: String): String {

    val prompt = """
        You are an expert skill coach and learning strategist.
        
        User Goal: $goal
        Timeline: $timeline
        
        Your job is to convert the goal into a practical guided plan.
        
        Steps:
        1. Evaluate if the goal is achievable.
        2. Give a feasibility score (0–100).
        3. Estimate daily effort required.
        4. Create a step-by-step learning plan.
        5. The plan must be simple and actionable.
        
        If the timeline is short, compress the plan intelligently.
        
        Respond in this format:
        
        Feasibility Score: XX%
        
        Daily Effort Needed: ___ per day
        
        Verdict: (Achievable / Hard / Unrealistic)
        
        Guided Plan:
        
        Phase 1 (0-30%) – Foundation
        - step
        - step
        - step
        
        Phase 2 (30-70%) – Skill Building
        - step
        - step
        - step
        
        Phase 3 (70-100%) – Practice & Mastery
        - step
        - step
        - step
        
        Daily Routine Example:
        - activity 1
        - activity 2
        - activity 3
        """.trimIndent()

    val request = ChatRequest(
        model = "gpt-4o-mini",
        messages = listOf(
            Message("system", "You are a realistic productivity strategist."),
            Message("user", prompt)
        )
    )

    val response = RetrofitClient.api.chatCompletion(
        "Bearer ${Secrets.OPENAI_API_KEY}",
        request
    )


    return response.body()
        ?.choices
        ?.firstOrNull()
        ?.message
        ?.content ?: "Unable to analyze goal."

}