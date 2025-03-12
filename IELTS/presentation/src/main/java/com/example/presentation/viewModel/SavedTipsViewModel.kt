package com.example.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.data.DashboardCategory
import com.example.data.models.SavedTip
import com.example.data.preferences.SavedTipsManager

class SavedTipsViewModel(
    private val savedTipsManager: SavedTipsManager,
    private val savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {
    
    private val TAG = "SavedTipsViewModel"
    
    private val _savedTips = MutableLiveData<List<SavedTip>>()
    val savedTips: LiveData<List<SavedTip>> = _savedTips
    
    private val _favoriteTips = MutableLiveData<List<SavedTip>>()
    val favoriteTips: LiveData<List<SavedTip>> = _favoriteTips
    
    // Track whether we're showing favorites or all tips
    private val _showingFavorites = MutableLiveData<Boolean>(false)
    val showingFavorites: LiveData<Boolean> = _showingFavorites
    
    // Track the latest created tip ID
    private val _latestTipId = savedStateHandle.getLiveData<String>("latest_tip_id")
    val latestTipId: LiveData<String> = _latestTipId
    
    // Track newly created tip with timestamp
    private var newlyCreatedTipInfo: Pair<String, Long>? = null
    
    init {
        loadSavedTips()
        loadFavoriteTips()
    }
    
    /**
     * Load all saved tips
     */
    fun loadSavedTips() {
        val tips = savedTipsManager.getSavedTips().sortedByDescending { it.timestamp }
        android.util.Log.d(TAG, "Loading saved tips. Count: ${tips.size}, Latest tip ID: ${_latestTipId.value}")
        _savedTips.value = tips
    }
    
    /**
     * Load favorite tips
     */
    fun loadFavoriteTips() {
        val favorites = savedTipsManager.getFavoriteTips().sortedByDescending { it.timestamp }
        android.util.Log.d(TAG, "Loading favorite tips. Count: ${favorites.size}, Latest tip ID: ${_latestTipId.value}")
        _favoriteTips.value = favorites
    }
    
    /**
     * Toggle between showing all tips and favorites
     */
    fun toggleFavoritesView() {
        _showingFavorites.value = !(_showingFavorites.value ?: false)
    }
    
    /**
     * Set whether to show favorites or all tips
     */
    fun setShowingFavorites(showFavorites: Boolean) {
        _showingFavorites.value = showFavorites
    }
    
    /**
     * Save a new tip
     */
    fun saveTip(tip: SavedTip) {
        android.util.Log.d(TAG, "Saving new tip with ID: ${tip.id}")
        savedTipsManager.saveTip(tip)
        savedStateHandle["latest_tip_id"] = tip.id
        android.util.Log.d(TAG, "Set latest tip ID to: ${tip.id}")
        // Record the creation time
        newlyCreatedTipInfo = tip.id to System.currentTimeMillis()
        loadSavedTips() // Reload tips after saving
        loadFavoriteTips() // Also reload favorites
    }
    
    /**
     * Check if a tip was just created (within last 2 seconds)
     */
    fun isNewlyCreatedTip(tipId: String): Boolean {
        return newlyCreatedTipInfo?.let { (id, timestamp) ->
            id == tipId && (System.currentTimeMillis() - timestamp) < 2000
        } ?: false
    }
    
    /**
     * Clear creation tracking after animation
     */
    fun clearNewTipTracking() {
        newlyCreatedTipInfo = null
    }
    
    /**
     * Delete a tip by ID
     */
    fun deleteTip(tipId: String) {
        savedTipsManager.deleteTip(tipId)
        loadSavedTips() // Reload tips after deleting
        loadFavoriteTips() // Also reload favorites
    }
    
    /**
     * Toggle favorite status of a tip
     */
    fun toggleFavorite(tipId: String) {
        savedTipsManager.toggleFavorite(tipId)
        loadSavedTips() // Reload tips after toggling favorite
        loadFavoriteTips() // Also reload favorites
    }
    
    /**
     * Get tips for a specific category
     */
    fun getTipsForCategory(category: DashboardCategory): List<SavedTip> {
        return _savedTips.value?.filter { it.category == category } ?: emptyList()
    }
    
    /**
     * Clear the latest tip ID
     */
    fun clearLatestTipId() {
        _latestTipId.value = null
    }
    
    /**
     * Clear all saved tips
     */
    fun clearAllTips() {
        savedTipsManager.clearAllTips()
        loadSavedTips() // Reload tips after clearing
        loadFavoriteTips() // Also reload favorites
    }
} 