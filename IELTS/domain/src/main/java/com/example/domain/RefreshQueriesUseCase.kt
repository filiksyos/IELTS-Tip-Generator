package com.example.domain

import com.example.data.DashboardCategory
import com.example.data.DashboardItems
import com.example.data.RepositoryInterface
import kotlinx.coroutines.flow.StateFlow

/**
 * Use case for observing dashboard items
 * Note: Refresh functionality has been removed as we now generate tips on-demand
 */
class DashboardItemsObserverUseCase(private val repository: RepositoryInterface) {
    /**
     * Observes dashboard items
     */
    fun observeDashboardItems(): StateFlow<Map<DashboardCategory, List<DashboardItems>>> {
        return repository.dashboardItemsFlow
    }
} 