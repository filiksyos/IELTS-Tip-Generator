package com.example.data.ai

import android.util.Log
import com.example.data.DashboardCategory
import com.example.data.api.ApiService
import com.example.data.api.provider.APIProviderFactory
import com.example.data.models.ChatCompletion
import com.example.data.models.ChatResponse
import com.example.data.models.IELTSContent
import com.example.data.models.UserPreferences
import com.example.data.preferences.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.util.regex.Pattern

/**
 * Generates search queries for IELTS categories using AI
 * Each category uses a different API provider for specialized responses
 */
class AISearchQueryGenerator(
    private val preferencesManager: PreferencesManager
) {
    private val TAG = "AISearchQueryGenerator"
    private val json = Json { 
        ignoreUnknownKeys = true 
        encodeDefaults = true
        isLenient = true
    }
    
    /**
     * Generates search queries for all IELTS categories using different API providers in parallel
     * - Reading: Groq
     * - Listening: OpenRouter
     * - Writing: Mistral
     * - Speaking: New Groq
     * @return Map of category to search query
     */
    suspend fun generateQueriesForAllCategories(): Map<DashboardCategory, IELTSContent> = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val preferences = preferencesManager.getUserPreferences()
        
        // Create a map to store results
        val result = mutableMapOf<DashboardCategory, IELTSContent>()
        
        // Launch parallel coroutines for each category
        Log.d(TAG, "Starting parallel API requests for all IELTS categories")
        val deferreds = listOf(
            async { 
                generateTipForCategory(
                    DashboardCategory.READING, 
                    preferences, 
                    timestamp, 
                    APIProviderFactory.ProviderType.GROQ
                ) 
            },
            async { 
                generateTipForCategory(
                    DashboardCategory.LISTENING, 
                    preferences, 
                    timestamp, 
                    APIProviderFactory.ProviderType.OPENROUTER
                ) 
            },
            async { 
                generateTipForCategory(
                    DashboardCategory.WRITING, 
                    preferences, 
                    timestamp, 
                    APIProviderFactory.ProviderType.MISTRAL
                ) 
            },
            async { 
                generateTipForCategory(
                    DashboardCategory.SPEAKING, 
                    preferences, 
                    timestamp, 
                    APIProviderFactory.ProviderType.NEW_GROQ
                ) 
            }
        )
        
        // Wait for all results and combine them
        val results = deferreds.awaitAll()
        results.forEach { (category, content) ->
            result[category] = content
        }
        
        Log.d(TAG, "Completed all parallel API requests. Results: ${result.size}/${DashboardCategory.values().size} categories")
        
        return@withContext result
    }
    
    /**
     * Generates a tip for a specific IELTS category using the specified API provider
     * @param category The IELTS category
     * @param preferences The user preferences
     * @param timestamp The current timestamp
     * @param providerType The API provider to use
     * @return A pair of the category and the generated content
     */
    private suspend fun generateTipForCategory(
        category: DashboardCategory,
        preferences: UserPreferences,
        timestamp: Long,
        providerType: APIProviderFactory.ProviderType
    ): Pair<DashboardCategory, IELTSContent> = withContext(Dispatchers.IO) {
        try {
            val prompt = buildPersonalizedPromptForCategory(category, preferences, timestamp)
            
            val messages = listOf(
                mapOf("role" to "system", "content" to "You are a knowledgeable IELTS exam preparation assistant. Generate a unique and detailed explanation for an IELTS ${category.name.lowercase()} tip."),
                mapOf("role" to "user", "content" to prompt)
            )
            
            val chatCompletion = ChatCompletion(
                messages = messages,
                stream = true,
                temperature = 0.8,
                maxTokens = 200  // Reduced since we're only generating one tip
            )
            
            Log.d(TAG, "Sending request for ${category.name} with provider ${providerType.name}")
            val response = ApiService.getChatCompletion(chatCompletion, providerType)
            val responseText = response.byteStream().bufferedReader().use { it.readText() }
            Log.d(TAG, "Received response for ${category.name} from ${providerType.name}")
            
            val content = extractContentFromResponse(responseText)
            val parsedContent = parseSingleTip(content)
            
            if (parsedContent != null) {
                Log.d(TAG, "Successfully parsed tip for ${category.name} from ${providerType.name}")
                return@withContext category to parsedContent
            } else {
                Log.w(TAG, "Failed to parse tip for ${category.name} from ${providerType.name}, using fallback")
                return@withContext category to fallbackContent(category)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating content for ${category.name} with ${providerType.name}", e)
            return@withContext category to fallbackContent(category)
        }
    }
    
    /**
     * Generates a tip for a single category using the provided user input
     * @param category The IELTS category to generate a tip for
     * @param userInput The user's specific problem or issue
     * @return The generated IELTS content (tip and explanation)
     */
    suspend fun generateTipForSingleCategory(
        category: DashboardCategory,
        userInput: String
    ): IELTSContent = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            val prompt = buildPromptWithUserInput(category, userInput, timestamp)
            
            // Select the appropriate API provider for the category
            val providerType = when (category) {
                DashboardCategory.READING -> APIProviderFactory.ProviderType.GROQ
                DashboardCategory.LISTENING -> APIProviderFactory.ProviderType.OPENROUTER
                DashboardCategory.WRITING -> APIProviderFactory.ProviderType.MISTRAL
                DashboardCategory.SPEAKING -> APIProviderFactory.ProviderType.NEW_GROQ
            }
            
            val messages = listOf(
                mapOf("role" to "system", "content" to "You are a knowledgeable IELTS exam preparation assistant. Generate a unique and detailed explanation for an IELTS ${category.name.lowercase()} tip."),
                mapOf("role" to "user", "content" to prompt)
            )
            
            val chatCompletion = ChatCompletion(
                messages = messages,
                stream = true,
                temperature = 0.8,
                maxTokens = 200
            )
            
            Log.d(TAG, "Sending request for single ${category.name} tip with provider ${providerType.name}")
            val response = ApiService.getChatCompletion(chatCompletion, providerType)
            val responseText = response.byteStream().bufferedReader().use { it.readText() }
            Log.d(TAG, "Received response for single ${category.name} tip from ${providerType.name}")
            
            val content = extractContentFromResponse(responseText)
            val parsedContent = parseSingleTip(content)
            
            if (parsedContent != null) {
                Log.d(TAG, "Successfully parsed single tip for ${category.name}")
                return@withContext parsedContent
            } else {
                Log.w(TAG, "Failed to parse single tip for ${category.name}, using fallback")
                return@withContext fallbackContent(category)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating single tip for ${category.name}", e)
            return@withContext fallbackContent(category)
        }
    }
    
    /**
     * Builds a personalized prompt for a specific IELTS category
     * @param category The IELTS category
     * @param preferences The user preferences
     * @param timestamp The current timestamp
     * @return The personalized prompt
     */
    private fun buildPersonalizedPromptForCategory(
        category: DashboardCategory, 
        preferences: UserPreferences, 
        timestamp: Long
    ): String {
        // Get the specific problem for this category
        val specificProblem = when (category) {
            DashboardCategory.READING -> preferences.readingProblems
            DashboardCategory.LISTENING -> preferences.listeningProblems
            DashboardCategory.WRITING -> preferences.writingProblems
            DashboardCategory.SPEAKING -> preferences.speakingProblems
        }
        
        return """
            Generate 1 NEW and UNIQUE IELTS ${category.name} study tip with detailed explanation (timestamp: $timestamp).
            The tip should be concise and actionable, with a detailed explanation of how to implement it.
            
            User Profile:
            - ${category.name} problems: ${specificProblem}
            - Study goal: ${preferences.studyGoal}
            
            RESPONSE FORMAT:
            Return exactly 1 pair in this format:
            "{tip} || {detailed explanation}"
            
            RULES:
            1. Tip should be 10-15 words
            2. Explanation should be 50-100 words and provide actionable guidance
            3. Use || to separate tip and explanation
            4. Make this tip especially targeted to solve the user's specific ${category.name.lowercase()} problem
        """.trimIndent()
    }
    
    /**
     * Builds a prompt with user input for a specific IELTS category
     * @param category The IELTS category
     * @param userInput The user's specific problem or issue
     * @param timestamp The current timestamp
     * @return The personalized prompt
     */
    private fun buildPromptWithUserInput(
        category: DashboardCategory,
        userInput: String,
        timestamp: Long
    ): String {
        return """
            Generate 1 NEW and UNIQUE IELTS ${category.name} study tip with detailed explanation (timestamp: $timestamp).
            The tip should be concise and actionable, with a detailed explanation of how to implement it.
            
            User's ${category.name} Problem:
            $userInput
            
            RESPONSE FORMAT:
            Return exactly 1 pair in this format:
            "{tip} || {detailed explanation}"
            
            RULES:
            1. Tip should be 10-15 words
            2. Explanation should be 50-100 words and provide actionable guidance
            3. Use || to separate tip and explanation
            4. Make this tip especially targeted to solve the user's specific ${category.name.lowercase()} problem
        """.trimIndent()
    }
    
    /**
     * Extracts content from the API response
     */
    private fun extractContentFromResponse(response: String): String {
        return try {
            Log.d(TAG, "Starting content extraction from response")
            val dataLines = response.split("\n")
                .filter { it.startsWith("data: ") && it != "data: [DONE]" }
                .map { it.substring(6) }
                .filterNot { it.isBlank() }
            
            Log.d(TAG, "Found ${dataLines.size} data lines")
            
            val content = StringBuilder()
            for (dataLine in dataLines) {
                try {
                    Log.d(TAG, "Processing data line: $dataLine")
                    val chatResponse = json.decodeFromString<ChatResponse>(dataLine)
                    when {
                        chatResponse.choices.firstOrNull()?.delta?.content != null -> {
                            val deltaContent = chatResponse.choices.first().delta?.content
                            Log.d(TAG, "Found delta content: $deltaContent")
                            content.append(deltaContent ?: "")
                        }
                        chatResponse.choices.firstOrNull()?.message?.content != null -> {
                            val messageContent = chatResponse.choices.first().message?.content
                            Log.d(TAG, "Found message content: $messageContent")
                            content.append(messageContent ?: "")
                        }
                        else -> {
                            Log.w(TAG, "No content found in response choice")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing SSE data: $dataLine", e)
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
     * Parses a single tip from the response
     * @param response The response text
     * @return The parsed IELTS content or null if parsing failed
     */
    private fun parseSingleTip(response: String): IELTSContent? {
        // Simplified parsing for a single tip
        val pattern = Pattern.compile("""([^|]+)\|\|\s*([^"]+)""", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(response)
        
        if (matcher.find()) {
            val tip = matcher.group(1)
            val explanation = matcher.group(2)
            if (tip != null && explanation != null) {
                return IELTSContent(tip.trim(), explanation.trim())
            }
        }
        
        return null
    }
    
    /**
     * Provides fallback content for a category if the API request fails
     * @param category The IELTS category
     * @return The fallback IELTS content
     */
    private fun fallbackContent(category: DashboardCategory): IELTSContent {
        return IELTSContent(
            tip = "Practice ${category.name.lowercase()} with official IELTS materials",
            explanation = "Focus on official IELTS ${category.name.lowercase()} practice materials to familiarize yourself with the exam format and requirements."
        )
    }
} 
