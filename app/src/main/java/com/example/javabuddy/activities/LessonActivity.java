package com.example.javabuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javabuddy.R;
import com.example.javabuddy.adapters.LessonAdapter;
import com.example.javabuddy.database.DatabasePopulator;
import com.example.javabuddy.database.JavaBuddyDatabase;
import com.example.javabuddy.database.entities.Lesson;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LessonActivity extends AppCompatActivity implements LessonAdapter.OnLessonClickListener {

    private RecyclerView recyclerView;
    private LessonAdapter adapter;
    private List<Lesson> lessonList;
    private JavaBuddyDatabase database;
    private ExecutorService executor;
    private FloatingActionButton aiHelpFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        initializeViews();
        setupRecyclerView();
        loadLessons();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Java Lessons");
        }

        recyclerView = findViewById(R.id.lessons_recycler_view);
        database = JavaBuddyDatabase.getDatabase(this);
        executor = Executors.newSingleThreadExecutor();
        lessonList = new ArrayList<>();
        
        aiHelpFab = findViewById(R.id.ai_help_fab);
        if (aiHelpFab != null) {
            aiHelpFab.setOnClickListener(v -> {
                Intent intent = new Intent(this, AIHelpActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupRecyclerView() {
        adapter = new LessonAdapter(lessonList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadLessons() {
        executor.execute(() -> {
            List<Lesson> lessons = database.lessonDao().getAllLessons();
            runOnUiThread(() -> {
                lessonList.clear();
                lessonList.addAll(lessons);
                adapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onLessonClick(Lesson lesson) {
        Intent intent = new Intent(this, LessonDetailActivity.class);
        intent.putExtra("lesson_id", lesson.getId());
        intent.putExtra("lesson_title", lesson.getTitle());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lesson_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh_lessons) {
            refreshLessons();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshLessons() {
        Toast.makeText(this, "Refreshing lessons...", Toast.LENGTH_SHORT).show();
        
        executor.execute(() -> {
            // Force database repopulation
            DatabasePopulator.forceRepopulate(database);
            
            // Reload lessons
            List<Lesson> lessons = database.lessonDao().getAllLessons();
            runOnUiThread(() -> {
                lessonList.clear();
                lessonList.addAll(lessons);
                adapter.notifyDataSetChanged();
                Toast.makeText(LessonActivity.this, "Lessons refreshed!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}