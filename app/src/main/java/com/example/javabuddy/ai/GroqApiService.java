package com.example.javabuddy.ai;

import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Queue;
import java.util.LinkedList;

/**
 * Groq AI API Service for JavaBuddy
 * 
 * Supported Models (as of November 2025):
 * - llama-3.3-70b-versatile: Latest high-quality model for complex tasks (RECOMMENDED)
 * - llama-3.1-8b-instant: Fast responses, good for simple tasks
 * - mixtral-8x7b-32768: Alternative model with good performance
 * - gemma2-9b-it: Newer Google model option
 * 
 * Note: Models may change over time. Check https://console.groq.com/docs/models for latest.
 */
public class GroqApiService {
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String API_KEY = "YOUR_GROQ_API_KEY_HERE"; // Replace with your actual key from https://console.groq.com
    
    // Rate limiting configuration
    private static final int MAX_REQUESTS_PER_MINUTE = 30; // Groq free tier limit
    private static final int MAX_TOKENS_PER_MINUTE = 14400; // Groq free tier limit
    private static final long RATE_LIMIT_WINDOW_MS = 60000; // 1 minute
    
    private final OkHttpClient client;
    private final Gson gson;
    
    // Rate limiting components
    private final Semaphore requestSemaphore;
    private final Queue<Long> requestTimestamps;
    private final AtomicLong totalTokensUsed;
    private final Queue<TokenUsage> tokenUsageHistory;
    private final Object rateLimitLock = new Object();

    
    public GroqApiService() {
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        this.gson = new Gson();
        
        // Initialize rate limiting components
        this.requestSemaphore = new Semaphore(MAX_REQUESTS_PER_MINUTE);
        this.requestTimestamps = new LinkedList<>();
        this.totalTokensUsed = new AtomicLong(0);
        this.tokenUsageHistory = new LinkedList<>();
    }
    
    // Inner class to track token usage with timestamps
    private static class TokenUsage {
        final long timestamp;
        final int tokens;
        
        TokenUsage(long timestamp, int tokens) {
            this.timestamp = timestamp;
            this.tokens = tokens;
        }
    }
    
    /**
     * Generate custom quiz questions based on Java topic
     */
    public CompletableFuture<String> generateQuizQuestions(String topic, int numberOfQuestions, String difficulty) {
        String prompt = String.format(
            "Generate %d Java programming quiz questions about '%s' at %s level. " +
            "Format each question as JSON with this structure:\n" +
            "{\n" +
            "  \"question\": \"The question text\",\n" +
            "  \"options\": [\"A) option1\", \"B) option2\", \"C) option3\", \"D) option4\"],\n" +
            "  \"correctAnswer\": \"A\",\n" +
            "  \"explanation\": \"Why this answer is correct\"\n" +
            "}\n" +
            "Return a JSON array of questions. Make questions practical and educational.",
            numberOfQuestions, topic, difficulty
        );
        return sendChatRequest(prompt, "llama-3.3-70b-versatile");
    }
    
    /**
     * Generate programming challenges/coding problems
     */
    public CompletableFuture<String> generateProgrammingChallenge(String topic, String difficulty, boolean includeTestCases) {
        String prompt = String.format(
            "Create a Java programming challenge about '%s' at %s difficulty level. " +
            "Format as JSON:\n" +
            "{\n" +
            "  \"title\": \"Challenge title\",\n" +
            "  \"description\": \"Problem description with examples\",\n" +
            "  \"difficulty\": \"%s\",\n" +
            "  \"expectedOutput\": \"What the solution should output\",\n" +
            "  \"starterCode\": \"Basic Java method signature to start with\",\n" +
            "  \"hints\": [\"Hint 1\", \"Hint 2\"],\n" +
            "  \"testCases\": %s,\n" +
            "  \"solution\": \"Complete working solution with comments\"\n" +
            "}\n" +
            "Make it educational and engaging for Java learners.",
            topic, difficulty, difficulty,
            includeTestCases ? "[{\"input\": \"test input\", \"expectedOutput\": \"expected result\"}]" : "[]"
        );
        return sendChatRequest(prompt, "llama-3.3-70b-versatile");
    }
    
