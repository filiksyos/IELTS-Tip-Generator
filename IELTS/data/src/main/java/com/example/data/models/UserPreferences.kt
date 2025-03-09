package com.example.data.models

data class UserPreferences(
    val readingProblems: String = "",
    val listeningProblems: String = "",
    val writingProblems: String = "",
    val speakingProblems: String = "",
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