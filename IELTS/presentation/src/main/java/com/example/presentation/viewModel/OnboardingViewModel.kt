package com.example.presentation.viewModel

import androidx.lifecycle.ViewModel
import com.example.data.models.UserPreferences
import com.example.data.preferences.PreferencesManager

class OnboardingViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    fun savePreferences(preferences: UserPreferences) {
        preferencesManager.saveUserPreferences(preferences)
    }

    fun isFirstTime(): Boolean {
        return preferencesManager.isFirstTime()
    }
} 