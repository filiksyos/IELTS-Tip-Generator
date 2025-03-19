package com.example.data

import android.util.Log
import com.example.data.ai.AISearchQueryGenerator
import com.example.data.models.IELTSContent
import com.example.data.preferences.CreditManager
import com.example.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(
    private val preferencesManager: PreferencesManager,
    private val creditManager: CreditManager
) : RepositoryInterface {
    private val TAG = "IELTS_Repository"
    private val queryGenerator = AISearchQueryGenerator(preferencesManager)

    // StateFlow to hold the dashboard items for each category
    private val _dashboardItemsFlow =
        MutableStateFlow<Map<DashboardCategory, List<DashboardItems>>>(emptyMap())
    override val dashboardItemsFlow: StateFlow<Map<DashboardCategory, List<DashboardItems>>> =
        _dashboardItemsFlow

    // StateFlow to hold the remaining credits
    private val _remainingCreditsFlow = MutableStateFlow(creditManager.getRemainingCredits())
    override val remainingCreditsFlow: StateFlow<Int> = _remainingCreditsFlow

    // No automatic initialization of queries
    init {
        // Initialize with empty data
        Log.d(TAG, "Repository initialized")
        setDefaultItems()
        updateRemainingCredits()
    }
    
    /**
     * Generate a tip for a specific category with user input
     * This is the only method that should trigger API requests
     */
    override suspend fun generateTipForCategory(
        category: DashboardCategory, 
        userInput: String
    ): IELTSContent = withContext(Dispatchers.IO) {
        try {
            Log.e(TAG, "STARTING TIP GENERATION: category=$category, input=$userInput")
            
            // Check if we have credits available
            if (!creditManager.useCredit()) {
                Log.e(TAG, "No credits remaining, cannot generate tip")
                updateRemainingCredits()
                return@withContext noCreditContent(category)
            }
            
            // Update remaining credits after using one
            updateRemainingCredits()
            
            // Validate input
            if (userInput.isBlank()) {
                Log.e(TAG, "User input is blank, cannot generate tip")
                return@withContext fallbackContent(category)
            }
            
            Log.e(TAG, "Calling AISearchQueryGenerator.generateTipForSingleCategory")
            val result = queryGenerator.generateTipForSingleCategory(category, userInput)
            
            Log.e(TAG, "Received result from AISearchQueryGenerator: $result")
            
            // Validate result
            if (result.tip.isBlank() || result.explanation.isBlank()) {
                Log.e(TAG, "Generated tip or explanation is blank, using fallback")
                return@withContext fallbackContent(category)
            }
            
            return@withContext result
        } catch (e: Exception) {
            Log.e(TAG, "Error generating tip for category: $category", e)
            return@withContext fallbackContent(category)
        }
    }
    
    /**
     * Update the remaining credits flow
     */
    private fun updateRemainingCredits() {
        _remainingCreditsFlow.value = creditManager.getRemainingCredits()
    }
    
    /**
     * Set default empty items for all categories
     */
    private fun setDefaultItems() {
        val emptyItems = DashboardCategory.values().associateWith { emptyList<DashboardItems>() }
        _dashboardItemsFlow.value = emptyItems
        Log.d(TAG, "Default empty items set")
    }
    
    /**
     * Fallback content in case of API failure
     */
    private fun fallbackContent(category: DashboardCategory): IELTSContent {
        Log.e(TAG, "USING FALLBACK CONTENT for category: $category")
        return IELTSContent(
            tip = "Tip generation failed. Please try again.",
            explanation = "We couldn't generate a tip for ${category.name.lowercase()} at this time. Please check your internet connection and try again."
        )
    }
    
    /**
     * No credit content when daily limit is reached
     */
    private fun noCreditContent(category: DashboardCategory): IELTSContent {
        Log.e(TAG, "NO CREDITS REMAINING for category: $category")
        return IELTSContent(
            tip = "Daily credit limit reached.",
            explanation = "You've reached your daily limit for generating tips. Please try again tomorrow when your credits will be reset."
        )
    }
    
    /**
     * Creates dashboard items for a category from IELTS content
     */
    fun createDashboardItemsForCategory(
        category: DashboardCategory,
        content: IELTSContent
    ): List<DashboardItems> {
        return listOf(
            DashboardItems(
                itemText = category.title,
                cardType = "Tip",
                color = category.color,
                explanation = content.explanation,
                displayTip = content.tip,
                id = System.currentTimeMillis().toString()
            )
        )
    }
}
