# PERSON 3 - CODE EXPLANATION SCRIPT
## 💻 IDE, Practice Problems & Timed Test Code Walkthrough

---

## 🎯 YOUR TASK
Explain the **CODE** behind the Java IDE (code editor and execution), Practice Problems, and Timed Test features.

---

## 📝 PART 1: IDE ACTIVITY CODE

### File: `IDEActivity.java`

**What to Say:**
> "The IDE is the most complex feature. It includes a code editor with syntax highlighting and three different methods to compile and run Java code."

---

### KEY SECTIONS:

#### A) CLASS VARIABLES

```java
private CodeEditor codeEditor;
private String currentCode = "";
private JavaCompiler javaCompiler;
private AdvancedJavaInterpreter advancedInterpreter;
private RealJavaCompiler realCompiler;
private int compilerMode = 2; // 0=Basic, 1=Advanced, 2=Real Compiler
```

**Explain:**
- **`CodeEditor`**: "This is a third-party library (Sora Code Editor) that provides syntax highlighting and line numbers."
- **Three compiler types**: "We have three ways to run Java code, each with different capabilities."
- **`compilerMode`**: "Tracks which compiler is currently active (default = 2, Real Compiler)."

---

#### B) DEFAULT_CODE Template

```java
private final String DEFAULT_CODE = 
    "public class Main {\n" +
    "    public static void main(String[] args) {\n" +
    "        // Write your Java code here\n" +
    "        System.out.println(\"Hello, JavaBuddy!\");\n" +
    "    }\n" +
    "}";
```

**Explain:**
- "This is the starter template that loads when you open the IDE."
- "It's a complete Java program with main method ready to run."
- **`\n`**: "Creates newlines in the string."

---

#### C) onCreate METHOD

```java
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
```

**Explain:**
- "Creates instances of all three compilers when activity starts."
- "Then sets up the views and editor."

---

#### D) setupCodeEditor METHOD

```java
private void setupCodeEditor() {
    // Set Java language for syntax highlighting
    codeEditor.setTypefaceText(android.graphics.Typeface.MONOSPACE);
    codeEditor.setEditorLanguage(new JavaLanguage());
    
    // Set color scheme
    codeEditor.setColorScheme(new EditorColorScheme());
    
    // Set default code
    codeEditor.setText(DEFAULT_CODE);
    
    // Configure editor settings
    codeEditor.setTextSize(14);
    codeEditor.setLineSpacing(2f, 1.0f);
}
```

**Explain (Line by Line):**

1. **`setTypefaceText(Typeface.MONOSPACE)`**: "Uses monospace font (all characters same width - like code editors should)."

2. **`setEditorLanguage(new JavaLanguage())`**: "Tells the editor this is Java code, enabling Java syntax highlighting."

3. **`setColorScheme(new EditorColorScheme())`**: "Sets the color theme for keywords, strings, comments, etc."

4. **`setText(DEFAULT_CODE)`**: "Loads the default template into editor."

5. **`setTextSize(14)`**: "Sets font size to 14."

6. **`setLineSpacing(2f, 1.0f)`**: "Adds spacing between lines for readability."

---

#### E) runCode METHOD (MOST IMPORTANT!)

```java
private void runCode() {
    currentCode = codeEditor.getText().toString();
    
    if (currentCode.trim().isEmpty()) {
        Toast.makeText(this, "Please write some code first!", 
            Toast.LENGTH_SHORT).show();
        return;
    }
    
    final String mode = getCompilerModeName();
    Toast.makeText(this, "Compiling with " + mode + "...", 
        Toast.LENGTH_SHORT).show();
    
    // Run compilation in background thread
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
                Toast.makeText(IDEActivity.this, 
                    "Error: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
            });
        }
    }).start();
}
```

**Explain (Step by Step):**

1. **Get code from editor**:
   ```java
   currentCode = codeEditor.getText().toString();
   ```
   "Extracts all the text user wrote in the editor."

2. **Validation check**:
   ```java
   if (currentCode.trim().isEmpty()) {
       Toast.makeText(this, "Please write some code first!", ...).show();
       return;
   }
   ```
   - **`trim()`**: "Removes whitespace from beginning and end."
   - **`isEmpty()`**: "Checks if there's no code."
   - "If empty, shows error message and exits method with `return`."

