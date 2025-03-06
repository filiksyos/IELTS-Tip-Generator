package com.example.presentation.utils

import com.example.data.DashboardCategory
import com.example.data.DashboardItems
import com.example.presentation.R

object PlaceholderUtils {
    fun getPlaceholderForItem(item: DashboardItems): Int {
        return when (item.itemText) {
            DashboardCategory.READING.title -> R.drawable.ic_reading
            DashboardCategory.LISTENING.title -> R.drawable.ic_listening
            DashboardCategory.WRITING.title -> R.drawable.ic_writing
            DashboardCategory.SPEAKING.title -> R.drawable.ic_speaking
            else -> R.drawable.placeholder
        }
    }
} 