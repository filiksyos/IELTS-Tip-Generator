package com.example.assistant.models

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ChatResponse(
    val id: String,
    @SerialName("object")
    val type: String,
    val choices: List<Choice>
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Choice(
    val index: Int,
    val message: Message? = null,
    val delta: Delta? = null,
    @SerialName("finish_reason")
    val finishReason: String? = null
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Delta(
    val content: String? = null
)