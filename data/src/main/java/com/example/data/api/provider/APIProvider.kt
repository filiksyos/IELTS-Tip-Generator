package com.example.data.api.provider

import com.example.data.models.ChatCompletion
import okhttp3.ResponseBody
import retrofit2.Retrofit

/**
 * Interface for API providers
 * This interface defines the contract for different AI API providers
 */
interface APIProvider {
    /**
     * Get the base URL for the API
     */
    fun getBaseUrl(): String
    
    /**
     * Get the API key for the provider
     */
    fun getApiKey(): String
    
    /**
     * Create a Retrofit instance for the API
     */
    fun createRetrofit(): Retrofit
    
    /**
     * Get a chat completion from the API
     * @param completion The chat completion request
     * @return The response body
     */
    suspend fun getChatCompletion(completion: ChatCompletion): ResponseBody
} 