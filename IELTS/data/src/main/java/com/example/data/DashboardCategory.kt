package com.example.data

enum class DashboardCategory(val title: String, val color: Int, val iconUri: String) {
    READING("Reading", 0xFF2196F3.toInt(), "android.resource://com.example.app/drawable/ic_reading"),
    LISTENING("Listening", 0xFFF44336.toInt(), "android.resource://com.example.app/drawable/ic_listening"),
    WRITING("Writing", 0xFF9C27B0.toInt(), "android.resource://com.example.app/drawable/ic_writing"),
    SPEAKING("Speaking", 0xFF4CAF50.toInt(), "android.resource://com.example.app/drawable/ic_speaking");
}
