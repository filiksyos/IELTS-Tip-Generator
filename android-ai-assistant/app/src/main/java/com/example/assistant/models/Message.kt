package com.example.assistant.models

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Message data class for chat messages
 *
 * @property role Role of the message sender (system, user, assistant)
 * @property content Content of the message
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    val assistant: String? = null,
    val role: String,
    var content: String,
) {
    companion object {
        const val ROLE_SYSTEM = "system"
        const val ROLE_USER = "user"
        const val ROLE_ASSISTANT = "assistant"
    }
}