    /**
     * Generate personalized study recommendations
     */
    public CompletableFuture<String> generateStudyRecommendations(String weakAreas, String strongAreas, String currentLevel) {
        String prompt = String.format(
            "Based on this Java learning analysis, provide personalized study recommendations:\n\n" +
            "Weak Areas: %s\n" +
            "Strong Areas: %s\n" +
            "Current Level: %s\n\n" +
            "Format as JSON:\n" +
            "{\n" +
            "  \"recommendations\": [\n" +
            "    {\n" +
            "      \"topic\": \"Topic to focus on\",\n" +
            "      \"priority\": \"High/Medium/Low\",\n" +
            "      \"suggestedActions\": [\"Action 1\", \"Action 2\"],\n" +
            "      \"estimatedTime\": \"Time needed to improve\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"nextSteps\": \"What to do next in learning journey\",\n" +
            "  \"motivationalMessage\": \"Encouraging message for the learner\"\n" +
            "}",
            weakAreas, strongAreas, currentLevel
        );
        return sendChatRequest(prompt, "llama-3.3-70b-versatile");
    }
    
    /**
     * Explain Java concepts in simple terms
     */
    public CompletableFuture<String> explainJavaConcept(String concept, String userLevel) {
        String prompt = String.format(
            "Explain the Java concept '%s' to a %s level programmer. " +
            "Use simple language, provide examples, and make it easy to understand. " +
            "Include practical use cases and common mistakes to avoid.",
            concept, userLevel
        );
        return sendChatRequest(prompt, "llama-3.3-70b-versatile");
    }

    /**
     * General helper for ad-hoc questions from the AI help panel.
     */
    public CompletableFuture<String> askGeneralQuestion(String question) {
        String prompt = "You are a friendly Java tutor helping learners using the JavaBuddy app. " +
            "Answer the question clearly, with short paragraphs or bullet points when helpful, " +
            "and include quick tips if relevant. If the question is not about Java, still provide " +
            "a helpful, concise answer.\n\nQuestion: " + question;
        return sendChatRequest(prompt, "llama-3.3-70b-versatile");
    }
    
    /**
     * Check if the service can handle a request immediately
     */
    public boolean canMakeRequestNow() {
        return canMakeRequest(1000); // Assume 1000 tokens for a typical request
    }
    
    /**
     * Get estimated wait time before next request can be made
     */
    public long getEstimatedWaitTimeMs() {
        synchronized (rateLimitLock) {
            if (canMakeRequest(1000)) {
                return 0;
            }
            
            long currentTime = System.currentTimeMillis();
            long earliestAvailable = currentTime + RATE_LIMIT_WINDOW_MS;
            
            // Find the earliest time when we'll be under limits
            if (!requestTimestamps.isEmpty()) {
                earliestAvailable = Math.min(earliestAvailable, 
                    requestTimestamps.peek() + RATE_LIMIT_WINDOW_MS);
            }
            
            if (!tokenUsageHistory.isEmpty()) {
                earliestAvailable = Math.min(earliestAvailable, 
                    tokenUsageHistory.peek().timestamp + RATE_LIMIT_WINDOW_MS);
            }
            
            return Math.max(0, earliestAvailable - currentTime);
        }
    }
    
    /**
     * Check if we can make a request without hitting rate limits
     */
    private boolean canMakeRequest(int estimatedTokens) {
        synchronized (rateLimitLock) {
            long currentTime = System.currentTimeMillis();
            
            // Clean up old request timestamps (older than 1 minute)
            requestTimestamps.removeIf(timestamp -> currentTime - timestamp > RATE_LIMIT_WINDOW_MS);
            
            // Clean up old token usage (older than 1 minute)
            tokenUsageHistory.removeIf(usage -> currentTime - usage.timestamp > RATE_LIMIT_WINDOW_MS);
            
            // Check request limit
            if (requestTimestamps.size() >= MAX_REQUESTS_PER_MINUTE) {
                return false;
            }
            
            // Check token limit
            int tokensInLastMinute = tokenUsageHistory.stream()
                .mapToInt(usage -> usage.tokens)
                .sum();
            
            if (tokensInLastMinute + estimatedTokens > MAX_TOKENS_PER_MINUTE) {
                return false;
            }
            
            return true;
        }
    }
    
