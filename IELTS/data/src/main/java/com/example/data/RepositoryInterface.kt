package com.example.data

import com.example.data.models.IELTSContent
import kotlinx.coroutines.flow.StateFlow

interface RepositoryInterface {
    suspend fun getDashboardItems(): Map<DashboardCategory, IELTSContent>
    
    /**
     * Refreshes the AI-generated queries
     */
    suspend fun refreshQueries(): Map<DashboardCategory, IELTSContent>
    
    /**
     * StateFlow to observe dashboard items
     */
    val dashboardItemsFlow: StateFlow<Map<DashboardCategory, List<DashboardItems>>>

    suspend fun generateTipForCategory(category: DashboardCategory, userInput: String): IELTSContent
}
