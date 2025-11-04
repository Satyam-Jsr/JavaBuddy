package com.example.javabuddy.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.javabuddy.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Presents AI-generated quiz questions in a simple play-through experience.
 */
public class AIQuizPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_QUESTIONS = "com.example.javabuddy.activities.AIQuizPlayerActivity.EXTRA_QUESTIONS";

    private MaterialToolbar toolbar;
    private TextView questionCounter;
    private TextView questionText;
    private LinearLayout optionsContainer;
    private MaterialButton revealButton;
    private TextView answerText;
    private TextView explanationText;
    private MaterialButton nextButton;
    private LottieAnimationView questionAnimation;

    private ArrayList<AIQuizGeneratorActivity.QuizQuestion> questions;
    private int currentIndex;
    private int correctCount;
    private boolean questionResolved;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_quiz_player);

        ArrayList<AIQuizGeneratorActivity.QuizQuestion> extras =
            getIntent().getParcelableArrayListExtra(EXTRA_QUESTIONS);
        if (extras == null || extras.isEmpty()) {
            Toast.makeText(this, R.string.ai_quiz_unable_to_start, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        questions = extras;

        initViews();
        setupToolbar();
        setupListeners();
        showCurrentQuestion();
    }

    private void initViews() {
        toolbar = findViewById(R.id.quiz_toolbar);
        questionCounter = findViewById(R.id.question_counter);
        questionText = findViewById(R.id.question_text);
        optionsContainer = findViewById(R.id.options_container);
        revealButton = findViewById(R.id.reveal_button);
        answerText = findViewById(R.id.answer_text);
        explanationText = findViewById(R.id.explanation_text);
        nextButton = findViewById(R.id.next_button);
        questionAnimation = findViewById(R.id.question_animation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.ai_quiz_play_title);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        revealButton.setOnClickListener(v -> toggleSolutionVisibility());
        nextButton.setOnClickListener(v -> {
            if (!questionResolved) {
                Toast.makeText(this, R.string.ai_quiz_select_option_prompt, Toast.LENGTH_SHORT).show();
                return;
            }
            goToNextQuestion();
        });
    }

    private void showCurrentQuestion() {
        AIQuizGeneratorActivity.QuizQuestion question = questions.get(currentIndex);
        questionCounter.setText(getString(R.string.ai_quiz_question_counter, currentIndex + 1, questions.size()));
        questionText.setText(question.question);
        answerText.setText(getString(R.string.ai_quiz_correct_answer, question.correctAnswer));
        explanationText.setText(getString(R.string.ai_quiz_explanation, question.explanation));
        answerText.setVisibility(View.GONE);
        explanationText.setVisibility(View.GONE);
        revealButton.setText(R.string.ai_quiz_generator_reveal);
        questionResolved = false;
        nextButton.setEnabled(false);
        nextButton.setText(currentIndex == questions.size() - 1
            ? R.string.ai_quiz_finish
            : R.string.ai_quiz_next_question);

        bindAnimation(question);
        buildOptions(question);
    }

    private void bindAnimation(AIQuizGeneratorActivity.QuizQuestion question) {
        if (questionAnimation == null) {
            return;
        }
        if (question.animationFile != null && !question.animationFile.isEmpty()) {
            questionAnimation.setVisibility(View.VISIBLE);
            questionAnimation.setAnimation(question.animationFile);
            questionAnimation.playAnimation();
        } else {
            questionAnimation.cancelAnimation();
            questionAnimation.setVisibility(View.GONE);
        }
    }

    private void buildOptions(AIQuizGeneratorActivity.QuizQuestion question) {
        optionsContainer.removeAllViews();
        if (question.options == null || question.options.isEmpty()) {
            optionsContainer.setVisibility(View.GONE);
            revealButton.setVisibility(View.VISIBLE);
            return;
        }

        optionsContainer.setVisibility(View.VISIBLE);
        revealButton.setVisibility(View.VISIBLE);

        for (String option : question.options) {
            MaterialButton optionButton = new MaterialButton(this, null,
                com.google.android.material.R.attr.materialButtonOutlinedStyle);
            optionButton.setText(option);
            optionButton.setLayoutParams(buildOptionLayoutParams());
            optionButton.setAllCaps(false);
            optionButton.setOnClickListener(v -> handleOptionSelected(optionButton, option, question));
            optionsContainer.addView(optionButton);
        }
    }

    private LinearLayout.LayoutParams buildOptionLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = dpToPx(8);
        params.setMargins(0, margin, 0, margin);
        return params;
    }

    private void handleOptionSelected(MaterialButton selectedButton, String option,
                                      AIQuizGeneratorActivity.QuizQuestion question) {
        if (questionResolved) {
            return;
        }

        disableOptionButtons();
        boolean isCorrect = isAnswerCorrect(option, question.correctAnswer);
        styleOptionResult(selectedButton, isCorrect);

        if (isCorrect) {
            correctCount++;
            Toast.makeText(this, R.string.ai_quiz_answer_correct, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.ai_quiz_answer_incorrect, Toast.LENGTH_SHORT).show();
            highlightCorrectAnswer(question);
        }

        showSolution(true);
        questionResolved = true;
        nextButton.setEnabled(true);
    }

    private void disableOptionButtons() {
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            View child = optionsContainer.getChildAt(i);
            child.setEnabled(false);
        }
    }

    private void styleOptionResult(MaterialButton selectedButton, boolean isCorrect) {
        int colorRes = isCorrect ? R.color.success_color : R.color.error_color;
        int color = ContextCompat.getColor(this, colorRes);
        selectedButton.setBackgroundTintList(ColorStateList.valueOf(color));
        selectedButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
    }

    private void highlightCorrectAnswer(AIQuizGeneratorActivity.QuizQuestion question) {
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            View child = optionsContainer.getChildAt(i);
            if (child instanceof MaterialButton) {
                MaterialButton button = (MaterialButton) child;
                if (isAnswerCorrect(button.getText().toString(), question.correctAnswer)) {
                    styleOptionResult(button, true);
                    break;
                }
            }
        }
    }

    private boolean isAnswerCorrect(String option, String correctAnswer) {
        if (correctAnswer == null) {
            return false;
        }
        String normalizedOption = option.trim().toLowerCase(Locale.US);
        String normalizedAnswer = correctAnswer.trim().toLowerCase(Locale.US);
        if (normalizedOption.equals(normalizedAnswer)) {
            return true;
        }
        if (normalizedAnswer.length() == 1) {
            char answerChar = Character.toUpperCase(normalizedAnswer.charAt(0));
            if (!normalizedOption.isEmpty() && Character.toUpperCase(normalizedOption.charAt(0)) == answerChar) {
                return true;
            }
        }
        return normalizedOption.contains(normalizedAnswer);
    }

    private void toggleSolutionVisibility() {
        boolean showing = answerText.getVisibility() == View.VISIBLE;
        showSolution(!showing);
        if (!questionResolved) {
            questionResolved = true;
            nextButton.setEnabled(true);
        }
    }

    private void showSolution(boolean show) {
        answerText.setVisibility(show ? View.VISIBLE : View.GONE);
        explanationText.setVisibility(show ? View.VISIBLE : View.GONE);
        revealButton.setText(show ? R.string.ai_quiz_hide_answer : R.string.ai_quiz_generator_reveal);
    }

    private void goToNextQuestion() {
        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            showCurrentQuestion();
        } else {
            showSummaryDialog();
        }
    }

    private void showSummaryDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.ai_quiz_summary_title)
            .setMessage(getString(R.string.ai_quiz_summary_message, correctCount, questions.size()))
            .setCancelable(false)
            .setPositiveButton(R.string.ai_quiz_done, (dialog, which) -> finish())
            .setNegativeButton(R.string.ai_quiz_restart, (dialog, which) -> restartQuiz())
            .show();
    }

    private void restartQuiz() {
        currentIndex = 0;
        correctCount = 0;
        questionResolved = false;
        showCurrentQuestion();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onBackPressed() {
        if (questionAnimation != null) {
            questionAnimation.cancelAnimation();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (questionAnimation != null) {
            questionAnimation.cancelAnimation();
        }
        super.onDestroy();
    }
}
