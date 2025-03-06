from dataclasses import dataclass
from typing import List, Dict, Optional, Any

@dataclass
class ChatCompletion:
    messages: List[Dict[str, str]]
    model: str = "mixtral-8x7b-32768"
    stream: bool = False
    temperature: float = 0.7
    max_tokens: int = 50

@dataclass
class Message:
    role: str
    content: str

@dataclass
class Delta:
    content: Optional[str] = None

@dataclass
class Choice:
    index: int
    message: Message
    logprobs: Optional[Any] = None
    finish_reason: Optional[str] = None

@dataclass
class Usage:
    queue_time: float
    prompt_tokens: int
    prompt_time: float
    completion_tokens: int
    completion_time: float
    total_tokens: int
    total_time: float

@dataclass
class XGroq:
    id: str

@dataclass
class ChatResponse:
    id: str
    object: str
    created: int
    model: str
    choices: List[Choice]
    usage: Usage
    system_fingerprint: str
    x_groq: XGroq 