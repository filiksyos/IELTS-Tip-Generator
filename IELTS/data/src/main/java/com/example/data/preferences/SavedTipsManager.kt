package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.data.DashboardCategory
import com.example.data.models.SavedTip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Manager class for saved tips using SharedPreferences
 */
class SavedTipsManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    // Cache for saved tips to avoid repeated deserialization
    private var cachedTips: List<SavedTip>? = null
    private var cachedFavoriteTips: List<SavedTip>? = null

    /**
     * Save a tip to SharedPreferences
     */
    fun saveTip(savedTip: SavedTip) {
        val savedTips = getSavedTips().toMutableList()
        savedTips.add(savedTip)
        val tipsJson = gson.toJson(savedTips)
        sharedPreferences.edit().putString(KEY_SAVED_TIPS, tipsJson).apply()
        
        // Update cache
        cachedTips = savedTips
        // Update favorites cache if needed
        if (savedTip.isFavorite) {
            cachedFavoriteTips = cachedFavoriteTips?.plus(savedTip) ?: listOf(savedTip)
        }
    }

    /**
     * Get all saved tips from SharedPreferences
     */
    fun getSavedTips(): List<SavedTip> {
        // Return cached tips if available
        cachedTips?.let { return it }
        
        val tipsJson = sharedPreferences.getString(KEY_SAVED_TIPS, null)
        val tips = if (tipsJson != null) {
            val type = object : TypeToken<List<SavedTip>>() {}.type
            gson.fromJson<List<SavedTip>>(tipsJson, type)
        } else {
            emptyList()
        }
        
        // Cache the result
        cachedTips = tips
        return tips
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
        
        // Update caches
        cachedTips = updatedTips
        cachedFavoriteTips = cachedFavoriteTips?.filter { it.id != tipId }
    }

    /**
     * Toggle favorite status of a tip
     */
    fun toggleFavorite(tipId: String) {
        val savedTips = getSavedTips().toMutableList()
        val tipIndex = savedTips.indexOfFirst { it.id == tipId }
        
        if (tipIndex != -1) {
            val tip = savedTips[tipIndex]
            val updatedTip = tip.copy(isFavorite = !tip.isFavorite)
            savedTips[tipIndex] = updatedTip
            val tipsJson = gson.toJson(savedTips)
            sharedPreferences.edit().putString(KEY_SAVED_TIPS, tipsJson).apply()
            
            // Update caches
            cachedTips = savedTips
            cachedFavoriteTips = null // Invalidate favorites cache
        }
    }

    /**
     * Get all favorite tips
     */
    fun getFavoriteTips(): List<SavedTip> {
        // Return cached favorites if available
        cachedFavoriteTips?.let { return it }
        
        val favorites = getSavedTips().filter { it.isFavorite }
        
        // Cache the result
        cachedFavoriteTips = favorites
        return favorites
    }

    /**
     * Clear all saved tips
     */
    fun clearAllTips() {
        sharedPreferences.edit().remove(KEY_SAVED_TIPS).apply()
        
        // Clear caches
        cachedTips = emptyList()
        cachedFavoriteTips = emptyList()
    }
    
    /**
     * Suspend function to get saved tips off the main thread
     */
    suspend fun getSavedTipsAsync(): List<SavedTip> = withContext(Dispatchers.IO) {
        getSavedTips()
    }
    
    /**
     * Suspend function to get favorite tips off the main thread
     */
    suspend fun getFavoriteTipsAsync(): List<SavedTip> = withContext(Dispatchers.IO) {
        getFavoriteTips()
    }

    companion object {
        private const val PREFERENCES_NAME = "ielts_saved_tips"
        private const val KEY_SAVED_TIPS = "saved_tips"
    }
} 