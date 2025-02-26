package com.example.data.Utils

object YouTubeLink {

    const val BASE_URL = "https://www.youtube.com/results?search_query="

    fun getLink(query: String?): String {
        return "$BASE_URL$query" ?: "$BASE_URL"
    }

}
