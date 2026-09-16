package com.aiide.app.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService(private val apiKeyProvider: () -> String) {

    private fun getModel(modelName: String = "gemini-1.5-flash"): GenerativeModel {
        val apiKey = apiKeyProvider()
        return GenerativeModel(
            modelName = modelName,
            apiKey = if (apiKey.isNotBlank()) apiKey else "AIzaSyDummyKeyForTesting"
        )
    }

    suspend fun generateCodeOrAnswer(prompt: String, contextCode: String = ""): String {
        return withContext(Dispatchers.IO) {
            try {
                val model = getModel()
                val fullPrompt = if (contextCode.isNotBlank()) {
                    "Current Code Context:\n```\n$contextCode\n```\n\nUser Request: $prompt"
                } else {
                    prompt
                }
                val response = model.generateContent(fullPrompt)
                response.text ?: "No response generated."
            } catch (e: Exception) {
                "Error communicating with Gemini AI: ${e.localizedMessage}\nPlease make sure your Gemini API key is configured correctly in the app settings."
            }
        }
    }

    suspend fun chat(history: List<Pair<String, String>>, currentMessage: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val model = getModel()
                val chat = model.startChat(
                    history = history.map { (role, text) ->
                        content(role) { text(text) }
                    }
                )
                val response = chat.sendMessage(currentMessage)
                response.text ?: "No response generated."
            } catch (e: Exception) {
                "Error in AI Chat: ${e.localizedMessage}"
            }
        }
    }
}
