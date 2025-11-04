package com.example.javabuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.javabuddy.activities.DebugActivity;
import com.example.javabuddy.activities.IDEActivity;
import com.example.javabuddy.activities.LessonActivity;
import com.example.javabuddy.activities.SettingsActivity;
import com.example.javabuddy.activities.PracticeActivity;
import com.example.javabuddy.activities.ProgressActivity;
import com.example.javabuddy.activities.QuizSelectionActivity;
import com.example.javabuddy.activities.AIHelpActivity;
import com.example.javabuddy.activities.BookmarksActivity;
import com.example.javabuddy.database.DatabasePopulator;
import com.example.javabuddy.database.JavaBuddyDatabase;
import com.example.javabuddy.fragments.HomeFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private JavaBuddyDatabase database;
    private SharedPreferences preferences;
    private FloatingActionButton aiHelpFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize preferences
        preferences = getSharedPreferences("JavaBuddyPrefs", MODE_PRIVATE);
        
        setContentView(R.layout.activity_main);

        // Initialize database with error handling
        try {
            database = JavaBuddyDatabase.getDatabase(this);
            
            // Force database population on every startup for debugging
            JavaBuddyDatabase.databaseWriteExecutor.execute(() -> {
                try {
                    int lessonCount = database.lessonDao().getLessonCount();
                    int quizCount = database.quizDao().getQuizCount();
                    android.util.Log.d("MainActivity", "Startup - lesson count: " + lessonCount + ", quiz count: " + quizCount);
                    
                    // Force populate if either is missing
                    if (lessonCount == 0 || quizCount == 0) {
                        android.util.Log.d("MainActivity", "Missing data, forcing population...");
                        DatabasePopulator.forceRepopulate(database);
                    }
                } catch (Exception e) {
                    android.util.Log.e("MainActivity", "Database population error", e);
                }
            });
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Database initialization error", e);
            // Show user-friendly error message
            runOnUiThread(() -> {
                Toast.makeText(this, "Database initialization error. Please restart the app.", Toast.LENGTH_LONG).show();
            });
        }

        // Database status toasts removed for production

        // Initialize views
        initializeViews();
        
        // Setup navigation
        setupNavigation();
        
        // Load home fragment by default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        aiHelpFab = findViewById(R.id.ai_help_fab);

        if (aiHelpFab != null) {
            aiHelpFab.setOnClickListener(v -> {
                Intent intent = new Intent(this, AIHelpActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupNavigation() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_home) {
            loadFragment(new HomeFragment());
        } else if (itemId == R.id.nav_lessons) {
            startActivity(new Intent(this, LessonActivity.class));
        } else if (itemId == R.id.nav_quiz) {
            startActivity(new Intent(this, QuizSelectionActivity.class));
        } else if (itemId == R.id.nav_ide) {
            startActivity(new Intent(this, IDEActivity.class));
        } else if (itemId == R.id.nav_practice) {
            startActivity(new Intent(this, PracticeActivity.class));
        } else if (itemId == R.id.nav_progress) {
            startActivity(new Intent(this, ProgressActivity.class));
        } else if (itemId == R.id.nav_bookmarks) {
            startActivity(new Intent(this, BookmarksActivity.class));
        } else if (itemId == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (itemId == R.id.nav_about) {
            // Handle about
            Toast.makeText(this, "JavaBuddy v1.0 - Learn Java Interactively", Toast.LENGTH_LONG).show();
        } else if (itemId == R.id.nav_debug) {
            // Handle debug
            startActivity(new Intent(this, DebugActivity.class));
        } else if (itemId == R.id.nav_ai_quiz) {
            // Handle AI Quiz Generator
            startActivity(new Intent(this, com.example.javabuddy.activities.AIQuizGeneratorActivity.class));
        } else if (itemId == R.id.nav_ai_challenges) {
            // Handle AI Programming Challenges
            startActivity(new Intent(this, com.example.javabuddy.activities.AIProgrammingChallengeActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}