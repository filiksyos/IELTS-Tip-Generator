package com.example.assistant.models

import android.util.Log
import com.example.assistant.BuildConfig
import com.example.assistant.data.assistants

// Settings data class for app configuration - Using Mixtral model through XAI/Groq API only
data class Settings(
    // Fixed to Mixtral model - no other options needed
    val model: Model = Model("mixtral-8x7b-32768"),
    // Currently active assistant profile
    var selectedAssistant: String = "Multi-Search Generator",
    // Map of assistant names to their system prompts
    var prompts: Map<String, String> = assistants.associate {
        it.name to it.defaultPrompt 
    },
    // XAI API key from BuildConfig
    val xaiKey: String = BuildConfig.XAI_API_KEY,
    // Temperature setting to control randomness (0.0-1.0)
    // Lower values (0.1-0.3) produce more focused, deterministic responses
    val temperature: Double = 0.2,
    // Maximum tokens to generate in the response
    val maxTokens: Int = 150
) {
    companion object {
        private const val TAG = "Settings"

        /**
         * Creates a Settings instance with required assistant configuration
         * 
         * @param selectedAssistant Assistant name, defaults to "Multi-Search Generator"
         * @param prompts System prompts for assistants, defaults to multi-search query generator
         * @param temperature Controls randomness (0.0-1.0), lower is more deterministic
         * @param maxTokens Maximum length of generated response
         */
        fun create(
            selectedAssistant: String? = null,
            prompts: Map<String, String>? = null,
            temperature: Double = 0.2,
            maxTokens: Int = 150
        ): Settings {
            Log.d(TAG, "Creating settings")
            
            // Only assistant configuration is customizable
            val assistant = selectedAssistant ?: "Multi-Search Generator"
            val assistantPrompts = prompts ?: mapOf(
                assistant to """You are a search query generator that provides multiple search options.
                |Your job is to convert user questions into 3 different effective search queries.
                |
                |RESPONSE FORMAT:
                |Return exactly 3 search queries in this format:
                |"first search query",
                |"second search query with different focus",
                |"third search query with another perspective"
                |
                |RULES:
                |1. Always return EXACTLY 3 queries
                |2. Each query must be in quotation marks and separated by commas
                |3. Each query should offer a different perspective or focus
                |4. Keep queries concise (under 10 words each)
                |5. Do NOT include explanations or additional text
                |
                |Examples:
                |User: "How do I learn programming?"
                |Assistant: 
                |"programming tutorials for beginners",
                |"best programming languages to learn first",
                |"project based programming learning"
                |
                |User: "Show me funny videos"
                |Assistant:
                |"funny videos 2022",
                |"hilarious videos 2023",
                |"funny YouTube videos humor"
                |""".trimMargin()
            )

            Log.d(TAG, "Using assistant: $assistant")
            Log.d(TAG, "Using prompts: $assistantPrompts")
            Log.d(TAG, "Using temperature: $temperature")
            Log.d(TAG, "Using max tokens: $maxTokens")
            Log.d(TAG, "Using XAI key: ${BuildConfig.XAI_API_KEY.take(5)}...")

            return Settings(
                selectedAssistant = assistant,
                prompts = assistantPrompts,
                temperature = temperature,
                maxTokens = maxTokens
            ).also {
                Log.d(TAG, "Settings created successfully")
            }
        }
    }
}