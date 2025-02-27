package com.example.domain

import com.example.data.DashboardCategory
import com.example.data.DashboardItems
import com.example.data.RepositoryInterface
import kotlinx.coroutines.flow.StateFlow

class RefreshQueriesUseCase(private val repository: RepositoryInterface) {
    /**
     * Refreshes the AI-generated queries
     */
    operator fun invoke() {
        repository.refreshQueries()
    }
    
    /**
     * Observes dashboard items
     */
    fun observeDashboardItems(): StateFlow<Map<DashboardCategory, List<DashboardItems>>> {
        return repository.dashboardItemsFlow
    }
} 