package com.example.assistant.models

import android.annotation.SuppressLint
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Required

/**
 * Request body for chat completion API
 *
 * @property model Model to use for completion
 * @property messages List of messages in the conversation
 * @property temperature Controls randomness (0.0-1.0), lower is more deterministic
 * @property max_tokens Maximum number of tokens to generate
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ChatCompletion(
    @SerialName("model")
    @Required
    val model: String = "mixtral-8x7b-32768",
    val messages: List<Map<String, String>>,
    val stream: Boolean = false,
    val temperature: Double = 0.2,
    val max_tokens: Int? = null
) {
    init {
        Log.d("ChatCompletion", "Creating chat completion")
        Log.d("ChatCompletion", "Model: $model")
        Log.d("ChatCompletion", "Messages count: ${messages.size}")
        Log.d("ChatCompletion", "Stream: $stream")
    }

    companion object {
        /**
         * Creates a chat completion request with the given settings and messages
         */
        fun create(settings: Settings, messages: List<Message>): ChatCompletion {
            return ChatCompletion(
                model = settings.model.id,
                messages = messages.map { mapOf("role" to it.role, "content" to it.content) },
                temperature = settings.temperature,
                max_tokens = settings.maxTokens
            )
        }
    }
}