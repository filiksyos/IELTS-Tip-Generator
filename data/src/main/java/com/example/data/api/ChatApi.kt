package com.example.data.api

import com.example.data.models.ChatCompletion
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

/**
 * Interface for the Chat API
 */
interface ChatApi {
    @POST("chat/completions")
    @Streaming
    suspend fun getChatCompletion(@Body completion: ChatCompletion): ResponseBody
} 