3. **Show loading message**:
   ```java
   final String mode = getCompilerModeName();
   Toast.makeText(this, "Compiling with " + mode + "...", ...).show();
   ```
   "Tells user which compiler is being used."

4. **Background thread creation**:
   ```java
   new Thread(() -> {
       // ... compilation code ...
   }).start();
   ```
   - "Creates and starts a new background thread."
   - "Why? Compilation is slow, we don't want to freeze the UI."

5. **Switch statement for compiler selection**:
   ```java
   switch (compilerMode) {
       case 0: 
           result = javaCompiler.compileAndRun(currentCode);
           break;
       case 1: 
           result = advancedInterpreter.compileAndRun(currentCode);
           break;
       case 2: 
           result = realCompiler.compileAndRun(currentCode);
           break;
   }
   ```
   - "Checks which compiler mode is active."
   - "Calls the appropriate compiler's `compileAndRun` method."
   - **`break`**: "Exits the switch statement after executing case."

6. **Return to main thread**:
   ```java
   final Object finalResult = result;
   runOnUiThread(() -> {
       handleCompilerResult(finalResult, mode);
   });
   ```
   - **`final Object finalResult = result`**: "Copies result to final variable (required for lambda)."
   - **`runOnUiThread`**: "Switches back to main thread to update UI."
   - **`handleCompilerResult`**: "Processes and displays the result."

7. **Error handling**:
   ```java
   catch (Exception e) {
       runOnUiThread(() -> {
           Toast.makeText(IDEActivity.this, 
               "Error: " + e.getMessage(), 
               Toast.LENGTH_LONG).show();
       });
   }
   ```
   "If anything goes wrong, catches the error and shows user-friendly message."

---

#### F) handleCompilerResult METHOD

```java
private void handleCompilerResult(Object result, String mode) {
    if (result instanceof RealJavaCompiler.CompilationResult) {
        RealJavaCompiler.CompilationResult compResult = 
            (RealJavaCompiler.CompilationResult) result;
        
        if (compResult.success) {
            showResultDialog("Success", 
                "Compilation successful!\n\nOutput:\n" + compResult.output);
        } else {
            showResultDialog("Compilation Error", 
                "Compilation failed!\n\nErrors:\n" + compResult.errorOutput);
        }
    } else if (result != null) {
        showResultDialog("Output", result.toString());
    } else {
        showResultDialog("Error", "Compilation failed!");
    }
}
```

**Explain:**
- **`instanceof`**: "Checks if result is of a specific type."
- **If CompilationResult**: "Checks success field to determine if it worked."
- **If success**: "Shows output in dialog."
- **If failed**: "Shows compilation errors."
- **`showResultDialog`**: "Displays result in a popup dialog."

---

#### G) loadTemplate METHOD (Example Programs)

```java
private void loadTemplate() {
    final String[] templates = {
        "Hello World",
        "Variables",
        "If-Else",
        "For Loop",
        "While Loop",
        "Calculator"
    };
    
    new AlertDialog.Builder(this)
        .setTitle("Choose Example")
        .setItems(templates, (dialog, which) -> {
            String code = getTemplateCode(templates[which]);
            codeEditor.setText(code);
            Toast.makeText(this, "Loaded: " + templates[which], 
                Toast.LENGTH_SHORT).show();
        })
        .show();
}
```

**Explain:**
- **`AlertDialog.Builder`**: "Creates a popup dialog."
- **`setTitle("Choose Example")`**: "Sets the dialog title."
- **`setItems(templates, ...)`**: "Creates a list of clickable items."
- **Lambda `(dialog, which) -> {...}`**: "When user clicks an item:"
  - **`which`**: "Is the index of clicked item (0-5)."
  - **`getTemplateCode(templates[which])`**: "Gets the code for that example."
  - **`codeEditor.setText(code)`**: "Loads the example code into editor."
- **`.show()`**: "Actually displays the dialog."

---

#### H) toggleCompilerMode METHOD

```java
private void toggleCompilerMode() {
    compilerMode = (compilerMode + 1) % 3;
    String mode = getCompilerModeName();
    Toast.makeText(this, "Switched to: " + mode, 
        Toast.LENGTH_SHORT).show();
}

private String getCompilerModeName() {
    switch (compilerMode) {
        case 0: return "Basic Interpreter";
        case 1: return "Advanced Interpreter";
        case 2: return "Real Java Compiler";
        default: return "Unknown";
    }
}
```

