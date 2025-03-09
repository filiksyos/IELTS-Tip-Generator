package com.example.presentation.viewModel

import androidx.lifecycle.ViewModel
import com.example.data.models.UserPreferences
import com.example.data.preferences.PreferencesManager

class OnboardingViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // Temporary storage for onboarding data
    private var readingProblems: String = ""
    private var listeningProblems: String = ""
    private var speakingProblems: String = ""
    private var writingProblems: String = ""
    private var studyGoal: String = ""

    // Setters for each field
    fun setReadingProblems(problems: String) {
        readingProblems = problems
    }

    fun setListeningProblems(problems: String) {
        listeningProblems = problems
    }

    fun setSpeakingProblems(problems: String) {
        speakingProblems = problems
    }

    fun setWritingProblems(problems: String) {
        writingProblems = problems
    }

    fun setStudyGoal(goal: String) {
        studyGoal = goal
    }

    // Getters for each field
    fun getReadingProblems(): String = readingProblems
    fun getListeningProblems(): String = listeningProblems
    fun getSpeakingProblems(): String = speakingProblems
    fun getWritingProblems(): String = writingProblems
    fun getStudyGoal(): String = studyGoal

    fun savePreferences(preferences: UserPreferences) {
        preferencesManager.saveUserPreferences(preferences)
    }

    fun isFirstTime(): Boolean {
        return preferencesManager.isFirstTime()
    }
} 