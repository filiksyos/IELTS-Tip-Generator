package com.example.presentation.presentationTest

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.presentation.adapter.DashboardAdapter
import com.example.presentation.utils.RecyclerViewHelper
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class RecyclerViewHelperTest {

    @Test
    fun `setupRecyclerView should correctly configure RecyclerView`() {
        // Observation: RecyclerViewHelper sets up RecyclerView.
        // Question: Does it assign the correct layout manager and adapter?
        // Hypothesis: The RecyclerView should have the expected layout manager and adapter.

        // Arrange
        val mockRecyclerView = mockk<RecyclerView>(relaxed = true)
        val mockAdapter = mockk<DashboardAdapter>(relaxed = true)
        val context = mockk<Context>(relaxed = true)

        // Act
        RecyclerViewHelper.setupRecyclerView(mockRecyclerView, context, mockAdapter)

        // Assert
        verify { mockRecyclerView.layoutManager = any<LinearLayoutManager>() }
        verify { mockRecyclerView.adapter = mockAdapter }
    }
}
