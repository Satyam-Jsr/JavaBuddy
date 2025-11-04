package com.example.javabuddy.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.javabuddy.R;
import com.example.javabuddy.compiler.AdvancedJavaInterpreter;
import com.example.javabuddy.compiler.RealJavaCompiler;
import com.example.javabuddy.compiler.JavaCompiler;

import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.langs.java.JavaLanguage;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class IDEActivity extends AppCompatActivity {

    private CodeEditor codeEditor;
    private String currentCode = "";
    private JavaCompiler javaCompiler;
    private AdvancedJavaInterpreter advancedInterpreter;
    private RealJavaCompiler realCompiler;
    private int compilerMode = 2; // 0=Basic, 1=Advanced, 2=Real Compiler
    
    // Sample Java template
    private final String DEFAULT_CODE = "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        // Write your Java code here\n" +
            "        System.out.println(\"Hello, JavaBuddy!\");\n" +
            "    }\n" +
            "}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ide);

        // Initialize compilers
        javaCompiler = new JavaCompiler(this);
        advancedInterpreter = new AdvancedJavaInterpreter(this);
        realCompiler = new RealJavaCompiler(this);

        initializeViews();
        setupCodeEditor();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Java IDE");
        }

        codeEditor = findViewById(R.id.code_editor);
        
        // Setup FAB
        com.google.android.material.floatingactionbutton.FloatingActionButton fabRun = findViewById(R.id.fab_run);
        fabRun.setOnClickListener(v -> runCode());
    }

    private void setupCodeEditor() {
        // Set Java language for syntax highlighting
        codeEditor.setTypefaceText(android.graphics.Typeface.MONOSPACE);
        codeEditor.setEditorLanguage(new JavaLanguage());
        
        // Set color scheme (you can customize this)
        codeEditor.setColorScheme(new EditorColorScheme());
        
        // Set default code
        codeEditor.setText(DEFAULT_CODE);
        
        // Configure editor settings
        codeEditor.setTextSize(14);
        codeEditor.setLineSpacing(2f, 1.0f);
        // setOverScrollEnabled method not available in this version
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ide_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_run) {
            runCode();
            return true;
        } else if (itemId == R.id.action_clear) {
            clearCode();
            return true;
        } else if (itemId == R.id.action_save) {
            saveCode();
            return true;
        } else if (itemId == R.id.action_load_template) {
            loadTemplate();
            return true;
        } else if (itemId == R.id.action_toggle_mode) {
            toggleCompilerMode();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void runCode() {
        currentCode = codeEditor.getText().toString();
        
        if (currentCode.trim().isEmpty()) {
            Toast.makeText(this, "Please write some code first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading message with current mode
        final String mode = getCompilerModeName();
        Toast.makeText(this, "Compiling with " + mode + "...", Toast.LENGTH_SHORT).show();
        
        // Run compilation and execution in background thread
        new Thread(() -> {
            try {
                Object result = null;
                
                switch (compilerMode) {
                    case 0: // Basic Interpreter
                        result = javaCompiler.compileAndRun(currentCode);
                        break;
                    case 1: // Advanced Interpreter
                        result = advancedInterpreter.compileAndRun(currentCode);
                        break;
                    case 2: // Real Java Compiler
                        result = realCompiler.compileAndRun(currentCode);
                        break;
                }
                
                // Update UI on main thread
                final Object finalResult = result;
                runOnUiThread(() -> {
                    handleCompilerResult(finalResult, mode);
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showOutputDialog("Error", "Unexpected error in " + mode + ": " + e.getMessage());
                });
            }
        }).start();
    }
    
    private String getCompilerModeName() {
        switch (compilerMode) {
            case 0: return "Basic Interpreter";
            case 1: return "Advanced Interpreter";
            case 2: return "Real Java Compiler";
            default: return "Unknown";
        }
    }
    
    private void handleCompilerResult(Object result, String mode) {
        if (compilerMode == 0) {
            // Basic Interpreter
            JavaCompiler.CompileResult basicResult = (JavaCompiler.CompileResult) result;
            if (basicResult.isSuccess()) {
                String output = basicResult.getOutput();
                if (output.isEmpty()) {
                    output = "Program executed successfully! (No output)";
                }
                showOutputDialog(mode + " - Success", output);
            } else {
                showOutputDialog(mode + " - Error", basicResult.getMessage());
            }
        } else if (compilerMode == 1) {
            // Advanced Interpreter
            AdvancedJavaInterpreter.CompileResult advResult = (AdvancedJavaInterpreter.CompileResult) result;
            if (advResult.isSuccess()) {
                String output = advResult.getOutput();
                if (output.isEmpty()) {
                    output = "Program executed successfully! (No output)";
                }
                showOutputDialog(mode + " - Success", output);
            } else {
                showOutputDialog(mode + " - Error", advResult.getMessage());
            }
        } else if (compilerMode == 2) {
            // Real Java Compiler - can return CompileResult or String
            if (result instanceof AdvancedJavaInterpreter.CompileResult) {
                AdvancedJavaInterpreter.CompileResult compileResult = (AdvancedJavaInterpreter.CompileResult) result;
                if (compileResult.isSuccess()) {
                    String output = compileResult.getOutput();
                    if (output.isEmpty()) {
                        output = "Program compiled and executed successfully! (No output)";
                    }
                    showOutputDialog(mode + " - Success", output);
                } else {
                    showOutputDialog(mode + " - Error", compileResult.getMessage());
                }
            } else if (result instanceof String) {
                String output = (String) result;
                if (output.startsWith("Compilation Error:") || output.startsWith("Runtime Error:") || output.startsWith("Error:")) {
                    showOutputDialog(mode + " - Error", output);
                } else {
                    if (output.isEmpty()) {
                        output = "Program compiled and executed successfully! (No output)";
                    }
                    showOutputDialog(mode + " - Success", output);
                }
            } else {
                showOutputDialog(mode + " - Result", result.toString());
            }
        }
    }

    private void showOutputDialog(String title, String output) {
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(title)
               .setMessage(output)
               .setPositiveButton("OK", null)
               .show();
    }

    private void clearCode() {
        codeEditor.setText("");
        Toast.makeText(this, "Editor cleared", Toast.LENGTH_SHORT).show();
    }

    private void saveCode() {
        currentCode = codeEditor.getText().toString();
        // In a real app, you would save to file or database
        Toast.makeText(this, "Code saved locally", Toast.LENGTH_SHORT).show();
    }

    private void loadTemplate() {
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(this);
        
        String[] templates = {
            "Hello World",
            "Variables Example", 
            "Loop Example",
            "Method Example",
            "Class & Objects (Advanced)",
            "Inheritance (Advanced)",
            "Arrays (Advanced)"
        };
        
        builder.setTitle("Select Template")
               .setItems(templates, (dialog, which) -> {
                   loadSelectedTemplate(which);
               })
               .show();
    }

    private void loadSelectedTemplate(int templateIndex) {
        String template;
        
        switch (templateIndex) {
            case 0: // Hello World
                template = DEFAULT_CODE;
                break;
            case 1: // Variables
                template = "public class Main {\n" +
                          "    public static void main(String[] args) {\n" +
                          "        int age = 25;\n" +
                          "        String name = \"JavaBuddy\";\n" +
                          "        double price = 19.99;\n" +
                          "        boolean isActive = true;\n" +
                          "        \n" +
                          "        System.out.println(\"Name: \" + name);\n" +
                          "        System.out.println(\"Age: \" + age);\n" +
                          "        System.out.println(\"Price: $\" + price);\n" +
                          "        System.out.println(\"Active: \" + isActive);\n" +
                          "    }\n" +
                          "}";
                break;
            case 2: // Loop
                template = "public class Main {\n" +
                          "    public static void main(String[] args) {\n" +
                          "        // For loop example\n" +
                          "        for (int i = 1; i <= 5; i++) {\n" +
                          "            System.out.println(\"Count: \" + i);\n" +
                          "        }\n" +
                          "        \n" +
                          "        // While loop example\n" +
                          "        int j = 1;\n" +
                          "        while (j <= 3) {\n" +
                          "            System.out.println(\"While: \" + j);\n" +
                          "            j++;\n" +
                          "        }\n" +
                          "    }\n" +
                          "}";
                break;
            case 3: // Method
                template = "public class Main {\n" +
                          "    public static void main(String[] args) {\n" +
                          "        int result = addNumbers(5, 3);\n" +
                          "        System.out.println(\"Result: \" + result);\n" +
                          "        \n" +
                          "        greetUser(\"JavaBuddy\");\n" +
                          "    }\n" +
                          "    \n" +
                          "    public static int addNumbers(int a, int b) {\n" +
                          "        return a + b;\n" +
                          "    }\n" +
                          "    \n" +
                          "    public static void greetUser(String name) {\n" +
                          "        System.out.println(\"Hello, \" + name + \"!\");\n" +
                          "    }\n" +
                          "}";
                break;
            case 4: // Class & Objects
                template = "public class Person {\n" +
                          "    private String name;\n" +
                          "    private int age;\n" +
                          "    \n" +
                          "    public Person(String name, int age) {\n" +
                          "        this.name = name;\n" +
                          "        this.age = age;\n" +
                          "    }\n" +
                          "    \n" +
                          "    public void introduce() {\n" +
                          "        System.out.println(\"Hi, I'm \" + name + \" and I'm \" + age + \" years old.\");\n" +
                          "    }\n" +
                          "    \n" +
                          "    public static void main(String[] args) {\n" +
                          "        Person person = new Person(\"Alice\", 25);\n" +
                          "        person.introduce();\n" +
                          "    }\n" +
                          "}";
                break;
            case 5: // Inheritance
                template = "class Animal {\n" +
                          "    protected String name;\n" +
                          "    \n" +
                          "    public Animal(String name) {\n" +
                          "        this.name = name;\n" +
                          "    }\n" +
                          "    \n" +
                          "    public void speak() {\n" +
                          "        System.out.println(name + \" makes a sound\");\n" +
                          "    }\n" +
                          "}\n" +
                          "\n" +
                          "class Dog extends Animal {\n" +
                          "    public Dog(String name) {\n" +
                          "        super(name);\n" +
                          "    }\n" +
                          "    \n" +
                          "    public void speak() {\n" +
                          "        System.out.println(name + \" barks!\");\n" +
                          "    }\n" +
                          "}\n" +
                          "\n" +
                          "public class Main {\n" +
                          "    public static void main(String[] args) {\n" +
                          "        Animal animal = new Animal(\"Generic\");\n" +
                          "        Dog dog = new Dog(\"Buddy\");\n" +
                          "        \n" +
                          "        animal.speak();\n" +
                          "        dog.speak();\n" +
                          "    }\n" +
                          "}";
                break;
            case 6: // Arrays
                template = "public class Main {\n" +
                          "    public static void main(String[] args) {\n" +
                          "        int[] numbers = {1, 2, 3, 4, 5};\n" +
                          "        String[] names = new String[3];\n" +
                          "        names[0] = \"Alice\";\n" +
                          "        names[1] = \"Bob\";\n" +
                          "        names[2] = \"Charlie\";\n" +
                          "        \n" +
                          "        System.out.println(\"Numbers:\");\n" +
                          "        for (int i = 0; i < numbers.length; i++) {\n" +
                          "            System.out.println(\"Number \" + i + \": \" + numbers[i]);\n" +
                          "        }\n" +
                          "        \n" +
                          "        System.out.println(\"Names:\");\n" +
                          "        for (String name : names) {\n" +
                          "            System.out.println(\"Name: \" + name);\n" +
                          "        }\n" +
                          "    }\n" +
                          "}";
                break;
            default:
                template = DEFAULT_CODE;
        }
        
        codeEditor.setText(template);
        Toast.makeText(this, "Template loaded", Toast.LENGTH_SHORT).show();
    }
    
    private void toggleCompilerMode() {
        compilerMode = (compilerMode + 1) % 3; // Cycle through 0, 1, 2
        String mode = getCompilerModeName();
        
        Toast.makeText(this, "Switched to " + mode, Toast.LENGTH_LONG).show();
        
        // Update menu item title
        invalidateOptionsMenu();
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem toggleItem = menu.findItem(R.id.action_toggle_mode);
        if (toggleItem != null) {
            String currentMode = getCompilerModeName();
            toggleItem.setTitle("Current: " + currentMode + " (Tap to switch)");
        }
        return super.onPrepareOptionsMenu(menu);
    }
}