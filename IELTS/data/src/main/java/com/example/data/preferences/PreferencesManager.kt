package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.data.models.UserPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )
    private val gson = Gson()

    fun saveUserPreferences(preferences: UserPreferences) {
        val preferencesJson = gson.toJson(preferences)
        sharedPreferences.edit().putString(KEY_USER_PREFERENCES, preferencesJson).apply()
    }

    fun getUserPreferences(): UserPreferences {
        val preferencesJson = sharedPreferences.getString(KEY_USER_PREFERENCES, null)
        return if (preferencesJson != null) {
            gson.fromJson(preferencesJson, UserPreferences::class.java)
        } else {
            UserPreferences()
        }
    }

    fun isFirstTime(): Boolean {
        return getUserPreferences().isFirstTime
    }

    companion object {
        private const val PREFERENCES_NAME = "ielts_preferences"
        private const val KEY_USER_PREFERENCES = "user_preferences"
    }
} 