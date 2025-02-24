package com.example.assistant.models

import android.util.Log
import com.example.assistant.BuildConfig

// Settings data class for app configuration - Using Mixtral model through XAI/Groq API only
data class Settings(
    // Fixed to Mixtral model - no other options needed
    val model: Model = Model("mixtral-8x7b-32768"),
    // Currently active assistant profile
    var selectedAssistant: String,
    // Map of assistant names to their system prompts
    var prompts: Map<String, String>,
    // XAI API key from BuildConfig
    val xaiKey: String = BuildConfig.XAI_API_KEY
) {
    companion object {
        private const val TAG = "Settings"

        /**
         * Creates a Settings instance with required assistant configuration
         * 
         * @param selectedAssistant Assistant name, defaults to "Personal Assistant"
         * @param prompts System prompts for assistants, defaults to basic helpful assistant
         */
        fun create(
            selectedAssistant: String? = null,
            prompts: Map<String, String>? = null
        ): Settings {
            Log.d(TAG, "Creating settings")
            
            // Only assistant configuration is customizable
            val assistant = selectedAssistant ?: "Personal Assistant"
            val assistantPrompts = prompts ?: mapOf(
                assistant to "You are a helpful assistant."
            )

            Log.d(TAG, "Using assistant: $assistant")
            Log.d(TAG, "Using prompts: $assistantPrompts")
            Log.d(TAG, "Using XAI key: ${BuildConfig.XAI_API_KEY.take(5)}...")

            return Settings(
                selectedAssistant = assistant,
                prompts = assistantPrompts
            ).also {
                Log.d(TAG, "Settings created successfully")
            }
        }
    }
}