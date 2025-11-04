# NON-TECH PERSON 3: CODE EXPLANATION SCRIPT
## 💻 IDE, Practice & Timed Tests - Code Implementation

---

## 🎯 YOUR CODE EXPLANATION MISSION

Walk through **actual code files** that implement the code editor, code execution, practice problems, and timed test countdown.

---

## 📝 SUGGESTED SCRIPT (Use Android Studio while presenting)

### OPENING (30 seconds)
"I'll explain how the code implements the hands-on practice features - the IDE, practice problems, and timed tests. These are the most interactive parts of the app."

---

## SECTION 1: JAVA IDE CODE EDITOR (3 minutes)

### File to Open: `activity_ide.xml`

**Script:**

"First, let's look at the IDE layout. This is in `activity_ide.xml`."

**Point to this XML:**
```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    
    <!-- Code Editor Section -->
    <TextView
        android:text="Code Editor"
        android:textStyle="bold" />
    
    <EditText
        android:id="@+id/code_editor"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:gravity="top|start"
        android:fontFamily="monospace"
        android:textSize="14sp"
        android:background="@color/code_background"
        android:textColor="@color/code_text"
        android:hint="Write your Java code here..."
        android:padding="8dp"
        android:inputType="textMultiLine|textNoSuggestions"
        android:scrollbars="vertical" />
    
    <!-- Control Buttons -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">
        
        <Button
            android:id="@+id/run_button"
            android:text="▶ Run Code"
            android:backgroundTint="@color/primary" />
        
        <Button
            android:id="@+id/clear_button"
            android:text="🧹 Clear" />
        
        <Button
            android:id="@+id/examples_button"
            android:text="📋 Examples" />
    </LinearLayout>
    
    <!-- Output Console -->
    <TextView
        android:text="Output Console"
        android:textStyle="bold" />
    
    <TextView
        android:id="@+id/output_console"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="@color/console_background"
        android:textColor="@color/console_text"
        android:fontFamily="monospace"
        android:padding="8dp"
        android:scrollbars="vertical" />
        
</LinearLayout>
```

**Explain:**

"The layout has three main sections:

**1. Code Editor (EditText):**
- **`android:fontFamily='monospace'`** - Uses a fixed-width font like professional code editors
- **`android:inputType='textMultiLine|textNoSuggestions'`** - Allows multiple lines, disables autocorrect
- **`android:gravity='top|start'`** - Text starts at top-left
- **`android:scrollbars='vertical'`** - Allows scrolling for longer code
- **`layout_weight='1'`** - Takes up available space

**2. Control Buttons:**
- Three buttons arranged horizontally: Run, Clear, Examples

**3. Output Console (TextView):**
- **`fontFamily='monospace'`** - Matches the editor font
- **`layout_weight='1'`** - Also takes available space
- Shows results after code runs"

### File to Open: `IDEActivity.java`

**Script:**

"Now let's see the Java code that makes the IDE work."

**Point to this code:**
```java
public class IDEActivity extends AppCompatActivity {
    
    private EditText codeEditor;
    private TextView outputConsole;
    private Button runButton, clearButton, examplesButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ide);
        
        // Initialize views
        codeEditor = findViewById(R.id.code_editor);
        outputConsole = findViewById(R.id.output_console);
        runButton = findViewById(R.id.run_button);
        clearButton = findViewById(R.id.clear_button);
        examplesButton = findViewById(R.id.examples_button);
        
        // Setup button clicks
        setupButtons();
    }
    
    private void setupButtons() {
        // Run button
        runButton.setOnClickListener(v -> {
            String code = codeEditor.getText().toString();
            if (validateCode(code)) {
                executeCode(code);
            }
        });
        
        // Clear button
        clearButton.setOnClickListener(v -> {
            codeEditor.setText("");
            outputConsole.setText("");
        });
        
        // Examples button
        examplesButton.setOnClickListener(v -> {
            showExamplesDialog();
        });
    }
    
    private boolean validateCode(String code) {
        if (code.trim().isEmpty()) {
            Toast.makeText(this, "Please enter code first", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!code.contains("class")) {
            Toast.makeText(this, "Code must contain a class definition", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!code.contains("main")) {
            Toast.makeText(this, "Code must contain a main method", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
}
```

