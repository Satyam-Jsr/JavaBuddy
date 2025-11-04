package com.example.javabuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javabuddy.R;
import com.example.javabuddy.adapters.QuizTopicAdapter;
import com.example.javabuddy.database.DatabasePopulator;
import com.example.javabuddy.database.JavaBuddyDatabase;
import com.example.javabuddy.database.entities.Lesson;
import com.example.javabuddy.database.entities.Quiz;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizSelectionActivity extends AppCompatActivity {

    private RecyclerView quizTopicsRecycler;
    private TextView emptyStateText;
    private QuizTopicAdapter adapter;
    private JavaBuddyDatabase database;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_selection);

        initializeViews();
        setupToolbar();
        
    // Removed loading toast and immediate database toasts for a cleaner UX
    forcePopulateIfNeeded();
    loadLessonsWithQuizzes();
    }

    private void initializeViews() {
        quizTopicsRecycler = findViewById(R.id.quiz_topics_recycler);
        emptyStateText = findViewById(R.id.empty_state_text);
        
        database = JavaBuddyDatabase.getDatabase(this);
        executor = Executors.newSingleThreadExecutor();
        
        // Setup RecyclerView
        adapter = new QuizTopicAdapter(this);
        quizTopicsRecycler.setLayoutManager(new LinearLayoutManager(this));
        quizTopicsRecycler.setAdapter(adapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Select Problems");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz_selection_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_refresh_data) {
            refreshQuizData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void forcePopulateIfNeeded() {
        // Run synchronously to ensure population before UI loading
        try {
            int lessonCount = database.lessonDao().getLessonCount();
            int quizCount = database.quizDao().getQuizCount();
            
            android.util.Log.d("QuizSelection", "Initial check: " + lessonCount + " lessons, " + quizCount + " quizzes");
            
            if (lessonCount == 0 || quizCount == 0) {
                android.util.Log.d("QuizSelection", "Force populating database immediately...");
                DatabasePopulator.forceRepopulate(database);
                
                // Verify after population
                lessonCount = database.lessonDao().getLessonCount();
                quizCount = database.quizDao().getQuizCount();
                android.util.Log.d("QuizSelection", "After population: " + lessonCount + " lessons, " + quizCount + " quizzes");
            }
        } catch (Exception e) {
            android.util.Log.e("QuizSelection", "Error in forcePopulateIfNeeded: " + e.getMessage(), e);
        }
    }
    
    private void refreshQuizData() {
        Toast.makeText(this, "Refreshing problem data...", Toast.LENGTH_SHORT).show();
        
        executor.execute(() -> {
            // Clear and force repopulate database
            android.util.Log.d("QuizSelection", "Manually clearing database...");
            database.clearAllTables();
            
            android.util.Log.d("QuizSelection", "Manually populating database...");
            DatabasePopulator.forceRepopulate(database);
            
            // Check results
            int lessonCount = database.lessonDao().getLessonCount();
            int quizCount = database.quizDao().getQuizCount();
            android.util.Log.d("QuizSelection", "After manual population: " + lessonCount + " lessons, " + quizCount + " quizzes");
            
            runOnUiThread(() -> {
                Toast.makeText(this, "Problem data refreshed! " + lessonCount + " lessons, " + quizCount + " problems", Toast.LENGTH_LONG).show();
                // Reload the lessons
                loadLessonsWithQuizzes();
            });
        });
    }

    private void loadLessonsWithQuizzes() {
        android.util.Log.d("QuizSelection", "loadLessonsWithQuizzes() called");
        executor.execute(() -> {
            android.util.Log.d("QuizSelection", "Executing database queries...");
            // Force check database status
            int lessonCount = database.lessonDao().getLessonCount();
            int quizCount = database.quizDao().getQuizCount();
            int totalQuizzes = database.quizDao().getAllQuizzes().size();
            
            android.util.Log.d("QuizSelection", "Database status: " + lessonCount + " lessons, " + quizCount + " quiz records, " + totalQuizzes + " total problems");
            
            // If no data, force populate immediately
            if (lessonCount == 0 || quizCount == 0) {
                android.util.Log.d("QuizSelection", "No data found, forcing population...");
                DatabasePopulator.forceRepopulate(database);
                
                // Re-check after population
                lessonCount = database.lessonDao().getLessonCount();
                quizCount = database.quizDao().getQuizCount();
                android.util.Log.d("QuizSelection", "After population: " + lessonCount + " lessons, " + quizCount + " quiz records");
            }
            
            List<Lesson> lessons = database.lessonDao().getAllLessons();
            List<QuizTopicAdapter.QuizTopicItem> topicItems = new ArrayList<>();
            
            android.util.Log.d("QuizSelection", "Found " + lessons.size() + " lessons");
            
            for (Lesson lesson : lessons) {
                List<Quiz> quizzes = database.quizDao().getQuizzesByLessonId(lesson.getId());
                int lessonQuizCount = quizzes.size();
                android.util.Log.d("QuizSelection", "Lesson " + lesson.getId() + " (" + lesson.getTitle() + ") has " + lessonQuizCount + " problems");
                topicItems.add(new QuizTopicAdapter.QuizTopicItem(lesson, lessonQuizCount));
            }
            
            runOnUiThread(() -> {
                if (topicItems.isEmpty()) {
                    quizTopicsRecycler.setVisibility(View.GONE);
                    emptyStateText.setVisibility(View.VISIBLE);
                } else {
                    quizTopicsRecycler.setVisibility(View.VISIBLE);
                    emptyStateText.setVisibility(View.GONE);
                    adapter.setTopicItems(topicItems);
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh adapter to pick up settings changes (like animation toggle)
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}