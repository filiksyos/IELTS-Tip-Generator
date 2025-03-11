package com.example.data

import android.graphics.Color
import java.util.UUID

data class DashboardItems(
    val id: String = UUID.randomUUID().toString(),
    val itemText: String,
    val cardType: String,
    val color: Int? = null,
    val explanation: String,
    val displayTip: String
)
