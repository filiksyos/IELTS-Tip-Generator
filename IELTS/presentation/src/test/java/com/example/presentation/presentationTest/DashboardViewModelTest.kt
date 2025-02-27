package com.example.presentation.presentationTest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.data.DashboardCategory
import com.example.data.DashboardItems
import com.example.data.Repository
import com.example.domain.DashboardCategoryType
import com.example.domain.GetDashboardItemsUseCase
import com.example.presentation.viewModel.DashboardViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DashboardViewModel
    private val repository = mockk<Repository>()
    private val useCase = GetDashboardItemsUseCase(repository)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DashboardViewModel(
            useCase,
            refreshQueriesUseCase = TODO()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDashboardItems updates LiveData correctly`() = runTest(testDispatcher) {
        // Observation: The ViewModel should update LiveData based on repository data.
        // Question: Does LiveData contain the correct data for each category?
        // Hypothesis: LiveData should contain the same data returned by the repository.

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

        // Act
        viewModel.loadDashboardItems()

        // Assert
        viewModel.dashboardItems.observeForever { dashboardItems ->
            assertNotNull(dashboardItems)
            assertEquals(readingItems, dashboardItems[DashboardCategoryType.READING])
            assertEquals(listeningItems, dashboardItems[DashboardCategoryType.LISTENING])
        }
    }
}

