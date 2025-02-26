package com.example.presentation.dataTest

import com.example.data.Utils.YouTubeLink
import org.junit.Assert.assertTrue
import kotlin.test.Test
import kotlin.test.assertNotNull

class YouTubeLinkTest {

    //This area is coded because it would be added later as Link.kt improves.
/*    @Test
    fun `getLink returns valid YouTube URL`() {
        // Observation: The function constructs a URL based on the provided title.
        // Question: Does it return a valid YouTube URL for a given title?
        // Hypothesis: The returned string should match the YouTube URL pattern.

        // Independent variable: Title passed to the function.
        // Dependent variable: The returned URL string.
        // Controlled variables: Base URL and query parameters.

        // Act
        val link = YouTubeLink.getLink("IELTS Lesson")

        // Assert
        assertTrue(link.startsWith("https://www.youtube.com/"))
        assertTrue(link.contains("IELTS+Lesson"))
    }*/

/*    @Test
    fun `getLink handles special characters in title`() {
        // Observation: Titles with special characters should be properly URL-encoded.
        // Question: Does the function handle special characters without errors?
        // Hypothesis: The returned URL should encode special characters correctly.

        // Arrange
        val title = "IELTS: Advanced & Beginner's Guide"

        // Act
        val link = YouTubeLink.getLink(title)

        // Assert
        assertTrue(link.contains("IELTS%3A+Advanced+%26+Beginner%27s+Guide"))
    }*/

    @Test
    fun `getLink handles empty or null title gracefully`() {
        // Observation: An empty or null title could cause issues in URL construction.
        // Question: Does the function handle these inputs without crashing?
        // Hypothesis: The function should return a base URL or handle null gracefully.

        // Act
        val emptyLink = YouTubeLink.getLink("")
        val nullLink = YouTubeLink.getLink(null)

        // Assert
        assertNotNull(emptyLink)
        assertTrue(emptyLink.startsWith("https://www.youtube.com/"))
        assertNotNull(nullLink)
        assertTrue(nullLink.startsWith("https://www.youtube.com/"))
    }
}
