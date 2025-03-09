package com.example.data

import com.example.data.models.IELTSContent
import kotlinx.coroutines.flow.StateFlow

interface RepositoryInterface {
    /**
     * StateFlow to observe dashboard items
     */
    val dashboardItemsFlow: StateFlow<Map<DashboardCategory, List<DashboardItems>>>

    /**
     * Generates a tip for a specific category using the provided user input
     * This is the only method that should trigger API requests
     * @param category The IELTS category to generate a tip for
     * @param userInput The user's specific problem or issue
     * @return The generated IELTS content (tip and explanation)
     */
    suspend fun generateTipForCategory(category: DashboardCategory, userInput: String): IELTSContent
}
