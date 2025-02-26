package com.example.domain

import com.example.data.DashboardCategory
import com.example.data.RepositoryInterface
import com.example.data.DashboardItems

class GetDashboardItemsUseCase(private val repository: RepositoryInterface) {
    fun invoke(categoryType: DashboardCategoryType): List<DashboardItems> {
        val category = when (categoryType) {
            DashboardCategoryType.READING -> DashboardCategory.READING
            DashboardCategoryType.LISTENING -> DashboardCategory.LISTENING
            DashboardCategoryType.WRITING -> DashboardCategory.WRITING
            DashboardCategoryType.SPEAKING -> DashboardCategory.SPEAKING
        }
        return repository.getDashboardItems(category)
    }
}
