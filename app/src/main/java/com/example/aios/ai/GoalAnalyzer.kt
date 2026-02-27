package com.example.aios.ai

import com.example.aios.ai.ChatRequest
import com.example.aios.ai.Message
import com.example.aios.ai.RetrofitClient
import com.example.aios.ai.Secrets

suspend fun analyzeGoal(goal: String, timeline: String): String {

    val prompt = """
You are an advanced productivity strategist.

User Goal: $goal
User Timeline: $timeline

Your task:
1. Estimate if this goal is realistically achievable.
2. Give a feasibility score from 0 to 100.
3. Estimate required daily effort.
4. Give reasoning.
5. Keep response concise but structured.

Format response like:

Feasibility Score: XX%

Daily Effort Needed: ___ per day

Verdict: (Achievable / Hard / Unrealistic)

Reasoning:
- point 1
- point 2
- point 3
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