package com.example.data

enum class DashboardCategory(val title: String, val color: Int, val iconUri: String) {
    READING("Reading", 0xFF2196F3.toInt(), "android.resource://com.example.ielts/drawable/ic_reading_lesson_card"),
    LISTENING("Listening", 0xFFF44336.toInt(), "android.resource://com.example.ielts/drawable/ic_listening_image_transparent_background"),
    WRITING("Writing", 0xFF9C27B0.toInt(), "android.resource://com.example.ielts/drawable/ic_test_card"),
    SPEAKING("Speaking", 0xFF4CAF50.toInt(), "android.resource://com.example.ielts/drawable/ic_speaking_image_transparent_background");
}
