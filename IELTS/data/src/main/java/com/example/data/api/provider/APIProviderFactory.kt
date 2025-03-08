package com.example.data.api.provider

/**
 * Factory for creating API providers
 */
object APIProviderFactory {
    /**
     * Available API provider types
     */
    enum class ProviderType {
        GROQ,
        OPENROUTER,
        MISTRAL
        // Add more providers here as needed (e.g., OPENAI, etc.)
    }
    
    /**
     * Get an API provider by type
     * @param type The provider type
     * @return The API provider
     */
    fun getProvider(type: ProviderType = ProviderType.GROQ): APIProvider {
        return when (type) {
            ProviderType.GROQ -> GroqProvider()
            ProviderType.OPENROUTER -> OpenRouterProvider()
            ProviderType.MISTRAL -> MistralProvider()
            // Add more cases here as more providers are added
        }
    }
} 