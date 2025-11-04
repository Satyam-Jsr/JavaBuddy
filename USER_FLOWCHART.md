# JavaBuddy - User Interaction Flowchart
## Complete User Journey & Screen Flow

---

## 📱 User Experience Flow (What User Sees & Does)

### Main User Journey

```
┌─────────────────────────────────────────────────────────────────┐
│                    USER OPENS APP                               │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│               SPLASH SCREEN (2 seconds)                         │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  • Animated logo                                          │  │
│  │  • "JavaBuddy" title                                      │  │
│  │  • "Your Java Learning Companion" subtitle                │  │
│  │  • Loading indicator                                      │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                    HOME SCREEN                                  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP:                                                     │  │
│  │  • Hamburger menu (opens navigation drawer)              │  │
│  │  • "JavaBuddy" title                                     │  │
│  │                                                           │  │
│  │  HEADER CARD (Primary Color):                            │  │
│  │  • Bird animation (left)                                 │  │
│  │  • "Welcome to JavaBuddy!"                               │  │
│  │  • Progress: X/15 lessons completed                      │  │
│  │                                                           │  │
│  │  MOTIVATION CARD:                                         │  │
│  │  • 💡 + Random motivational quote                        │  │
│  │                                                           │  │
│  │  LEARN JAVA SECTION:                                     │  │
│  │  ┌──────────┐  ┌──────────┐                             │  │
│  │  │ 📚       │  │ ❓       │                             │  │
│  │  │ Lessons  │  │ Quiz     │                             │  │
│  │  └──────────┘  └──────────┘                             │  │
│  │  ┌──────────┐  ┌──────────┐                             │  │
│  │  │ 💻       │  │ ⚡       │                             │  │
│  │  │ Java IDE │  │ Practice │                             │  │
│  │  └──────────┘  └──────────┘                             │  │
│  │  ┌──────────┐  ┌──────────┐                             │  │
│  │  │ 📊       │  │ ⏱️       │                             │  │
│  │  │ Progress │  │ Timed    │                             │  │
│  │  └──────────┘  └──────────┘                             │  │
│  │                                                           │  │
│  │  AI-POWERED TOOLS SECTION:                               │  │
│  │  • 🤖 AI Quiz Generator                                  │  │
│  │  • 💡 AI Coding Challenges                               │  │
│  │                                                           │  │
│  │  BOTTOM RIGHT:                                            │  │
│  │  • 🤖 AI Help button (floating)                          │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────────┘
                         │
            ┌────────────┼────────────┐
            │            │            │
            ▼            ▼            ▼
       LEARNING      PRACTICE      AI TOOLS
```

---

## 🎓 Learning Path Flow

### 1. LESSONS FLOW

