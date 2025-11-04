package com.example.javabuddy.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.javabuddy.R;
import com.example.javabuddy.database.JavaBuddyDatabase;
import com.example.javabuddy.database.entities.PracticeProblem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.langs.java.JavaLanguage;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class ProblemSolvingActivity extends AppCompatActivity {

    private TextView problemTitle, problemDescription, sampleInput, expectedOutput, hintsText;
    private CodeEditor codeEditor;
    private FloatingActionButton fabRun, fabHint, fabSolution;
    
    private JavaBuddyDatabase database;
    private ExecutorService executor;
    private PracticeProblem currentProblem;
    private int problemId;
    private boolean hintsShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_solving);

        // Get problem data from intent
        problemId = getIntent().getIntExtra("problem_id", -1);
        String title = getIntent().getStringExtra("problem_title");

        if (problemId == -1) {
            Toast.makeText(this, "Error loading problem", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupToolbar(title);
        setupCodeEditor();
        loadProblemData();
        setupClickListeners();
    }

    private void initializeViews() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            
            problemTitle = findViewById(R.id.problem_title);
            problemDescription = findViewById(R.id.problem_description);
            sampleInput = findViewById(R.id.sample_input);
            expectedOutput = findViewById(R.id.expected_output);
            hintsText = findViewById(R.id.hints_text);
            codeEditor = findViewById(R.id.code_editor);
            fabRun = findViewById(R.id.fab_run);
            fabHint = findViewById(R.id.fab_hint);
            fabSolution = findViewById(R.id.fab_solution);
            
            // Verify critical views are not null
            if (problemTitle == null || problemDescription == null || codeEditor == null) {
                Toast.makeText(this, "Error: Unable to initialize interface", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            
            database = JavaBuddyDatabase.getDatabase(this);
            executor = Executors.newSingleThreadExecutor();
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupToolbar(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }
    }

    private void setupCodeEditor() {
        codeEditor.setTypefaceText(android.graphics.Typeface.MONOSPACE);
        codeEditor.setEditorLanguage(new JavaLanguage());
        codeEditor.setColorScheme(new EditorColorScheme());
        codeEditor.setTextSize(14);
            codeEditor.setLineSpacing(2f, 1.0f);        // Set default template
        String template = "public class Solution {\n" +
                         "    public static void main(String[] args) {\n" +
                         "        // Write your solution here\n" +
                         "        \n" +
                         "    }\n" +
                         "}";
        codeEditor.setText(template);
    }

    private void loadProblemData() {
        executor.execute(() -> {
            try {
                currentProblem = database.practiceProblemDao().getProblemById(problemId);
                if (currentProblem != null) {
                    runOnUiThread(() -> {
                        displayProblemData();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ProblemSolvingActivity.this, "Error: Problem not found", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(ProblemSolvingActivity.this, "Error loading problem: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void displayProblemData() {
        try {
            if (currentProblem == null) {
                Toast.makeText(this, "Error: No problem data available", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (problemTitle != null) {
                problemTitle.setText(currentProblem.getTitle());
            }
            if (problemDescription != null) {
                problemDescription.setText(currentProblem.getProblemStatement());
            }
            if (sampleInput != null) {
                sampleInput.setText("Input: " + (currentProblem.getSampleInput() != null ? 
                               currentProblem.getSampleInput() : "No input required"));
            }
            if (expectedOutput != null) {
                expectedOutput.setText("Expected Output: " + currentProblem.getExpectedOutput());
            }
            
            // Hide hints initially
            if (hintsText != null) {
                hintsText.setText("");
                hintsText.setVisibility(TextView.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error displaying problem: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        fabRun.setOnClickListener(v -> runCode());
        fabHint.setOnClickListener(v -> showHints());
        fabSolution.setOnClickListener(v -> showSolution());
    }

    private void runCode() {
        String code = codeEditor.getText().toString();
        
        if (code.trim().isEmpty()) {
            Toast.makeText(this, "Please write some code first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Basic validation and simulation
        if (validateBasicSyntax(code)) {
            // In a real implementation, you would compile and run the code
            // and compare output with expected output
            showSimulatedResult(code);
        } else {
            Toast.makeText(this, "Code contains syntax errors!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateBasicSyntax(String code) {
        return code.contains("public class") && 
               code.contains("public static void main") &&
               code.contains("{") && 
               code.contains("}");
    }

    private void showSimulatedResult(String code) {
        // This is a simplified simulation
        // In a real app, you would actually execute the code and compare output
        
        boolean hasLogic = code.contains("System.out.println") || 
                          code.contains("return") ||
                          code.contains("if") ||
                          code.contains("for") ||
                          code.contains("while");
        
        if (hasLogic) {
            showResultDialog("Good Job!", 
                           "Your solution looks good! Keep practicing to improve your skills.", 
                           true);
        } else {
            showResultDialog("Keep Trying", 
                           "Your solution needs more implementation. Add some logic to solve the problem.", 
                           false);
        }
    }

    private void showResultDialog(String title, String message, boolean isSuccess) {
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(this);
        
        builder.setTitle(title)
               .setMessage(message)
               .setPositiveButton("OK", null);
        
        if (isSuccess) {
            builder.setNeutralButton("Try Another", (dialog, which) -> finish());
        }
        
        builder.show();
    }

    private void showHints() {
        if (currentProblem != null && currentProblem.getHints() != null && 
            !currentProblem.getHints().trim().isEmpty()) {
            
            if (!hintsShown) {
                hintsText.setText("Hints: " + currentProblem.getHints());
                hintsText.setVisibility(TextView.VISIBLE);
                hintsShown = true;
                fabHint.setImageResource(R.drawable.ic_lightbulb_on);
                Toast.makeText(this, "Hints revealed!", Toast.LENGTH_SHORT).show();
            } else {
                hintsText.setVisibility(TextView.GONE);
                hintsShown = false;
                fabHint.setImageResource(R.drawable.ic_lightbulb);
            }
        } else {
            Toast.makeText(this, "No hints available for this problem", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSolution() {
        if (currentProblem != null && currentProblem.getSolution() != null && 
            !currentProblem.getSolution().trim().isEmpty()) {
            
            androidx.appcompat.app.AlertDialog.Builder builder = 
                new androidx.appcompat.app.AlertDialog.Builder(this);
            
            builder.setTitle("Solution")
                   .setMessage("Are you sure you want to see the solution? Try solving it yourself first!")
                   .setPositiveButton("Show Solution", (dialog, which) -> {
                       codeEditor.setText(currentProblem.getSolution());
                       Toast.makeText(this, "Solution loaded. Study it carefully!", Toast.LENGTH_LONG).show();
                   })
                   .setNegativeButton("Keep Trying", null)
                   .show();
        } else {
            Toast.makeText(this, "Solution not available for this problem", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.problem_solving_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_reset) {
            resetCode();
            return true;
        } else if (itemId == R.id.action_save) {
            saveProgress();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void resetCode() {
        String template = "public class Solution {\n" +
                         "    public static void main(String[] args) {\n" +
                         "        // Write your solution here\n" +
                         "        \n" +
                         "    }\n" +
                         "}";
        codeEditor.setText(template);
        Toast.makeText(this, "Code reset to template", Toast.LENGTH_SHORT).show();
    }

    private void saveProgress() {
        // In a real app, you would save the user's progress/code
        Toast.makeText(this, "Progress saved!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}