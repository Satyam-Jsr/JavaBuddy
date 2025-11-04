# NON-TECH PERSON 2: CODE EXPLANATION SCRIPT
## 📚 Lessons & Quizzes - Code Implementation

---

## 🎯 YOUR CODE EXPLANATION MISSION

Walk through **actual code files** that implement lesson loading, the 3-tab system, quiz logic, and score calculation.

---

## 📝 SUGGESTED SCRIPT (Use Android Studio while presenting)

### OPENING (30 seconds)
"I'll explain how the code implements the learning system - lessons and quizzes. Let me show you the actual implementation."

---

## SECTION 1: LESSON LOADING CODE (3 minutes)

### File to Open: `LessonActivity.java`

**Script:**

"When users tap 'Lessons' from the home screen, `LessonActivity.java` opens. Let me show you how lessons are loaded from the database and displayed."

**Point to this code:**
```java
public class LessonActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private LessonAdapter adapter;
    private LessonRepository repository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Setup RecyclerView
        recyclerView = findViewById(R.id.lessons_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new LessonAdapter(this);
        recyclerView.setAdapter(adapter);
        
        // Load lessons from database
        repository = new LessonRepository(getApplication());
        loadLessons();
    }
    
    private void loadLessons() {
        repository.getAllLessons().observe(this, lessons -> {
            if (lessons != null) {
                adapter.setLessons(lessons);
            }
        });
    }
}
```

**Explain:**

"Let me break down this code:

**1. RecyclerView Setup:**
- **`RecyclerView recyclerView`** - This is like a smart scrollable list
- **`new LinearLayoutManager(this)`** - Makes items display vertically one after another
- **`new LessonAdapter(this)`** - Creates the adapter that connects data to views
- **`recyclerView.setAdapter(adapter)`** - Connects the adapter to the RecyclerView

**2. Loading Data:**
- **`new LessonRepository(getApplication())`** - Creates a repository object that talks to the database
- **`repository.getAllLessons()`** - Calls a method that queries the database for all 15 lessons
- **`.observe(this, lessons -> { ... })`** - This is the LiveData observer pattern. It watches for data changes

**3. LiveData Magic:**
The `.observe()` method means 'whenever the lesson data changes in the database, automatically run this code'. So:
- Database query happens in background
- When data arrives, the lambda `lessons -> { ... }` runs
- **`adapter.setLessons(lessons)`** - Updates the adapter with new data
- RecyclerView automatically refreshes to show the lessons

**Flow:** Activity starts → Setup RecyclerView → Create repository → Query database → Data returns → Adapter updates → UI shows 15 lessons"

### File to Open: `LessonAdapter.java`

**Script:**

"Now let's see how the adapter displays each lesson card."

**Point to this code:**
```java
public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {
    
    private List<Lesson> lessons;
    private Context context;
    
    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.bind(lesson);
    }
    
    @Override
    public int getItemCount() {
        return lessons != null ? lessons.size() : 0;
    }
    
    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
        notifyDataSetChanged(); // Tells RecyclerView to refresh
    }
    
    static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        LottieAnimationView animationView;
        TextView difficultyBadge;
        
        LessonViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.lesson_title);
            descriptionTextView = itemView.findViewById(R.id.lesson_description);
            animationView = itemView.findViewById(R.id.lesson_animation);
            difficultyBadge = itemView.findViewById(R.id.difficulty_badge);
        }
        
        void bind(Lesson lesson) {
            titleTextView.setText(lesson.getTitle());
            descriptionTextView.setText(lesson.getDescription());
            difficultyBadge.setText(lesson.getDifficulty());
            
            // Load animation
            String animation = AnimationPalette.getAnimationForLesson(lesson.getId());
            animationView.setAnimation(animation);
            animationView.playAnimation();
            
            // Click listener
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), LessonDetailActivity.class);
                intent.putExtra("lesson_id", lesson.getId());
                v.getContext().startActivity(intent);
            });
        }
    }
}
```

