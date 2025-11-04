# Groq AI API Setup Instructions

## Quick Setup Steps

### 1. Get Your Free Groq API Key
1. Visit [Groq Console](https://console.groq.com/keys)
2. Sign up for a free account (if you don't have one)
3. Go to the API Keys section
4. Click "Create API Key"
5. Copy your new API key (it starts with `gsk_`)

### 2. Add Your API Key to the App
1. Open the file: `app/src/main/java/com/example/javabuddy/ai/GroqApiService.java`
2. Find this line (around line 17):
   ```java
   private static final String API_KEY = "gsk_YOUR_ACTUAL_GROQ_API_KEY_HERE";
   ```
3. Replace `gsk_YOUR_ACTUAL_GROQ_API_KEY_HERE` with your actual API key

### 3. Example
```java
private static final String API_KEY = "gsk_1234567890abcdef1234567890abcdef"; // Your actual key
```

### 4. Rebuild and Test
1. Build the app: `.\gradlew assembleDebug`
2. Install: `.\gradlew installDebug`
3. Test the AI features in the app

## Groq Models Used (November 2025)
- **llama-3.1-70b-versatile**: High-quality responses for complex tasks (quiz generation, challenges, explanations)
- **llama-3.1-8b-instant**: Fast responses for simple tasks (quiz grading) 
- **mixtral-8x7b-32768**: Alternative model for code evaluation

## Groq Free Tier Limits
- **30 requests per minute**
- **6,000 tokens per minute**
- The app automatically handles these limits with rate limiting

## Troubleshooting
- **"API Key Required" error**: Your API key is not set or invalid
- **"Rate limit exceeded"**: Wait 1 minute before trying again
- **"Invalid API Key"**: Double-check your API key is correct
- **Network errors**: Check your internet connection

## Available AI Features
1. **AI Quiz Generator**: Generate custom Java quiz questions
2. **AI Programming Challenges**: Create coding exercises with solutions
Your JavaBuddy app now has powerful AI features powered by Groq's fast LLM models!