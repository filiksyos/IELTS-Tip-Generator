package com.example.data.models

data class UserPreferences(
    val studyGoal: String = "",
    val isFirstTime: Boolean = true
)

enum class IELTSSkill {
    READING,
    LISTENING,
    WRITING,
    SPEAKING
} 