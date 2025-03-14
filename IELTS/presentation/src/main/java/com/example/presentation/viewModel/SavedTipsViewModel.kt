package com.example.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DashboardCategory
import com.example.data.models.SavedTip
import com.example.data.preferences.SavedTipsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    
    // Flag to track if data is currently being loaded
    private var isLoading = false
    
    init {
        loadSavedTips()
    }
    
    /**
     * Load all saved tips
     */
    fun loadSavedTips() {
        if (isLoading) return
        
        isLoading = true
        viewModelScope.launch {
            try {
                // Load tips in background thread
                val tips = withContext(Dispatchers.IO) {
                    savedTipsManager.getSavedTips().sortedByDescending { it.timestamp }
                }
                
                android.util.Log.d(TAG, "Loading saved tips. Count: ${tips.size}, Latest tip ID: ${_latestTipId.value}")
                _savedTips.value = tips
                
                // Also load favorites if needed
                if (_showingFavorites.value == true) {
                    loadFavoriteTips()
                }
            } finally {
                isLoading = false
            }
        }
    }
    
    /**
     * Load favorite tips
     */
    fun loadFavoriteTips() {
        if (isLoading) return
        
        isLoading = true
        viewModelScope.launch {
            try {
                // Load favorites in background thread
                val favorites = withContext(Dispatchers.IO) {
                    savedTipsManager.getFavoriteTips().sortedByDescending { it.timestamp }
                }
                
                android.util.Log.d(TAG, "Loading favorite tips. Count: ${favorites.size}, Latest tip ID: ${_latestTipId.value}")
                _favoriteTips.value = favorites
            } finally {
                isLoading = false
            }
        }
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
        
        viewModelScope.launch(Dispatchers.IO) {
            savedTipsManager.saveTip(tip)
            
            withContext(Dispatchers.Main) {
                savedStateHandle["latest_tip_id"] = tip.id
                android.util.Log.d(TAG, "Set latest tip ID to: ${tip.id}")
                
                // Record the creation time
                newlyCreatedTipInfo = tip.id to System.currentTimeMillis()
                
                // Reload tips after saving
                loadSavedTips()
            }
        }
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
        viewModelScope.launch(Dispatchers.IO) {
            savedTipsManager.deleteTip(tipId)
            
            // Reload tips after deleting
            loadSavedTips()
        }
    }
    
    /**
     * Toggle favorite status of a tip
     */
    fun toggleFavorite(tipId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            savedTipsManager.toggleFavorite(tipId)
            
            // Reload tips after toggling favorite
            loadSavedTips()
        }
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
        viewModelScope.launch(Dispatchers.IO) {
            savedTipsManager.clearAllTips()
            
            // Reload tips after clearing
            loadSavedTips()
        }
    }
} 