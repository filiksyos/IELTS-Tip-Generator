import os
import sys
import logging
from dotenv import load_dotenv
from telegram import Update
from telegram.ext import Application, CommandHandler, ContextTypes
from telegram.error import NetworkError
from models import ChatCompletion
from api_service import APIService

# Enable logging
logging.basicConfig(
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    level=logging.INFO
)

# Load environment variables
load_dotenv()

# Initialize API service
api_service = APIService()

# Command handlers
async def start_command(update: Update, context: ContextTypes.DEFAULT_TYPE):
    """Send a message when the command /start is issued."""
    await update.message.reply_text('Hi! I am your IELTS search bot. Use /search followed by your question.')

async def search_command(update: Update, context: ContextTypes.DEFAULT_TYPE):
    """Handle the /search command"""
    # Get the query text after /search
    query = ' '.join(context.args)
    if not query:
        await update.message.reply_text('Please provide a question after /search')
        return

    try:
        # Create chat completion request
        chat_completion = ChatCompletion(
            messages=[
                {"role": "user", "content": query}
            ]
        )
        
        # Get response from AI
        response = await api_service.get_chat_completion(chat_completion)
        
        # Extract and send the response
        ai_response = response.choices[0].message.content if response.choices else "No response generated"
        await update.message.reply_text(ai_response)
        
    except Exception as e:
        logging.error(f"Error in search command: {e}")
        await update.message.reply_text("Sorry, I encountered an error processing your request.")

async def error_handler(update: object, context: ContextTypes.DEFAULT_TYPE) -> None:
    """Handle errors caused by updates."""
    logging.error(f"Error occurred: {context.error}")

def run_bot():
    """Run the bot."""
    token = os.getenv('TELEGRAM_TOKEN')
    if not token:
        logging.error("Error: TELEGRAM_TOKEN not found in .env file")
        sys.exit(1)

    # Create the Application
    app = Application.builder().token(token).build()

    # Add handlers
    app.add_handler(CommandHandler("start", start_command))
    app.add_handler(CommandHandler("search", search_command))
    app.add_error_handler(error_handler)

    # Start the bot
    logging.info("Starting bot...")
    app.run_polling(allowed_updates=Update.ALL_TYPES)

if __name__ == '__main__':
    try:
        run_bot()
    except KeyboardInterrupt:
        logging.info("Bot stopped by user")
    except Exception as e:
        logging.error(f"Error running bot: {e}")
        sys.exit(1) 