    /**
     * Wait for rate limit to reset if necessary
     */
    private void waitForRateLimit(int estimatedTokens) throws InterruptedException {
        while (!canMakeRequest(estimatedTokens)) {
            synchronized (rateLimitLock) {
                // Find the oldest request timestamp
                Long oldestRequest = requestTimestamps.peek();
                TokenUsage oldestTokenUsage = tokenUsageHistory.peek();
                
                long waitTime = RATE_LIMIT_WINDOW_MS;
                
                if (oldestRequest != null) {
                    waitTime = Math.min(waitTime, 
                        RATE_LIMIT_WINDOW_MS - (System.currentTimeMillis() - oldestRequest));
                }
                
                if (oldestTokenUsage != null) {
                    waitTime = Math.min(waitTime, 
                        RATE_LIMIT_WINDOW_MS - (System.currentTimeMillis() - oldestTokenUsage.timestamp));
                }
                
                // Wait at least 1 second to avoid busy waiting
                waitTime = Math.max(1000, waitTime);
                
                android.util.Log.i("GroqApiService", 
                    "Rate limit reached. Waiting " + waitTime + "ms before retry...");
            }
            
            Thread.sleep(1000); // Wait 1 second and check again
        }
    }
    
    /**
     * Record a successful request for rate limiting
     */
    private void recordRequest(int tokensUsed) {
        synchronized (rateLimitLock) {
            long currentTime = System.currentTimeMillis();
            requestTimestamps.offer(currentTime);
            tokenUsageHistory.offer(new TokenUsage(currentTime, tokensUsed));
            totalTokensUsed.addAndGet(tokensUsed);
        }
    }
    
    /**
     * Estimate tokens for a request (rough approximation)
     */
    private int estimateTokens(String userMessage, int maxTokens) {
        // Rough estimation: 1 token ≈ 4 characters for English text
        int inputTokens = (userMessage.length() / 4) + 50; // Add system message tokens
        return inputTokens + maxTokens; // Input + max output tokens
    }
    
    /**
     * Get current rate limit status
     */
    public RateLimitStatus getRateLimitStatus() {
        synchronized (rateLimitLock) {
            long currentTime = System.currentTimeMillis();
            
            // Clean up old data
            requestTimestamps.removeIf(timestamp -> currentTime - timestamp > RATE_LIMIT_WINDOW_MS);
            tokenUsageHistory.removeIf(usage -> currentTime - usage.timestamp > RATE_LIMIT_WINDOW_MS);
            
            int requestsInLastMinute = requestTimestamps.size();
            int tokensInLastMinute = tokenUsageHistory.stream()
                .mapToInt(usage -> usage.tokens)
                .sum();
            
            return new RateLimitStatus(
                requestsInLastMinute, 
                MAX_REQUESTS_PER_MINUTE,
                tokensInLastMinute, 
                MAX_TOKENS_PER_MINUTE,
                totalTokensUsed.get()
            );
        }
    }
    
    public static class RateLimitStatus {
        public final int currentRequests;
        public final int maxRequests;
        public final int currentTokens;
        public final int maxTokens;
        public final long totalTokensUsed;
        
        public RateLimitStatus(int currentRequests, int maxRequests, 
                              int currentTokens, int maxTokens, long totalTokensUsed) {
            this.currentRequests = currentRequests;
            this.maxRequests = maxRequests;
            this.currentTokens = currentTokens;
            this.maxTokens = maxTokens;
            this.totalTokensUsed = totalTokensUsed;
        }
        
