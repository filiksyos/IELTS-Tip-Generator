package com.example.domain

import com.example.data.DashboardCategory
import com.example.data.DashboardItems
import com.example.data.Repository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class GetDashboardItemsUseCaseTest {

    private val repository = mockk<Repository>()
    private val useCase = GetDashboardItemsUseCase(repository)

    @Test
    fun `invoke returns correct items for each category`() {
        // Observation: The use case fetches dashboard items from the repository.
        // Question: Does it return correct data for each category?
        // Hypothesis: Each category should return the corresponding data.

        // Arrange
        val readingItems = listOf(
            DashboardItems("uri1", "Reading", "Lesson"),
            DashboardItems("uri2", "Reading", "Test")
        )
        val listeningItems = listOf(
            DashboardItems("uri3", "Listening", "Lesson"),
            DashboardItems("uri4", "Listening", "Test")
        )
        every { repository.getDashboardItems(DashboardCategory.READING) } returns readingItems
        every { repository.getDashboardItems(DashboardCategory.LISTENING) } returns listeningItems

        // Act & Assert
        assertEquals(readingItems, useCase.invoke(DashboardCategoryType.READING))
        assertEquals(listeningItems, useCase.invoke(DashboardCategoryType.LISTENING))
    }
}

