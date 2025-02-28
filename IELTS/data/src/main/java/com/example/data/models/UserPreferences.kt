package com.example.data.models

data class UserPreferences(
    val weakestSkill: String = "",
    val targetBandScore: Float = 0.0f,
    val focusAreas: List<String> = emptyList(),
    val studyGoal: String = "",
    val isFirstTime: Boolean = true
)

enum class IELTSSkill {
    READING,
    LISTENING,
    WRITING,
    SPEAKING
} 