package com.example.data

import kotlinx.coroutines.flow.StateFlow

interface RepositoryInterface {
    fun getDashboardItems(category: DashboardCategory): List<DashboardItems>
    
    /**
     * Refreshes the AI-generated queries
     */
    fun refreshQueries()
    
    /**
     * StateFlow to observe dashboard items
     */
    val dashboardItemsFlow: StateFlow<Map<DashboardCategory, List<DashboardItems>>>
}
