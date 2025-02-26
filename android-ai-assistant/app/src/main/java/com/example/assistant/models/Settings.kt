package com.example.assistant.models

import android.util.Log
import com.example.assistant.BuildConfig
import com.example.assistant.data.assistants

// Settings data class for app configuration - Using Mixtral model through XAI/Groq API only
data class Settings(
    // Fixed to Mixtral model - no other options needed
    val model: Model = Model("mixtral-8x7b-32768"),
    // Currently active assistant profile
    var selectedAssistant: String = "Personal Assistant",
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
         * @param selectedAssistant Assistant name, defaults to "YouTube Search"
         * @param prompts System prompts for assistants, defaults to YouTube search query generator
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
            val assistant = selectedAssistant ?: "YouTube Search"
            val assistantPrompts = prompts ?: mapOf(
                assistant to """You are a YouTube search query generator. 
                |Your ONLY job is to convert user questions into effective YouTube search queries.
                |
                |RESPONSE FORMAT:
                |1. Respond ONLY with the search query in the format: "search_query"
                |2. Do NOT include explanations, introductions, or additional text
                |3. Keep queries concise (under 10 words when possible)
                |4. Do NOT use markdown formatting
                |
                |Examples:
                |User: "How do I make pasta carbonara?"
                |Assistant: "pasta carbonara recipe easy homemade"
                |
                |User: "Tell me about quantum physics"
                |Assistant: "quantum physics explained simply"
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