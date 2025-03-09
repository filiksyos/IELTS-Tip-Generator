package com.example.data

import android.util.Log
import com.example.data.ai.AISearchQueryGenerator
import com.example.data.models.IELTSContent
import com.example.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(
    private val preferencesManager: PreferencesManager
) : RepositoryInterface {
    private val TAG = "IELTS_Repository"
    private val queryGenerator = AISearchQueryGenerator(preferencesManager)

    // StateFlow to hold the dashboard items for each category
    private val _dashboardItemsFlow =
        MutableStateFlow<Map<DashboardCategory, List<DashboardItems>>>(emptyMap())
    override val dashboardItemsFlow: StateFlow<Map<DashboardCategory, List<DashboardItems>>> =
        _dashboardItemsFlow

    // No automatic initialization of queries
    init {
        // Initialize with empty data
        Log.d(TAG, "Repository initialized")
        setDefaultItems()
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
