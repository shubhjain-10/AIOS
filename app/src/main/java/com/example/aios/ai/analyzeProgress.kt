package com.example.aios.ai
import com.example.aios.storage.Aim
suspend fun analyzeProgress(userText: String, aim: Aim): Int {

    val request = ChatRequest(
        model = "gpt-4o-mini",
        messages = listOf(
            Message(
                "system",
                """
            Calculate progress percentage toward a goal.

            Return ONLY a number between 0 and 100.

            Goal: ${aim.description}
            Deadline: ${aim.deadline}
            """
            ),
            Message(
                "user",
                "Progress update: $userText"
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
        ?.content ?: "0"

    println("GPT RESPONSE: $reply")

    return reply.trim().toIntOrNull() ?: 0
}