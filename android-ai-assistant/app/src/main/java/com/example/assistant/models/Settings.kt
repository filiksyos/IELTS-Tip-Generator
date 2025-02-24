package com.example.assistant.models

import android.util.Log
import com.example.assistant.BuildConfig
import com.example.assistant.data.assistants
import com.example.assistant.data.models

data class Settings(
    var selectedModel: Model,
    var selectedAssistant: String,
    var prompts: Map<String, String>,
    var openAiKey: String,
    var usageCounter: Double
) {
    companion object {
        private const val TAG = "Settings"

        fun withDefaults(
            selectedModel: Model? = null,
            selectedAssistant: String? = null,
            prompts: Map<String, String>? = null,
            openAiKey: String? = null,
            usageCounter: Double? = null
        ): Settings {
            Log.d(TAG, "selectedModel: $selectedModel")
            Log.d(TAG, "selectedAssistant: $selectedAssistant")
            Log.d(TAG, "prompts: $prompts")
            Log.d(TAG, "openAiKey: $openAiKey")
            Log.d(TAG, "usageCounter: $usageCounter")

            val defaultModel = try {
                models.firstOrNull() ?: Model("default_model", inputPrice = 0.0, outputPrice = 0.0)
            } catch (e: Exception) {
                Model("default_model", inputPrice = 0.0, outputPrice = 0.0)
            }

            val defaultAssistant = try {
                assistants.firstOrNull()?.name ?: "default_assistant"
            } catch (e: Exception) {
                "default_assistant"
            }

            val defaultPrompts = try {
                assistants.associateBy({ it.name }, { prompts?.get(it.name) ?: it.defaultPrompt })
            } catch (e: Exception) {
                mapOf("default_assistant" to "default_prompt")
            }

            val defaultOpenAiKey = try {
                BuildConfig.OPENAI_API_KEY ?: "default_openai_key"
            } catch (e: Exception) {
                "default_openai_key"
            }

            return Settings(
                selectedModel = selectedModel ?: defaultModel,
                selectedAssistant = selectedAssistant ?: defaultAssistant,
                prompts = prompts ?: defaultPrompts,
                openAiKey = openAiKey ?: defaultOpenAiKey,
                usageCounter = usageCounter ?: 0.0
            )
        }
    }
}