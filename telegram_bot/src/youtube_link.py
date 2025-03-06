class YouTubeLink:
    BASE_URL = "https://www.youtube.com/results?search_query="

    @staticmethod
    def get_link(query: str) -> str:
        """Generate a YouTube search link from a query."""
        if not query:
            return YouTubeLink.BASE_URL
        # Replace spaces with + for URL formatting
        formatted_query = query.strip().replace(" ", "+")
        return f"{YouTubeLink.BASE_URL}{formatted_query}" 