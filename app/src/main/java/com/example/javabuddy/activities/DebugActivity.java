package com.example.javabuddy.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.javabuddy.R;
import com.example.javabuddy.database.DatabasePopulator;
import com.example.javabuddy.database.JavaBuddyDatabase;
import com.example.javabuddy.database.entities.Lesson;
import com.example.javabuddy.database.entities.Quiz;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DebugActivity extends AppCompatActivity {

    private TextView debugText;
    private JavaBuddyDatabase database;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        setupToolbar();
        initializeViews();
        loadDebugInfo();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Debug Information");
        }
    }

    private void initializeViews() {
        debugText = findViewById(R.id.debug_text);
        database = JavaBuddyDatabase.getDatabase(this);
        executor = Executors.newSingleThreadExecutor();
    }

    private void loadDebugInfo() {
        executor.execute(() -> {
            try {
                // Force repopulate database
                DatabasePopulator.forceRepopulate(database);
                
                // Get counts
                int lessonCount = database.lessonDao().getLessonCount();
                int quizCount = database.quizDao().getQuizCount();
                int problemCount = database.practiceProblemDao().getProblemCount();
                
                // Get sample data
                List<Lesson> lessons = database.lessonDao().getAllLessons();
                List<Quiz> quizzes = database.quizDao().getAllQuizzes();
                
                StringBuilder debugInfo = new StringBuilder();
                debugInfo.append("=== DATABASE DEBUG INFO ===\n\n");
                debugInfo.append("Lesson Count: ").append(lessonCount).append("\n");
                debugInfo.append("Quiz Count: ").append(quizCount).append("\n");
                debugInfo.append("Problem Count: ").append(problemCount).append("\n\n");
                
                debugInfo.append("=== SAMPLE LESSONS ===\n");
                for (int i = 0; i < Math.min(3, lessons.size()); i++) {
                    Lesson lesson = lessons.get(i);
                    debugInfo.append("Lesson ").append(lesson.getId()).append(": ").append(lesson.getTitle()).append("\n");
                }
                
                debugInfo.append("\n=== SAMPLE QUIZZES ===\n");
                for (int i = 0; i < Math.min(5, quizzes.size()); i++) {
                    Quiz quiz = quizzes.get(i);
                    debugInfo.append("Quiz ").append(quiz.getId()).append(" (Lesson ").append(quiz.getLessonId()).append("): ").append(quiz.getQuestion()).append("\n");
                }
                
                debugInfo.append("\n=== QUIZZES BY LESSON ===\n");
                for (Lesson lesson : lessons) {
                    List<Quiz> lessonQuizzes = database.quizDao().getQuizzesByLessonId(lesson.getId());
                    debugInfo.append("Lesson ").append(lesson.getId()).append(" has ").append(lessonQuizzes.size()).append(" quizzes\n");
                }
                
                runOnUiThread(() -> {
                    debugText.setText(debugInfo.toString());
                    Toast.makeText(this, "Debug info loaded successfully!", Toast.LENGTH_SHORT).show();
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    debugText.setText("Error loading debug info: " + e.getMessage());
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}

