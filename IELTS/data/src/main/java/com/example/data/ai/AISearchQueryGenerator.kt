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
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.util.regex.Pattern

/**
 * Generates search queries for IELTS categories using Groq AI
 */
class AISearchQueryGenerator(
    private val preferencesManager: PreferencesManager
) {
    private val TAG = "IELTS_AIGenerator"
    private val json = Json { 
        ignoreUnknownKeys = true 
        encodeDefaults = true
        isLenient = true
    }
    
    init {
        Log.e(TAG, "AISearchQueryGenerator initialized")
    }
    
    /**
     * Generates search queries for all IELTS categories using Groq API
     * @return Map of category to search query
     */
    suspend fun generateQueriesForAllCategories(): Map<DashboardCategory, IELTSContent> = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val preferences = preferencesManager.getUserPreferences()
        
        // Create a map to store results
        val result = mutableMapOf<DashboardCategory, IELTSContent>()
        
        // Generate tips for each category sequentially using Groq
        Log.d(TAG, "Starting sequential API requests for all IELTS categories using Groq")
        DashboardCategory.values().forEach { category ->
            try {
                val content = generateTipForCategory(category, preferences, timestamp)
                result[content.first] = content.second
            } catch (e: Exception) {
                Log.e(TAG, "Error generating content for ${category.name}", e)
                result[category] = fallbackContent(category)
            }
        }
        
        Log.d(TAG, "Completed all API requests. Results: ${result.size}/${DashboardCategory.values().size} categories")
        
        return@withContext result
    }
    
    /**
     * Generates a tip for a specific IELTS category using Groq API
     */
    private suspend fun generateTipForCategory(
        category: DashboardCategory,
        preferences: UserPreferences,
        timestamp: Long
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
                maxTokens = 200
            )
            
            Log.d(TAG, "Sending request for ${category.name} with Groq")
            val response = ApiService.getChatCompletion(chatCompletion, APIProviderFactory.ProviderType.GROQ)
            val responseText = response.byteStream().bufferedReader().use { it.readText() }
            Log.d(TAG, "Received response for ${category.name} from Groq")
            
            val content = extractContentFromResponse(responseText)
            val parsedContent = parseSingleTip(content)
            
            if (parsedContent != null) {
                Log.d(TAG, "Successfully parsed tip for ${category.name}")
                return@withContext category to parsedContent
            } else {
                Log.w(TAG, "Failed to parse tip for ${category.name}, using fallback")
                return@withContext category to fallbackContent(category)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating content for ${category.name}", e)
            return@withContext category to fallbackContent(category)
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
        return """
            Generate 1 NEW and UNIQUE IELTS ${category.name} study tip with detailed explanation (timestamp: $timestamp).
            The tip should be concise and actionable, with a detailed explanation of how to implement it.
            
            User Profile:
            - Study goal: ${preferences.studyGoal}
            
            RESPONSE FORMAT:
            Return exactly 1 pair in this format:
            "{tip} || {detailed explanation}"
            
            RULES:
            1. Tip should be 10-15 words
            2. Explanation should be 50-100 words and provide actionable guidance
            3. Use || to separate tip and explanation
            4. Make this tip especially targeted to help achieve the user's study goal
        """.trimIndent()
    }
    
    /**
     * Generates a tip for a single category using the provided user input
     */
    suspend fun generateTipForSingleCategory(
        category: DashboardCategory,
        userInput: String
    ): IELTSContent = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            Log.e(TAG, "GENERATING TIP: Building prompt for category: ${category.name} with input: $userInput")
            val prompt = buildPromptWithUserInput(category, userInput, timestamp)
            
            val messages = listOf(
                mapOf("role" to "system", "content" to "You are a knowledgeable IELTS exam preparation assistant. Generate a unique and detailed explanation for an IELTS ${category.name.lowercase()} tip."),
                mapOf("role" to "user", "content" to prompt)
            )
            
            val chatCompletion = ChatCompletion(
                messages = messages,
                stream = false,
                temperature = 0.7,
                maxTokens = 300
            )
            
            Log.e(TAG, "SENDING API REQUEST for ${category.name} tip with Groq")
            try {
                val response = ApiService.getChatCompletion(chatCompletion, APIProviderFactory.ProviderType.GROQ)
                val responseText = response.byteStream().bufferedReader().use { it.readText() }
                Log.e(TAG, "RECEIVED API RESPONSE for ${category.name} tip from Groq: $responseText")
                
                val content = extractContentFromResponse(responseText)
                Log.e(TAG, "EXTRACTED CONTENT: $content")
                
                val parsedContent = parseSingleTip(content)
                
                if (parsedContent != null) {
                    Log.e(TAG, "SUCCESSFULLY PARSED TIP for ${category.name}: ${parsedContent.tip}")
                    return@withContext parsedContent
                } else {
                    Log.e(TAG, "FAILED TO PARSE TIP for ${category.name}, using fallback")
                    return@withContext fallbackContent(category)
                }
            } catch (e: Exception) {
                Log.e(TAG, "API CALL FAILED for ${category.name}: ${e.message}", e)
                throw e
            }
        } catch (e: Exception) {
            Log.e(TAG, "ERROR GENERATING TIP for ${category.name}: ${e.message}", e)
            return@withContext fallbackContent(category)
        }
    }
    
    /**
     * Builds a prompt with user input for a specific category
     */
    private fun buildPromptWithUserInput(
        category: DashboardCategory,
        userInput: String,
        timestamp: Long
    ): String {
        val prompt = """
            Generate 1 NEW and UNIQUE IELTS ${category.name} study tip with detailed explanation (timestamp: $timestamp).
            The tip should be concise and actionable, with a detailed explanation of how to implement it.
            
            User's specific issue: $userInput
            
            RESPONSE FORMAT:
            Return exactly 1 pair in this format:
            "{tip} || {detailed explanation}"
            
            RULES:
            1. Tip should be 10-15 words
            2. Explanation should be 50-100 words and provide actionable guidance
            3. Use || to separate tip and explanation
            4. Make this tip especially targeted to help with the user's specific issue
            5. Be very specific and practical in your advice
        """.trimIndent()
        
        Log.d(TAG, "Built prompt: $prompt")
        return prompt
    }
    
    /**
     * Extracts content from the API response
     */
    private fun extractContentFromResponse(responseText: String): String {
        try {
            Log.d(TAG, "Extracting content from response: $responseText")
            
            // For non-streaming responses
            if (!responseText.startsWith("data:")) {
                try {
                    val response = json.decodeFromString<ChatResponse>(responseText)
                    val content = response.choices.firstOrNull()?.message?.content ?: ""
                    Log.d(TAG, "Extracted content from non-streaming response: $content")
                    return content
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing non-streaming response: ${e.message}", e)
                    return responseText
                }
            }
            
            // For streaming responses
            val contentBuilder = StringBuilder()
            responseText.split("\n").forEach { line ->
                if (line.startsWith("data:") && line.length > 5) {
                    val jsonContent = line.substring(5).trim()
                    if (jsonContent != "[DONE]") {
                        try {
                            val response = json.decodeFromString<ChatResponse>(jsonContent)
                            val content = response.choices.firstOrNull()?.delta?.content
                            if (!content.isNullOrEmpty()) {
                                contentBuilder.append(content)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing streaming response line: $jsonContent", e)
                        }
                    }
                }
            }
            val result = contentBuilder.toString()
            Log.d(TAG, "Extracted content from streaming response: $result")
            return result
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting content from response", e)
            // If we can't parse the JSON, return the raw text (might be useful for debugging)
            return responseText
        }
    }
    
    /**
     * Parses a single tip from the content
     */
    private fun parseSingleTip(content: String): IELTSContent? {
        try {
            Log.d(TAG, "Parsing tip from content: $content")
            
            // Look for the separator pattern
            val pattern = Pattern.compile("(.*?)\\s*\\|\\|\\s*(.*)")
            val matcher = pattern.matcher(content)
            
            if (matcher.find()) {
                val tip = matcher.group(1)?.trim() ?: ""
                val explanation = matcher.group(2)?.trim() ?: ""
                
                if (tip.isNotEmpty() && explanation.isNotEmpty()) {
                    Log.d(TAG, "Successfully parsed tip: $tip")
                    Log.d(TAG, "Successfully parsed explanation: $explanation")
                    return IELTSContent(tip = tip, explanation = explanation)
                } else {
                    Log.e(TAG, "Parsed tip or explanation is empty")
                }
            } else {
                // If no separator is found, try to use the whole content as a tip
                if (content.isNotEmpty()) {
                    Log.d(TAG, "No separator found, using entire content as tip")
                    return IELTSContent(
                        tip = content.take(50).trim() + if (content.length > 50) "..." else "",
                        explanation = content
                    )
                } else {
                    Log.e(TAG, "Content is empty, cannot parse tip")
                }
            }
            
            return null
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing tip: ${e.message}", e)
            return null
        }
    }
    
    /**
     * Fallback content in case of API failure
     */
    private fun fallbackContent(category: DashboardCategory): IELTSContent {
        Log.e(TAG, "Using fallback content for category: $category")
        return IELTSContent(
            tip = "Practice with official IELTS ${category.name.lowercase()} materials",
            explanation = "Focus on official IELTS practice materials to familiarize yourself with the ${category.name.lowercase()} section format and requirements."
        )
    }
} 