```
HOME → Tap "Lessons" card
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│                    LESSONS LIST SCREEN                          │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back button ← "Java Lessons"                          │  │
│  │  • Refresh button (menu)                                  │  │
│  │                                                           │  │
│  │  USER SEES:                                               │  │
│  │  📚 Lesson Cards (scrollable list):                      │  │
│  │                                                           │  │
│  │  ┌──────────────────────────────────────────────────┐    │  │
│  │  │ 🎨 [Random Lottie Animation]                     │    │  │
│  │  │ Introduction to Java            [Beginner]       │    │  │
│  │  │ Introduction • Learn the basics...               │    │  │
│  │  └──────────────────────────────────────────────────┘    │  │
│  │  ┌──────────────────────────────────────────────────┐    │  │
│  │  │ 🎨 [Random Lottie Animation]                     │    │  │
│  │  │ Variables and Data Types        [Beginner]       │    │  │
│  │  │ Basics • Store and manipulate...                 │    │  │
│  │  └──────────────────────────────────────────────────┘    │  │
│  │  • 15 total lessons                                       │  │
│  │                                                           │  │
│  │  BOTTOM RIGHT:                                            │  │
│  │  • 🤖 AI Help button                                     │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Tap any lesson card to open                           │  │
│  │  ✓ Tap refresh to reload lessons                         │  │
│  │  ✓ Tap AI Help for questions                             │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────────┘
                         │
                    Tap lesson
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  LESSON DETAIL SCREEN                           │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back ← "Introduction to Java"                         │  │
│  │  • 🔖 Bookmark icon (tap to save)                        │  │
│  │  • 📤 Share icon                                          │  │
│  │                                                           │  │
│  │  LESSON TITLE (Banner):                                   │  │
│  │  "Introduction to Java"                                   │  │
│  │                                                           │  │
│  │  THREE TABS:                                              │  │
│  │  [Content] [Code Example] [Practice]                     │  │
│  │                                                           │  │
│  │  ═══════════════════════════════════════════════════════ │  │
│  │  TAB 1 - CONTENT:                                         │  │
│  │  • Lesson text (scrollable)                              │  │
│  │  • Formatted with headings                                │  │
│  │  • Key concepts highlighted                               │  │
│  │                                                           │  │
│  │  TAB 2 - CODE EXAMPLE:                                    │  │
│  │  • Java code samples                                      │  │
│  │  • Syntax highlighted                                     │  │
│  │  • Explained line by line                                 │  │
│  │                                                           │  │
│  │  TAB 3 - PRACTICE:                                        │  │
│  │  • Practice problems                                      │  │
│  │  • Interactive exercises                                  │  │
│  │                                                           │  │
│  │  BOTTOM LEFT:                                             │  │
│  │  • 🤖 AI Help button (ask about lesson)                  │  │
│  │                                                           │  │
│  │  BOTTOM RIGHT:                                            │  │
│  │  • ✅ Mark Complete button                               │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Swipe/tap tabs to switch content                      │  │
│  │  ✓ Bookmark lesson (star icon)                           │  │
│  │  ✓ Share lesson content                                   │  │
│  │  ✓ Mark lesson as complete                                │  │
│  │  ✓ Ask AI questions about the lesson                      │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────────┘
                         │
                    Complete ✅
                         │
                         ▼
              Progress Updated! 🎉
```

---

### 2. QUIZ FLOW

