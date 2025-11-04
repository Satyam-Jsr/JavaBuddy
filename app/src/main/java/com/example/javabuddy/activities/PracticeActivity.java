package com.example.javabuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javabuddy.R;
import com.example.javabuddy.adapters.PracticeProblemAdapter;
import com.example.javabuddy.database.JavaBuddyDatabase;
import com.example.javabuddy.database.entities.PracticeProblem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PracticeActivity extends AppCompatActivity implements PracticeProblemAdapter.OnProblemClickListener {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private PracticeProblemAdapter adapter;
    
    private JavaBuddyDatabase database;
    private ExecutorService executor;
    private List<PracticeProblem> allProblems;
    private List<PracticeProblem> filteredProblems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        initializeViews();
        setupRecyclerView();
        setupTabs();
        loadProblems();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Practice Problems");
        }

        tabLayout = findViewById(R.id.tab_layout);
        recyclerView = findViewById(R.id.problems_recycler_view);
        
        database = JavaBuddyDatabase.getDatabase(this);
        executor = Executors.newSingleThreadExecutor();
        allProblems = new ArrayList<>();
        filteredProblems = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new PracticeProblemAdapter(filteredProblems, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Beginner"));
        tabLayout.addTab(tabLayout.newTab().setText("Intermediate"));
        tabLayout.addTab(tabLayout.newTab().setText("Advanced"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterProblemsByDifficulty(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadProblems() {
        executor.execute(() -> {
            List<PracticeProblem> problems = database.practiceProblemDao().getAllProblems();
            runOnUiThread(() -> {
                allProblems.clear();
                allProblems.addAll(problems);
                filterProblemsByDifficulty("All");
            });
        });
    }

    private void filterProblemsByDifficulty(String difficulty) {
        filteredProblems.clear();
        
        if ("All".equals(difficulty)) {
            filteredProblems.addAll(allProblems);
        } else {
            for (PracticeProblem problem : allProblems) {
                if (difficulty.equals(problem.getDifficulty())) {
                    filteredProblems.add(problem);
                }
            }
        }
        
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onProblemClick(PracticeProblem problem) {
        Intent intent = new Intent(this, ProblemSolvingActivity.class);
        intent.putExtra("problem_id", problem.getId());
        intent.putExtra("problem_title", problem.getTitle());
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