**Explain:**
- **`(compilerMode + 1) % 3`**: "Cycles through 0, 1, 2, then back to 0."
  - If current is 0: (0+1) % 3 = 1
  - If current is 1: (1+1) % 3 = 2
  - If current is 2: (2+1) % 3 = 0
- **`getCompilerModeName`**: "Returns human-readable name for current mode."

---

## 📝 PART 2: PRACTICE ACTIVITY CODE

### File: `PracticeActivity.java` (Similar to LessonActivity)

**What to Say:**
> "PracticeActivity works similarly to LessonActivity - it uses RecyclerView with an adapter to display a list of practice problems."

---

### KEY CONCEPTS:

#### A) Loading Practice Problems

```java
private void loadProblems() {
    executor.execute(() -> {
        List<PracticeProblem> problems = 
            database.practiceProblemDao().getAllProblems();
        
        runOnUiThread(() -> {
            problemList.clear();
            problemList.addAll(problems);
            adapter.notifyDataSetChanged();
        });
    });
}
```

**Explain:**
- "Same pattern as LessonActivity: background thread for database, main thread for UI update."
- **`practiceProblemDao()`**: "The DAO for PracticeProblem table."
- **`getAllProblems()`**: "Gets all practice problems from database."

---

#### B) Problem Click Handling

```java
@Override
public void onProblemClick(PracticeProblem problem) {
    Intent intent = new Intent(this, ProblemSolvingActivity.class);
    intent.putExtra("problem_id", problem.getId());
    intent.putExtra("problem_title", problem.getTitle());
    intent.putExtra("problem_description", problem.getDescription());
    intent.putExtra("hints", problem.getHints());
    startActivity(intent);
}
```

**Explain:**
- "When user taps a problem, opens ProblemSolvingActivity."
- "Passes all problem details via Intent extras."

---

## 📝 PART 3: PROBLEM SOLVING ACTIVITY CODE

### File: `ProblemSolvingActivity.java`

**What to Say:**
> "This activity combines the code editor (like IDE) with a hint system and solution checking."

---

### KEY SECTIONS:

#### A) Showing Hints

```java
private void showHint() {
    if (hints != null && !hints.isEmpty()) {
        String[] hintArray = hints.split("\\|");
        
        if (currentHintIndex < hintArray.length) {
            String hint = hintArray[currentHintIndex];
            hintTextView.setText("Hint " + (currentHintIndex + 1) + 
                ": " + hint);
            hintTextView.setVisibility(View.VISIBLE);
            currentHintIndex++;
        } else {
            Toast.makeText(this, "No more hints!", 
                Toast.LENGTH_SHORT).show();
        }
    }
}
```

**Explain:**
- **`split("\\|")`**: "Splits hints string into array using | as separator."
  - Example: "Use a loop|Try for loop|Start at 0" → ["Use a loop", "Try for loop", "Start at 0"]
- **`currentHintIndex`**: "Tracks which hint we're on."
- "Shows hints one at a time, not all at once."

---

#### B) Checking Solution

```java
private void checkSolution() {
    String userCode = codeEditor.getText().toString();
    
    // Run user's code
    new Thread(() -> {
        try {
            Object result = realCompiler.compileAndRun(userCode);
            
            runOnUiThread(() -> {
                if (result != null) {
                    // Compare with expected output
                    boolean correct = checkOutput(result.toString());
                    if (correct) {
                        showSuccessDialog();
                    } else {
                        showTryAgainDialog();
                    }
                }
            });
        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Compilation Error: " + 
                    e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
    }).start();
}
```

**Explain:**
- "Compiles and runs user's code in background."
- "Compares output with expected output."
- "Shows success or try-again dialog based on result."

---

## 📝 PART 4: TIMED TEST ACTIVITY CODE

### File: `TimedTestActivity.java`

**What to Say:**
> "TimedTestActivity is the configuration screen before starting a timed quiz. It lets users customize settings like time limit and number of questions."

---

### KEY SECTIONS:

#### A) Settings UI