```
HOME → Tap "Quiz" card
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  QUIZ SELECTION SCREEN                          │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back ← "Select Problems"                              │  │
│  │  • Refresh button                                         │  │
│  │                                                           │  │
│  │  HEADER:                                                  │  │
│  │  "Choose a lesson to take its quiz"                      │  │
│  │                                                           │  │
│  │  USER SEES:                                               │  │
│  │  Quiz Topic Cards (by lesson):                           │  │
│  │                                                           │  │
│  │  ┌──────────────────────────────────────────────────┐    │  │
│  │  │ 🎨 [Random Lottie Animation]                     │    │  │
│  │  │ Introduction to Java                             │    │  │
│  │  │ ✅ 5 problems available                          │    │  │
│  │  │ Learn the basics of Java programming             │    │  │
│  │  └──────────────────────────────────────────────────┘    │  │
│  │  ┌──────────────────────────────────────────────────┐    │  │
│  │  │ 🎨 [Random Lottie Animation]                     │    │  │
│  │  │ Variables and Data Types                         │    │  │
│  │  │ ✅ 6 problems available                          │    │  │
│  │  │ Store and manipulate data...                     │    │  │
│  │  └──────────────────────────────────────────────────┘    │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Tap any topic to start quiz                           │  │
│  │  ✓ See problem count for each topic                      │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────────┘
                         │
                    Tap topic
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                     QUIZ SCREEN                                 │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back ← "Introduction to Java Quiz"                    │  │
│  │  • Question 1 of 5                                        │  │
│  │                                                           │  │
│  │  PROGRESS BAR:                                            │  │
│  │  ████░░░░░░░░░░░░ 20%                                    │  │
│  │                                                           │  │
│  │  QUESTION:                                                │  │
│  │  "What is Java?"                                          │  │
│  │                                                           │  │
│  │  OPTIONS (Multiple Choice):                              │  │
│  │  ○ A programming language                                │  │
│  │  ○ A coffee brand                                         │  │
│  │  ○ An island                                              │  │
│  │  ○ None of the above                                      │  │
│  │                                                           │  │
│  │  BOTTOM:                                                  │  │
│  │  • [Submit Answer] button                                │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Select one answer                                      │  │
│  │  ✓ Submit answer                                          │  │
│  │  ✓ See immediate feedback (✅ correct / ❌ wrong)        │  │
│  │  ✓ Navigate to next question                             │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────────┘
                         │
                Complete all questions
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                   QUIZ RESULT SCREEN                            │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back ← "Quiz Results"                                 │  │
│  │                                                           │  │
│  │  USER SEES:                                               │  │
│  │                                                           │  │
│  │  🎉 CONGRATULATIONS!                                      │  │
│  │                                                           │  │
│  │  YOUR SCORE:                                              │  │
│  │        80%                                                │  │
│  │     (4 out of 5)                                          │  │
│  │                                                           │  │
│  │  BREAKDOWN:                                               │  │
│  │  ✅ Correct: 4                                            │  │
│  │  ❌ Wrong: 1                                              │  │
│  │                                                           │  │
│  │  PERFORMANCE:                                             │  │
│  │  "Great job! Keep learning!"                             │  │
│  │                                                           │  │
│  │  BUTTONS:                                                 │  │
│  │  [Review Answers]  [Retake Quiz]  [Home]                 │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Review all questions & answers                        │  │
│  │  ✓ Retake the quiz                                        │  │
│  │  ✓ Return to home                                         │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

### 3. JAVA IDE FLOW

```
HOME → Tap "Java IDE" card
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│                    JAVA IDE SCREEN                              │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back ← "Java IDE"                                     │  │
│  │  • Clear button                                           │  │
│  │  • Examples menu                                          │  │
│  │                                                           │  │
│  │  CODE EDITOR (Large text area):                          │  │
│  │  ┌─────────────────────────────────────────────────┐     │  │
│  │  │ 1  public class Main {                          │     │  │
│  │  │ 2      public static void main(String[] args) { │     │  │
│  │  │ 3          System.out.println("Hello Java!");   │     │  │
│  │  │ 4      }                                         │     │  │
│  │  │ 5  }                                             │     │  │
│  │  │                                                  │     │  │
│  │  └─────────────────────────────────────────────────┘     │  │
│  │                                                           │  │
│  │  BUTTONS:                                                 │  │
│  │  [▶️ Run Code]  [🧹 Clear]  [📋 Examples]                │  │
│  │                                                           │  │
│  │  OUTPUT CONSOLE:                                          │  │
│  │  ┌─────────────────────────────────────────────────┐     │  │
│  │  │ > Running...                                     │     │  │
│  │  │ Hello Java!                                      │     │  │
│  │  │ > Execution completed successfully               │     │  │
│  │  └─────────────────────────────────────────────────┘     │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Type/edit Java code                                   │  │
│  │  ✓ Run code and see output                               │  │
│  │  ✓ Load example programs                                 │  │
│  │  ✓ Clear code editor                                      │  │
│  │  ✓ See compilation errors                                │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

### 4. PRACTICE FLOW

