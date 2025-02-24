package com.example.assistant.api

import android.util.Log
import com.example.assistant.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object ApiService {
    private const val TAG = "ApiService"
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
                    .addHeader("Authorization", "Bearer ${BuildConfig.XAI_API_KEY}")
                    .build()
                
                val response = chain.proceed(request)
                
                if (!response.isSuccessful) {
                    val errorBody = response.peekBody(Long.MAX_VALUE).string()
                    Log.e(TAG, """
                        API Error:
                        Code: ${response.code}
                        Message: ${response.message}
                        Body: $errorBody
                    """.trimIndent())
                }
                
                response
            }
            .build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}