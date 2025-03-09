package com.example.data

import android.util.Log
import com.example.data.ai.AISearchQueryGenerator
import com.example.data.models.IELTSContent
import com.example.data.preferences.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Repository(
    private val preferencesManager: PreferencesManager
) : RepositoryInterface {
    private val TAG = "Repository"
    private val queryGenerator = AISearchQueryGenerator(preferencesManager)

    // StateFlow to hold the dashboard items for each category
    private val _dashboardItemsFlow =
        MutableStateFlow<Map<DashboardCategory, List<DashboardItems>>>(emptyMap())
    override val dashboardItemsFlow: StateFlow<Map<DashboardCategory, List<DashboardItems>>> =
        _dashboardItemsFlow

    // Initialize the repository by generating AI queries
    init {
        initializeQueries()
    }
    
    /**
     * Initialize queries without causing conflicts with suspend functions
     */
    private fun initializeQueries() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val content = queryGenerator.generateQueriesForAllCategories()
                updateDashboardItems(content)
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing content", e)
                setDefaultItems()
            }
        }
    }

    /**
     * Updates the dashboard items with the given content
     */
    private fun updateDashboardItems(content: Map<DashboardCategory, IELTSContent>) {
        val dashboardItems = content.mapValues { (category, ieltsContent) ->
            createDashboardItemsForCategory(category, ieltsContent)
        }
        _dashboardItemsFlow.value = dashboardItems
    }

    /**
     * Sets default items if AI generation fails
     */
    private fun setDefaultItems() {
        val defaultItems = DashboardCategory.values().associateWith { category ->
            createDefaultDashboardItems(category)
        }
        _dashboardItemsFlow.value = defaultItems
    }

    /**
     * Gets dashboard items for all categories
     * This implements the RepositoryInterface method
     */
    override suspend fun getDashboardItems(): Map<DashboardCategory, IELTSContent> = withContext(Dispatchers.IO) {
        return@withContext queryGenerator.generateQueriesForAllCategories()
    }

    /**
     * Refreshes the AI-generated queries
     * This implements the RepositoryInterface method
     */
    override suspend fun refreshQueries(): Map<DashboardCategory, IELTSContent> = withContext(Dispatchers.IO) {
        try {
            val content = queryGenerator.generateQueriesForAllCategories()
            updateDashboardItems(content)
            return@withContext content
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing content", e)
            setDefaultItems()
            throw e
        }
    }

    /**
     * Creates dashboard items for a category with the given query
     */
    private fun createDashboardItemsForCategory(
        category: DashboardCategory,
        ieltsContent: IELTSContent
    ): List<DashboardItems> {
        return listOf(
            DashboardItems(
                itemText = category.title,
                cardType = "Tip",
                color = category.color,
                explanation = ieltsContent.explanation,
                displayTip = ieltsContent.tip
            )
        )
    }

    /**
     * Gets dashboard items for a specific category
     * This is a helper method used by the existing code
     */
    fun getDashboardItemsForCategory(category: DashboardCategory): List<DashboardItems> {
        return _dashboardItemsFlow.value[category] ?: createDefaultDashboardItems(category)
    }

    /**
     * Fallback method to create dashboard items if AI generation fails
     */
    private fun createDefaultDashboardItems(category: DashboardCategory): List<DashboardItems> {
        return listOf(
            DashboardItems(
                itemText = category.title,
                cardType = "Tip",
                color = category.color,
                explanation = "Focus on official IELTS ${category.title.lowercase()} practice materials to familiarize yourself with the exam format and requirements.",
                displayTip = "Practice ${category.title.lowercase()} with official IELTS materials"
            )
        )
    }

    /**
     * Generates a tip for a specific category using the provided user input
     * @param category The IELTS category to generate a tip for
     * @param userInput The user's specific problem or issue
     * @return The generated IELTS content (tip and explanation)
     */
    override suspend fun generateTipForCategory(category: DashboardCategory, userInput: String): IELTSContent = withContext(Dispatchers.IO) {
        return@withContext queryGenerator.generateTipForSingleCategory(category, userInput)
    }
}
