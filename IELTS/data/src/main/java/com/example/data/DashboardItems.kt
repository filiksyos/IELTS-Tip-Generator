package com.example.data

import android.graphics.Color
import com.example.data.Utils.YouTubeLink.BASE_URL

data class DashboardItems(
    val itemImageUri: String? = null,
    val itemText: String? = null,
    val cardType: String? = null,
    val color: Int = Color.GRAY,
    val query: String = BASE_URL
)
