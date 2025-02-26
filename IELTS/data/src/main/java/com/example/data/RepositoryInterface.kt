package com.example.data

interface RepositoryInterface {
    fun getDashboardItems(category: DashboardCategory): List<DashboardItems>
}