```
HOME → Tap "Practice" card
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│                   PRACTICE SCREEN                               │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back ← "Practice Problems"                            │  │
│  │                                                           │  │
│  │  USER SEES:                                               │  │
│  │  Practice Problem Cards:                                  │  │
│  │                                                           │  │
│  │  ┌──────────────────────────────────────────────────┐    │  │
│  │  │ Problem 1                            [Easy]      │    │  │
│  │  │ Write a program to print numbers 1-10            │    │  │
│  │  │ 🏆 Points: 10                                    │    │  │
│  │  └──────────────────────────────────────────────────┘    │  │
│  │  ┌──────────────────────────────────────────────────┐    │  │
│  │  │ Problem 2                            [Medium]    │    │  │
│  │  │ Calculate factorial of a number                  │    │  │
│  │  │ 🏆 Points: 20                                    │    │  │
│  │  └──────────────────────────────────────────────────┘    │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Tap any problem to attempt                            │  │
│  │  ✓ See difficulty level                                  │  │
│  │  ✓ Track points earned                                   │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────────┘
                         │
                    Tap problem
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                PROBLEM SOLVING SCREEN                           │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back ← "Problem 1"                                    │  │
│  │                                                           │  │
│  │  PROBLEM DESCRIPTION:                                     │  │
│  │  "Write a program to print numbers from 1 to 10"        │  │
│  │                                                           │  │
│  │  HINTS: 💡                                                │  │
│  │  • Use a for loop                                         │  │
│  │  • System.out.println()                                   │  │
│  │                                                           │  │
│  │  CODE EDITOR:                                             │  │
│  │  [User writes solution here]                             │  │
│  │                                                           │  │
│  │  BUTTONS:                                                 │  │
│  │  [💡 Hint]  [✅ Submit]  [👁️ View Solution]             │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Write code solution                                    │  │
│  │  ✓ Get hints                                              │  │
│  │  ✓ Submit for checking                                    │  │
│  │  ✓ View solution if stuck                                │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

### 5. PROGRESS TRACKING FLOW

```
HOME → Tap "Progress" card
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│                   PROGRESS SCREEN                               │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back ← "Your Progress"                                │  │
│  │                                                           │  │
│  │  HEADER CARD (with bird animation):                      │  │
│  │  🐦 Track Your Progress                                   │  │
│  │  "Keep up the great work! 🎉"                            │  │
│  │                                                           │  │
│  │  OVERALL PROGRESS:                                        │  │
│  │  ████████░░░░░░░░░░ 53%                                  │  │
│  │  "Keep learning to unlock new achievements!"            │  │
│  │                                                           │  │
│  │  STATISTICS CARDS (2x2 grid):                            │  │
│  │                                                           │  │
│  │  ┌───────────┐  ┌───────────┐                           │  │
│  │  │ 📚        │  │ ❓        │                           │  │
│  │  │ 8 / 15    │  │ 85.5%     │                           │  │
│  │  │ Lessons   │  │ Avg Score │                           │  │
│  │  │ ████░░░   │  │ ████████░ │                           │  │
│  │  └───────────┘  └───────────┘                           │  │
│  │  ┌───────────┐  ┌───────────┐                           │  │
│  │  │ ⏱️        │  │ 🏆        │                           │  │
│  │  │ 3h 25m    │  │ 4 earned  │                           │  │
│  │  │ Time      │  │ Achieve.  │                           │  │
│  │  └───────────┘  └───────────┘                           │  │
│  │                                                           │  │
│  │  USER SEES:                                               │  │
│  │  ✓ Completed lessons count                               │  │
│  │  ✓ Average quiz scores                                    │  │
│  │  ✓ Total time spent learning                             │  │
│  │  ✓ Achievements earned                                    │  │
│  │  ✓ Visual progress bars                                  │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

### 6. TIMED TEST FLOW

