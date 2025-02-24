import OpenAI from "openai";
import dotenv from "dotenv";

dotenv.config();

export const llm = new OpenAI({
    baseURL: "https://api.groq.com/openai/v1",
    apiKey: process.env.XAI_API_KEY,
});