        public boolean isNearRequestLimit() {
            return currentRequests > (maxRequests * 0.8);
        }
        
        public boolean isNearTokenLimit() {
            return currentTokens > (maxTokens * 0.8);
        }
    }

    private CompletableFuture<String> sendChatRequest(String userMessage, String model) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                android.util.Log.d("GroqApiService", "Starting chat request for model: " + model);
                android.util.Log.d("GroqApiService", "User message length: " + userMessage.length());
                
                int maxTokens = 2048;
                int estimatedTokens = estimateTokens(userMessage, maxTokens);
                android.util.Log.d("GroqApiService", "Estimated tokens: " + estimatedTokens);
                
                // Wait for rate limit if necessary
                try {
                    waitForRateLimit(estimatedTokens);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Request interrupted while waiting for rate limit", e);
                }
                
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", model);
                
                JsonArray messages = new JsonArray();
                JsonObject systemMessage = new JsonObject();
                systemMessage.addProperty("role", "system");
                systemMessage.addProperty("content", 
                    "You are an expert Java programming instructor and evaluator. " +
                    "Provide accurate, educational, and helpful responses. " +
                    "When generating JSON, ensure it's valid and well-formatted.");
                
                JsonObject userMessageObj = new JsonObject();
                userMessageObj.addProperty("role", "user");
                userMessageObj.addProperty("content", userMessage);
                
                messages.add(systemMessage);
                messages.add(userMessageObj);
                requestBody.add("messages", messages);
                
                requestBody.addProperty("temperature", 0.7);
                requestBody.addProperty("max_tokens", maxTokens);
                
                RequestBody body = RequestBody.create(
                    gson.toJson(requestBody),
                    MediaType.parse("application/json")
                );
                
                Request request = new Request.Builder()
                    .url(GROQ_API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();
                
                // Check if API key is set
                if (API_KEY.equals("gsk_YOUR_ACTUAL_GROQ_API_KEY_HERE")) {
                    throw new RuntimeException("Please set your actual Groq API key in GroqApiService.java. " +
                        "Get your free API key from https://console.groq.com/keys");
                }
                
                android.util.Log.d("GroqApiService", "Making request to: " + GROQ_API_URL);
                android.util.Log.d("GroqApiService", "API key length: " + API_KEY.length());
                android.util.Log.d("GroqApiService", "Request body: " + gson.toJson(requestBody));
                
                try (Response response = client.newCall(request).execute()) {
                    // Handle rate limit response (429)
                    if (response.code() == 429) {
                        android.util.Log.w("GroqApiService", "Rate limit hit (HTTP 429), waiting and retrying...");
                        try {
                            Thread.sleep(5000); // Wait 5 seconds
                            return sendChatRequest(userMessage, model).get(); // Retry
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Request interrupted during rate limit retry", e);
                        }
                    }
                    
                    String responseBody = response.body().string();
                    android.util.Log.d("GroqApiService", "Response code: " + response.code());
                    android.util.Log.d("GroqApiService", "Response body: " + responseBody);
                    
                    if (!response.isSuccessful()) {
                        throw new IOException("API Error: " + response.code() + " - " + response.message() + ". Response: " + responseBody);
                    }
                    
                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                    
                    if (jsonResponse.has("error")) {
                        throw new IOException("API Error: " + jsonResponse.get("error").getAsString());
                    }
                    
                    // Record the successful request for rate limiting
                    int actualTokensUsed = estimatedTokens; // Use estimation since Groq doesn't always return usage
                    if (jsonResponse.has("usage")) {
                        JsonObject usage = jsonResponse.getAsJsonObject("usage");
                        if (usage.has("total_tokens")) {
                            actualTokensUsed = usage.get("total_tokens").getAsInt();
                        }
                    }
                    recordRequest(actualTokensUsed);
                    
                    return jsonResponse.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to get AI response: " + e.getMessage(), e);
            }
        });
    }
}