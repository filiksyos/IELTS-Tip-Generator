package com.example.data.api

import android.util.Log
import com.example.data.api.provider.APIProviderFactory
import com.example.data.models.ChatCompletion
import okhttp3.ResponseBody
import retrofit2.Retrofit

/**
 * API service for communicating with AI APIs
 * This class now uses the APIProvider interface to support multiple API providers
 */
object ApiService {
    private const val TAG = "ApiService"
    
    // Get the default provider (Groq)
    private val defaultProvider = APIProviderFactory.getProvider()
    
    /**
     * Create a Retrofit instance for the API
     * @return Retrofit instance
     */
    fun create(): Retrofit {
        return defaultProvider.createRetrofit()
    }
    
    /**
     * Get a chat completion from the API
     * @param completion The chat completion request
     * @param providerType The provider type to use (defaults to GROQ)
     * @return The response body
     */
    suspend fun getChatCompletion(
        completion: ChatCompletion,
        providerType: APIProviderFactory.ProviderType = APIProviderFactory.ProviderType.GROQ
    ): ResponseBody {
        val provider = APIProviderFactory.getProvider(providerType)
        Log.d(TAG, "Using provider: ${providerType.name}")
        return provider.getChatCompletion(completion)
    }
} 