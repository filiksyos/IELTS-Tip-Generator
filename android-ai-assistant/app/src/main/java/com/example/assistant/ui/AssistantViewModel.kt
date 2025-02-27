package com.example.assistant.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.assistant.AssistantApplication
import com.example.assistant.api.ApiService
import com.example.assistant.api.ChatApi
import com.example.assistant.data.assistants
import com.example.assistant.getSettingsFlow
import com.example.assistant.models.ChatCompletion
import com.example.assistant.models.Message
import com.example.assistant.models.Settings
import com.example.assistant.ui.chat.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

@OptIn(ExperimentalCoroutinesApi::class)
class AssistantViewModel(private val application: AssistantApplication): AndroidViewModel(application) {

    companion object {
        const val TAG = "ChatViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY]) as AssistantApplication
                return AssistantViewModel((application )) as T
            }
        }
    }

    private val messagesRepository = application.messagesRepository
    private val chatViewModel = ChatViewModel(
        chatApi = ApiService.create().create(ChatApi::class.java)
    )

    val settingsFlow = getSettingsFlow(application)
    val messagesFlow = settingsFlow
        .distinctUntilChanged { oldSettings, newSettings ->
            oldSettings.selectedAssistant == newSettings.selectedAssistant
        }
        .flatMapLatest { settings ->
            messagesRepository.getAllMessagesFlow(settings.selectedAssistant)
        }
    var gettingCompletion by mutableStateOf(false)

    private var getCompletionJob: Job? = null

    fun clearMessages(assistant: String) {
        viewModelScope.launch {
            getCompletionJob?.cancelAndJoin()
            messagesRepository.deleteAllMessages(assistant)
        }
    }

    fun onNewMessage(text: String, messages: List<Message>, settings: Settings) {
        if (settings.selectedAssistant == "Multi-Search Generator") {
            val sanitizedText = text.trim()
            if (sanitizedText.isEmpty()) return
        }
        
        viewModelScope.launch {
            val newMessage = Message(
                assistant = settings.selectedAssistant,
                role = "user",
                content = text
            )
            messagesRepository.insertMessage(newMessage)
            getCompletion(messages + newMessage, settings)
        }
    }

    suspend fun addFirstMessage(assistant: String) {
        Log.d(TAG, "Adding first message for $assistant")
        val firstMessage = assistants
            .find { a -> a.name == assistant }
            ?.firstMessage

        if (firstMessage != null) {
            messagesRepository.insertMessage(
                Message(assistant = assistant, role = "assistant", content = firstMessage)
            )
        }
    }

    private suspend fun getCompletion(messages: List<Message>, settings: Settings) = withContext(Dispatchers.Default) {
        gettingCompletion = true

        val messagesToBeSent = messages
            .reduceMessages()
            .addContext(settings.selectedAssistant, settings.prompts[settings.selectedAssistant] ?: "")
            .map { mapOf("role" to it.role, "content" to it.content) }

        try {
            val chat = ChatCompletion(
                model = "mixtral-8x7b-32768",
                messages = messagesToBeSent,
                stream = true
            )
            Log.d(TAG, "Sending chat: $chat")
            
            // Get streaming response as string
            val responseContent = chatViewModel.getCompletion(chat)
            
            // Create and save assistant message
            val assistantMessage = Message(
                assistant = settings.selectedAssistant,
                role = "assistant",
                content = responseContent
            )
            
            // Process multi-search response if needed
            if (settings.selectedAssistant == "Multi-Search Generator") {
                Log.d(TAG, "Processing multi-search response")
                // Simple text parsing for the new format
                val queries = parseSearchQueries(responseContent)
                if (queries.isNotEmpty()) {
                    Log.d(TAG, "Parsed ${queries.size} search queries: $queries")
                }
            }
            
            messagesRepository.insertMessage(assistantMessage)
            
        } catch (e: Exception) {
            Log.e(TAG, "Chat error", e)
            // Create error message
            val errorMessage = Message(
                assistant = settings.selectedAssistant,
                role = "assistant",
                content = "Error: ${e.message}"
            )
            messagesRepository.insertMessage(errorMessage)
        } finally {
            gettingCompletion = false
        }
    }

    /**
     * Parses search queries from the response text
     * Expects format: "query1", "query2", "query3"
     */
    private fun parseSearchQueries(response: String): List<String> {
        val regex = """"([^"]+)"""".toRegex()
        val matches = regex.findAll(response)
        return matches.map { it.groupValues[1] }.toList()
    }

    private fun List<Message>.reduceMessages(): List<Message> {
        return this.takeLast(9)
    }

    private fun List<Message>.addContext(assistant: String, context: String): List<Message> {
        return listOf(Message(assistant = assistant, role = "system", content = context)) + this
    }

    private fun Message.addContent(content: String) {
        this.content += content
    }
}