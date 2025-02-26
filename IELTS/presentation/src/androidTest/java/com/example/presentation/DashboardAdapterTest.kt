package com.example.presentation

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.DashboardItems
import com.example.presentation.adapter.DashboardAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Before

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DashboardAdapterTest {

    private lateinit var adapter: DashboardAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        adapter = DashboardAdapter { /* Do nothing for now */ }
        recyclerView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@DashboardAdapterTest.adapter
        }
    }

    @Test
    fun bindUpdatesUIElementsCorrectly() {
        // Observation: The ViewHolder should update the UI based on the item provided.
        // Question: Does the bind function correctly populate views with the item's data?
        // Hypothesis: The text views, image view, and background color should match the item's attributes.

        // Arrange
        val item = DashboardItems(
            itemImageUri = "https://example.com/image.jpg",
            itemText = "Sample Item",
            cardType = "Lesson",
            color = 0xFF2196F3.toInt()
        )
        val viewHolder = adapter.onCreateViewHolder(recyclerView, 0)

        // Act
        viewHolder.bind(item)

        // Assert
        val binding = viewHolder.binding
        assertEquals("Sample Item", binding.tvItemName.text.toString())
        assertEquals("Lesson", binding.tvLessonOrTest.text.toString())
        assertEquals(0xFF2196F3.toInt(), binding.cvItemsMainBackground.cardBackgroundColor.defaultColor)
    }

    @Test
    fun clickingRootViewTriggersOnItemClickCallback() {
        // Observation: Clicking the root view should trigger the onItemClick callback.
        // Question: Does clicking the root invoke the callback with the correct item?
        // Hypothesis: The callback should receive the clicked item as a parameter.

        // Arrange
        var clickedItem: DashboardItems? = null
        val item = DashboardItems(
            itemImageUri = "https://example.com/image.jpg",
            itemText = "Clicked Item",
            cardType = "Test",
            color = 0xFFFF5722.toInt()
        )
        adapter = DashboardAdapter { clickedItem = it }
        adapter.submitList(listOf(item))

        val viewHolder = adapter.onCreateViewHolder(recyclerView, 0)
        viewHolder.bind(item)

        // Act
        viewHolder.itemView.performClick()

        // Assert
        assertEquals(item, clickedItem)
    }

    @Test
    fun DiffUtilCorrectlyIdentifiesItemChanges() {
        // Observation: DiffUtil determines whether items or contents are the same.
        // Question: Does DiffUtil correctly compare item identity and contents?
        // Hypothesis: areItemsTheSame should compare `itemText`, and areContentsTheSame should check full equality.

        // Arrange
        val oldItem = DashboardItems(
            itemImageUri = "https://example.com/image1.jpg",
            itemText = "Item",
            cardType = "Lesson",
            color = 0xFF2196F3.toInt()
        )
        val newItemSame = oldItem.copy(itemImageUri = "https://example.com/image2.jpg")
        val completelyNewItem = DashboardItems(
            itemImageUri = "https://example.com/image3.jpg",
            itemText = "New Item",
            cardType = "Test",
            color = 0xFFFF5722.toInt()
        )

        val diffCallback = DashboardAdapter.DashboardDiffCallback()

        // Act & Assert
        assertTrue(diffCallback.areItemsTheSame(oldItem, newItemSame))
        assertFalse(diffCallback.areContentsTheSame(oldItem, newItemSame))
        assertFalse(diffCallback.areItemsTheSame(oldItem, completelyNewItem))
    }
}
