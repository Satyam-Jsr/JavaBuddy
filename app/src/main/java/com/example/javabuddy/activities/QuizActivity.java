package com.example.javabuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.javabuddy.R;
import com.example.javabuddy.database.JavaBuddyDatabase;
import com.example.javabuddy.database.entities.Quiz;
import com.example.javabuddy.database.entities.UserProgress;
import com.example.javabuddy.ui.AnimationPalette;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizActivity extends AppCompatActivity {

    private TextView questionText, questionCounter, scoreText, answerFeedbackText, timerText;
    private LinearLayout optionsContainer;
    private Button submitButton, nextButton;
    private ProgressBar progressBar;
    private CardView questionCard, feedbackCard;
    private LottieAnimationView questionAnimation;

    private JavaBuddyDatabase database;
    private ExecutorService executor;
    private List<Quiz> quizList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int selectedOption = -1;
    private int lessonId;
    private boolean timedMode = false;
    private int timedQuestionCount = 0;
    private int timeLimitSeconds = 0;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get lesson ID from intent, default to 1 if not provided
        lessonId = getIntent().getIntExtra("lesson_id", 1);

        initializeViews();
        setupToolbar();
        loadQuizData();
    }

    private void initializeViews() {
        questionText = findViewById(R.id.question_text);
        questionCounter = findViewById(R.id.question_counter);
        scoreText = findViewById(R.id.score_text);
        timerText = findViewById(R.id.timer_text);
        answerFeedbackText = findViewById(R.id.answer_feedback_text);
        optionsContainer = findViewById(R.id.options_container);
        submitButton = findViewById(R.id.submit_button);
        nextButton = findViewById(R.id.next_button);
        progressBar = findViewById(R.id.progress_bar);
        questionCard = findViewById(R.id.question_card);
        feedbackCard = findViewById(R.id.feedback_card);
        questionAnimation = findViewById(R.id.question_animation);

        database = JavaBuddyDatabase.getDatabase(this);
        executor = Executors.newSingleThreadExecutor();
        quizList = new ArrayList<>();

        submitButton.setOnClickListener(v -> submitAnswer());
        nextButton.setOnClickListener(v -> nextQuestion());

        timedMode = getIntent().getBooleanExtra(TimedTestActivity.EXTRA_TIMED_MODE, false);
        if (timedMode) {
            timedQuestionCount = getIntent().getIntExtra(TimedTestActivity.EXTRA_TIMED_QUESTION_COUNT, 10);
            timeLimitSeconds = getIntent().getIntExtra(TimedTestActivity.EXTRA_TIME_LIMIT_SECONDS, 120);
            if (timerText != null) {
                timerText.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            
            // Get lesson title from intent
            String lessonTitle = getIntent().getStringExtra("lesson_title");
            if (lessonTitle != null) {
                getSupportActionBar().setTitle("Quiz: " + lessonTitle);
            } else {
                getSupportActionBar().setTitle("Quiz - Lesson " + lessonId);
            }
        }
    }

    private void loadQuizData() {
        executor.execute(() -> {
            List<Quiz> quizzes;
            if (timedMode) {
                int desiredCount = Math.max(5, timedQuestionCount);
                quizzes = database.quizDao().getRandomQuizzes(desiredCount);
            } else {
                quizzes = database.quizDao().getQuizzesByLessonId(lessonId);
            }
            int totalQuizzesInDB = database.quizDao().getAllQuizzes().size();
            
            runOnUiThread(() -> {
                if (quizzes.isEmpty()) {
                    String message = timedMode
                        ? "Not enough questions available for a timed test."
                        : "No quiz available for lesson " + lessonId +
                            " (Total quizzes in DB: " + totalQuizzesInDB + ")";
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                
                quizList.clear();
                if (timedMode && timedQuestionCount > 0 && quizzes.size() > timedQuestionCount) {
                    quizList.addAll(quizzes.subList(0, timedQuestionCount));
                } else {
                    quizList.addAll(quizzes);
                }
                displayQuestion();
            });
        });
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= quizList.size()) {
            showResults();
            return;
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        
        // Hide previous answer feedback
        if (answerFeedbackText != null) {
            answerFeedbackText.setVisibility(TextView.GONE);
        }
        if (feedbackCard != null) {
            feedbackCard.setVisibility(CardView.GONE);
        }
        
        Quiz currentQuiz = quizList.get(currentQuestionIndex);        // Update UI
        questionText.setText(currentQuiz.getQuestion());
        questionCounter.setText(String.format("Question %d of %d", 
            currentQuestionIndex + 1, quizList.size()));
        scoreText.setText(String.format("Score: %d/%d", score, quizList.size()));

        playQuestionAnimation();
        
        // Update progress bar
        int progress = (int) ((float) currentQuestionIndex / quizList.size() * 100);
        progressBar.setProgress(progress);

        if (timedMode) {
            startTimer();
        } else if (timerText != null) {
            timerText.setVisibility(View.GONE);
        }

        // Clear previous options
        optionsContainer.removeAllViews();
        selectedOption = -1;

        // Create options based on question type
        if ("true_false".equals(currentQuiz.getQuestionType())) {
            createTrueFalseOptions();
        } else {
            createMultipleChoiceOptions(currentQuiz);
        }

        // Reset buttons
        submitButton.setEnabled(false);
        nextButton.setVisibility(Button.GONE);
    }

    private void playQuestionAnimation() {
        if (questionAnimation == null) {
            return;
        }
        if (AnimationPalette.allAnimations().length == 0) {
            questionAnimation.cancelAnimation();
            questionAnimation.setVisibility(View.GONE);
            return;
        }

        String animationFile = AnimationPalette.randomQuizAnimation();
        Object tag = questionAnimation.getTag();
        if (!(tag instanceof String) || !animationFile.equals(tag)) {
            questionAnimation.setAnimation(animationFile);
            questionAnimation.setTag(animationFile);
        }
    questionAnimation.setVisibility(View.VISIBLE);
        questionAnimation.setRepeatCount(LottieDrawable.INFINITE);
        questionAnimation.playAnimation();
    }

    private void createMultipleChoiceOptions(Quiz quiz) {
        String[] options = {quiz.getOption1(), quiz.getOption2(), quiz.getOption3(), quiz.getOption4()};
        
        for (int i = 0; i < options.length; i++) {
            if (options[i] != null && !options[i].trim().isEmpty()) {
                Button optionButton = createOptionButton(options[i], i + 1);
                optionsContainer.addView(optionButton);
            }
        }
    }

    private void createTrueFalseOptions() {
        Button trueButton = createOptionButton("True", 1);
        Button falseButton = createOptionButton("False", 2);
        
        optionsContainer.addView(trueButton);
        optionsContainer.addView(falseButton);
    }

    private Button createOptionButton(String text, int optionNumber) {
        Button button = new Button(this);
        button.setText(text);
        button.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        button.setBackgroundResource(R.drawable.option_button_background);
        
        int margin = getResources().getDimensionPixelSize(R.dimen.option_button_margin);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
        params.setMargins(0, margin, 0, margin);
        
        button.setOnClickListener(v -> selectOption(optionNumber, button));
        
        return button;
    }

    private void selectOption(int optionNumber, Button selectedButton) {
        selectedOption = optionNumber;
        submitButton.setEnabled(true);

        // Reset all buttons
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            Button button = (Button) optionsContainer.getChildAt(i);
            button.setBackgroundResource(R.drawable.option_button_background);
        }

        // Highlight selected button
        selectedButton.setBackgroundResource(R.drawable.option_button_selected);
    }

    private void submitAnswer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        Quiz currentQuiz = quizList.get(currentQuestionIndex);
        boolean isCorrect = (selectedOption == currentQuiz.getCorrectAnswer());

        if (isCorrect) {
            score++;
        }

        // Show correct answer
        showCorrectAnswer(currentQuiz, isCorrect);
        
        submitButton.setVisibility(Button.GONE);
        nextButton.setVisibility(Button.VISIBLE);
    }

    private void showCorrectAnswer(Quiz quiz, boolean wasCorrect) {
        int correctOption = quiz.getCorrectAnswer();
        String[] options = {quiz.getOption1(), quiz.getOption2(), quiz.getOption3(), quiz.getOption4()};
        
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            Button button = (Button) optionsContainer.getChildAt(i);
            
            if (i + 1 == correctOption) {
                button.setBackgroundResource(R.drawable.option_button_correct);
            } else if (i + 1 == selectedOption && !wasCorrect) {
                button.setBackgroundResource(R.drawable.option_button_wrong);
            }
            
            button.setEnabled(false);
        }

        // Show detailed feedback instead of toast
        if (answerFeedbackText != null) {
            StringBuilder feedback = new StringBuilder();
            
            // Your answer
            if (selectedOption > 0 && selectedOption <= options.length) {
                feedback.append("Your Answer: ").append(options[selectedOption - 1]).append("\n");
            } else {
                feedback.append("Your Answer: (No answer selected)\n");
            }
            
            // Correct answer
            if (correctOption > 0 && correctOption <= options.length) {
                feedback.append("Correct Answer: ").append(options[correctOption - 1]).append("\n");
            }
            
            // Result
            feedback.append("Result: ").append(wasCorrect ? "✓ Correct!" : "✗ Incorrect").append("\n");
            
            // Explanation
            if (quiz.getExplanation() != null && !quiz.getExplanation().trim().isEmpty()) {
                feedback.append("\nExplanation: ").append(quiz.getExplanation());
            }
            
            answerFeedbackText.setText(feedback.toString());
            answerFeedbackText.setVisibility(TextView.VISIBLE);
            
            // Show the feedback card
            if (feedbackCard != null) {
                feedbackCard.setVisibility(CardView.VISIBLE);
            }
        }
    }

    private void nextQuestion() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        currentQuestionIndex++;
        displayQuestion();
        
        nextButton.setVisibility(Button.GONE);
        submitButton.setVisibility(Button.VISIBLE);
    }

    private void startTimer() {
        if (timerText == null) {
            return;
        }

        timerText.setVisibility(View.VISIBLE);
        timerText.setText(formatTime(timeLimitSeconds * 1000L));

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLimitSeconds * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(formatTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                timerText.setText("00:00");
                if (submitButton.getVisibility() == View.VISIBLE) {
                    submitAnswer();
                } else {
                    nextQuestion();
                }
            }
        };

        countDownTimer.start();
    }

    private String formatTime(long millis) {
        long totalSeconds = millis / 1000L;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void showResults() {
        // Update progress in database
        executor.execute(() -> {
            UserProgress progress = database.userProgressDao().getProgressByLessonId(lessonId);
            int finalScore = (int) ((float) score / quizList.size() * 100);
            
            if (progress != null) {
                progress.setScore(Math.max(progress.getScore(), finalScore));
                progress.setTimestamp(System.currentTimeMillis());
                progress.setAttempts(progress.getAttempts() + 1);
                database.userProgressDao().updateProgress(progress);
            } else {
                UserProgress newProgress = new UserProgress(lessonId, finalScore >= 70, 
                    finalScore, System.currentTimeMillis(), 0, 1);
                database.userProgressDao().insertProgress(newProgress);
            }
        });

        // Show results activity
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total_questions", quizList.size());
        intent.putExtra("lesson_id", lessonId);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        if (questionAnimation != null) {
            questionAnimation.cancelAnimation();
        }
        super.onDestroy();
    }
}