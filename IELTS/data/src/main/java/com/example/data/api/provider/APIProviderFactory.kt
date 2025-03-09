package com.example.data.api.provider

/**
 * Factory for creating API providers
 * Currently configured to always use Groq API for all requests
 */
object APIProviderFactory {
    /**
     * Available API provider types
     * Note: Currently all types resolve to Groq provider
     */
    enum class ProviderType {
        GROQ,
        OPENROUTER,
        MISTRAL,
        NEW_GROQ
    }
    
    /**
     * Get an API provider by type
     * Currently returns Groq provider for all types
     * @param type The provider type (currently ignored as all requests use Groq)
     * @return The Groq API provider
     */
    fun getProvider(type: ProviderType = ProviderType.GROQ): APIProvider {
        // Always return Groq provider regardless of the requested type
        return GroqProvider()
    }
} 