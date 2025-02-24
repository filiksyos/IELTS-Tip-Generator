package com.example.assistant.models

import android.annotation.SuppressLint
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Required

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ChatCompletion(
    @SerialName("model")
    @Required
    val model: String = "mixtral-8x7b-32768",
    val messages: List<Map<String, String>>,
    val stream: Boolean = false
) {
    init {
        Log.d("ChatCompletion", "Creating chat completion")
        Log.d("ChatCompletion", "Model: $model")
        Log.d("ChatCompletion", "Messages count: ${messages.size}")
        Log.d("ChatCompletion", "Stream: $stream")
    }
}