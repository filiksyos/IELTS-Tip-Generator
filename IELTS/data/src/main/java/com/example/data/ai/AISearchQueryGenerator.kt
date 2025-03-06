package com.example.data.ai

import android.util.Log
import com.example.data.DashboardCategory
import com.example.data.api.ApiService
import com.example.data.api.ChatApi
import com.example.data.models.ChatCompletion
import com.example.data.models.ChatResponse
import com.example.data.models.IELTSContent
import com.example.data.models.UserPreferences
import com.example.data.preferences.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.regex.Pattern

/**
 * Generates search queries for IELTS categories using AI
 */
class AISearchQueryGenerator(
    private val preferencesManager: PreferencesManager
) {
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
    suspend fun generateQueriesForAllCategories(): Map<DashboardCategory, IELTSContent> = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val preferences = preferencesManager.getUserPreferences()
        
        val prompt = buildPersonalizedPrompt(preferences, timestamp)
        
        try {
            val messages = listOf(
                mapOf("role" to "system", "content" to "You are a creative IELTS exam preparation assistant. Generate unique and varied tips with search queries each time."),
                mapOf("role" to "user", "content" to prompt)
            )
            
            val chatCompletion = ChatCompletion(
                messages = messages,
                stream = true,
                temperature = 0.8,  // Add temperature for more variation
                maxTokens = 200
            )
            
            Log.d(TAG, "Sending request with completion: $chatCompletion")
            val response = chatApi.getChatCompletion(chatCompletion)
            val responseText = response.byteStream().bufferedReader().use { it.readText() }
            Log.d(TAG, "Raw API Response: $responseText")
            
            // Extract the content from the response
            val content = extractContentFromResponse(responseText)
            Log.d(TAG, "Extracted content: $content")
            
            // Parse the queries
            val tipQueryPairs = parseQueries(content)
            Log.d(TAG, "Parsed tip-query pairs: $tipQueryPairs")
            
            // Map queries to categories
            val result = mutableMapOf<DashboardCategory, IELTSContent>()
            for ((categoryName, content) in tipQueryPairs) {
                val category = when (categoryName.lowercase()) {
                    "reading" -> DashboardCategory.READING
                    "listening" -> DashboardCategory.LISTENING
                    "writing" -> DashboardCategory.WRITING
                    "speaking" -> DashboardCategory.SPEAKING
                    else -> continue
                }
                result[category] = content
            }
            
            // If any category is missing, provide a default content
            for (category in DashboardCategory.values()) {
                if (!result.containsKey(category)) {
                    result[category] = IELTSContent(
                        tip = "Practice ${category.name.lowercase()} with official IELTS materials",
                        searchQuery = "IELTS ${category.name.lowercase()} practice"
                    )
                }
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error generating queries", e)
            // Provide default content in case of error
            DashboardCategory.values().associateWith { category ->
                IELTSContent(
                    tip = "Practice ${category.name.lowercase()} with official IELTS materials",
                    searchQuery = "IELTS ${category.name.lowercase()} practice"
                )
            }
        }
    }
    
    private fun buildPersonalizedPrompt(preferences: UserPreferences, timestamp: Long): String {
        return """
            Generate 4 NEW and DIFFERENT IELTS study tips with corresponding search queries (timestamp: $timestamp).
            Each tip should be concise and actionable, with a related search query for more details.
            
            User Profile:
            - Weakest skill: ${preferences.weakestSkill}
            - Target band score: ${preferences.targetBandScore}
            - Study goal: ${preferences.studyGoal}
            
            Focus on these skills:
            1. Reading
            2. Listening
            3. Writing
            4. Speaking
            
            RESPONSE FORMAT:
            Return exactly 4 pairs in this format:
            "Reading: {tip} || {search query}",
            "Listening: {tip} || {search query}",
            "Writing: {tip} || {search query}",
            "Speaking: {tip} || {search query}"
            
            RULES:
            1. Always return EXACTLY 4 pairs
            2. Each pair must be in quotation marks and separated by commas
            3. Each pair must start with the category name followed by colon
            4. Tips should be 10-15 words
            5. Search queries should be 3-5 words
            6. Use || to separate tip and search query
            7. Make the ${preferences.weakestSkill} tip especially targeted and specific
        """.trimIndent()
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
    private fun parseQueries(response: String): Map<String, IELTSContent> {
        val result = mutableMapOf<String, IELTSContent>()
        
        // Pattern to match category and query
        val pattern = Pattern.compile(""""(Reading|Listening|Writing|Speaking):\s*([^|]+)\|\|\s*([^"]+)"""", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(response)
        
        while (matcher.find()) {
            val category = matcher.group(1)
            val tip = matcher.group(2)
            val query = matcher.group(3)
            if (category != null && tip != null && query != null) {
                result[category] = IELTSContent(tip.trim(), query.trim())
            }
        }
        
        return result
    }
} 