**Explain:**

"This adapter has three key methods:

**1. onCreateViewHolder:**
- **`LayoutInflater.inflate(R.layout.item_lesson, ...)`** - Loads the lesson card layout from XML
- Creates a ViewHolder object that holds references to all the views in the card
- This is called only when creating new cards (efficient memory usage)

**2. onBindViewHolder:**
- **`lessons.get(position)`** - Gets the lesson at this position (0-14 for 15 lessons)
- **`holder.bind(lesson)`** - Fills the card with this lesson's data
- Called every time a card becomes visible on screen

**3. getItemCount:**
- Returns how many lessons there are (15)
- RecyclerView uses this to know how many cards to create

**4. ViewHolder bind method:**
- **`setText(...)`** - Sets the title and description text
- **`AnimationPalette.getAnimationForLesson(...)`** - Gets the right animation for this lesson
- **`setAnimation()` and `playAnimation()`** - Plays the Lottie animation
- **`setOnClickListener(...)`** - When card is tapped, create an Intent with the lesson ID and open LessonDetailActivity

**Why ViewHolder pattern?** When you scroll, RecyclerView reuses ViewHolders instead of creating new ones. This makes scrolling smooth even with many lessons."

---

## SECTION 2: LESSON DETAIL 3-TAB CODE (3 minutes)

### File to Open: `LessonDetailActivity.java`

**Script:**

"When a lesson card is clicked, `LessonDetailActivity` opens with 3 tabs. Let me show you the ViewPager2 and TabLayout code."

**Point to this code:**
```java
public class LessonDetailActivity extends AppCompatActivity {
    
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Lesson currentLesson;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);
        
        // Get lesson ID from intent
        int lessonId = getIntent().getIntExtra("lesson_id", 0);
        
        // Load lesson from database
        loadLesson(lessonId);
        
        // Setup ViewPager and Tabs
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        
        setupViewPager();
    }
    
    private void loadLesson(int lessonId) {
        LessonRepository repository = new LessonRepository(getApplication());
        repository.getLessonById(lessonId).observe(this, lesson -> {
            if (lesson != null) {
                currentLesson = lesson;
                setupViewPager();
            }
        });
    }
    
    private void setupViewPager() {
        if (currentLesson == null) return;
        
        // Create adapter with 3 fragments
        LessonPagerAdapter adapter = new LessonPagerAdapter(this, currentLesson);
        viewPager.setAdapter(adapter);
        
        // Connect tabs to ViewPager
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
}
```

**Explain:**

"Here's how the 3-tab system works:

**1. Getting the Lesson ID:**
- **`getIntent().getIntExtra('lesson_id', 0)`** - Retrieves the lesson ID that was passed from LessonAdapter's click listener
- The adapter sent it using `intent.putExtra('lesson_id', lesson.getId())`

**2. Loading Lesson:**
- **`repository.getLessonById(lessonId)`** - Queries database for this specific lesson
- **`.observe(...)`** - Waits for the data to arrive
- When it arrives, stores it in `currentLesson` variable

**3. ViewPager Setup:**
- **`new LessonPagerAdapter(this, currentLesson)`** - Creates an adapter that will provide 3 fragments
- **`viewPager.setAdapter(adapter)`** - Connects adapter to ViewPager2

**4. TabLayout Connection:**
- **`TabLayoutMediator`** - This is the magic class that syncs tabs with pages
- **`(tab, position) -> { ... }`** - This lambda runs for each tab
- **`position`** - The tab number (0, 1, 2)
- **`tab.setText(...)`** - Sets the tab label based on position
- **`.attach()`** - Activates the connection

Result: When user swipes or taps a tab, ViewPager automatically shows the right fragment."

### File to Open: `LessonPagerAdapter.java`

**Script:**

"Now let's see how the adapter creates the 3 fragments."