```java
private Spinner questionCountSpinner, timeLimitSpinner;
private Button startTestButton;

private void setupSpinners() {
    // Question count options
    String[] questionCounts = {"5", "10", "15", "20"};
    ArrayAdapter<String> questionAdapter = new ArrayAdapter<>(
        this, android.R.layout.simple_spinner_item, questionCounts);
    questionAdapter.setDropDownViewResource(
        android.R.layout.simple_spinner_dropdown_item);
    questionCountSpinner.setAdapter(questionAdapter);
    
    // Time limit options
    String[] timeLimits = {"2 min", "5 min", "10 min", "15 min"};
    ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(
        this, android.R.layout.simple_spinner_item, timeLimits);
    timeAdapter.setDropDownViewResource(
        android.R.layout.simple_spinner_dropdown_item);
    timeLimitSpinner.setAdapter(timeAdapter);
}
```

**Explain:**
- **`Spinner`**: "Android's dropdown menu component."
- **`ArrayAdapter`**: "Connects array of strings to Spinner."
- **`setDropDownViewResource`**: "Sets the layout for dropdown items."
- "Users can select from predefined options."

---

#### B) Starting Timed Test

```java
private void startTimedTest() {
    // Get selected values
    int questionCount = Integer.parseInt(
        questionCountSpinner.getSelectedItem().toString());
    
    String timeLimitStr = timeLimitSpinner.getSelectedItem().toString();
    int timeLimitMinutes = Integer.parseInt(
        timeLimitStr.split(" ")[0]); // Extract number from "5 min"
    int timeLimitSeconds = timeLimitMinutes * 60;
    
    // Start QuizActivity in timed mode
    Intent intent = new Intent(this, QuizActivity.class);
    intent.putExtra("EXTRA_TIMED_MODE", true);
    intent.putExtra("EXTRA_TIMED_QUESTION_COUNT", questionCount);
    intent.putExtra("EXTRA_TIME_LIMIT_SECONDS", timeLimitSeconds);
    startActivity(intent);
    finish();
}
```

**Explain:**
- **`getSelectedItem().toString()`**: "Gets the selected value from Spinner."
- **`Integer.parseInt(...)`**: "Converts string to integer."
- **`split(" ")[0]`**: "Splits '5 min' and takes '5'."
- **`timeLimitMinutes * 60`**: "Converts minutes to seconds."
- "Passes all settings to QuizActivity via Intent."

---

## 🎤 PRESENTATION STRUCTURE

### Opening (30 seconds):
"I'll explain the code behind the IDE, practice problems, and timed test features."

### Part 1: IDE Activity (4-5 minutes):
- Show CodeEditor setup
- Explain three compiler types
- Detail runCode method with threading
- Show example template loading
- Demonstrate compiler mode toggling

### Part 2: Practice & Problem Solving (2 minutes):
- Show problem loading (similar to lessons)
- Explain hint system with split
- Show solution checking

### Part 3: Timed Test (1-2 minutes):
- Show Spinner setup for settings
- Explain value extraction and conversion
- Show how settings pass to QuizActivity

### Closing (30 seconds):
"The IDE is the most complex with three compilation methods. Practice and Timed Test reuse quiz logic with added features like hints and timers."

---

## ❓ ANTICIPATED QUESTIONS

**Q: What's the difference between the three compilers?**
**A:** "Basic = simple interpreter, Advanced = handles more features, Real = actual Java compilation using javax.tools."

**Q: Why use a third-party code editor?**
**A:** "Building syntax highlighting from scratch is very complex. Sora Code Editor provides it ready-made."

**Q: How does hint system work?**
**A:** "Hints are stored as a single string separated by | characters. We split on | to create an array and show one at a time."

**Q: What happens when timer runs out?**
**A:** "QuizActivity's CountDownTimer calls onFinish(), which triggers finishQuiz() automatically."

**Q: Can users save their IDE code?**
**A:** "Currently no persistence - code is lost when activity closes. Could be added using SharedPreferences or files."

---

## ✅ DEMONSTRATION CHECKLIST

- [ ] Show IDEActivity.java code
- [ ] Run app and open IDE
- [ ] Write and run simple code
- [ ] Load example template
- [ ] Toggle compiler modes
- [ ] Show PracticeActivity code
- [ ] Open a practice problem
- [ ] Use hint system
- [ ] Show TimedTestActivity code
- [ ] Configure and start timed test

---

**The IDE code is impressive - showcase the threading and compiler selection! 💪**
