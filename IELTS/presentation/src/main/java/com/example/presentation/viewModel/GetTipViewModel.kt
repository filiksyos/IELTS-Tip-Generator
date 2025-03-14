package com.example.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DashboardCategory
import com.example.data.RepositoryInterface
import com.example.data.models.IELTSContent
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class GetTipViewModel(
    private val repository: RepositoryInterface
) : ViewModel() {
    private val TAG = "IELTS_GetTipViewModel"
    
    // Selected category for tip generation
    private val _selectedCategory = MutableLiveData<DashboardCategory>()
    val selectedCategory: LiveData<DashboardCategory> = _selectedCategory
    
    // User input for the selected category
    private var userInput: String = ""
    
    // Generated tip content
    private val _generatedTip = MutableLiveData<IELTSContent>()
    val generatedTip: LiveData<IELTSContent> = _generatedTip
    
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Error state
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    // Network error state
    private val _networkError = MutableLiveData<Boolean>()
    val networkError: LiveData<Boolean> = _networkError
    
    // Server error state
    private val _serverError = MutableLiveData<Boolean>()
    val serverError: LiveData<Boolean> = _serverError
    
    // Credit exhausted state
    private val _creditExhausted = MutableLiveData<Boolean>()
    val creditExhausted: LiveData<Boolean> = _creditExhausted
    
    // Remaining credits
    private val _remainingCredits = MutableLiveData<Int>()
    val remainingCredits: LiveData<Int> = _remainingCredits
    
    init {
        Log.e(TAG, "GetTipViewModel initialized")
        updateRemainingCredits()
    }
    
    /**
     * Set the selected category
     */
    fun setSelectedCategory(category: DashboardCategory) {
        _selectedCategory.value = category
        // Clear the previously generated tip when selecting a new category
        clearGeneratedTip()
    }
    
    /**
     * Set the user input
     */
    fun setUserInput(input: String) {
        userInput = input
    }
    
    /**
     * Clear the generated tip
     */
    fun clearGeneratedTip() {
        _generatedTip.value = null
    }
    
    /**
     * Generate a tip for the selected category
     */
    fun generateTip() {
        val category = _selectedCategory.value ?: return
        
        if (userInput.isBlank()) {
            _error.value = "Please enter a specific issue or question"
            return
        }
        
        _isLoading.value = true
        _networkError.value = false
        _serverError.value = false
        _creditExhausted.value = false
        
        viewModelScope.launch {
            try {
                val result = repository.generateTipForCategory(category, userInput)
                
                // Check if this is a credit exhausted response
                if (result.tip == "Daily credit limit reached.") {
                    _creditExhausted.postValue(true)
                } else {
                    _generatedTip.postValue(result)
                }
                
                // Update remaining credits
                updateRemainingCredits()
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    
    /**
     * Update remaining credits
     */
    private fun updateRemainingCredits() {
        viewModelScope.launch {
            repository.remainingCreditsFlow.collect { credits ->
                _remainingCredits.postValue(credits)
            }
        }
    }
    
    /**
     * Handle errors during tip generation
     */
    private fun handleError(e: Exception) {
        when (e) {
            is IOException, is SocketTimeoutException, is UnknownHostException -> {
                _networkError.postValue(true)
                Log.e(TAG, "Network error: ${e.message}", e)
            }
            else -> {
                _serverError.postValue(true)
                Log.e(TAG, "Server error: ${e.message}", e)
            }
        }
    }
    
    /**
     * Reset error states
     */
    fun resetErrorStates() {
        _error.value = ""
        _networkError.value = false
        _serverError.value = false
        _creditExhausted.value = false
    }
    
    fun getCategoryName(): String {
        val name = _selectedCategory.value?.name?.lowercase()?.capitalize() ?: ""
        Log.d(TAG, "Getting category name: $name")
        return name
    }
    
    // Extension function to capitalize first letter
    private fun String.capitalize(): String {
        return this.replaceFirstChar { it.uppercase() }
    }
} 