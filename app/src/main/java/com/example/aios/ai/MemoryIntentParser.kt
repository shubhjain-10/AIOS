package com.example.aios.ai

object MemoryIntentParser {

    fun shouldRemember(text: String): Boolean {
        return text.lowercase().startsWith("remember")
    }

    fun extractMemory(text: String): String {
        return text.removePrefix("remember").trim()
    }

    fun shouldForget(text: String): Boolean {
        return text.lowercase().startsWith("forget")
    }

    fun extractForgetKeyword(text: String): String {
        return text.removePrefix("forget").trim()
    }
}