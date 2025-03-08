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
 * Generates search queries for IELTS categories using AI
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
     * Generates search queries for all IELTS categories
     * @return Map of category to search query
     */
    suspend fun generateQueriesForAllCategories(): Map<DashboardCategory, IELTSContent> = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val preferences = preferencesManager.getUserPreferences()
        
        val prompt = buildPersonalizedPrompt(preferences, timestamp)
        
        try {
            val messages = listOf(
                mapOf("role" to "system", "content" to "You are a knowledgeable IELTS exam preparation assistant. Generate unique and detailed explanations for IELTS tips."),
                mapOf("role" to "user", "content" to prompt)
            )
            
            val chatCompletion = ChatCompletion(
                messages = messages,
                stream = true,
                temperature = 0.8,
                maxTokens = 400  // Increased for longer explanations
            )
            
            Log.d(TAG, "Sending request with completion: $chatCompletion")
            // Use Mistral provider for IELTS tip generation
            val response = ApiService.getChatCompletion(
                chatCompletion,
                APIProviderFactory.ProviderType.MISTRAL
            )
            val responseText = response.byteStream().bufferedReader().use { it.readText() }
            Log.d(TAG, "Raw API Response: $responseText")
            
            val content = extractContentFromResponse(responseText)
            Log.d(TAG, "Extracted content: $content")
            
            val tipExplanationPairs = parseContent(content)
            Log.d(TAG, "Parsed tip-explanation pairs: $tipExplanationPairs")
            
            val result = mutableMapOf<DashboardCategory, IELTSContent>()
            for ((categoryName, content) in tipExplanationPairs) {
                val category = when (categoryName.lowercase()) {
                    "reading" -> DashboardCategory.READING
                    "listening" -> DashboardCategory.LISTENING
                    "writing" -> DashboardCategory.WRITING
                    "speaking" -> DashboardCategory.SPEAKING
                    else -> continue
                }
                result[category] = content
            }
            
            for (category in DashboardCategory.values()) {
                if (!result.containsKey(category)) {
                    result[category] = IELTSContent(
                        tip = "Practice ${category.name.lowercase()} with official IELTS materials",
                        explanation = "Focus on official IELTS ${category.name.lowercase()} practice materials to familiarize yourself with the exam format and requirements."
                    )
                }
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error generating content", e)
            DashboardCategory.values().associateWith { category ->
                IELTSContent(
                    tip = "Practice ${category.name.lowercase()} with official IELTS materials",
                    explanation = "Focus on official IELTS ${category.name.lowercase()} practice materials to familiarize yourself with the exam format and requirements."
                )
            }
        }
    }
    
    private fun buildPersonalizedPrompt(preferences: UserPreferences, timestamp: Long): String {
        return """
            Generate 4 NEW and DIFFERENT IELTS study tips with detailed explanations (timestamp: $timestamp).
            Each tip should be concise and actionable, with a detailed explanation of how to implement it.
            
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
            "Reading: {tip} || {detailed explanation}",
            "Listening: {tip} || {detailed explanation}",
            "Writing: {tip} || {detailed explanation}",
            "Speaking: {tip} || {detailed explanation}"
            
            RULES:
            1. Always return EXACTLY 4 pairs
            2. Each pair must be in quotation marks and separated by commas
            3. Each pair must start with the category name followed by colon
            4. Tips should be 10-15 words
            5. Explanations should be 50-100 words and provide actionable guidance
            6. Use || to separate tip and explanation
            7. Make the ${preferences.weakestSkill} tip especially targeted and specific
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
     * Parses the queries from the response
     * @return Map of category name to query
     */
    private fun parseContent(response: String): Map<String, IELTSContent> {
        val result = mutableMapOf<String, IELTSContent>()
        
        val pattern = Pattern.compile(""""(Reading|Listening|Writing|Speaking):\s*([^|]+)\|\|\s*([^"]+)"""", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(response)
        
        while (matcher.find()) {
            val category = matcher.group(1)
            val tip = matcher.group(2)
            val explanation = matcher.group(3)
            if (category != null && tip != null && explanation != null) {
                result[category] = IELTSContent(tip.trim(), explanation.trim())
            }
        }
        
        return result
    }
} 