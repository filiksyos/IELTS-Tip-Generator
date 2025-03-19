package com.example.data

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


class RepositoryTest {
/*
    private val repository = Repository(preferencesManager = mockk())

    @Test
    fun `getDashboardItems returns correct items for valid category`() {
        // Observation: The function should return a list of dashboard items based on the category.
        // Question: Does the function return the correct items for a valid category?
        // Hypothesis: For a valid category, the returned list should match the expected structure.

        // Independent variable: `category` passed to the function.
        // Dependent variable: Items in the returned list.
        // Controlled variables: Structure of the `DashboardItems`.

        // Act
        val result = repository.getDashboardItems(DashboardCategory.READING)

        // Assert
        assertEquals(2, result.size)
        assertEquals("Reading", result[0].itemText)
        assertEquals("Lesson", result[0].cardType)
        assertEquals("Test", result[1].cardType)
    }

    @Test
    fun `getDashboardItems returns empty list on exception`() {
        // Observation: The function wraps execution in a try-catch block.
        // Question: Does it return an empty list in case of an exception?
        // Hypothesis: Any exception should lead to an empty list being returned.

        // Experiment:
        // Independent variable: A mocked `DashboardCategory` causing an exception.
        // Dependent variable: The returned list should be empty.
        // Controlled variables: Structure and logic of the function.

        // Arrange
        val exceptionThrowingCategory = mockk<DashboardCategory>()
        every { exceptionThrowingCategory.iconUri } throws IllegalArgumentException("Forced exception")

        // Act
        val result = repository.getDashboardItems(exceptionThrowingCategory)

        // Assert
        assertTrue(result.isEmpty())
    }*/
}


