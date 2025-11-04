package com.example.javabuddy.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.javabuddy.R;
import com.example.javabuddy.ai.GroqApiService;
import com.example.javabuddy.ui.AnimationPalette;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AIQuizGeneratorActivity extends AppCompatActivity {
    
    private EditText topicInput;
    private Spinner difficultySpinner;
    private SeekBar questionCountSeekBar;
    private TextView questionCountLabel;
    private Button generateButton;
    private Button takeQuizButton;
    private ProgressBar loadingProgress;
    private RecyclerView quizRecyclerView;
    private TextView statusText;
    private TextView quizHeader;
    private NestedScrollView mainScrollView;
    private Uri lastSavedQuizUri;
    private String lastSavedFileName;
    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        boolean hasQuestions = generatedQuestions != null && !generatedQuestions.isEmpty();
        android.view.MenuItem saveItem = menu.findItem(R.id.action_save_quiz);
        android.view.MenuItem shareItem = menu.findItem(R.id.action_share_quiz);
        android.view.MenuItem takeItem = menu.findItem(R.id.action_take_quiz);
        if (saveItem != null) saveItem.setEnabled(hasQuestions);
        if (shareItem != null) shareItem.setEnabled(hasQuestions);
        if (takeItem != null) takeItem.setEnabled(hasQuestions);
        return super.onPrepareOptionsMenu(menu);
    }

    
    private GroqApiService groqService;
    private List<QuizQuestion> generatedQuestions;
    private QuizAdapter quizAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_quiz_generator);
        
    initViews();
    setupToolbar();
    setupSpinner();
    setupSeekBar();

    generatedQuestions = new ArrayList<>();
    groqService = new GroqApiService();

    setupRecyclerView();
    setupClickListeners();
    }
    
    private void initViews() {
        mainScrollView = findViewById(R.id.quiz_scroll_container);
        topicInput = findViewById(R.id.topic_input);
        difficultySpinner = findViewById(R.id.difficulty_spinner);
        questionCountSeekBar = findViewById(R.id.question_count_seekbar);
        questionCountLabel = findViewById(R.id.question_count_label);
    generateButton = findViewById(R.id.generate_button);
    takeQuizButton = findViewById(R.id.take_quiz_button);
        loadingProgress = findViewById(R.id.loading_progress);
        quizRecyclerView = findViewById(R.id.quiz_recycler_view);
        statusText = findViewById(R.id.status_text);
        quizHeader = findViewById(R.id.quiz_header);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("AI Quiz Generator");
        }
    }
    
    private void setupSpinner() {
        String[] difficulties = {"Beginner", "Intermediate", "Advanced"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, difficulties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);
    }
    
    private void setupSeekBar() {
        questionCountSeekBar.setMax(20);
        questionCountSeekBar.setProgress(5);
        updateQuestionCountLabel(5);
        
        questionCountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int count = Math.max(1, progress); // Minimum 1 question
                updateQuestionCountLabel(count);
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    
    private void updateQuestionCountLabel(int count) {
        questionCountLabel.setText(count + " questions");
    }
    
    private void setupRecyclerView() {
        quizAdapter = new QuizAdapter(generatedQuestions);
        quizRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizRecyclerView.setAdapter(quizAdapter);
        quizRecyclerView.setNestedScrollingEnabled(false);
    }
    
    private void setupClickListeners() {
        generateButton.setOnClickListener(v -> generateQuiz());
        if (takeQuizButton != null) {
            takeQuizButton.setOnClickListener(v -> startPracticeQuiz());
            takeQuizButton.setVisibility(View.GONE);
        }
        
        // Quick topic buttons
        findViewById(R.id.btn_variables).setOnClickListener(v -> 
            topicInput.setText("Java Variables and Data Types"));
        findViewById(R.id.btn_loops).setOnClickListener(v -> 
            topicInput.setText("Java Loops and Control Structures"));
        findViewById(R.id.btn_oop).setOnClickListener(v -> 
            topicInput.setText("Object-Oriented Programming in Java"));
        findViewById(R.id.btn_collections).setOnClickListener(v -> 
            topicInput.setText("Java Collections Framework"));
    }
    
    private void generateQuiz() {
        String topic = topicInput.getText().toString().trim();
        if (topic.isEmpty()) {
            Toast.makeText(this, "Please enter a topic", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Check rate limit status before making request
        if (!groqService.canMakeRequestNow()) {
            long waitTime = groqService.getEstimatedWaitTimeMs();
            if (waitTime > 0) {
                String waitTimeText = waitTime < 60000 ? 
                    (waitTime / 1000) + " seconds" : 
                    (waitTime / 60000) + " minutes";
                Toast.makeText(this, 
                    "⏳ AI rate limit reached. Please wait " + waitTimeText + " before generating another quiz.", 
                    Toast.LENGTH_LONG).show();
                return;
            }
        }
        
        String difficulty = difficultySpinner.getSelectedItem().toString();
        int questionCount = Math.max(1, questionCountSeekBar.getProgress());
        
        showLoading(true);
        statusText.setText("Generating " + questionCount + " questions about " + topic + "...");
        
        // Show rate limit warning if approaching limits
        GroqApiService.RateLimitStatus rateLimitStatus = groqService.getRateLimitStatus();
        if (rateLimitStatus.isNearRequestLimit() || rateLimitStatus.isNearTokenLimit()) {
            Toast.makeText(this, 
                "⚠️ Approaching AI usage limits. Consider reducing request frequency.", 
                Toast.LENGTH_SHORT).show();
        }
        
        groqService.generateQuizQuestions(topic, questionCount, difficulty)
            .thenAccept(response -> runOnUiThread(() -> {
                showLoading(false);
                parseAndDisplayQuestions(response);
                
                // Update status with current usage
                GroqApiService.RateLimitStatus newStatus = groqService.getRateLimitStatus();
                statusText.setText("Generated " + generatedQuestions.size() + " questions successfully! " +
                    String.format("(AI Usage: %d/%d requests)", 
                        newStatus.currentRequests, newStatus.maxRequests));
            }))
            .exceptionally(throwable -> {
                runOnUiThread(() -> {
                    showLoading(false);
                    String errorMessage = throwable.getMessage();
                    
                    // Handle specific errors
                    if (errorMessage.contains("Please set your actual Groq API key")) {
                        statusText.setText("❌ API Key Required: Please configure your Groq API key in the app settings.");
                        Toast.makeText(this, "Please set up your Groq API key to use AI features.\nGet your free key from console.groq.com", Toast.LENGTH_LONG).show();
                    } else if (errorMessage.contains("rate limit") || errorMessage.contains("429")) {
                        statusText.setText("⏳ AI rate limit exceeded. Please wait a minute before trying again.");
                        Toast.makeText(this, "Rate limit exceeded. Try again in a minute.", Toast.LENGTH_LONG).show();
                    } else if (errorMessage.contains("401") || errorMessage.contains("Unauthorized")) {
                        statusText.setText("❌ Invalid API Key: Please check your Groq API key configuration.");
                        Toast.makeText(this, "Invalid API key. Please verify your Groq API key.", Toast.LENGTH_LONG).show();
                    } else {
                        statusText.setText("Error generating quiz: " + errorMessage);
                        Toast.makeText(this, "Failed to generate quiz. Please check your internet connection and try again.", Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            });
    }

    private void parseAndDisplayQuestions(String aiResponse) {
        try {
            android.util.Log.d("AIQuizGenerator", "AI Response received: " + aiResponse);
            generatedQuestions.clear();
            
            // Try to parse as JSON array
            JsonArray questionsArray;
            try {
                questionsArray = JsonParser.parseString(aiResponse).getAsJsonArray();
                android.util.Log.d("AIQuizGenerator", "Direct JSON parsing successful");
            } catch (Exception e) {
                android.util.Log.d("AIQuizGenerator", "Direct parsing failed, trying extraction: " + e.getMessage());
                // If direct parsing fails, try to extract JSON from the response
                String jsonPart = extractJsonFromResponse(aiResponse);
                android.util.Log.d("AIQuizGenerator", "Extracted JSON: " + jsonPart);
                questionsArray = JsonParser.parseString(jsonPart).getAsJsonArray();
            }
            
            android.util.Log.d("AIQuizGenerator", "Questions array size: " + questionsArray.size());
            
            for (int i = 0; i < questionsArray.size(); i++) {
                JsonObject questionObj = questionsArray.get(i).getAsJsonObject();
                
                QuizQuestion question = new QuizQuestion();
                question.question = questionObj.get("question").getAsString();
                question.correctAnswer = questionObj.get("correctAnswer").getAsString();
                question.explanation = questionObj.get("explanation").getAsString();
                question.animationFile = AnimationPalette.randomQuizAnimation();
                
                JsonArray optionsArray = questionObj.getAsJsonArray("options");
                question.options = new ArrayList<>();
                for (int j = 0; j < optionsArray.size(); j++) {
                    question.options.add(optionsArray.get(j).getAsString());
                }
                
                generatedQuestions.add(question);
                android.util.Log.d("AIQuizGenerator", "Added question " + (i+1) + ": " + question.question);
            }
            
            android.util.Log.d("AIQuizGenerator", "Total questions generated: " + generatedQuestions.size());
            
            // Show the quiz section
            quizHeader.setVisibility(View.VISIBLE);
            quizRecyclerView.setVisibility(View.VISIBLE);
            quizAdapter.notifyDataSetChanged();
            android.util.Log.d("AIQuizGenerator", "RecyclerView visibility: " + quizRecyclerView.getVisibility());
            android.util.Log.d("AIQuizGenerator", "RecyclerView adapter item count: " + quizAdapter.getItemCount());
            statusText.setText("✅ Generated " + generatedQuestions.size() + " questions successfully!");
            
            if (takeQuizButton != null) {
                takeQuizButton.setVisibility(generatedQuestions.isEmpty() ? View.GONE : View.VISIBLE);
            }
            
            // Scroll to show the questions
            if (mainScrollView != null) {
                mainScrollView.post(() -> {
                    mainScrollView.smoothScrollTo(0, quizHeader.getTop());
                    android.util.Log.d("AIQuizGenerator", "ScrollView scrolled to quiz header");
                });
            }
            invalidateOptionsMenu();
            
        } catch (Exception e) {
            android.util.Log.e("AIQuizGenerator", "Error parsing quiz questions", e);
            statusText.setText("❌ Error parsing quiz questions: " + e.getMessage());
            // Fallback: display raw response for debugging
            displayRawResponse(aiResponse);
        }
    }
    
    private String extractJsonFromResponse(String response) {
        // Try to find JSON array in the response
        int startIndex = response.indexOf('[');
        int endIndex = response.lastIndexOf(']');
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex + 1);
        }
        throw new RuntimeException("No valid JSON found in response");
    }
    
    private void displayRawResponse(String response) {
        android.util.Log.d("AIQuizGenerator", "Displaying raw response as fallback");
        // Create a simple text display for raw response
        generatedQuestions.clear();
        QuizQuestion errorQuestion = new QuizQuestion();
        errorQuestion.question = "⚠️ Raw AI Response (for debugging):";
        errorQuestion.options = new ArrayList<>();
        errorQuestion.options.add(response.length() > 500 ? response.substring(0, 500) + "..." : response);
        errorQuestion.correctAnswer = "N/A";
        errorQuestion.explanation = "Could not parse as structured quiz questions. Check the AI response format.";
        errorQuestion.animationFile = AnimationPalette.randomQuizAnimation();
        generatedQuestions.add(errorQuestion);
        quizHeader.setVisibility(View.VISIBLE);
        quizRecyclerView.setVisibility(View.VISIBLE);
        quizAdapter.notifyDataSetChanged();
        if (mainScrollView != null) {
            mainScrollView.post(() -> mainScrollView.smoothScrollTo(0, quizHeader.getTop()));
        }
        invalidateOptionsMenu();
        android.util.Log.d("AIQuizGenerator", "Raw response displayed in RecyclerView");
    }
    
    private void showLoading(boolean show) {
        loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        generateButton.setEnabled(!show);
        generateButton.setText(show ? "Generating..." : "Generate Quiz");
    }
    
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.ai_quiz_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_save_quiz) {
            saveQuizToFile();
            return true;
        } else if (itemId == R.id.action_share_quiz) {
            shareQuiz();
            return true;
        } else if (itemId == R.id.action_take_quiz) {
            startPracticeQuiz();
            return true;
        } else if (itemId == R.id.action_clear_all) {
            clearAllQuestions();
            return true;
        } else if (itemId == R.id.action_rate_limit_status) {
            showRateLimitStatus();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void showRateLimitStatus() {
        GroqApiService.RateLimitStatus status = groqService.getRateLimitStatus();
        String message = String.format(
            "🤖 AI Usage Status\n\n" +
            "Requests this minute: %d/%d (%.1f%%)\n" +
            "Tokens this minute: %d/%d (%.1f%%)\n" +
            "Total tokens used: %d\n\n" +
            "%s",
            status.currentRequests, status.maxRequests, 
            (status.currentRequests * 100.0 / status.maxRequests),
            status.currentTokens, status.maxTokens,
            (status.currentTokens * 100.0 / status.maxTokens),
            status.totalTokensUsed,
            getRateLimitAdvice(status)
        );
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("AI Usage Status")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }
    
    private String getRateLimitAdvice(GroqApiService.RateLimitStatus status) {
        if (status.isNearRequestLimit() && status.isNearTokenLimit()) {
            return "💡 Tip: Consider reducing request frequency and asking shorter questions.";
        } else if (status.isNearRequestLimit()) {
            return "💡 Tip: Try waiting a bit between AI requests.";
        } else if (status.isNearTokenLimit()) {
            return "💡 Tip: Try asking shorter questions or reducing quiz sizes.";
        } else {
            return "✅ You're well within the usage limits!";
        }
    }
    
    private void clearAllQuestions() {
        generatedQuestions.clear();
        quizAdapter.notifyDataSetChanged();
        statusText.setText("Questions cleared. Generate new AI-powered quiz questions!");
        quizHeader.setVisibility(View.GONE);
        quizRecyclerView.setVisibility(View.GONE);
        if (takeQuizButton != null) {
            takeQuizButton.setVisibility(View.GONE);
        }
        lastSavedQuizUri = null;
        lastSavedFileName = null;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    // Inner classes for quiz data
    public static class QuizQuestion implements Parcelable {
        public String question;
        public List<String> options;
        public String correctAnswer;
        public String explanation;
        public String animationFile;

        public QuizQuestion() {}

        protected QuizQuestion(Parcel in) {
            question = in.readString();
            options = new ArrayList<>();
            in.readStringList(options);
            correctAnswer = in.readString();
            explanation = in.readString();
            animationFile = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(question);
            dest.writeStringList(options);
            dest.writeString(correctAnswer);
            dest.writeString(explanation);
            dest.writeString(animationFile);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<QuizQuestion> CREATOR = new Creator<QuizQuestion>() {
            @Override
            public QuizQuestion createFromParcel(Parcel in) {
                return new QuizQuestion(in);
            }

            @Override
            public QuizQuestion[] newArray(int size) {
                return new QuizQuestion[size];
            }
        };
    }
    
    // Adapter for displaying quiz questions
    private static class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
        private List<QuizQuestion> questions;
        
        public QuizAdapter(List<QuizQuestion> questions) {
            this.questions = questions;
        }
        
        @Override
        public QuizViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_question, parent, false);
            return new QuizViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(QuizViewHolder holder, int position) {
            if (questions != null && position < questions.size()) {
                QuizQuestion question = questions.get(position);
                holder.bind(question, position + 1);
            }
        }
        
        @Override
        public int getItemCount() {
            return questions != null ? questions.size() : 0;
        }
        
        static class QuizViewHolder extends RecyclerView.ViewHolder {
            TextView questionText;
            TextView optionsText;
            TextView answerText;
            TextView explanationText;
            LottieAnimationView questionAnimation;
            Button revealButton;
            
            public QuizViewHolder(View itemView) {
                super(itemView);
                questionText = itemView.findViewById(R.id.question_text);
                optionsText = itemView.findViewById(R.id.options_text);
                answerText = itemView.findViewById(R.id.answer_text);
                explanationText = itemView.findViewById(R.id.explanation_text);
                questionAnimation = itemView.findViewById(R.id.question_animation);
                revealButton = itemView.findViewById(R.id.reveal_button);
            }
            
            public void bind(QuizQuestion question, int questionNumber) {
                android.util.Log.d("AIQuizGenerator", "Binding question " + questionNumber);
                questionText.setText("Q" + questionNumber + ": " + question.question);

                if (questionAnimation != null) {
                    if (question.animationFile != null) {
                        questionAnimation.setVisibility(View.VISIBLE);
                        Object existingTag = questionAnimation.getTag();
                        if (!(existingTag instanceof String) || !question.animationFile.equals(existingTag)) {
                            questionAnimation.setAnimation(question.animationFile);
                            questionAnimation.setTag(question.animationFile);
                        }
                        questionAnimation.setRepeatCount(LottieDrawable.INFINITE);
                        if (!questionAnimation.isAnimating()) {
                            questionAnimation.playAnimation();
                        }
                    } else {
                        questionAnimation.cancelAnimation();
                        questionAnimation.setVisibility(View.GONE);
                        questionAnimation.setTag(null);
                    }
                }
                
                if (question.options != null && !question.options.isEmpty()) {
                    StringBuilder optionsBuilder = new StringBuilder();
                    for (String option : question.options) {
                        optionsBuilder.append(option).append("\n");
                    }
                    optionsText.setText(optionsBuilder.toString());
                    optionsText.setVisibility(View.VISIBLE);
                } else {
                    optionsText.setVisibility(View.GONE);
                }
                
                answerText.setText("Correct Answer: " + question.correctAnswer);
                explanationText.setText("Explanation: " + question.explanation);

                if (revealButton != null) {
                    answerText.setVisibility(View.GONE);
                    explanationText.setVisibility(View.GONE);
                    revealButton.setVisibility(View.VISIBLE);
                    revealButton.setOnClickListener(v -> {
                        boolean showing = answerText.getVisibility() == View.VISIBLE;
                        answerText.setVisibility(showing ? View.GONE : View.VISIBLE);
                        explanationText.setVisibility(showing ? View.GONE : View.VISIBLE);
                        revealButton.setText(showing ? "Reveal Answer" : "Hide Answer");
                    });
                }
            }
        }
    }

    private void startPracticeQuiz() {
        if (!ensureQuestionsAvailable()) {
            return;
        }

        Intent intent = new Intent(this, AIQuizPlayerActivity.class);
        intent.putParcelableArrayListExtra(AIQuizPlayerActivity.EXTRA_QUESTIONS, new ArrayList<>(generatedQuestions));
        startActivity(intent);
    }

    private boolean ensureQuestionsAvailable() {
        if (generatedQuestions == null || generatedQuestions.isEmpty()) {
            Toast.makeText(this, R.string.ai_quiz_generate_first, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveQuizToFile() {
        if (!ensureQuestionsAvailable()) {
            return;
        }

        String quizText = buildQuizText();
        String filename = String.format(Locale.US, "quiz_%tY%<tm%<td_%<tH%<tM.txt", System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, filename);
            values.put(MediaStore.Downloads.MIME_TYPE, "text/plain");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/JavaBuddy");

            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                Toast.makeText(this, "Unable to save quiz", Toast.LENGTH_SHORT).show();
                return;
            }

            try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                if (outputStream == null) {
                    throw new IOException("OutputStream is null");
                }
                outputStream.write(quizText.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                lastSavedQuizUri = uri;
                lastSavedFileName = filename;
                Toast.makeText(this, getString(R.string.ai_quiz_saved_downloads, filename), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                resolver.delete(uri, null, null);
                android.util.Log.e("AIQuizGenerator", "Failed to save quiz", e);
                Toast.makeText(this, "Failed to save quiz", Toast.LENGTH_SHORT).show();
            }
        } else {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File javaBuddyDir = new File(downloadsDir, "JavaBuddy");
            if (!javaBuddyDir.exists() && !javaBuddyDir.mkdirs()) {
                Toast.makeText(this, "Unable to access storage", Toast.LENGTH_SHORT).show();
                return;
            }

            File output = new File(javaBuddyDir, filename);
            try (OutputStream outputStream = new java.io.FileOutputStream(output)) {
                outputStream.write(quizText.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                lastSavedQuizUri = Uri.fromFile(output);
                lastSavedFileName = filename;
                Toast.makeText(this, getString(R.string.ai_quiz_saved_downloads, filename), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                android.util.Log.e("AIQuizGenerator", "Failed to save quiz", e);
                Toast.makeText(this, "Failed to save quiz", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareQuiz() {
        if (!ensureQuestionsAvailable()) {
            return;
        }

        String quizText = buildQuizText();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.ai_quiz_share_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, quizText);
        startActivity(Intent.createChooser(shareIntent, "Share AI Quiz"));
    }

    private String buildQuizText() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < generatedQuestions.size(); i++) {
            QuizQuestion question = generatedQuestions.get(i);
            builder.append(String.format(Locale.US, "Q%d: %s\n", i + 1, question.question));
            if (question.options != null) {
                for (String option : question.options) {
                    builder.append("   ").append(option).append('\n');
                }
            }
            builder.append("Answer: ").append(question.correctAnswer).append('\n');
            builder.append("Explanation: ").append(question.explanation).append("\n\n");
        }
        return builder.toString();
    }
}