**Explain:**

"Here's how the buttons work:

**1. Run Button:**
- **`codeEditor.getText().toString()`** - Gets all the code from the editor as a String
- **`validateCode(code)`** - Checks if code is valid before running
- **`executeCode(code)`** - Runs the code (I'll explain this next)

**2. Clear Button:**
- **`codeEditor.setText('')`** - Empties the editor
- **`outputConsole.setText('')`** - Empties the output console
- Simple but useful for starting fresh

**3. Validate Code:**
Three checks:
- **`code.trim().isEmpty()`** - Is there any code at all?
- **`!code.contains('class')`** - Does it have a class definition?
- **`!code.contains('main')`** - Does it have a main method?
- If any check fails, shows a Toast message and returns false

These basic checks prevent common errors before execution."

---

## SECTION 2: CODE EXECUTION (3-4 minutes)

### Approach 1: Online API (Simpler to explain)

**Script:**

"For code execution, we use an online API called JDoodle. Here's the code:"

**Point to this code:**
```java
private void executeCode(String code) {
    // Show loading message
    outputConsole.setText("> Compiling and running...\n");
    runButton.setEnabled(false);
    
    // Create new thread for network operation
    new Thread(() -> {
        try {
            // Build JSON request
            JSONObject request = new JSONObject();
            request.put("script", code);
            request.put("language", "java");
            request.put("versionIndex", "3");
            request.put("clientId", "YOUR_CLIENT_ID");
            request.put("clientSecret", "YOUR_CLIENT_SECRET");
            
            // Create HTTP request
            OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
            
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                request.toString()
            );
            
            Request apiRequest = new Request.Builder()
                .url("https://api.jdoodle.com/v1/execute")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
            
            // Send request
            Response response = client.newCall(apiRequest).execute();
            
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                
                String output = jsonResponse.getString("output");
                String memory = jsonResponse.optString("memory", "N/A");
                String cpuTime = jsonResponse.optString("cpuTime", "N/A");
                
                // Update UI on main thread
                runOnUiThread(() -> {
                    outputConsole.setText("> Execution completed\n\n");
                    outputConsole.append("Output:\n");
                    outputConsole.append(output);
                    outputConsole.append("\n\n> Memory: " + memory);
                    outputConsole.append("\n> CPU Time: " + cpuTime);
                    runButton.setEnabled(true);
                });
            } else {
                // Error response
                runOnUiThread(() -> {
                    outputConsole.setText("> Error: " + response.message());
                    runButton.setEnabled(true);
                });
            }
            
        } catch (Exception e) {
            runOnUiThread(() -> {
                outputConsole.setText("> Error: " + e.getMessage());
                runButton.setEnabled(true);
            });
        }
    }).start();
}
```

**Explain:**

"Let me break down the code execution:

**1. Setup:**
- **`outputConsole.setText('> Compiling and running...')`** - Shows loading message
- **`runButton.setEnabled(false)`** - Disables Run button to prevent multiple clicks
- **`new Thread(() -> { ... }).start()`** - Creates a background thread (network operations must not run on UI thread)

**2. Building JSON Request:**
- **`new JSONObject()`** - Creates a JSON object to send to the API
- **`put('script', code)`** - Adds the user's code
- **`put('language', 'java')`** - Tells API it's Java code
- **`put('versionIndex', '3')`** - Specifies Java version (Java 17)
- **Client ID and Secret** - API authentication

**3. Creating HTTP Request:**
- **`OkHttpClient`** - Library for making HTTP requests
- **`connectTimeout(30, TimeUnit.SECONDS)`** - Wait max 30 seconds to connect
- **`readTimeout(30, TimeUnit.SECONDS)`** - Wait max 30 seconds for response
- **`RequestBody.create(...)`** - Wraps the JSON as HTTP body
- **`MediaType.parse('application/json')`** - Tells server we're sending JSON
- **`Request.Builder()`** - Builds the HTTP POST request
- **`.url('https://api.jdoodle.com/v1/execute')`** - API endpoint
- **`.post(body)`** - Uses POST method
- **`.addHeader('Content-Type', 'application/json')`** - Sets content type header

**4. Sending Request:**
- **`client.newCall(apiRequest).execute()`** - Sends the request and waits for response
- **`if (response.isSuccessful())`** - Checks if HTTP status is 200 OK

**5. Parsing Response:**
- **`response.body().string()`** - Gets the response text
- **`new JSONObject(responseBody)`** - Parses it as JSON
- **`jsonResponse.getString('output')`** - Extracts the program output
- **`optString('memory', 'N/A')`** - Gets memory usage (or 'N/A' if not available)

**6. Updating UI:**
- **`runOnUiThread(() -> { ... })`** - Switches back to main thread (only main thread can update UI)
- **`outputConsole.setText(...)`** - Displays the results
- **`runButton.setEnabled(true)`** - Re-enables the Run button

**Why Thread?** Android doesn't allow network operations on the main UI thread because they might freeze the app. We use a background thread, then use `runOnUiThread` to update the UI with results.

**Error Handling:** The try-catch catches any errors (network issues, JSON parsing errors, etc.) and displays them in the output console."

---

## SECTION 3: EXAMPLE PROGRAMS (2 minutes)

**Script:**

"Users can load pre-written examples. Here's how:"

**Point to this code:**
```java
private void showExamplesDialog() {
    String[] exampleNames = {
        "Hello World",
        "Variables Demo",
        "If-Else Example",
        "For Loop",
        "While Loop",
        "Calculator"
    };
    
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Choose an Example");
    builder.setItems(exampleNames, (dialog, which) -> {
        String code = getExampleCode(exampleNames[which]);
        codeEditor.setText(code);
        Toast.makeText(this, "Example loaded", Toast.LENGTH_SHORT).show();
    });
    builder.show();
}

private String getExampleCode(String exampleName) {
    switch (exampleName) {
        case "Hello World":
            return "public class Main {\n" +
                   "    public static void main(String[] args) {\n" +
                   "        System.out.println(\"Hello, World!\");\n" +
                   "    }\n" +
                   "}";
        
        case "Variables Demo":
            return "public class Main {\n" +
                   "    public static void main(String[] args) {\n" +
                   "        int age = 25;\n" +
                   "        String name = \"John\";\n" +
                   "        double height = 5.9;\n" +
                   "        \n" +
                   "        System.out.println(\"Name: \" + name);\n" +
                   "        System.out.println(\"Age: \" + age);\n" +
                   "        System.out.println(\"Height: \" + height);\n" +
                   "    }\n" +
                   "}";
        
        case "For Loop":
            return "public class Main {\n" +
                   "    public static void main(String[] args) {\n" +
                   "        for (int i = 1; i <= 5; i++) {\n" +
                   "            System.out.println(\"Count: \" + i);\n" +
                   "        }\n" +
                   "    }\n" +
                   "}";
        
        // More examples...
        
        default:
            return "";
    }
}
```

**Explain:**

"**Dialog Creation:**
- **`String[] exampleNames`** - Array of example names
- **`AlertDialog.Builder`** - Creates a popup dialog
- **`.setTitle('Choose an Example')`** - Sets dialog title
- **`.setItems(exampleNames, (dialog, which) -> { ... })`** - Shows the array as a list
- **`which`** - The index of the item clicked (0-5)

**Loading Example:**
- **`exampleNames[which]`** - Gets the name of the clicked example
- **`getExampleCode(exampleNames[which])`** - Calls method to get the code
- **`codeEditor.setText(code)`** - Puts the example code into the editor

**getExampleCode Method:**
- **`switch (exampleName)`** - Checks which example was requested
- **`return '...'`** - Returns the corresponding code as a String
- Each example is a multi-line String with proper Java syntax
- **`\n`** - Newline character to format the code properly

When users click an example, that code instantly appears in the editor, ready to run."

---

## SECTION 4: PRACTICE PROBLEMS CODE (2-3 minutes)

### File to Open: `PracticeActivity.java`

**Script:**

"Practice problems work similarly to lessons. Let me show you."

**Point to this code:**
```java
public class PracticeActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private PracticeAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        
        recyclerView = findViewById(R.id.practice_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new PracticeAdapter(this);
        recyclerView.setAdapter(adapter);
        
        loadProblems();
    }
    
    private void loadProblems() {
        // Load practice problems from database or assets
        PracticeRepository repository = new PracticeRepository(getApplication());
        repository.getAllProblems().observe(this, problems -> {
            if (problems != null) {
                adapter.setProblems(problems);
            }
        });
    }
}
```

**Explain:**

"This is similar to LessonActivity:
- RecyclerView displays practice problems as cards
- Repository loads problems from database
- Adapter handles display
- When a card is clicked, opens `ProblemSolvingActivity`"

### File to Open: `ProblemSolvingActivity.java`

**Script:**

"When a problem is clicked, this activity opens with the problem and a code editor."

**Point to this code:**
```java
public class ProblemSolvingActivity extends AppCompatActivity {
    
    private TextView problemTextView;
    private EditText codeEditor;
    private Button hintButton, checkButton;
    private TextView hintTextView;
    
    private String expectedOutput;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_solving);
        
        // Load problem
        int problemId = getIntent().getIntExtra("problem_id", 0);
        loadProblem(problemId);
        
        // Setup buttons
        hintButton.setOnClickListener(v -> {
            hintTextView.setVisibility(View.VISIBLE);
        });
        
        checkButton.setOnClickListener(v -> {
            checkSolution();
        });
    }
    
    private void checkSolution() {
        String code = codeEditor.getText().toString();
        
        // Execute code and get output
        executeCodeAndCheck(code, output -> {
            if (output.trim().equals(expectedOutput.trim())) {
                // Correct solution
                showSuccessDialog();
                markProblemComplete();
            } else {
                // Wrong solution
                showErrorDialog("Output doesn't match expected result");
            }
        });
    }
    
    private void markProblemComplete() {
        // Save completion to database
        PracticeRepository repository = new PracticeRepository(getApplication());
        repository.markComplete(problemId);
    }
}
```

**Explain:**

"**Hint Button:**
- **`hintTextView.setVisibility(View.VISIBLE)`** - Simply shows the hidden hint TextView
- Initially, hint is hidden with `View.GONE`

**Check Solution:**
- Gets code from editor
- Executes the code (similar to IDE)
- **`output.trim().equals(expectedOutput.trim())`** - Compares actual output to expected output
- **`.trim()`** - Removes extra spaces/newlines for accurate comparison
- If match: Show success dialog and mark problem complete
- If mismatch: Show error message

**Marking Complete:**
- Calls repository method to save completion status in database
- Problem card then shows a checkmark or different color"

---

## SECTION 5: TIMED TEST CODE (3-4 minutes)

### File to Open: `TimedTestActivity.java`

**Script:**

"The timed test feature uses Android's CountDownTimer. Let me show you."

**Point to this code:**
```java
public class TimedTestActivity extends AppCompatActivity {
    
    private TextView timerTextView;
    private TextView questionTextView;
    private Button option1Button, option2Button, option3Button, option4Button;
    
    private CountDownTimer countDownTimer;
    private long timeRemainingInMillis;
    private int currentQuestionIndex = 0;
    private int score = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed_test);
        
        // Get test configuration
        int numQuestions = getIntent().getIntExtra("num_questions", 10);
        int timeLimit = getIntent().getIntExtra("time_limit", 10); // minutes
        
        // Convert minutes to milliseconds
        timeRemainingInMillis = timeLimit * 60 * 1000;
        
        // Load questions
        loadQuestions(numQuestions);
        
        // Start timer
        startTimer();
    }
    
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeRemainingInMillis, 1000) {
            
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemainingInMillis = millisUntilFinished;
                updateTimerDisplay();
            }
            
            @Override
            public void onFinish() {
                // Time's up!
                autoSubmitTest();
            }
        }.start();
    }
    
    private void updateTimerDisplay() {
        int minutes = (int) (timeRemainingInMillis / 1000) / 60;
        int seconds = (int) (timeRemainingInMillis / 1000) % 60;
        
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);
        
        // Change color when time is running out
        if (minutes == 0 && seconds <= 30) {
            timerTextView.setTextColor(Color.RED);
        }
    }
    
    private void autoSubmitTest() {
        countDownTimer.cancel();
        
        Toast.makeText(this, "Time's up! Test auto-submitted", Toast.LENGTH_LONG).show();
        
        // Calculate and show results
        showResults();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Prevent memory leak
        }
    }
}
```

**Explain:**

"Let me explain the timer implementation:

**1. Time Conversion:**
- **`timeLimit * 60 * 1000`** - Converts minutes to milliseconds
- Example: 10 minutes = 10 × 60 × 1000 = 600,000 milliseconds
- Milliseconds are used because Android timers work with milliseconds

**2. CountDownTimer:**
- **`new CountDownTimer(timeRemainingInMillis, 1000)`** - Creates timer
  - First parameter: Total time in milliseconds
  - Second parameter: Tick interval (1000 = 1 second)
- **`.start()`** - Starts the countdown

**3. onTick Method:**
- **Called every 1000 milliseconds (every second)**
- **`millisUntilFinished`** - How much time is left
- **`timeRemainingInMillis = millisUntilFinished`** - Store the remaining time
- **`updateTimerDisplay()`** - Updates the UI

**4. updateTimerDisplay Method:**
- **`(timeRemainingInMillis / 1000) / 60`** - Converts milliseconds to minutes
  - Example: 600000 / 1000 = 600 seconds, 600 / 60 = 10 minutes
- **`(timeRemainingInMillis / 1000) % 60`** - Gets remaining seconds
  - **`%`** is modulo operator - gives remainder after division
  - Example: 665 seconds % 60 = 5 seconds (11 minutes and 5 seconds)
- **`String.format('%02d:%02d', minutes, seconds)`** - Formats as 'MM:SS'
  - **`%02d`** means 'integer with at least 2 digits, pad with 0 if needed'
  - Example: 9 becomes 09, so we get '09:05' not '9:5'
- **Color change logic**: If less than 30 seconds, text turns red as a warning

**5. onFinish Method:**
- **Called when timer reaches 0:00**
- **`countDownTimer.cancel()`** - Stops the timer
- **`autoSubmitTest()`** - Automatically submits the test
- Shows 'Time's up!' message

**6. Memory Management:**
- **`onDestroy()`** - Called when activity is closed
- **`countDownTimer.cancel()`** - Must cancel timer to prevent memory leaks
- If you don't cancel, timer keeps running even after activity closes

**Flow:** User configures test → Timer starts → onTick() updates display every second → User answers questions → Either: User finishes early OR timer hits 0:00 → Test auto-submits → Show results"

---

## 🎯 KEY POINTS TO EMPHASIZE

1. **EditText with Monospace**: Professional code editor appearance
2. **Thread for Network**: Background thread for API calls, runOnUiThread for UI updates
3. **JSON and HTTP**: Building and sending API requests with OkHttpClient
4. **CountDownTimer**: onTick (every second), onFinish (when done)
5. **Time Calculations**: Minutes/seconds conversion with division and modulo

---

## 📊 CODE FILES YOU REFERENCED

✅ `IDEActivity.java` - Code editor and execution
✅ `activity_ide.xml` - IDE layout
✅ `PracticeActivity.java` - Practice problems list
✅ `ProblemSolvingActivity.java` - Individual problem solving
✅ `TimedTestActivity.java` - Timed test with countdown

---

**Remember: Explain the logic, show the calculations, demonstrate the flow!**
