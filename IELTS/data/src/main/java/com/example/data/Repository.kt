package com.example.data

import android.util.Log
import com.example.data.Utils.YouTubeLink
import com.example.data.ai.AISearchQueryGenerator
import com.example.data.models.IELTSContent
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
                val content = queryGenerator.generateQueriesForAllCategories()

                val dashboardItems = content.mapValues { (category, ieltsContent) ->
                    createDashboardItemsForCategory(category, ieltsContent)
                }

                _dashboardItemsFlow.value = dashboardItems

                Log.d(TAG, "Successfully refreshed content: $content")
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing content", e)

                val defaultItems = DashboardCategory.values().associateWith { category ->
                    createDefaultDashboardItems(category)
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
        ieltsContent: IELTSContent
    ): List<DashboardItems> {
        val youtubeLink = YouTubeLink.getLink(ieltsContent.searchQuery)
        return listOf(
            DashboardItems(
                itemText = category.title,
                cardType = "Tip",
                color = category.color,
                query = youtubeLink,
                displayTip = ieltsContent.tip
            )
        )
    }

    /**
     * Gets dashboard items for a specific category
     * This method is used by the existing code
     */
    override fun getDashboardItems(category: DashboardCategory): List<DashboardItems> {
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
                query = YouTubeLink.getLink("IELTS ${category.title} practice"),
                displayTip = "Practice ${category.title.lowercase()} with official IELTS materials"
            )
        )
    }
}
