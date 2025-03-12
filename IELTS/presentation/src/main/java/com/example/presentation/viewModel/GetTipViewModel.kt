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
    
    init {
        Log.e(TAG, "GetTipViewModel initialized")
    }
    
    fun setSelectedCategory(category: DashboardCategory) {
        Log.e(TAG, "Setting selected category: $category")
        _selectedCategory.value = category
    }
    
    fun setUserInput(input: String) {
        Log.e(TAG, "Setting user input: $input")
        userInput = input
    }
    
    fun generateTip() {
        val category = _selectedCategory.value
        if (category == null) {
            Log.e(TAG, "Cannot generate tip: No category selected")
            _error.value = "Please select a category first"
            return
        }
        
        if (userInput.isBlank()) {
            Log.e(TAG, "Cannot generate tip: User input is blank")
            _error.value = "Please enter your specific issue"
            return
        }
        
        Log.e(TAG, "GENERATING TIP for category: $category with input: $userInput")
        _isLoading.value = true
        // Reset error states
        _networkError.value = false
        _serverError.value = false
        _error.value = ""
        
        viewModelScope.launch {
            try {
                // Generate a tip for the selected category using the user input
                Log.e(TAG, "Calling repository.generateTipForCategory")
                val tip = repository.generateTipForCategory(category, userInput)
                Log.e(TAG, "Received tip from repository: $tip")
                _generatedTip.postValue(tip)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                Log.e(TAG, "Error generating tip: ${e.message}", e)
                
                when (e) {
                    // Network-related exceptions
                    is IOException, is SocketTimeoutException, is UnknownHostException -> {
                        Log.e(TAG, "Network error: ${e.message}")
                        _networkError.postValue(true)
                    }
                    // Server-related or other exceptions
                    else -> {
                        Log.e(TAG, "Server error: ${e.message}")
                        _serverError.postValue(true)
                    }
                }
                
                _error.postValue("Failed to generate tip: ${e.message}")
                _isLoading.postValue(false)
            }
        }
    }
    
    fun resetErrorStates() {
        _networkError.value = false
        _serverError.value = false
        _error.value = ""
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