```
HOME → Tap "Timed Test" card
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│                 TIMED TEST SETUP SCREEN                         │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back ← "Timed Mastery Test"                           │  │
│  │                                                           │  │
│  │  CONFIGURATION:                                           │  │
│  │                                                           │  │
│  │  Number of Questions:                                     │  │
│  │  [━━━━●━━━━━] 10 questions                               │  │
│  │                                                           │  │
│  │  Time Limit:                                              │  │
│  │  [━━━●━━━━━━] 10 minutes                                 │  │
│  │                                                           │  │
│  │  Difficulty:                                              │  │
│  │  ○ Easy  ● Mixed  ○ Hard                                 │  │
│  │                                                           │  │
│  │  [🚀 Start Test]                                          │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Set number of questions (5-20)                        │  │
│  │  ✓ Set time limit (5-30 mins)                            │  │
│  │  ✓ Choose difficulty                                      │  │
│  │  ✓ Start when ready                                       │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────────┘
                         │
                    Start Test
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                    TIMED TEST SCREEN                            │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • ⏱️ 08:45 remaining (counting down)                     │  │
│  │  • Question 3 of 10                                       │  │
│  │                                                           │  │
│  │  [Question and options same as quiz]                     │  │
│  │                                                           │  │
│  │  WARNING:                                                 │  │
│  │  ⚠️ Time pressure!                                        │  │
│  │  ⚠️ Can't go back to previous questions                  │  │
│  │                                                           │  │
│  │  USER SEES:                                               │  │
│  │  ✓ Timer counting down (red when < 1 min)               │  │
│  │  ✓ Current question number                               │  │
│  │  ✓ Must answer quickly                                    │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🤖 AI Tools Flow

### 7. AI QUIZ GENERATOR

```
HOME → Tap "AI Quiz Generator" card
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│               AI QUIZ GENERATOR SCREEN                          │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back ← "AI Quiz Generator"                            │  │
│  │                                                           │  │
│  │  🤖 Generate Custom Java Quiz                             │  │
│  │                                                           │  │
│  │  CONFIGURATION:                                           │  │
│  │                                                           │  │
│  │  Topic:                                                   │  │
│  │  ┌────────────────────────────────────────────────┐      │  │
│  │  │ Enter any Java topic...                        │      │  │
│  │  │ e.g., "Arrays", "Loops", "OOP"                 │      │  │
│  │  └────────────────────────────────────────────────┘      │  │
│  │                                                           │  │
│  │  Difficulty:                                              │  │
│  │  ○ Beginner  ○ Intermediate  ● Advanced                  │  │
│  │                                                           │  │
│  │  Number of Questions:                                     │  │
│  │  [━━━━●━━━━━] 5 questions                                │  │
│  │                                                           │  │
│  │  [✨ Generate Quiz]                                       │  │
│  │                                                           │  │
│  │  STATUS:                                                  │  │
│  │  🔄 Generating... (shows loading animation)              │  │
│  │  ✅ Quiz generated! (when done)                          │  │
│  │                                                           │  │
│  │  BUTTONS (after generation):                             │  │
│  │  [▶️ Play Quiz]  [💾 Save to File]  [🔄 Regenerate]      │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Enter custom topic                                     │  │
│  │  ✓ Select difficulty                                      │  │
│  │  ✓ Choose question count (1-10)                          │  │
│  │  ✓ Generate AI quiz                                       │  │
│  │  ✓ Play generated quiz                                    │  │
│  │  ✓ Save quiz to Downloads folder                         │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────────┘
                         │
                    Play Quiz
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                 AI QUIZ PLAYER SCREEN                           │
│  [Same quiz interface as regular quiz]                         │
│  • Shows AI-generated questions                                │
│  • Same scoring and result display                             │
└─────────────────────────────────────────────────────────────────┘
```

---

### 8. AI PROGRAMMING CHALLENGES

```
HOME → Tap "AI Coding Challenges" card
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│          AI PROGRAMMING CHALLENGE SCREEN                        │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back ← "AI Programming Challenges"                    │  │
│  │                                                           │  │
│  │  🎯 Generate Coding Challenge                             │  │
│  │                                                           │  │
│  │  Challenge Type:                                          │  │
│  │  ┌────────────────────────────────────────────────┐      │  │
│  │  │ Describe what you want to learn...             │      │  │
│  │  │ e.g., "Create a calculator"                    │      │  │
│  │  │      "Sort an array"                           │      │  │
│  │  └────────────────────────────────────────────────┘      │  │
│  │                                                           │  │
│  │  Difficulty:                                              │  │
│  │  ○ Easy  ● Medium  ○ Hard                                │  │
│  │                                                           │  │
│  │  Include:                                                 │  │
│  │  ☑️ Starter code                                          │  │
│  │  ☑️ Hints                                                 │  │
│  │  ☑️ Solution                                              │  │
│  │                                                           │  │
│  │  [✨ Generate Challenge]                                  │  │
│  │                                                           │  │
│  │  STATUS:                                                  │  │
│  │  🔄 Creating challenge... (AI working)                   │  │
│  │  ✅ Challenge ready!                                      │  │
│  │                                                           │  │
│  │  GENERATED CHALLENGE:                                     │  │
│  │  ┌────────────────────────────────────────────────┐      │  │
│  │  │ Challenge: Build a Simple Calculator           │      │  │
│  │  │                                                 │      │  │
│  │  │ Description:                                    │      │  │
│  │  │ Create a calculator that performs +, -, *, /   │      │  │
│  │  │                                                 │      │  │
│  │  │ Requirements:                                   │      │  │
│  │  │ 1. Take two numbers as input                   │      │  │
│  │  │ 2. Choose operation                            │      │  │
│  │  │ 3. Display result                              │      │  │
│  │  │                                                 │      │  │
│  │  │ Starter Code:                                   │      │  │
│  │  │ public class Calculator { ... }                │      │  │
│  │  │                                                 │      │  │
│  │  │ Hints:                                          │      │  │
│  │  │ 💡 Use switch statement                        │      │  │
│  │  │ 💡 Handle division by zero                     │      │  │
│  │  └────────────────────────────────────────────────┘      │  │
│  │                                                           │  │
│  │  BUTTONS:                                                 │  │
│  │  [👁️ View Solution]  [💾 Save]  [🔄 New Challenge]      │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Describe desired challenge                            │  │
│  │  ✓ Set difficulty level                                  │  │
│  │  ✓ Choose what to include                                │  │
│  │  ✓ View generated challenge                              │  │
│  │  ✓ See starter code                                       │  │
│  │  ✓ Get hints                                              │  │
│  │  ✓ View solution when needed                             │  │
│  │  ✓ Save challenge for later                              │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

