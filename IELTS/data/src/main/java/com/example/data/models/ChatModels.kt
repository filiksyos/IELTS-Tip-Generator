package com.example.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for chat completion API
 */
@Serializable
data class ChatCompletion(
    val model: String = "mixtral-8x7b-32768",
    val messages: List<Map<String, String>>,
    val stream: Boolean = false,
    val temperature: Double = 0.2,
    @SerialName("max_tokens")
    val maxTokens: Int = 150
)

/**
 * Response from chat completion API
 */
@Serializable
data class ChatResponse(
    val id: String,
    @SerialName("object")
    val type: String,
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val index: Int,
    val message: Message? = null,
    val delta: Delta? = null,
    @SerialName("finish_reason")
    val finishReason: String? = null
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class Delta(
    val content: String? = null
) 