**Point to this code:**
```java
public class LessonPagerAdapter extends FragmentStateAdapter {
    
    private Lesson lesson;
    
    public LessonPagerAdapter(FragmentActivity fa, Lesson lesson) {
        super(fa);
        this.lesson = lesson;
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ContentFragment.newInstance(lesson.getContent());
            case 1:
                return CodeExampleFragment.newInstance(lesson.getCodeExamples());
            case 2:
                return PracticeFragment.newInstance(lesson.getPractice());
            default:
                return new Fragment();
        }
    }
    
    @Override
    public int getItemCount() {
        return 3; // 3 tabs
    }
}
```

**Explain:**

"This adapter is simple but powerful:

**1. createFragment method:**
- Called by ViewPager2 when it needs to show a tab
- **`position`** - Which tab (0 = Content, 1 = Code, 2 = Practice)
- **`switch (position)`** - Checks which tab is being requested
- **`ContentFragment.newInstance(...)`** - Creates a new fragment with the lesson's content data
- **`lesson.getContent()`** - Gets the content text from the lesson object
- Returns the appropriate fragment for that tab

**2. getItemCount:**
- Returns 3 because we have 3 tabs

**Fragment.newInstance pattern:**
Each fragment has a static `newInstance` method that creates the fragment and passes data to it using a Bundle. This is the proper way to pass data to fragments.

**Flow:** User swipes to tab 2 → ViewPager calls `createFragment(1)` → Switch returns `CodeExampleFragment` with code data → Fragment displays the code example"

---

## SECTION 3: QUIZ LOGIC CODE (4 minutes)

### File to Open: `QuizActivity.java`

**Script:**

"Now let's look at the quiz logic - how questions are shown and answers are checked."

**Point to this code:**
```java
public class QuizActivity extends AppCompatActivity {
    
    private TextView questionTextView;
    private Button option1Button, option2Button, option3Button, option4Button;
    private Button nextButton;
    private TextView questionNumberTextView;
    
    private List<Quiz> quizQuestions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int totalQuestions;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        
        // Initialize views
        questionTextView = findViewById(R.id.question_text);
        option1Button = findViewById(R.id.option1_button);
        option2Button = findViewById(R.id.option2_button);
        option3Button = findViewById(R.id.option3_button);
        option4Button = findViewById(R.id.option4_button);
        nextButton = findViewById(R.id.next_button);
        questionNumberTextView = findViewById(R.id.question_number);
        
        // Load quiz questions
        loadQuizQuestions();
        
        // Setup button clicks
        setupOptionButtons();
    }
    
    private void loadQuizQuestions() {
        String topic = getIntent().getStringExtra("quiz_topic");
        
        QuizRepository repository = new QuizRepository(getApplication());
        repository.getQuizByTopic(topic).observe(this, questions -> {
            if (questions != null && !questions.isEmpty()) {
                quizQuestions = questions;
                totalQuestions = questions.size();
                showQuestion(0);
            }
        });
    }
    
    private void showQuestion(int index) {
        if (index >= quizQuestions.size()) {
            // Quiz finished
            showResults();
            return;
        }
        
        Quiz currentQuestion = quizQuestions.get(index);
        
        // Display question
        questionTextView.setText(currentQuestion.getQuestion());
        questionNumberTextView.setText("Question " + (index + 1) + "/" + totalQuestions);
        
        // Display options
        option1Button.setText(currentQuestion.getOption1());
        option2Button.setText(currentQuestion.getOption2());
        option3Button.setText(currentQuestion.getOption3());
        option4Button.setText(currentQuestion.getOption4());
        
        // Reset button colors
        resetButtonColors();
        
        // Hide next button
        nextButton.setVisibility(View.GONE);
    }
    
    private void setupOptionButtons() {
        View.OnClickListener optionClickListener = v -> {
            Button clickedButton = (Button) v;
            checkAnswer(clickedButton);
        };
        
        option1Button.setOnClickListener(optionClickListener);
        option2Button.setOnClickListener(optionClickListener);
        option3Button.setOnClickListener(optionClickListener);
        option4Button.setOnClickListener(optionClickListener);
        
        nextButton.setOnClickListener(v -> {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        });
    }
    
    private void checkAnswer(Button selectedButton) {
        Quiz currentQuestion = quizQuestions.get(currentQuestionIndex);
        String selectedAnswer = selectedButton.getText().toString();
        String correctAnswer = currentQuestion.getCorrectAnswer();
        
        // Disable all buttons after selection
        disableOptionButtons();
        
        if (selectedAnswer.equals(correctAnswer)) {
            // Correct answer
            selectedButton.setBackgroundColor(Color.GREEN);
            score++;
            Toast.makeText(this, "Correct! ✓", Toast.LENGTH_SHORT).show();
        } else {
            // Wrong answer
            selectedButton.setBackgroundColor(Color.RED);
            highlightCorrectAnswer(correctAnswer);
            Toast.makeText(this, "Wrong! The correct answer is highlighted", Toast.LENGTH_SHORT).show();
        }
        
        // Show next button
        nextButton.setVisibility(View.VISIBLE);
    }
    
    private void highlightCorrectAnswer(String correctAnswer) {
        if (option1Button.getText().toString().equals(correctAnswer)) {
            option1Button.setBackgroundColor(Color.GREEN);
        } else if (option2Button.getText().toString().equals(correctAnswer)) {
            option2Button.setBackgroundColor(Color.GREEN);
        } else if (option3Button.getText().toString().equals(correctAnswer)) {
            option3Button.setBackgroundColor(Color.GREEN);
        } else if (option4Button.getText().toString().equals(correctAnswer)) {
            option4Button.setBackgroundColor(Color.GREEN);
        }
    }
    
    private void showResults() {
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total", totalQuestions);
        startActivity(intent);
        finish();
    }
}
```

