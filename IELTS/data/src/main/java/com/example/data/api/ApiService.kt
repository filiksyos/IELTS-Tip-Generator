package com.example.data.api

import android.util.Log
import com.example.data.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * API service for communicating with the Groq API
 */
object ApiService {
    private const val TAG = "ApiService"
    private const val BASE_URL = "https://api.groq.com/openai/v1/"
    
    // You'll need to add your API key to local.properties
    // groq_api_key=your_api_key_here
    private val API_KEY = try {
        BuildConfig.GROQ_API_KEY.also { key ->
            if (key.isBlank()) {
                Log.e(TAG, "API key is missing or empty")
            } else {
                Log.d(TAG, "API key loaded successfully")
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error getting API key from BuildConfig", e)
        "" // Fallback to empty string
    }
    
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    fun create(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                Log.d(TAG, "Making request to: ${original.url}")
                
                val request = original.newBuilder()
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .build()
                
                val response = chain.proceed(request)
                
                Log.d(TAG, "Response received - Status: ${response.code}")
                
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
            }
            .build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
} 