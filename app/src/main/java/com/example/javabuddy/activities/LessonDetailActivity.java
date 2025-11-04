package com.example.javabuddy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.javabuddy.R;
import com.example.javabuddy.adapters.LessonDetailPagerAdapter;
import com.example.javabuddy.database.JavaBuddyDatabase;
import com.example.javabuddy.database.dao.LessonBookmarkDao;
import com.example.javabuddy.database.entities.Lesson;
import com.example.javabuddy.database.entities.LessonBookmark;
import com.example.javabuddy.database.entities.UserProgress;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LessonDetailActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextView lessonTitle;
    private FloatingActionButton fabComplete;
    private FloatingActionButton aiHelpFab;
    
    private JavaBuddyDatabase database;
    private ExecutorService executor;
    private Lesson currentLesson;
    private int lessonId;
    private SharedPreferences preferences;
    private LessonBookmarkDao bookmarkDao;
    private boolean isBookmarked;
    private MenuItem bookmarkMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);

        // Get lesson data from intent
        lessonId = getIntent().getIntExtra("lesson_id", -1);
        String title = getIntent().getStringExtra("lesson_title");

        if (lessonId == -1) {
            Toast.makeText(this, "Error loading lesson", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupToolbar(title);
        setupViewPager();
        loadLessonData();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        lessonTitle = findViewById(R.id.lesson_title);
        fabComplete = findViewById(R.id.fab_complete);
        aiHelpFab = findViewById(R.id.ai_help_fab);
        
        database = JavaBuddyDatabase.getDatabase(this);
        executor = Executors.newSingleThreadExecutor();
        preferences = getSharedPreferences("JavaBuddyPrefs", MODE_PRIVATE);
    bookmarkDao = database.lessonBookmarkDao();

        fabComplete.setOnClickListener(v -> markLessonComplete());
        
        if (aiHelpFab != null) {
            aiHelpFab.setOnClickListener(v -> {
                Intent intent = new Intent(this, AIHelpActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupToolbar(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }
    }

    private void setupViewPager() {
        LessonDetailPagerAdapter adapter = new LessonDetailPagerAdapter(this, lessonId);
        viewPager.setAdapter(adapter);
        
        // Disable swipe gesture - only allow tab button navigation
        viewPager.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Content");
                    break;
                case 1:
                    tab.setText("Code Example");
                    break;
                case 2:
                    tab.setText("Practice");
                    break;
            }
        }).attach();
    }

    private void loadLessonData() {
        executor.execute(() -> {
            currentLesson = database.lessonDao().getLessonById(lessonId);
            UserProgress progress = database.userProgressDao().getProgressByLessonId(lessonId);
            boolean existingBookmark = bookmarkDao.isBookmarked(lessonId) > 0;
            
            runOnUiThread(() -> {
                if (currentLesson != null) {
                    lessonTitle.setText(currentLesson.getTitle());
                    isBookmarked = existingBookmark;
                    updateBookmarkMenuIcon();
                    
                    // Update FAB based on progress
                    if (progress != null && progress.isCompleted()) {
                        fabComplete.setImageResource(R.drawable.ic_check);
                        fabComplete.setEnabled(false);
                    }
                }
            });
        });
    }

    private void markLessonComplete() {
        executor.execute(() -> {
            UserProgress existingProgress = database.userProgressDao().getProgressByLessonId(lessonId);
            
            if (existingProgress != null) {
                existingProgress.setCompleted(true);
                existingProgress.setTimestamp(System.currentTimeMillis());
                database.userProgressDao().updateProgress(existingProgress);
            } else {
                UserProgress newProgress = new UserProgress(lessonId, true, 100, 
                    System.currentTimeMillis(), 0, 1);
                database.userProgressDao().insertProgress(newProgress);
            }
            
            runOnUiThread(() -> {
                fabComplete.setImageResource(R.drawable.ic_check);
                fabComplete.setEnabled(false);
                Toast.makeText(this, "Lesson completed!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lesson_detail_menu, menu);
        bookmarkMenuItem = menu.findItem(R.id.action_bookmark);
        updateBookmarkMenuIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_bookmark) {
            toggleBookmark();
            return true;
        } else if (itemId == R.id.action_share) {
            // Handle share
            shareLesson();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void toggleBookmark() {
        executor.execute(() -> {
            boolean currentlyBookmarked = bookmarkDao.isBookmarked(lessonId) > 0;
            if (currentlyBookmarked) {
                bookmarkDao.deleteBookmark(lessonId);
            } else {
                bookmarkDao.insertBookmark(new LessonBookmark(lessonId, System.currentTimeMillis()));
            }
            isBookmarked = !currentlyBookmarked;

            runOnUiThread(() -> {
                updateBookmarkMenuIcon();
                Toast.makeText(this,
                    isBookmarked ? R.string.bookmark_added_message : R.string.bookmark_removed_message,
                    Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void updateBookmarkMenuIcon() {
        if (bookmarkMenuItem == null) {
            return;
        }
        if (isBookmarked) {
            bookmarkMenuItem.setIcon(R.drawable.ic_bookmark);
        } else {
            bookmarkMenuItem.setIcon(R.drawable.ic_bookmark_border);
        }
    }

    private void shareLesson() {
        if (currentLesson != null) {
            String shareText = "Check out this Java lesson: " + currentLesson.getTitle() + 
                             "\n\n" + currentLesson.getSummary() + 
                             "\n\nLearn Java with JavaBuddy!";
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Share Lesson"));
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