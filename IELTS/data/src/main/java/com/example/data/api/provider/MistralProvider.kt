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
 * Implementation of APIProvider for Mistral AI API
 */
class MistralProvider : APIProvider {
    private val TAG = "MistralProvider"
    private val BASE_URL = "https://api.mistral.ai/v1/"
    private val DEFAULT_MODEL = "mistral-small-latest"
    
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }
    
    override fun getBaseUrl(): String = BASE_URL
    
    override fun getApiKey(): String {
        return try {
            BuildConfig.MISTRAL_API_KEY.also { key ->
                if (key.isBlank()) {
                    Log.e(TAG, "Mistral API key is missing or empty")
                } else {
                    Log.d(TAG, "Mistral API key loaded successfully")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting Mistral API key from BuildConfig", e)
            "" // Fallback to empty string
        }
    }
    
    override fun createRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                Log.d(TAG, "Making request to: ${original.url}")
                
                val request = original.newBuilder()
                    .addHeader("Authorization", "Bearer ${getApiKey()}")
                    .addHeader("Content-Type", "application/json")
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
            .baseUrl(getBaseUrl())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    
    override suspend fun getChatCompletion(completion: ChatCompletion): ResponseBody {
        // Override the model to use the Mistral model
        val mistralCompletion = completion.copy(
            model = DEFAULT_MODEL
        )
        return createRetrofit().create(ChatApi::class.java).getChatCompletion(mistralCompletion)
    }
} 