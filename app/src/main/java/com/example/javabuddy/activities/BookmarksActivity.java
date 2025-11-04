package com.example.javabuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javabuddy.R;
import com.example.javabuddy.adapters.LessonAdapter;
import com.example.javabuddy.database.JavaBuddyDatabase;
import com.example.javabuddy.database.entities.Lesson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookmarksActivity extends AppCompatActivity implements LessonAdapter.OnLessonClickListener {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private LessonAdapter adapter;
    private final List<Lesson> bookmarkedLessons = new ArrayList<>();
    private JavaBuddyDatabase database;
    private ExecutorService executor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.bookmarks_title);
        }

        recyclerView = findViewById(R.id.bookmarks_recycler_view);
        emptyView = findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LessonAdapter(bookmarkedLessons, this);
        recyclerView.setAdapter(adapter);

        database = JavaBuddyDatabase.getDatabase(this);
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookmarks();
    }

    private void loadBookmarks() {
        executor.execute(() -> {
            List<Lesson> lessons = database.lessonBookmarkDao().getBookmarkedLessons();
            runOnUiThread(() -> {
                bookmarkedLessons.clear();
                bookmarkedLessons.addAll(lessons);
                adapter.notifyDataSetChanged();
                emptyView.setVisibility(lessons.isEmpty() ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(lessons.isEmpty() ? View.GONE : View.VISIBLE);
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
