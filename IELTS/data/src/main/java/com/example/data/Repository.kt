package com.example.data

import android.util.Log
import com.example.data.Utils.YouTubeLink
import com.example.data.ai.AISearchQueryGenerator
import com.example.data.preferences.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
        refreshQueries()
    }

    /**
     * Refreshes the AI-generated queries
     */
    override fun refreshQueries() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Generate queries for all categories
                val queries = queryGenerator.generateQueriesForAllCategories()

                // Create dashboard items for each category
                val dashboardItems = queries.mapValues { (category, query) ->
                    createDashboardItemsForCategory(category, query)
                }

                // Update the StateFlow
                _dashboardItemsFlow.value = dashboardItems

                Log.d(TAG, "Successfully refreshed queries: $queries")
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing queries", e)

                // Fallback to default items if there's an error
                val defaultItems = DashboardCategory.values().associateWith { category ->
                    createDashboardItemsForCategory(category, "${category.title} practice")
                }

                _dashboardItemsFlow.value = defaultItems
            }
        }
    }

    /**
     * Creates dashboard items for a category with the given query
     */
    private fun createDashboardItemsForCategory(
        category: DashboardCategory,
        query: String
    ): List<DashboardItems> {
        val youtubeLink = YouTubeLink.getLink(query)
        return listOf(
            DashboardItems(
                itemText = category.title,
                cardType = query,
                color = category.color,
                query = youtubeLink,
                displayQuery = query,
                itemImageUri = category.iconUri
            )
        )
    }

    /**
     * Gets dashboard items for a specific category
     * This method is used by the existing code
     */
    override fun getDashboardItems(category: DashboardCategory): List<DashboardItems> {
        // Get items from the StateFlow if available
        val items = _dashboardItemsFlow.value[category]

        // Return items if available, otherwise return fallback items
        return items ?: fallbackDashboardItems(category)
    }

    /**
     * Fallback method to create dashboard items if AI generation fails
     */
    private fun fallbackDashboardItems(category: DashboardCategory): List<DashboardItems> {
        return listOf(
            DashboardItems(
                itemText = category.title,
                cardType = "Lesson",
                color = category.color,
                query = YouTubeLink.getLink("${category.title} IELTS Lesson"),
                displayQuery = "${category.title} IELTS Lesson",
                itemImageUri = category.iconUri
            )
        )
    }
}
