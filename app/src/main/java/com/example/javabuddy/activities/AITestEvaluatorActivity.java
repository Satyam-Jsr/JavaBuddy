package com.example.javabuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.example.javabuddy.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AITestEvaluatorActivity extends AppCompatActivity {
    
    private TextView titleText;
    private TextView scoreText;
    private TextView gradeText;
    private TextView overallFeedbackText;
    private CardView codeQualityCard;
    private TextView readabilityScore;
    private TextView efficiencyScore;
    private TextView bestPracticesScore;
    private TextView suggestionsText;
    private TextView errorsText;
    private ProgressBar loadingProgress;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_test_evaluator);
        
        initViews();
        setupToolbar();
        processEvaluationData();
    }
    
    private void initViews() {
        titleText = findViewById(R.id.title_text);
        scoreText = findViewById(R.id.score_text);
        gradeText = findViewById(R.id.grade_text);
        overallFeedbackText = findViewById(R.id.overall_feedback_text);
        codeQualityCard = findViewById(R.id.code_quality_card);
        readabilityScore = findViewById(R.id.readability_score);
        efficiencyScore = findViewById(R.id.efficiency_score);
        bestPracticesScore = findViewById(R.id.best_practices_score);
        suggestionsText = findViewById(R.id.suggestions_text);
        errorsText = findViewById(R.id.errors_text);
        loadingProgress = findViewById(R.id.loading_progress);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("AI Evaluation Results");
        }
    }
    
    private void processEvaluationData() {
        Intent intent = getIntent();
        String evaluationResult = intent.getStringExtra("evaluation_result");
        String problemTitle = intent.getStringExtra("problem_title");
        
        if (problemTitle != null) {
            titleText.setText("Results for: " + problemTitle);
        }
        
        if (evaluationResult != null) {
            parseAndDisplayEvaluation(evaluationResult);
        } else {
            showError("No evaluation data received");
        }
    }
    
    private void parseAndDisplayEvaluation(String evaluationJson) {
        try {
            loadingProgress.setVisibility(View.GONE);
            
            JsonObject evaluation;
            try {
                evaluation = JsonParser.parseString(evaluationJson).getAsJsonObject();
            } catch (Exception e) {
                // Try to extract JSON from response
                String jsonPart = extractJsonFromResponse(evaluationJson);
                evaluation = JsonParser.parseString(jsonPart).getAsJsonObject();
            }
            
            // Display score and grade
            if (evaluation.has("score")) {
                int score = evaluation.get("score").getAsInt();
                scoreText.setText(score + "%");
                scoreText.setTextColor(getScoreColor(score));
            }
            
            if (evaluation.has("grade")) {
                String grade = evaluation.get("grade").getAsString();
                gradeText.setText("Grade: " + grade);
                gradeText.setTextColor(getGradeColor(grade));
            }
            
            // Display feedback
            if (evaluation.has("feedback")) {
                overallFeedbackText.setText(evaluation.get("feedback").getAsString());
            }
            
            // Display code quality metrics
            if (evaluation.has("codeQuality")) {
                JsonObject codeQuality = evaluation.getAsJsonObject("codeQuality");
                codeQualityCard.setVisibility(View.VISIBLE);
                
                if (codeQuality.has("readability")) {
                    int readability = codeQuality.get("readability").getAsInt();
                    readabilityScore.setText(readability + "/10");
                }
                
                if (codeQuality.has("efficiency")) {
                    int efficiency = codeQuality.get("efficiency").getAsInt();
                    efficiencyScore.setText(efficiency + "/10");
                }
                
                if (codeQuality.has("bestPractices")) {
                    int bestPractices = codeQuality.get("bestPractices").getAsInt();
                    bestPracticesScore.setText(bestPractices + "/10");
                }
            }
            
            // Display suggestions
            if (evaluation.has("suggestions")) {
                JsonArray suggestions = evaluation.getAsJsonArray("suggestions");
                StringBuilder suggestionsBuilder = new StringBuilder("💡 Suggestions for Improvement:\n\n");
                
                for (int i = 0; i < suggestions.size(); i++) {
                    suggestionsBuilder.append("• ")
                        .append(suggestions.get(i).getAsString())
                        .append("\n\n");
                }
                
                suggestionsText.setText(suggestionsBuilder.toString());
                suggestionsText.setVisibility(View.VISIBLE);
            }
            
            // Display errors if any
            if (evaluation.has("errors") && evaluation.getAsJsonArray("errors").size() > 0) {
                JsonArray errors = evaluation.getAsJsonArray("errors");
                StringBuilder errorsBuilder = new StringBuilder("⚠️ Issues Found:\n\n");
                
                for (int i = 0; i < errors.size(); i++) {
                    errorsBuilder.append("• ")
                        .append(errors.get(i).getAsString())
                        .append("\n\n");
                }
                
                errorsText.setText(errorsBuilder.toString());
                errorsText.setVisibility(View.VISIBLE);
            }
            
            // Handle quiz evaluation results
            if (evaluation.has("detailedResults")) {
                displayQuizResults(evaluation);
            }
            
        } catch (Exception e) {
            showError("Error parsing evaluation: " + e.getMessage());
            displayRawResponse(evaluationJson);
        }
    }
    
    private void displayQuizResults(JsonObject evaluation) {
        // For quiz evaluations, show detailed question-by-question results
        if (evaluation.has("totalQuestions") && evaluation.has("correctAnswers")) {
            int total = evaluation.get("totalQuestions").getAsInt();
            int correct = evaluation.get("correctAnswers").getAsInt();
            
            scoreText.setText(correct + "/" + total);
            
            if (evaluation.has("score")) {
                int percentage = evaluation.get("score").getAsInt();
                gradeText.setText(percentage + "%");
            }
        }
        
        if (evaluation.has("detailedResults")) {
            JsonArray results = evaluation.getAsJsonArray("detailedResults");
            StringBuilder detailsBuilder = new StringBuilder("📊 Detailed Results:\n\n");
            
            for (int i = 0; i < results.size(); i++) {
                JsonObject result = results.get(i).getAsJsonObject();
                int questionNum = result.get("questionNumber").getAsInt();
                String userAnswer = result.get("userAnswer").getAsString();
                String correctAnswer = result.get("correctAnswer").getAsString();
                boolean isCorrect = result.get("isCorrect").getAsBoolean();
                String explanation = result.get("explanation").getAsString();
                
                detailsBuilder.append("Question ").append(questionNum).append(": ")
                    .append(isCorrect ? "✅ Correct" : "❌ Incorrect")
                    .append("\nYour answer: ").append(userAnswer)
                    .append("\nCorrect answer: ").append(correctAnswer)
                    .append("\nExplanation: ").append(explanation)
                    .append("\n\n");
            }
            
            overallFeedbackText.setText(detailsBuilder.toString());
        }
    }
    
    private String extractJsonFromResponse(String response) {
        int startIndex = response.indexOf('{');
        int endIndex = response.lastIndexOf('}');
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex + 1);
        }
        throw new RuntimeException("No valid JSON found in response");
    }
    
    private void displayRawResponse(String response) {
        overallFeedbackText.setText("Raw AI Response:\n\n" + response);
    }
    
    private void showError(String message) {
        loadingProgress.setVisibility(View.GONE);
        overallFeedbackText.setText("Error: " + message);
        overallFeedbackText.setVisibility(View.VISIBLE);
    }
    
    private int getScoreColor(int score) {
        if (score >= 90) return getResources().getColor(R.color.success_color);
        else if (score >= 70) return getResources().getColor(R.color.warning_color);
        else return getResources().getColor(R.color.error_color);
    }
    
    private int getGradeColor(String grade) {
        switch (grade.toUpperCase()) {
            case "A":
            case "A+":
                return getResources().getColor(R.color.success_color);
            case "B":
            case "B+":
            case "B-":
                return getResources().getColor(R.color.primary_color);
            case "C":
            case "C+":
            case "C-":
                return getResources().getColor(R.color.warning_color);
            default:
                return getResources().getColor(R.color.error_color);
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}