**Explain:**

"Let me walk through the quiz logic:

**1. Loading Questions:**
- **`getIntent().getStringExtra('quiz_topic')`** - Gets the quiz topic from the intent
- **`repository.getQuizByTopic(topic)`** - Queries database for questions on this topic
- **`.observe(...)`** - Waits for questions to load
- **`quizQuestions = questions`** - Stores all questions in a list
- **`showQuestion(0)`** - Displays the first question

**2. Displaying a Question:**
- **`quizQuestions.get(index)`** - Gets the question at this position
- **`questionTextView.setText(...)`** - Sets the question text
- **`questionNumberTextView.setText('Question ' + (index + 1) + '/' + totalQuestions)`** - Shows 'Question 1/10'
- **`option1Button.setText(currentQuestion.getOption1())`** - Sets option 1 text
- Same for other 3 option buttons
- **`resetButtonColors()`** - Makes all buttons the default color again
- **`nextButton.setVisibility(View.GONE)`** - Hides the next button until an answer is selected

**3. Checking Answer:**
When a button is clicked:
- **`selectedButton.getText().toString()`** - Gets the text of the clicked button
- **`currentQuestion.getCorrectAnswer()`** - Gets the correct answer from the database
- **`if (selectedAnswer.equals(correctAnswer))`** - Compares them
  - **If correct**: Button turns green, `score++` increments the score, show success toast
  - **If wrong**: Clicked button turns red, correct button turns green, show error toast
- **`disableOptionButtons()`** - Prevents changing the answer
- **`nextButton.setVisibility(View.VISIBLE)`** - Shows the next button

**4. Moving to Next Question:**
- **`currentQuestionIndex++`** - Increments to next question
- **`showQuestion(currentQuestionIndex)`** - Loads and displays next question
- If `index >= quizQuestions.size()`, quiz is complete, call `showResults()`

**5. Showing Results:**
- **`intent.putExtra('score', score)`** - Passes the score to result activity
- **`intent.putExtra('total', totalQuestions)`** - Passes total number of questions
- **`startActivity(intent)`** - Opens QuizResultActivity
- **`finish()`** - Closes quiz activity

