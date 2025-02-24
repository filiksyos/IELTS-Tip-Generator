package com.example.assistant

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.assistant.data.assistants
import com.example.assistant.models.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val TAG = "UserPreferences"

val Context.dataStore by preferencesDataStore("user_preferences")

val SELECTED_ASSISTANT = stringPreferencesKey("selected_assistant")
val PROMPTS = stringPreferencesKey("prompts")

fun getSettingsFlow(context: Context): Flow<Settings> {
    return context.dataStore.data
        .map { preferences ->
            Settings.create(
                selectedAssistant = preferences[SELECTED_ASSISTANT],
                prompts = preferences[PROMPTS]?.let { Json.decodeFromString<Map<String, String>>(it) }
            )
        }
}

suspend fun updateSetting(context: Context, key: Preferences.Key<String>, value: String) {
    Log.d(TAG, "Updating setting $key to $value")
    context.dataStore.edit { preferences ->
        preferences[key] = value
    }
}

suspend fun updatePrompt(context: Context, assistant: String, prompt: String) {
    Log.d(TAG, "Updating prompt of $assistant to $prompt")
    context.dataStore.edit { preferences ->
        val currentPrompts = preferences[PROMPTS]
        val prompts = if (currentPrompts == null)
            assistants.associateBy({ it.name }, { it.defaultPrompt }).toMutableMap()
        else
            Json.decodeFromString<MutableMap<String, String>>(currentPrompts)
        prompts[assistant] = prompt
        preferences[PROMPTS] = Json.encodeToString(prompts)
    }
}