import os
import aiohttp
import json
import logging
from models import ChatCompletion, ChatResponse, Message, Choice, Usage, XGroq

class APIService:
    def __init__(self):
        self.base_url = "https://api.groq.com/openai/v1/chat/completions"
        self.api_key = os.getenv('GROQ_API_KEY')
        if not self.api_key:
            raise ValueError("GROQ_API_KEY not found in environment variables")

    async def get_chat_completion(self, chat_completion: ChatCompletion) -> ChatResponse:
        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json"
        }
        
        request_data = {
            "model": chat_completion.model,
            "messages": chat_completion.messages,
            "stream": chat_completion.stream,
            "temperature": chat_completion.temperature,
            "max_tokens": chat_completion.max_tokens
        }
        
        try:
            async with aiohttp.ClientSession() as session:
                async with session.post(
                    self.base_url,
                    headers=headers,
                    json=request_data
                ) as response:
                    if response.status != 200:
                        error_text = await response.text()
                        logging.error(f"API request failed with status {response.status}: {error_text}")
                        raise Exception(f"API request failed with status {response.status}")
                    
                    data = await response.json()
                    logging.debug(f"API Response: {json.dumps(data, indent=2)}")
                    
                    try:
                        # Convert nested structures manually
                        choices = [
                            Choice(
                                index=c["index"],
                                message=Message(**c["message"]),
                                logprobs=c.get("logprobs"),
                                finish_reason=c.get("finish_reason")
                            )
                            for c in data["choices"]
                        ]
                        
                        usage = Usage(**data["usage"])
                        x_groq = XGroq(**data["x_groq"])
                        
                        return ChatResponse(
                            id=data["id"],
                            object=data["object"],
                            created=data["created"],
                            model=data["model"],
                            choices=choices,
                            usage=usage,
                            system_fingerprint=data["system_fingerprint"],
                            x_groq=x_groq
                        )
                    except (KeyError, TypeError) as e:
                        logging.error(f"Error parsing response: {e}")
                        logging.error(f"Response data: {data}")
                        raise
                        
        except aiohttp.ClientError as e:
            logging.error(f"Network error: {e}")
            raise
        except Exception as e:
            logging.error(f"Unexpected error: {e}")
            raise 