### 9. AI HELP (Available Everywhere)

```
Any Screen → Tap 🤖 AI Help button (bottom-right floating button)
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                     AI HELP SCREEN                              │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back ← "Ask JavaBuddy AI"                             │  │
│  │                                                           │  │
│  │  🤖 ASK ME ANYTHING ABOUT JAVA!                           │  │
│  │                                                           │  │
│  │  CHAT AREA (scrollable):                                  │  │
│  │  ┌────────────────────────────────────────────────┐      │  │
│  │  │                                                 │      │  │
│  │  │  [Previous Q&A conversation shows here]        │      │  │
│  │  │                                                 │      │  │
│  │  │  🧑 YOU:                                        │      │  │
│  │  │  "What is a variable?"                         │      │  │
│  │  │                                                 │      │  │
│  │  │  🤖 JAVABUDDY AI:                              │      │  │
│  │  │  A variable is a container that stores data... │      │  │
│  │  │  [Formatted, easy-to-read response]            │      │  │
│  │  │                                                 │      │  │
│  │  └────────────────────────────────────────────────┘      │  │
│  │                                                           │  │
│  │  INPUT AREA (bottom):                                     │  │
│  │  ┌────────────────────────────────────────────────┐      │  │
│  │  │ Ask anything about Java or this app...         │      │  │
│  │  └────────────────────────────────────────────────┘      │  │
│  │  [📤 Ask AI]                                              │  │
│  │                                                           │  │
│  │  EXAMPLE QUESTIONS:                                       │  │
│  │  • "Explain loops in Java"                               │  │
│  │  • "What's the difference between int and Integer?"      │  │
│  │  • "How do I create a class?"                            │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Ask any Java question                                 │  │
│  │  ✓ Get instant AI responses                              │  │
│  │  ✓ See conversation history                              │  │
│  │  ✓ Ask follow-up questions                               │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📚 Supporting Features Flow

### 10. BOOKMARKS

```
HOME → Open drawer → Tap "Bookmarks"
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                   BOOKMARKS SCREEN                              │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back ← "Bookmarked Lessons"                           │  │
│  │                                                           │  │
│  │  USER SEES:                                               │  │
│  │                                                           │  │
│  │  IF BOOKMARKS EXIST:                                      │  │
│  │  📚 Your Saved Lessons:                                   │  │
│  │                                                           │  │
│  │  ┌──────────────────────────────────────────────────┐    │  │
│  │  │ 🔖 Introduction to Java                          │    │  │
│  │  │ Saved on Nov 2, 2025                             │    │  │
│  │  └──────────────────────────────────────────────────┘    │  │
│  │  ┌──────────────────────────────────────────────────┐    │  │
│  │  │ 🔖 Object-Oriented Programming                   │    │  │
│  │  │ Saved on Nov 1, 2025                             │    │  │
│  │  └──────────────────────────────────────────────────┘    │  │
│  │                                                           │  │
│  │  IF NO BOOKMARKS:                                         │  │
│  │  📖 No bookmarks yet                                      │  │
│  │  "Bookmark lessons to access them quickly later"        │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Tap bookmarked lesson to open                         │  │
│  │  ✓ See when lesson was bookmarked                        │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

