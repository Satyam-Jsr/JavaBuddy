package com.example.javabuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.javabuddy.MainActivity;
import com.example.javabuddy.R;

public class QuizResultActivity extends AppCompatActivity {

    private TextView scoreText, percentageText, messageText;
    private Button retakeButton, continueButton, homeButton;
    private LottieAnimationView resultAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        initializeViews();
        setupToolbar();
        displayResults();
        setupClickListeners();
    }

    private void initializeViews() {
        scoreText = findViewById(R.id.score_text);
        percentageText = findViewById(R.id.percentage_text);
        messageText = findViewById(R.id.message_text);
        retakeButton = findViewById(R.id.retake_button);
        continueButton = findViewById(R.id.continue_button);
        homeButton = findViewById(R.id.home_button);
        resultAnimation = findViewById(R.id.result_animation);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quiz Results");
        }
    }

    private void displayResults() {
        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("total_questions", 1);
        int percentage = (int) ((float) score / totalQuestions * 100);

        scoreText.setText(String.format("%d / %d", score, totalQuestions));
        percentageText.setText(String.format("%d%%", percentage));

        // Set message based on performance
        String message;
        if (resultAnimation != null) {
            if (percentage >= 85) {
                resultAnimation.setAnimation("Clap.lottie");
                resultAnimation.setRepeatCount(LottieDrawable.INFINITE);
                resultAnimation.playAnimation();
            } else {
                resultAnimation.setAnimation("home_logo.lottie");
                resultAnimation.setRepeatCount(LottieDrawable.INFINITE);
                resultAnimation.playAnimation();
            }
        }

        if (percentage >= 90) {
            message = "Excellent! You've mastered this topic! 🎉";
        } else if (percentage >= 80) {
            message = "Great job! You're doing well! 👍";
        } else if (percentage >= 70) {
            message = "Good work! Keep practicing! 💪";
        } else if (percentage >= 60) {
            message = "Not bad, but there's room for improvement. 📚";
        } else {
            message = "Keep studying and try again! You can do it! 🚀";
        }

        messageText.setText(message);
    }

    private void setupClickListeners() {
        int lessonId = getIntent().getIntExtra("lesson_id", 1);

        retakeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("lesson_id", lessonId);
            startActivity(intent);
            finish();
        });

        continueButton.setOnClickListener(v -> {
            // Go to next lesson or lessons list
            Intent intent = new Intent(this, LessonActivity.class);
            startActivity(intent);
            finish();
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}