package com.example.presentation

import com.example.data.DashboardItems
import com.example.presentation.R

object PlaceholderUtils {
    fun getPlaceholderForItem(item: DashboardItems): Int {
        return when (item.itemText) {
            "Reading" -> R.drawable.ic_reading_lesson_card
            "Listening" -> R.drawable.ic_listening_test
            "Writing" -> R.drawable.ic_test_card
            "Speaking" -> R.drawable.ic_speaking_image_transparent_background
            else -> R.drawable.placeholder
        }
    }
}