### 11. SETTINGS

```
HOME → Open drawer → Tap "Settings"
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SETTINGS SCREEN                              │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  TOP BAR:                                                 │  │
│  │  • Back ← "Settings"                                     │  │
│  │                                                           │  │
│  │  USER SEES & CAN TOGGLE:                                  │  │
│  │                                                           │  │
│  │  DISPLAY SETTINGS:                                        │  │
│  │  ☑️ Enable card animations                               │  │
│  │     (Show/hide Lottie animations)                        │  │
│  │                                                           │  │
│  │  ☑️ Show motivational messages                           │  │
│  │     (Display daily motivation quotes)                    │  │
│  │                                                           │  │
│  │  AI HELPERS:                                              │  │
│  │  ☑️ AI quiz hints                                         │  │
│  │     (Show AI-generated hints in quizzes)                 │  │
│  │                                                           │  │
│  │  [Save Settings]                                          │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Enable/disable animations (improves performance)     │  │
│  │  ✓ Show/hide motivational quotes                        │  │
│  │  ✓ Configure AI features                                 │  │
│  │  ✓ Changes apply immediately                             │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

### 12. NAVIGATION DRAWER (Hamburger Menu)

```
Any Screen → Tap ☰ (top-left)
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────┐
│              NAVIGATION DRAWER (Slides from left)               │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  HEADER:                                                  │  │
│  │  ┌────────────────────────────────────────────────┐      │  │
│  │  │  JavaBuddy Logo                                │      │  │
│  │  │  Learn Java Anywhere, Anytime                  │      │  │
│  │  └────────────────────────────────────────────────┘      │  │
│  │                                                           │  │
│  │  MAIN MENU:                                               │  │
│  │  🏠 Home                                                  │  │
│  │  📚 Lessons                                               │  │
│  │  ❓ Quiz                                                  │  │
│  │  💻 Java IDE                                              │  │
│  │  ⚡ Practice                                              │  │
│  │  📊 Progress                                              │  │
│  │                                                           │  │
│  │  AI TOOLS:                                                │  │
│  │  🤖 AI Quiz Generator                                     │  │
│  │  💡 AI Challenges                                         │  │
│  │                                                           │  │
│  │  OTHER:                                                   │  │
│  │  🔖 Bookmarks                                             │  │
│  │  ⚙️ Settings                                              │  │
│  │  ℹ️ About                                                 │  │
│  │  🐛 Debug (developer option)                             │  │
│  │                                                           │  │
│  │  USER CAN:                                                │  │
│  │  ✓ Navigate to any section quickly                       │  │
│  │  ✓ Tap anywhere outside to close drawer                  │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🎯 Complete User Journey Summary

