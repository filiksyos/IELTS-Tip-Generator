# IELTS Search Bot

A Telegram bot for IELTS-related searches.

## Setup

1. Create a virtual environment:
```bash
python -m venv venv
```

2. Activate the virtual environment:
- Windows:
```bash
venv\Scripts\activate
```
- Unix/MacOS:
```bash
source venv/bin/activate
```

3. Install dependencies:
```bash
pip install -r requirements.txt
```

4. Set up your bot token:
- Get a bot token from [@BotFather](https://t.me/botfather) on Telegram
- Copy the token to the `.env` file:
```
TELEGRAM_TOKEN=your_bot_token_here
```

5. Run the bot:
```bash
python src/bot.py
```

## Available Commands

- `/start` - Start the bot
- `/search` - Search for IELTS resources (currently returns "searched") 