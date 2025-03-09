package com.example.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DashboardCategory
import com.example.data.RepositoryInterface
import com.example.data.models.IELTSContent
import kotlinx.coroutines.launch

class GetTipViewModel(
    private val repository: RepositoryInterface
) : ViewModel() {
    
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
    
    fun setSelectedCategory(category: DashboardCategory) {
        _selectedCategory.value = category
    }
    
    fun setUserInput(input: String) {
        userInput = input
    }
    
    fun generateTip() {
        val category = _selectedCategory.value ?: return
        
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                // Generate a tip for the selected category using the user input
                val tip = repository.generateTipForCategory(category, userInput)
                _generatedTip.postValue(tip)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                _error.postValue("Failed to generate tip: ${e.message}")
                _isLoading.postValue(false)
            }
        }
    }
    
    fun getCategoryName(): String {
        return _selectedCategory.value?.name?.lowercase()?.capitalize() ?: ""
    }
    
    // Extension function to capitalize first letter
    private fun String.capitalize(): String {
        return this.replaceFirstChar { it.uppercase() }
    }
} 