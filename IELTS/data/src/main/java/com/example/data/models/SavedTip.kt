package com.example.data.models

import com.example.data.DashboardCategory
import java.util.UUID

/**
 * Model class for saved tips
 */
data class SavedTip(
    val id: String = UUID.randomUUID().toString(),
    val category: DashboardCategory,
    val tip: String,
    val explanation: String,
    val timestamp: Long = System.currentTimeMillis()
) 