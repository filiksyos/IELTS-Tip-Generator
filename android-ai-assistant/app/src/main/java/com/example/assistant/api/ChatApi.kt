package com.example.assistant.api

import com.example.assistant.models.ChatCompletion
import com.example.assistant.models.ChatResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface ChatApi {
    @POST("chat/completions")
    @Streaming
    suspend fun getChatCompletion(@Body completion: ChatCompletion): ResponseBody
} 