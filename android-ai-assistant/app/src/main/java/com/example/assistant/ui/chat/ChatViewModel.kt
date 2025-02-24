package com.example.assistant.ui.chat

import com.example.assistant.api.ChatApi
import com.example.assistant.models.ChatCompletion
import com.example.assistant.models.ChatResponse
import android.util.Log
import kotlinx.serialization.json.Json

class ChatViewModel(
    private val chatApi: ChatApi
) {
    private val TAG = "ChatViewModel"
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getCompletion(chatCompletion: ChatCompletion): String {
        return try {
            Log.d(TAG, "Making streaming API request with completion: $chatCompletion")
            val response = chatApi.getChatCompletion(chatCompletion)
            
            // Read response as text
            val reader = response.byteStream().bufferedReader()
            val content = StringBuilder()
            
            // Process each SSE line
            reader.useLines { lines ->
                lines.forEach { line ->
                    if (line.startsWith("data: ")) {
                        val data = line.substring(6) // Remove "data: " prefix
                        if (data != "[DONE]") {
                            try {
                                val chatResponse = json.decodeFromString<ChatResponse>(data)
                                chatResponse.choices.firstOrNull()?.delta?.content?.let {
                                    content.append(it)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing SSE data: $data", e)
                            }
                        }
                    }
                }
            }
            
            content.toString()
        } catch (e: Exception) {
            Log.e(TAG, "API error", e)
            throw e
        }
    }
} 