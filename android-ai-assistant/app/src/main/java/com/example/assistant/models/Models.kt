package com.example.assistant.models

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Model(
    val name: String
) {
    companion object {
        const val DEFAULT_MODEL = "mixtral-8x7b-32768"
    }
}
