package com.example.data

import android.graphics.Color

data class DashboardItems(
    val itemText: String? = null,
    val cardType: String? = null,
    val color: Int = Color.GRAY,
    val explanation: String = "",
    val displayTip: String = itemText ?: ""
)
