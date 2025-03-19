package com.example.data.api.provider

import android.util.Log
import com.example.data.BuildConfig
import com.example.data.api.ChatApi
import com.example.data.models.ChatCompletion
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit

/**
 * Implementation of APIProvider for Groq API
 */
class GroqProvider : APIProvider {
    private val TAG = "IELTS_GroqProvider"
    private val BASE_URL = "https://api.groq.com/openai/v1/"
    
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    init {
        Log.e(TAG, "GroqProvider initialized")
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            Log.e(TAG, "WARNING: Groq API key is missing or empty!")
        } else {
            Log.e(TAG, "Groq API key is available (length: ${apiKey.length})")
        }
    }
    
    override fun getBaseUrl(): String = BASE_URL
    
    override fun getApiKey(): String {
        return try {
            val key = BuildConfig.GROQ_API_KEY
            if (key.isBlank()) {
                Log.e(TAG, "Groq API key is missing or empty in BuildConfig")
            } else {
                Log.e(TAG, "Groq API key loaded successfully from BuildConfig")
            }
            key
        } catch (e: Exception) {
            Log.e(TAG, "Error getting Groq API key from BuildConfig: ${e.message}", e)
            "" // Fallback to empty string
        }
    }
    
    override fun createRetrofit(): Retrofit {
        Log.d(TAG, "Creating Retrofit instance")
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                Log.e(TAG, "Making request to: ${original.url}")
                
                val apiKey = getApiKey()
                if (apiKey.isBlank()) {
                    Log.e(TAG, "API key is blank, request will fail")
                }
                
                val request = original.newBuilder()
                    .addHeader("Authorization", "Bearer ${apiKey}")
                    .build()
                
                try {
                    val response = chain.proceed(request)
                    
                    Log.e(TAG, "Response received - Status: ${response.code}")
                    
                    if (!response.isSuccessful) {
                        val errorBody = response.peekBody(Long.MAX_VALUE).string()
                        Log.e(TAG, """
                            API Error:
                            Code: ${response.code}
                            Message: ${response.message}
                            Body: $errorBody
                            Request URL: ${original.url}
                        """.trimIndent())
                    }
                    
                    response
                } catch (e: Exception) {
                    Log.e(TAG, "Network error during API request: ${e.message}", e)
                    throw e
                }
            }
            .build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl(getBaseUrl())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    
    override suspend fun getChatCompletion(completion: ChatCompletion): ResponseBody {
        Log.e(TAG, "Getting chat completion with model: ${completion.model}")
        try {
            val retrofit = createRetrofit()
            val chatApi = retrofit.create(ChatApi::class.java)
            Log.e(TAG, "Sending chat completion request")
            val response = chatApi.getChatCompletion(completion)
            Log.e(TAG, "Received chat completion response")
            return response
        } catch (e: Exception) {
            Log.e(TAG, "Error getting chat completion: ${e.message}", e)
            throw e
        }
    }
} 