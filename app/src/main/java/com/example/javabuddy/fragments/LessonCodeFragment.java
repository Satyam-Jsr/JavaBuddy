package com.example.javabuddy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.javabuddy.R;
import com.example.javabuddy.compiler.JavaCompiler;
import com.example.javabuddy.compiler.AdvancedJavaInterpreter;
import com.example.javabuddy.compiler.RealJavaCompiler;
import com.example.javabuddy.database.JavaBuddyDatabase;
import com.example.javabuddy.database.entities.Lesson;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.langs.java.JavaLanguage;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class LessonCodeFragment extends Fragment {

    private static final String ARG_LESSON_ID = "lesson_id";
    private CodeEditor codeEditor;
    private Button copyButton, runButton;
    private FloatingActionButton fabTryIt;
    private JavaBuddyDatabase database;
    private ExecutorService executor;
    private int lessonId;
    private String originalCode = "";

    public static LessonCodeFragment newInstance(int lessonId) {
        LessonCodeFragment fragment = new LessonCodeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LESSON_ID, lessonId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lessonId = getArguments().getInt(ARG_LESSON_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_code, container, false);
        
        initializeViews(view);
        setupCodeEditor();
        setupClickListeners();
        loadCodeExample();
        
        return view;
    }

    private void initializeViews(View view) {
        codeEditor = view.findViewById(R.id.code_editor);
        copyButton = view.findViewById(R.id.copy_button);
        runButton = view.findViewById(R.id.run_button);
        fabTryIt = view.findViewById(R.id.fab_try_it);
        
        database = JavaBuddyDatabase.getDatabase(requireContext());
        executor = Executors.newSingleThreadExecutor();
    }

    private void setupCodeEditor() {
        codeEditor.setTypefaceText(android.graphics.Typeface.MONOSPACE);
        codeEditor.setEditorLanguage(new JavaLanguage());
        codeEditor.setColorScheme(new EditorColorScheme());
        codeEditor.setTextSize(14);
                        codeEditor.setLineSpacing(2f, 1.0f);
        codeEditor.setEditable(false); // Initially read-only
    }

    private void setupClickListeners() {
        copyButton.setOnClickListener(v -> copyCodeToClipboard());
        runButton.setOnClickListener(v -> runCode());
        fabTryIt.setOnClickListener(v -> toggleEditMode());
    }

    private void loadCodeExample() {
        executor.execute(() -> {
            Lesson lesson = database.lessonDao().getLessonById(lessonId);
            if (getActivity() != null && lesson != null) {
                originalCode = lesson.getCodeExample();
                getActivity().runOnUiThread(() -> {
                    if (originalCode != null && !originalCode.trim().isEmpty()) {
                        codeEditor.setText(originalCode);
                    } else {
                        codeEditor.setText("// No code example available for this lesson\n// This will be added in future updates");
                    }
                });
            }
        });
    }

    private void copyCodeToClipboard() {
        String code = codeEditor.getText().toString();
        android.content.ClipboardManager clipboard = 
            (android.content.ClipboardManager) requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Java Code", code);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Code copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

    private void runCode() {
        String code = codeEditor.getText().toString();
        
        if (code.trim().isEmpty()) {
            Toast.makeText(getContext(), "No code to run!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show compiler selection dialog
        showCompilerSelectionDialog(code);
    }

    private void showCompilerSelectionDialog(String code) {
        if (getContext() == null) return;
        
        String[] compilerOptions = {"Basic Compiler", "Advanced Interpreter", "Real Java Compiler"};
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Choose Compiler")
               .setItems(compilerOptions, (dialog, which) -> {
                   executeWithCompiler(code, which);
               })
               .setNegativeButton("Cancel", null)
               .show();
    }
    
    private void executeWithCompiler(String code, int compilerType) {
        new Thread(() -> {
            try {
                String output;
                String compilerName;
                
                switch (compilerType) {
                    case 0: // Basic Compiler
                        JavaCompiler basicCompiler = new JavaCompiler(requireContext());
                        JavaCompiler.CompileResult basicResult = basicCompiler.compileAndRun(code);
                        output = basicResult.isSuccess() ? 
                                basicResult.getOutput() + "\n\n" + basicResult.getMessage() :
                                "Error: " + basicResult.getMessage();
                        compilerName = "Basic Compiler";
                        break;
                    case 1: // Advanced Interpreter
                        AdvancedJavaInterpreter advancedInterpreter = new AdvancedJavaInterpreter(requireContext());
                        AdvancedJavaInterpreter.CompileResult advancedResult = advancedInterpreter.compileAndRun(code);
                        output = advancedResult.isSuccess() ? 
                                advancedResult.getOutput() + "\n\n" + advancedResult.getMessage() :
                                "Error: " + advancedResult.getMessage();
                        compilerName = "Advanced Interpreter";
                        break;
                    case 2: // Real Java Compiler
                        RealJavaCompiler realCompiler = new RealJavaCompiler(requireContext());
                        Object realResult = realCompiler.compileAndRun(code);
                        if (realResult instanceof JavaCompiler.CompileResult) {
                            JavaCompiler.CompileResult result = (JavaCompiler.CompileResult) realResult;
                            output = result.isSuccess() ? 
                                    result.getOutput() + "\n\n" + result.getMessage() :
                                    "Error: " + result.getMessage();
                        } else {
                            output = String.valueOf(realResult);
                        }
                        compilerName = "Real Java Compiler";
                        break;
                    default:
                        output = "Unknown compiler selected";
                        compilerName = "Unknown";
                        break;
                }
                
                // Show output on UI thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showOutputDialog(compilerName + " Output", output);
                    });
                }
                
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showOutputDialog("Error", "Execution failed: " + e.getMessage());
                    });
                }
            }
        }).start();
    }

    private void showOutputDialog(String title, String output) {
        if (getContext() != null) {
            androidx.appcompat.app.AlertDialog.Builder builder = 
                new androidx.appcompat.app.AlertDialog.Builder(getContext());
            builder.setTitle(title)
                   .setMessage(output)
                   .setPositiveButton("OK", null)
                   .show();
        }
    }

    private void toggleEditMode() {
        if (codeEditor.isEditable()) {
            // Switch to read-only mode
            codeEditor.setEditable(false);
            codeEditor.setText(originalCode);
            fabTryIt.setImageResource(R.drawable.ic_edit);
            Toast.makeText(getContext(), "View mode - Code restored", Toast.LENGTH_SHORT).show();
        } else {
            // Switch to edit mode
            codeEditor.setEditable(true);
            fabTryIt.setImageResource(R.drawable.ic_restore);
            Toast.makeText(getContext(), "Edit mode - Try modifying the code!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}