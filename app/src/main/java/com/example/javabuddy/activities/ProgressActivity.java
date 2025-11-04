package com.example.javabuddy.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.javabuddy.R;
import com.example.javabuddy.database.JavaBuddyDatabase;
import com.example.javabuddy.database.entities.UserProgress;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgressActivity extends AppCompatActivity {

    private TextView completedLessonsText, averageScoreText, totalTimeText, achievementsText;
    private ProgressBar overallProgressBar, lessonProgressBar, quizProgressBar;
    private CardView completedCard, scoreCard, timeCard, achievementsCard;
    
    private JavaBuddyDatabase database;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        initializeViews();
        setupToolbar();
        initializeDatabase();
        loadProgressData();
    }

    private void initializeViews() {
        completedLessonsText = findViewById(R.id.tv_completed_lessons);
        averageScoreText = findViewById(R.id.tv_average_score);
        totalTimeText = findViewById(R.id.tv_total_time);
        achievementsText = findViewById(R.id.tv_achievements);
        
        overallProgressBar = findViewById(R.id.progress_overall);
        lessonProgressBar = findViewById(R.id.progress_lessons);
        quizProgressBar = findViewById(R.id.progress_quizzes);
        
        completedCard = findViewById(R.id.card_completed);
        scoreCard = findViewById(R.id.card_score);
        timeCard = findViewById(R.id.card_time);
        achievementsCard = findViewById(R.id.card_achievements);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Your Progress");
        }
    }

    private void initializeDatabase() {
        database = JavaBuddyDatabase.getDatabase(this);
        executor = Executors.newSingleThreadExecutor();
    }

    private void loadProgressData() {
        executor.execute(() -> {
            try {
                List<UserProgress> allProgress = database.userProgressDao().getAllProgress();
                
                // Calculate statistics
                int totalLessons = 15; // We now have 15 lessons
                int completedLessons = 0;
                int totalQuizScore = 0;
                int quizzesTaken = 0;
                int totalTimeMinutes = 0;
                
                for (UserProgress progress : allProgress) {
                    if (progress.isCompleted()) {
                        completedLessons++;
                    }
                    
                    if (progress.getBestScore() > 0) {
                        totalQuizScore += progress.getBestScore();
                        quizzesTaken++;
                    }
                    
                    totalTimeMinutes += progress.getTimeSpentMinutes();
                }
                
                double averageScore = quizzesTaken > 0 ? (double) totalQuizScore / quizzesTaken : 0;
                int overallProgress = (int) ((double) completedLessons / totalLessons * 100);
                
                // Count achievements
                int achievementCount = calculateAchievements(completedLessons, averageScore, totalTimeMinutes);
                
                // Create final copies for lambda
                final int finalCompletedLessons = completedLessons;
                final int finalTotalLessons = totalLessons;
                final double finalAverageScore = averageScore;
                final int finalTotalTimeMinutes = totalTimeMinutes;
                final int finalAchievementCount = achievementCount;
                final int finalOverallProgress = overallProgress;
                
                // Update UI on main thread
                runOnUiThread(() -> updateUI(finalCompletedLessons, finalTotalLessons, finalAverageScore, 
                    finalTotalTimeMinutes, finalAchievementCount, finalOverallProgress));
                    
            } catch (Exception e) {
                runOnUiThread(() -> showDefaultData());
            }
        });
    }

    private int calculateAchievements(int completedLessons, double averageScore, int totalTime) {
        int achievements = 0;
        
        // First lesson achievement
        if (completedLessons >= 1) achievements++;
        
        // Half way there achievement
        if (completedLessons >= 6) achievements++;
        
        // Course completed achievement
        if (completedLessons >= 15) achievements++;
        
        // High scorer achievement
        if (averageScore >= 80) achievements++;
        
        // Dedicated learner achievement
        if (totalTime >= 120) achievements++; // 2+ hours
        
        // Speed learner achievement
        if (completedLessons >= 3 && totalTime <= 60) achievements++;
        
        return achievements;
    }

    private void updateUI(int completedLessons, int totalLessons, double averageScore, 
                         int totalTime, int achievements, int overallProgress) {
        
        // Update text views
        completedLessonsText.setText(completedLessons + " / " + totalLessons + " lessons");
        averageScoreText.setText(String.format("%.1f%%", averageScore));
        
        int hours = totalTime / 60;
        int minutes = totalTime % 60;
        totalTimeText.setText(hours + "h " + minutes + "m");
        
        achievementsText.setText(achievements + " earned");
        
        // Update progress bars
        overallProgressBar.setProgress(overallProgress);
        lessonProgressBar.setProgress((int) ((double) completedLessons / totalLessons * 100));
        quizProgressBar.setProgress((int) averageScore);
        
        // Update card colors based on progress
        updateCardColors(overallProgress);
    }

    private void updateCardColors(int overallProgress) {
        int color;
        if (overallProgress >= 80) {
            color = Color.parseColor("#4CAF50"); // Green
        } else if (overallProgress >= 50) {
            color = Color.parseColor("#FF9800"); // Orange
        } else {
            color = Color.parseColor("#2196F3"); // Blue
        }
        
        // Apply subtle tint to cards
        completedCard.setCardBackgroundColor(Color.argb(20, Color.red(color), 
            Color.green(color), Color.blue(color)));
        scoreCard.setCardBackgroundColor(Color.argb(20, Color.red(color), 
            Color.green(color), Color.blue(color)));
        timeCard.setCardBackgroundColor(Color.argb(20, Color.red(color), 
            Color.green(color), Color.blue(color)));
        achievementsCard.setCardBackgroundColor(Color.argb(20, Color.red(color), 
            Color.green(color), Color.blue(color)));
    }

    private void showDefaultData() {
        completedLessonsText.setText("0 / 15 lessons");
        averageScoreText.setText("0.0%");
        totalTimeText.setText("0h 0m");
        achievementsText.setText("0 earned");
        
        overallProgressBar.setProgress(0);
        lessonProgressBar.setProgress(0);
        quizProgressBar.setProgress(0);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}