**Flow:** Load questions → Show question 1 → User taps answer → Check if correct → Color the buttons → Show next button → User taps next → Show question 2 → ... → After last question → Show results screen"

### File to Open: `QuizResultActivity.java`

**Script:**

"Finally, let's see how the score is calculated and displayed."

**Point to this code:**
```java
public class QuizResultActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);
        
        // Get score data
        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 0);
        
        // Calculate percentage
        int percentage = (score * 100) / total;
        
        // Display results
        TextView scoreText = findViewById(R.id.score_text);
        scoreText.setText(score + " / " + total);
        
        TextView percentageText = findViewById(R.id.percentage_text);
        percentageText.setText(percentage + "%");
        
        // Save progress to database
        saveProgress(percentage);
        
        // Show result message
        TextView messageText = findViewById(R.id.message_text);
        if (percentage >= 80) {
            messageText.setText("Excellent! 🎉");
            messageText.setTextColor(Color.GREEN);
        } else if (percentage >= 60) {
            messageText.setText("Good job! 👍");
            messageText.setTextColor(Color.BLUE);
        } else {
            messageText.setText("Keep practicing! 📚");
            messageText.setTextColor(Color.ORANGE);
        }
    }
    
    private void saveProgress(int percentage) {
        int lessonId = getIntent().getIntExtra("lesson_id", 0);
        
        UserProgress progress = new UserProgress();
        progress.setLessonId(lessonId);
        progress.setScore(percentage);
        progress.setTimestamp(System.currentTimeMillis());
        progress.setCompleted(percentage >= 60);
        
        ProgressRepository repository = new ProgressRepository(getApplication());
        repository.updateProgress(progress);
    }
}
```

**Explain:**

"The results screen code:

**1. Getting Data:**
- **`getIntExtra('score', 0)`** - Gets score (e.g., 7)
- **`getIntExtra('total', 0)`** - Gets total questions (e.g., 10)

**2. Calculating Percentage:**
- **`(score * 100) / total`** - Formula for percentage
- Example: (7 * 100) / 10 = 700 / 10 = 70%
- This is integer division, so 7.5 becomes 7

**3. Displaying Results:**
- **`scoreText.setText(score + ' / ' + total)`** - Shows '7 / 10'
- **`percentageText.setText(percentage + '%')`** - Shows '70%'

**4. Conditional Messages:**
- **`if (percentage >= 80)`** - 80% or higher = 'Excellent!'
- **`else if (percentage >= 60)`** - 60-79% = 'Good job!'
- **`else`** - Below 60% = 'Keep practicing!'
- Changes text color based on performance

**5. Saving Progress:**
- Creates a `UserProgress` object with lesson ID, score, and timestamp
- **`setCompleted(percentage >= 60)`** - Marks lesson complete if score is 60% or higher
- **`repository.updateProgress(progress)`** - Saves to database

This progress is then visible in the Progress screen."

---

## 🎯 KEY POINTS TO EMPHASIZE

1. **LiveData + Observe Pattern**: Automatic UI updates when database changes
2. **RecyclerView + Adapter**: Efficient list display with ViewHolder
3. **ViewPager2 + TabLayout**: Swipeable tabs implementation
4. **Quiz Logic**: String comparison to check answers
5. **Score Calculation**: `(correct * 100) / total` for percentage

---

## 📊 CODE FILES YOU REFERENCED

✅ `LessonActivity.java` - RecyclerView and database loading
✅ `LessonAdapter.java` - ViewHolder pattern
✅ `LessonDetailActivity.java` - ViewPager and TabLayout
✅ `LessonPagerAdapter.java` - Fragment creation
✅ `QuizActivity.java` - Quiz logic and answer checking
✅ `QuizResultActivity.java` - Score calculation

---

**Remember: Show the code, explain the logic, demonstrate the flow!**
