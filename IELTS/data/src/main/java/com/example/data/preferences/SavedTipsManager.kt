package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.data.DashboardCategory
import com.example.data.models.SavedTip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Manager class for saved tips using SharedPreferences
 */
class SavedTipsManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )
    private val gson = Gson()

    /**
     * Save a tip to SharedPreferences
     */
    fun saveTip(savedTip: SavedTip) {
        val savedTips = getSavedTips().toMutableList()
        savedTips.add(savedTip)
        val tipsJson = gson.toJson(savedTips)
        sharedPreferences.edit().putString(KEY_SAVED_TIPS, tipsJson).apply()
    }

    /**
     * Get all saved tips from SharedPreferences
     */
    fun getSavedTips(): List<SavedTip> {
        val tipsJson = sharedPreferences.getString(KEY_SAVED_TIPS, null)
        return if (tipsJson != null) {
            val type = object : TypeToken<List<SavedTip>>() {}.type
            gson.fromJson(tipsJson, type)
        } else {
            emptyList()
        }
    }

    /**
     * Get saved tips for a specific category
     */
    fun getSavedTipsForCategory(category: DashboardCategory): List<SavedTip> {
        return getSavedTips().filter { it.category == category }
    }

    /**
     * Delete a saved tip by ID
     */
    fun deleteTip(tipId: String) {
        val savedTips = getSavedTips().toMutableList()
        val updatedTips = savedTips.filter { it.id != tipId }
        val tipsJson = gson.toJson(updatedTips)
        sharedPreferences.edit().putString(KEY_SAVED_TIPS, tipsJson).apply()
    }

    /**
     * Clear all saved tips
     */
    fun clearAllTips() {
        sharedPreferences.edit().remove(KEY_SAVED_TIPS).apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "ielts_saved_tips"
        private const val KEY_SAVED_TIPS = "saved_tips"
    }
} 