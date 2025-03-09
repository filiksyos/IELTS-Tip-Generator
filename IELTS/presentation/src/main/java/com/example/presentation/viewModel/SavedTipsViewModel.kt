package com.example.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.data.DashboardCategory
import com.example.data.models.SavedTip
import com.example.data.preferences.SavedTipsManager

class SavedTipsViewModel(
    private val savedTipsManager: SavedTipsManager
) : ViewModel() {
    
    private val _savedTips = MutableLiveData<List<SavedTip>>()
    val savedTips: LiveData<List<SavedTip>> = _savedTips
    
    init {
        loadSavedTips()
    }
    
    /**
     * Load all saved tips
     */
    fun loadSavedTips() {
        val tips = savedTipsManager.getSavedTips()
        _savedTips.value = tips
    }
    
    /**
     * Save a new tip
     */
    fun saveTip(tip: SavedTip) {
        savedTipsManager.saveTip(tip)
        loadSavedTips() // Reload tips after saving
    }
    
    /**
     * Delete a tip by ID
     */
    fun deleteTip(tipId: String) {
        savedTipsManager.deleteTip(tipId)
        loadSavedTips() // Reload tips after deleting
    }
    
    /**
     * Get tips for a specific category
     */
    fun getTipsForCategory(category: DashboardCategory): List<SavedTip> {
        return _savedTips.value?.filter { it.category == category } ?: emptyList()
    }
    
    /**
     * Clear all saved tips
     */
    fun clearAllTips() {
        savedTipsManager.clearAllTips()
        loadSavedTips() // Reload tips after clearing
    }
} 