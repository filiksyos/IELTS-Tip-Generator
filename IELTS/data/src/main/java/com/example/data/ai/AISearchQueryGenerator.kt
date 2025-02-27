package com.example.data.ai

import android.util.Log
import com.example.data.DashboardCategory
import com.example.data.api.ApiService
import com.example.data.api.ChatApi
import com.example.data.models.ChatCompletion
import com.example.data.models.ChatResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.regex.Pattern

/**
 * Generates search queries for IELTS categories using AI
 */
class AISearchQueryGenerator {
    private val TAG = "AISearchQueryGenerator"
    private val chatApi = ApiService.create().create(ChatApi::class.java)
    private val json = Json { 
        ignoreUnknownKeys = true 
        encodeDefaults = true
        isLenient = true  // Add this for more forgiving parsing
    }
    
    /**
     * Generates search queries for all IELTS categories
     * @return Map of category to search query
     */
    suspend fun generateQueriesForAllCategories(): Map<DashboardCategory, String> = withContext(Dispatchers.IO) {
        val prompt = """
            Generate 4 different search queries for IELTS exam preparation, one for each skill:
            1. Reading
            2. Listening
            3. Writing
            4. Speaking
            
            Each query should be specific and helpful for someone preparing for the IELTS exam.
            
            RESPONSE FORMAT:
            Return exactly 4 search queries in this format:
            "Reading: specific reading search query",
            "Listening: specific listening search query",
            "Writing: specific writing search query",
            "Speaking: specific speaking search query"
            
            RULES:
            1. Always return EXACTLY 4 queries
            2. Each query must be in quotation marks and separated by commas
            3. Each query must start with the category name followed by colon
            4. Keep queries concise (under 10 words each)
            5. Do NOT include explanations or additional text
        """.trimIndent()
        
        try {
            val messages = listOf(
                mapOf("role" to "system", "content" to "You are a helpful IELTS exam preparation assistant."),
                mapOf("role" to "user", "content" to prompt)
            )
            
            val chatCompletion = ChatCompletion(
                messages = messages,
                stream = true  // Change to true for SSE format
            )
            
            Log.d(TAG, "Sending request with completion: $chatCompletion")
            val response = chatApi.getChatCompletion(chatCompletion)
            val responseText = response.byteStream().bufferedReader().use { it.readText() }
            Log.d(TAG, "Raw API Response: $responseText")
            
            // Extract the content from the response
            val content = extractContentFromResponse(responseText)
            Log.d(TAG, "Extracted content: $content")
            
            // Parse the queries
            val queries = parseQueries(content)
            Log.d(TAG, "Parsed queries: $queries")
            
            // Map queries to categories
            val result = mutableMapOf<DashboardCategory, String>()
            for ((categoryName, query) in queries) {
                val category = when (categoryName.lowercase()) {
                    "reading" -> DashboardCategory.READING
                    "listening" -> DashboardCategory.LISTENING
                    "writing" -> DashboardCategory.WRITING
                    "speaking" -> DashboardCategory.SPEAKING
                    else -> continue
                }
                result[category] = query
            }
            
            // If any category is missing, provide a default query
            for (category in DashboardCategory.values()) {
                if (!result.containsKey(category)) {
                    result[category] = "IELTS ${category.name.lowercase()} practice"
                }
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error generating queries", e)
            // Provide default queries in case of error
            DashboardCategory.values().associateWith { 
                "IELTS ${it.name.lowercase()} practice"
            }
        }
    }
    
    /**
     * Extracts content from the API response
     */
    private fun extractContentFromResponse(response: String): String {
        return try {
            Log.d(TAG, "Starting content extraction from response")
            // The response is a series of SSE events, we need to parse them
            val dataLines = response.split("\n")
                .filter { it.startsWith("data: ") && it != "data: [DONE]" }
                .map { it.substring(6) }
                .filterNot { it.isBlank() }  // Skip empty lines
            
            Log.d(TAG, "Found ${dataLines.size} data lines")
            
            val content = StringBuilder()
            for (dataLine in dataLines) {
                try {
                    Log.d(TAG, "Processing data line: $dataLine")
                    // Use explicit serializer instead of type inference
                    val chatResponse = json.decodeFromString(ChatResponse.serializer(), dataLine)
                    
                    // Handle both streaming and non-streaming responses
                    when {
                        // Streaming response
                        chatResponse.choices.firstOrNull()?.delta?.content != null -> {
                            val deltaContent = chatResponse.choices.first().delta?.content
                            Log.d(TAG, "Found delta content: $deltaContent")
                            content.append(deltaContent)
                        }
                        // Non-streaming response
                        chatResponse.choices.firstOrNull()?.message?.content != null -> {
                            val messageContent = chatResponse.choices.first().message?.content
                            Log.d(TAG, "Found message content: $messageContent")
                            content.append(messageContent)
                        }
                        else -> {
                            Log.w(TAG, "No content found in response choice")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing SSE data: $dataLine", e)
                    Log.e(TAG, "Parse error details:", e)
                }
            }
            
            val result = content.toString().trim()
            if (result.isBlank()) {
                Log.w(TAG, "Extracted content is blank")
                Log.w(TAG, "Original response was: $response")
            } else {
                Log.d(TAG, "Successfully extracted content: $result")
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting content", e)
            Log.e(TAG, "Original response was: $response", e)
            ""
        }
    }
    
    /**
     * Parses the queries from the response
     * @return Map of category name to query
     */
    private fun parseQueries(response: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        
        // Pattern to match category and query
        val pattern = Pattern.compile(""""(Reading|Listening|Writing|Speaking):\s*([^"]+)"""", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(response)
        
        while (matcher.find()) {
            val category = matcher.group(1)
            val query = matcher.group(2)
            if (category != null && query != null) {
                result[category] = query.trim()
            }
        }
        
        return result
    }
} 