### First Time User:

```
1. 📱 Open App
   ↓
2. 👀 See Splash Screen (2s)
   ↓
3. 🏠 Land on Home Screen
   - See welcome message
   - See progress (0/15)
   - See motivational quote
   - See 6 main feature cards
   - See 2 AI tool cards
   ↓
4. 🎓 Tap "Lessons"
   ↓
5. 📚 See 15 lesson cards with animations
   ↓
6. 👆 Tap first lesson
   ↓
7. 📖 Read content, code examples
   ↓
8. ✅ Mark as complete
   ↓
9. 🎉 Progress updates (1/15)
   ↓
10. 🔄 Continue learning journey...
```

### Returning User Flow:

```
1. 📱 Open App
   ↓
2. 🏠 Home shows updated progress (e.g., 5/15)
   ↓
3. User can:
   - Continue learning (Lessons)
   - Test knowledge (Quiz)
   - Practice coding (IDE/Practice)
   - Generate AI content
   - Check progress
   - Access bookmarks
   ↓
4. 🎯 Goal: Complete all 15 lessons
   ↓
5. 🏆 Track achievements & scores
```

---

## 💡 User Actions Summary Table

| Screen | User Can See | User Can Do |
|--------|-------------|-------------|
| **Home** | Welcome, progress, feature cards | Navigate to any feature, open drawer |
| **Lessons** | 15 lesson cards with animations | Select lesson, bookmark, get AI help |
| **Lesson Detail** | Content, code, practice tabs | Read, mark complete, bookmark, share |
| **Quiz Selection** | Topics with problem counts | Choose topic, start quiz |
| **Quiz** | Questions, options, progress | Answer, submit, navigate |
| **Quiz Result** | Score, breakdown, performance | Review, retake, return home |
| **IDE** | Code editor, output console | Write code, run, see results |
| **Practice** | Problem cards | Attempt problems, get hints |
| **Progress** | Stats, charts, achievements | View progress, track time |
| **Timed Test** | Timer, questions | Race against time, complete |
| **AI Quiz Gen** | Configuration, generated quiz | Set params, generate, play, save |
| **AI Challenges** | Challenge description, code | Get challenge, view hints, solution |
| **AI Help** | Chat interface | Ask questions, get answers |
| **Bookmarks** | Saved lessons list | Open bookmarked lessons |
| **Settings** | Preference toggles | Enable/disable features |

---

## 🎨 Visual Elements User Sees

### Colors & Themes:
- **Primary Color**: App bar, headers, buttons
- **Success Green**: Correct answers, completion
- **Warning Orange**: Incorrect, time warnings
- **Info Blue**: Informational elements

### Animations:
- **Bird_hi.lottie**: Welcome, progress sections
- **Random Lotties**: Each lesson/quiz card
- **Splash animation**: App startup

### Icons:
- 📚 Books (Lessons)
- ❓ Question marks (Quiz)
- 💻 Code (IDE)
- ⚡ Lightning (Practice)
- 📊 Charts (Progress)
- ⏱️ Timer (Timed Test)
- 🤖 Robot (AI Tools)
- 🔖 Bookmark
- ⚙️ Gear (Settings)

---

## 🚀 Quick Navigation Paths

### Fastest ways to reach features:

1. **Home → Lessons**: 1 tap
2. **Home → Quiz**: 1 tap
3. **Home → AI Help**: 1 tap (floating button)
4. **Any Screen → Settings**: Drawer → Settings (2 taps)
5. **Any Screen → Bookmarks**: Drawer → Bookmarks (2 taps)
6. **Lesson → Bookmark**: Menu → Bookmark (2 taps)
7. **Quiz → Retake**: Results → Retake (1 tap)

---

This comprehensive flowchart shows exactly what users see and how they interact with every part of your JavaBuddy application! 🎓✨
