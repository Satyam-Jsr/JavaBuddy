package com.example.javabuddy.ai;

import android.content.Context;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

/**
 * Helper class to display rate limit information to users
 */
public class RateLimitHelper {
    
    public static void showRateLimitStatus(Context context, GroqApiService.RateLimitStatus status) {
        String message = String.format(
            "AI Usage Status:\n\n" +
            "Requests: %d/%d (%.1f%%)\n" +
            "Tokens: %d/%d (%.1f%%)\n" +
            "Total tokens used: %d\n\n" +
            "%s",
            status.currentRequests, status.maxRequests, 
            (status.currentRequests * 100.0 / status.maxRequests),
            status.currentTokens, status.maxTokens,
            (status.currentTokens * 100.0 / status.maxTokens),
            status.totalTokensUsed,
            getRateLimitAdvice(status)
        );
        
        new AlertDialog.Builder(context)
            .setTitle("🤖 AI Rate Limit Status")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }
    
    public static void showRateLimitWarning(Context context, GroqApiService.RateLimitStatus status) {
        if (status.isNearRequestLimit() || status.isNearTokenLimit()) {
            String warningMessage = "⚠️ Approaching AI usage limits!\n\n" +
                getRateLimitAdvice(status);
            
            Toast.makeText(context, warningMessage, Toast.LENGTH_LONG).show();
        }
    }
    
    public static void showRateLimitExceeded(Context context, long waitTimeMs) {
        String waitTime = formatWaitTime(waitTimeMs);
        String message = String.format(
            "🚫 AI Rate Limit Reached\n\n" +
            "Please wait %s before making another request.\n\n" +
            "Tip: Use the app's offline features like lessons and practice problems while waiting!",
            waitTime
        );
        
        new AlertDialog.Builder(context)
            .setTitle("Rate Limit Exceeded")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }
    
    private static String getRateLimitAdvice(GroqApiService.RateLimitStatus status) {
        if (status.isNearRequestLimit() && status.isNearTokenLimit()) {
            return "💡 Consider reducing request frequency and asking shorter questions.";
        } else if (status.isNearRequestLimit()) {
            return "💡 Try waiting a bit between AI requests.";
        } else if (status.isNearTokenLimit()) {
            return "💡 Try asking shorter questions or reducing quiz sizes.";
        } else {
            return "✅ You're well within the usage limits!";
        }
    }
    
    private static String formatWaitTime(long waitTimeMs) {
        if (waitTimeMs < 1000) {
            return "a moment";
        } else if (waitTimeMs < 60000) {
            return String.format("%d seconds", waitTimeMs / 1000);
        } else {
            return String.format("%d minutes", waitTimeMs / 60000);
        }
    }
    
    public static String getUsageProgressText(GroqApiService.RateLimitStatus status) {
        return String.format("AI Usage: %d/%d requests, %d/%d tokens", 
            status.currentRequests, status.maxRequests,
            status.currentTokens, status.maxTokens);
    }
}