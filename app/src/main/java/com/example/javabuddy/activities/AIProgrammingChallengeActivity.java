package com.example.javabuddy.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.javabuddy.R;
import com.example.javabuddy.ai.GroqApiService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.rosemoe.sora.widget.CodeEditor;

public class AIProgrammingChallengeActivity extends AppCompatActivity {
    
    private EditText topicInput;
    private Spinner difficultySpinner;
    private Button generateButton;
    private ProgressBar loadingProgress;
    private TextView challengeTitle;
    private TextView challengeDescription;
    private CodeEditor codeEditor;
    private TextView hintsText;
    private Button showSolutionButton;
    private TextView statusText;
    
    private GroqApiService groqService;
    private String currentChallenge;
    private String expectedSolution;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_programming_challenge);
        
        initViews();
        setupToolbar();
        setupSpinner();
        setupCodeEditor();
        setupClickListeners();
        
        groqService = new GroqApiService();
    }
    
    private void initViews() {
        topicInput = findViewById(R.id.topic_input);
        difficultySpinner = findViewById(R.id.difficulty_spinner);
        generateButton = findViewById(R.id.generate_button);
        loadingProgress = findViewById(R.id.loading_progress);
        challengeTitle = findViewById(R.id.challenge_title);
        challengeDescription = findViewById(R.id.challenge_description);
        codeEditor = findViewById(R.id.code_editor);
        hintsText = findViewById(R.id.hints_text);
        showSolutionButton = findViewById(R.id.show_solution_button);
        statusText = findViewById(R.id.status_text);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("AI Programming Challenges");
        }
    }
    
    private void setupSpinner() {
        String[] difficulties = {"Beginner", "Intermediate", "Advanced"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, difficulties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);
    }
    
    private void setupCodeEditor() {
        codeEditor.setTextSize(14);
        codeEditor.setText("// Your Java code here...\npublic class Solution {\n    \n}");
    }
    
    private void setupClickListeners() {
        generateButton.setOnClickListener(v -> generateChallenge());
        showSolutionButton.setOnClickListener(v -> showSolution());
        
        // Quick topic buttons
        findViewById(R.id.btn_arrays).setOnClickListener(v -> 
            topicInput.setText("Array manipulation and algorithms"));
        findViewById(R.id.btn_strings).setOnClickListener(v -> 
            topicInput.setText("String processing and manipulation"));
        findViewById(R.id.btn_algorithms).setOnClickListener(v -> 
            topicInput.setText("Basic algorithms and data structures"));
        findViewById(R.id.btn_math).setOnClickListener(v -> 
            topicInput.setText("Mathematical problems and calculations"));
    }
    
    private void generateChallenge() {
        String topic = topicInput.getText().toString().trim();
        if (topic.isEmpty()) {
            Toast.makeText(this, "Please enter a topic", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String difficulty = difficultySpinner.getSelectedItem().toString();
        
        showLoading(true);
        statusText.setText("Generating programming challenge about " + topic + "...");
        
        groqService.generateProgrammingChallenge(topic, difficulty, true)
            .thenAccept(response -> runOnUiThread(() -> {
                showLoading(false);
                parseAndDisplayChallenge(response);
            }))
            .exceptionally(throwable -> {
                runOnUiThread(() -> {
                    showLoading(false);
                    statusText.setText("Error generating challenge: " + throwable.getMessage());
                    Toast.makeText(this, "Failed to generate challenge", Toast.LENGTH_SHORT).show();
                });
                return null;
            });
    }
    
    private void parseAndDisplayChallenge(String aiResponse) {
        try {
            currentChallenge = aiResponse;
            
            // Try to parse as JSON
            JsonObject challengeObj;
            try {
                challengeObj = JsonParser.parseString(aiResponse).getAsJsonObject();
            } catch (Exception e) {
                // If direct parsing fails, try to extract JSON from the response
                String jsonPart = extractJsonFromResponse(aiResponse);
                challengeObj = JsonParser.parseString(jsonPart).getAsJsonObject();
            }
            
            String title = challengeObj.get("title").getAsString();
            String description = challengeObj.get("description").getAsString();
            String starterCode = challengeObj.get("starterCode").getAsString();
            expectedSolution = challengeObj.get("solution").getAsString();
            
            challengeTitle.setText(title);
            challengeDescription.setText(description);
            codeEditor.setText(starterCode);
            
            // Display hints if available
            if (challengeObj.has("hints")) {
                StringBuilder hintsBuilder = new StringBuilder("💡 Hints:\n");
                challengeObj.getAsJsonArray("hints").forEach(hint -> 
                    hintsBuilder.append("• ").append(hint.getAsString()).append("\n"));
                hintsText.setText(hintsBuilder.toString());
                hintsText.setVisibility(View.VISIBLE);
            } else {
                hintsText.setVisibility(View.GONE);
            }
            
            showSolutionButton.setEnabled(true);
            statusText.setText("Challenge generated successfully! Start coding!");
            
        } catch (Exception e) {
            statusText.setText("Error parsing challenge: " + e.getMessage());
            displayRawResponse(aiResponse);
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
        challengeTitle.setText("Generated Challenge (Raw Response)");
        challengeDescription.setText(response);
        codeEditor.setText("// Challenge could not be parsed properly\n// Check the description above");
    }
    
    private void showSolution() {
        if (expectedSolution != null && !expectedSolution.isEmpty()) {
            codeEditor.setText(expectedSolution);
            statusText.setText("Solution displayed. Study it to understand the approach!");
        } else {
            Toast.makeText(this, "Solution not available", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showLoading(boolean show) {
        loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        generateButton.setEnabled(!show);
        generateButton.setText(show ? "Generating..." : "Generate Challenge");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.programming_challenge_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_clear_code) {
            codeEditor.setText("// Your Java code here...\npublic class Solution {\n    \n}");
            return true;
        } else if (itemId == R.id.action_save_challenge) {
            saveChallenge();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void saveChallenge() {
        // TODO: Implement saving challenge to database
        Toast.makeText(this, "Save feature coming soon!", Toast.LENGTH_SHORT).show();
    }
}