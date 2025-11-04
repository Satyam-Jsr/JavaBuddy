package com.example.javabuddy.database;

import android.graphics.Color;
import android.util.Log;
import com.example.javabuddy.database.entities.Lesson;
import com.example.javabuddy.database.entities.PracticeProblem;
import com.example.javabuddy.database.entities.Quiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabasePopulator {

    private static final String TAG = "DatabasePopulator";
    private static boolean isPopulated = false;
    private static final Object populationLock = new Object();

    public static void forceRepopulate(JavaBuddyDatabase database) {
        Log.d(TAG, "Force repopulating database...");
        
        // Clear existing data
        try {
            database.clearAllTables();
            Log.d(TAG, "Cleared all database tables");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing tables: " + e.getMessage(), e);
        }
        
        // Reset population flag
        isPopulated = false;
        
        // Force populate
        populateInitialData(database);
    }

    public static void populateInitialData(JavaBuddyDatabase database) {
        synchronized (populationLock) {
            Log.d(TAG, "Starting database population...");
            
            // Always check current counts for debugging
            int existingLessons = database.lessonDao().getLessonCount();
            int existingQuizzes = database.quizDao().getQuizCount();
            Log.d(TAG, "Existing lessons count: " + existingLessons + ", quizzes: " + existingQuizzes);
            
            // Only skip if we have BOTH lessons AND quizzes
            if (existingLessons > 0 && existingQuizzes > 0) {
                Log.d(TAG, "Database already populated with lessons and quizzes, skipping population");
                isPopulated = true;
                return;
            }
            
            // Clear and repopulate if incomplete data
            if (existingLessons > 0 || existingQuizzes > 0) {
                Log.d(TAG, "Incomplete data found, clearing and repopulating...");
                database.clearAllTables();
            }
            
            // Reset flag to allow population
            isPopulated = false;
            
            // Populate lessons
            Log.d(TAG, "Populating lessons...");
            populateLessons(database);
            
            // Populate quizzes
            Log.d(TAG, "Populating quizzes...");
            populateQuizzes(database);
            
            // Populate practice problems
            Log.d(TAG, "Populating practice problems...");
            populatePracticeProblems(database);
            
            int finalLessonsCount = database.lessonDao().getLessonCount();
            Log.d(TAG, "Population completed. Final lessons count: " + finalLessonsCount);
            
            isPopulated = true;
        }
    }

    /**
     * Helper method to build rich HTML lesson content with sections and styling
     */
    private static String buildLessonContent(String title, String overview, Map<String, String> sections, 
                                          String codeExample, String[] keyPoints, String summary) {
        StringBuilder contentBuilder = new StringBuilder();
        
        // Title & Overview section
        contentBuilder.append("<h2 style='color:#2196F3;'>").append(title).append("</h2>");
        contentBuilder.append("<div style='background-color:#E3F2FD;padding:10px;border-left:4px solid #2196F3;margin:10px 0;'>");
        contentBuilder.append("<p>").append(overview).append("</p>");
        contentBuilder.append("</div>");
        
        // Key sections
        for (Map.Entry<String, String> section : sections.entrySet()) {
            contentBuilder.append("<h3 style='color:#3F51B5;margin-top:16px;'>").append(section.getKey()).append("</h3>");
            contentBuilder.append("<p>").append(section.getValue()).append("</p>");
        }
        
        // Code example if provided
        if (codeExample != null && !codeExample.isEmpty()) {
            contentBuilder.append("<h3 style='color:#3F51B5;margin-top:16px;'>Code Example</h3>");
            contentBuilder.append("<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>");
            contentBuilder.append("<pre>").append(codeExample.replace("<", "&lt;").replace(">", "&gt;")).append("</pre>");
            contentBuilder.append("</div>");
        }
        
        // Key points if provided
        if (keyPoints != null && keyPoints.length > 0) {
            contentBuilder.append("<h3 style='color:#3F51B5;margin-top:16px;'>Key Points</h3>");
            contentBuilder.append("<ul style='background-color:#FFFDE7;padding:10px 10px 10px 30px;border-left:4px solid #FFC107;'>");
            for (String point : keyPoints) {
                contentBuilder.append("<li>").append(point).append("</li>");
            }
            contentBuilder.append("</ul>");
        }
        
        // Summary
        if (summary != null && !summary.isEmpty()) {
            contentBuilder.append("<div style='background-color:#E8F5E9;padding:10px;border-left:4px solid #4CAF50;margin:16px 0;'>");
            contentBuilder.append("<h3 style='color:#4CAF50;margin-top:0;'>Summary</h3>");
            contentBuilder.append("<p>").append(summary).append("</p>");
            contentBuilder.append("</div>");
        }
        
        return contentBuilder.toString();
    }

    private static void populateLessons(JavaBuddyDatabase database) {
        List<Lesson> lessons = new ArrayList<>();

        // Lesson 1: Introduction to Java
        Map<String, String> lesson1Sections = new HashMap<>();
        lesson1Sections.put("What is Java?", 
            "Java is a high-level, object-oriented programming language developed by Sun Microsystems (now Oracle) in 1995. " +
            "It was designed to be platform-independent and secure, making it ideal for a wide range of applications " +
            "from web servers and desktop applications to mobile apps and embedded systems.");
            
        lesson1Sections.put("The Java Platform", 
            "The Java platform consists of the Java Virtual Machine (JVM) and the Java API (Application Programming Interface). " +
            "When you compile Java code, it gets converted into bytecode (.class files), which the JVM then interprets and executes. " +
            "This two-step process is what enables Java's \"Write Once, Run Anywhere\" capability.");
            
        lesson1Sections.put("Key Features", 
            "<ul>" +
            "<li><strong>Platform Independence</strong>: Java programs can run on any device with a JVM</li>" +
            "<li><strong>Object-Oriented</strong>: Based on the concept of objects containing data and behavior</li>" +
            "<li><strong>Simple and Secure</strong>: No pointer arithmetic and automatic memory management</li>" +
            "<li><strong>Robust and Reliable</strong>: Strong type checking and exception handling</li>" +
            "<li><strong>Multithreaded</strong>: Built-in support for concurrent programming</li>" +
            "</ul>");

        String lesson1Overview = "Java is one of the world's most popular programming languages, known for its platform independence and reliability. " +
                               "This lesson introduces you to Java and its core features.";
        
        String[] lesson1KeyPoints = {
            "Java code is written once and can run on multiple platforms",
            "Java Virtual Machine (JVM) executes compiled Java bytecode",
            "Java is statically typed, meaning variable types must be declared",
            "Java's garbage collector automatically manages memory",
            "Java has a rich ecosystem of libraries and frameworks"
        };
        
        String lesson1CodeExample = 
            "// The classic Hello World program in Java\n" +
            "public class HelloWorld {\n" +
            "    public static void main(String[] args) {\n" +
            "        System.out.println(\"Hello, World!\");\n" +
            "    }\n" +
            "}";
            
        String lesson1Summary = "Java is a versatile, platform-independent programming language perfect for beginners and professionals alike. " +
                              "Its robust design, security features, and extensive library support make it an excellent choice for a wide " +
                              "range of applications from web servers to Android mobile apps.";
        
        String lesson1Content = buildLessonContent(
            "Introduction to Java & Programming Basics",
            lesson1Overview,
            lesson1Sections,
            lesson1CodeExample,
            lesson1KeyPoints,
            lesson1Summary
        );
        
        lessons.add(new Lesson(
            "Introduction to Java & Programming Basics",
            lesson1Content,
            "Introduction",
            1,
            "Beginner",
            lesson1CodeExample,
            lesson1Summary
        ));

        // Lesson 2: Variables and Data Types
        Map<String, String> lesson2Sections = new HashMap<>();
        lesson2Sections.put("What are Variables?", 
            "Variables are containers for storing data values. In Java, every variable must be declared with a specific data type " +
            "which determines what kind of values it can store and how much memory it will occupy. " +
            "Variables in Java must be declared before they can be used.");
            
        lesson2Sections.put("Primitive Data Types", 
            "<table border='1' style='border-collapse: collapse; width: 100%; margin: 10px 0;'>" +
            "<tr style='background-color: #E3F2FD;'><th>Data Type</th><th>Size</th><th>Range</th><th>Description</th></tr>" +
            "<tr><td>byte</td><td>8 bits</td><td>-128 to 127</td><td>Very small integer values</td></tr>" +
            "<tr><td>short</td><td>16 bits</td><td>-32,768 to 32,767</td><td>Small integer values</td></tr>" +
            "<tr><td>int</td><td>32 bits</td><td>-2<sup>31</sup> to 2<sup>31</sup>-1</td><td>Standard integer values</td></tr>" +
            "<tr><td>long</td><td>64 bits</td><td>-2<sup>63</sup> to 2<sup>63</sup>-1</td><td>Large integer values</td></tr>" +
            "<tr><td>float</td><td>32 bits</td><td>±3.4e±38</td><td>Floating-point numbers</td></tr>" +
            "<tr><td>double</td><td>64 bits</td><td>±1.7e±308</td><td>Precise floating-point numbers</td></tr>" +
            "<tr><td>char</td><td>16 bits</td><td>0 to 65,535</td><td>Single Unicode characters</td></tr>" +
            "<tr><td>boolean</td><td>1 bit</td><td>true or false</td><td>Logical values</td></tr>" +
            "</table>");
            
        lesson2Sections.put("Reference Types", 
            "Besides primitive types, Java also has reference types such as:" +
            "<ul>" +
            "<li><strong>String</strong>: For text data</li>" +
            "<li><strong>Arrays</strong>: For collections of values</li>" +
            "<li><strong>Classes</strong>: User-defined types</li>" +
            "<li><strong>Interfaces</strong>: Contract definitions</li>" +
            "</ul>" +
            "Unlike primitives which store actual values, reference types store addresses pointing to objects in memory.");

        lesson2Sections.put("Variable Declaration and Initialization", 
            "The syntax for declaring variables in Java is:<br>" +
            "<code style='background-color:#f8f8f8; padding:2px 5px;'>dataType variableName = value;</code><br><br>" +
            "For example:<br>" +
            "<code style='background-color:#f8f8f8; padding:2px 5px;'>int age = 25;</code><br>" +
            "<code style='background-color:#f8f8f8; padding:2px 5px;'>String name = \"John\";</code>");

        lesson2Sections.put("Naming Rules", 
            "<ul>" +
            "<li>Must start with a letter, underscore (_), or dollar sign ($)</li>" +
            "<li>Cannot start with numbers</li>" +
            "<li>Case-sensitive (age and Age are different variables)</li>" +
            "<li>Cannot use Java keywords (like class, int, for)</li>" +
            "<li>Should follow camelCase convention for readability</li>" +
            "</ul>");

        String lesson2Overview = "Variables allow programmers to store and manipulate data. Java requires each variable to have a specific " +
                               "data type that determines what kind of data it can hold.";
        
        String[] lesson2KeyPoints = {
            "Java is statically typed - all variables must have a declared type",
            "Primitive types store actual values while reference types store addresses",
            "Variable names should be descriptive and follow naming conventions",
            "Variable initialization provides an initial value",
            "Type casting allows conversion between compatible data types"
        };
        
        String lesson2CodeExample = 
            "// Variable declarations with different data types\n" +
            "int age = 25;\n" +
            "double salary = 50000.75;\n" +
            "char grade = 'A';\n" +
            "boolean isEmployed = true;\n" +
            "String name = \"Sarah Johnson\";\n\n" +
            "// Type conversion example\n" +
            "double price = 19.99;\n" +
            "int roundedPrice = (int)price;  // Explicit casting, result: 19";
            
        String lesson2Summary = "Variables are fundamental to any Java program, allowing storage and manipulation of different types of data. " +
                              "Understanding Java's data types and how to properly declare and use variables is essential for writing effective code.";
        
        String lesson2Content = buildLessonContent(
            "Variables and Data Types",
            lesson2Overview,
            lesson2Sections,
            lesson2CodeExample,
            lesson2KeyPoints,
            lesson2Summary
        );
        
        lessons.add(new Lesson(
            "Variables and Data Types",
            lesson2Content,
            "Data Types",
            2,
            "Beginner",
            "int age = 25;\ndouble price = 19.99;\nchar grade = 'A';\nboolean isActive = true;\nString name = \"John\";",
            "Variables store data values and must be declared with appropriate data types in Java."
        ));

        // Lesson 3: Operators and Expressions
        Map<String, String> lesson3Sections = new HashMap<>();
        lesson3Sections.put("What are Operators?", 
            "Operators are special symbols that perform specific operations on one, two, or three operands and then return a result. " +
            "Java provides many types of operators that can be used to perform various operations.");
            
        lesson3Sections.put("Arithmetic Operators", 
            "<table border='1' style='border-collapse: collapse; width: 100%; margin: 10px 0;'>" +
            "<tr style='background-color: #E8F5E9;'><th>Operator</th><th>Description</th><th>Example</th></tr>" +
            "<tr><td>+</td><td>Addition</td><td>a + b</td></tr>" +
            "<tr><td>-</td><td>Subtraction</td><td>a - b</td></tr>" +
            "<tr><td>*</td><td>Multiplication</td><td>a * b</td></tr>" +
            "<tr><td>/</td><td>Division</td><td>a / b</td></tr>" +
            "<tr><td>%</td><td>Modulus (remainder)</td><td>a % b</td></tr>" +
            "<tr><td>++</td><td>Increment</td><td>a++ or ++a</td></tr>" +
            "<tr><td>--</td><td>Decrement</td><td>a-- or --a</td></tr>" +
            "</table>" +
            "<p>Note: The increment (++) and decrement (--) operators have prefix (++a) and postfix (a++) forms. " +
            "In prefix form, the operation happens before the value is used. In postfix form, the operation happens after the value is used.</p>");
            
        lesson3Sections.put("Assignment Operators", 
            "<table border='1' style='border-collapse: collapse; width: 100%; margin: 10px 0;'>" +
            "<tr style='background-color: #E3F2FD;'><th>Operator</th><th>Description</th><th>Example</th><th>Equivalent to</th></tr>" +
            "<tr><td>=</td><td>Simple assignment</td><td>a = 5</td><td>a = 5</td></tr>" +
            "<tr><td>+=</td><td>Add and assign</td><td>a += 3</td><td>a = a + 3</td></tr>" +
            "<tr><td>-=</td><td>Subtract and assign</td><td>a -= 3</td><td>a = a - 3</td></tr>" +
            "<tr><td>*=</td><td>Multiply and assign</td><td>a *= 3</td><td>a = a * 3</td></tr>" +
            "<tr><td>/=</td><td>Divide and assign</td><td>a /= 3</td><td>a = a / 3</td></tr>" +
            "<tr><td>%=</td><td>Modulus and assign</td><td>a %= 3</td><td>a = a % 3</td></tr>" +
            "</table>");

        lesson3Sections.put("Comparison Operators", 
            "<table border='1' style='border-collapse: collapse; width: 100%; margin: 10px 0;'>" +
            "<tr style='background-color: #FFF8E1;'><th>Operator</th><th>Description</th><th>Example</th></tr>" +
            "<tr><td>==</td><td>Equal to</td><td>a == b</td></tr>" +
            "<tr><td>!=</td><td>Not equal to</td><td>a != b</td></tr>" +
            "<tr><td>></td><td>Greater than</td><td>a > b</td></tr>" +
            "<tr><td><</td><td>Less than</td><td>a < b</td></tr>" +
            "<tr><td>>=</td><td>Greater than or equal to</td><td>a >= b</td></tr>" +
            "<tr><td><=</td><td>Less than or equal to</td><td>a <= b</td></tr>" +
            "</table>" +
            "<p>Note: When comparing objects (reference types), == compares if they refer to the same object, " +
            "not if they have the same content. For content comparison, use the equals() method.</p>");

        lesson3Sections.put("Logical Operators", 
            "<table border='1' style='border-collapse: collapse; width: 100%; margin: 10px 0;'>" +
            "<tr style='background-color: #F3E5F5;'><th>Operator</th><th>Description</th><th>Example</th></tr>" +
            "<tr><td>&&</td><td>Logical AND</td><td>(a > b) && (c > d)</td></tr>" +
            "<tr><td>||</td><td>Logical OR</td><td>(a > b) || (c > d)</td></tr>" +
            "<tr><td>!</td><td>Logical NOT</td><td>!(a > b)</td></tr>" +
            "</table>" +
            "<p>Logical operators use short-circuit evaluation. For example, in (expr1 && expr2), if expr1 is false, " +
            "expr2 is not evaluated because the result will always be false.</p>");

        lesson3Sections.put("Bitwise Operators", 
            "<p>Java also provides operators to manipulate individual bits in integers:</p>" +
            "<ul>" +
            "<li><strong>&</strong>: Bitwise AND</li>" +
            "<li><strong>|</strong>: Bitwise OR</li>" +
            "<li><strong>^</strong>: Bitwise XOR</li>" +
            "<li><strong>~</strong>: Bitwise complement</li>" +
            "<li><strong><<</strong>: Left shift</li>" +
            "<li><strong>>></strong>: Right shift</li>" +
            "<li><strong>>>></strong>: Unsigned right shift</li>" +
            "</ul>");

        lesson3Sections.put("Operator Precedence", 
            "<p>Java operators have different precedence levels, which determine the order of evaluation when multiple " +
            "operators appear in an expression. Here's a simplified precedence order (highest to lowest):</p>" +
            "<ol>" +
            "<li>Postfix operators (expr++, expr--)</li>" +
            "<li>Prefix operators (++expr, --expr, +expr, -expr, !)</li>" +
            "<li>Multiplicative (*, /, %)</li>" +
            "<li>Additive (+, -)</li>" +
            "<li>Relational (<, >, <=, >=)</li>" +
            "<li>Equality (==, !=)</li>" +
            "<li>Logical AND (&&)</li>" +
            "<li>Logical OR (||)</li>" +
            "<li>Assignment (=, +=, -=, etc.)</li>" +
            "</ol>" +
            "<p>When in doubt, use parentheses to make the order of operations explicit.</p>");

        String lesson3Overview = "Operators are special symbols that perform operations on variables and values. " +
                              "They form the foundation of any programming language by allowing us to manipulate data " +
                              "and make decisions based on conditions.";
        
        String[] lesson3KeyPoints = {
            "Operators allow manipulation and comparison of values",
            "Arithmetic operators perform basic mathematical operations",
            "Comparison operators evaluate relationships between values",
            "Logical operators combine boolean expressions",
            "Java operators follow a specific order of precedence",
            "Use parentheses to make complex expressions clearer"
        };
        
        String lesson3CodeExample = 
            "// Arithmetic operators\n" +
            "int x = 10, y = 5;\n" +
            "int sum = x + y;      // 15\n" +
            "int product = x * y;  // 50\n" +
            "\n" +
            "// Increment/decrement\n" +
            "int a = 5;\n" +
            "System.out.println(a++);  // Prints 5, then a becomes 6\n" +
            "System.out.println(++a);  // a becomes 7, then prints 7\n" +
            "\n" +
            "// Logical operators\n" +
            "boolean result = (x > y) && (x == 10);  // true\n" +
            "boolean result2 = (x < y) || (y == 5);  // true";
            
        String lesson3Summary = "Operators enable you to manipulate variables and data values in your programs. " +
                              "Understanding Java's diverse set of operators and their precedence is crucial for writing " +
                              "efficient and bug-free code. Always consider operator precedence and use parentheses when " +
                              "needed to ensure your expressions are evaluated as intended.";
        
        String lesson3Content = buildLessonContent(
            "Operators and Expressions",
            lesson3Overview,
            lesson3Sections,
            lesson3CodeExample,
            lesson3KeyPoints,
            lesson3Summary
        );
        
        lessons.add(new Lesson(
            "Operators and Expressions",
            lesson3Content,
            "Operators",
            3,
            "Beginner",
            "int a = 10, b = 5;\nint sum = a + b;     // 15\nint diff = a - b;    // 5\nboolean result = (a > b) && (sum > 10); // true",
            "Operators perform operations on variables and values, including arithmetic, assignment, comparison, and logical operations."
        ));

        // Lesson 4: Control Structures (if-else, switch)
        Map<String, String> lesson4Sections = new HashMap<>();
        lesson4Sections.put("Decision Making in Programming", 
            "Control structures are the backbone of programming logic, allowing your code to make decisions and " +
            "execute different paths based on conditions. They determine the flow of execution in a program, " +
            "making your applications responsive and intelligent.");
            
        lesson4Sections.put("if-else Statement", 
            "The if-else statement is the most fundamental control structure that allows conditional execution of code. " +
            "It works by evaluating a boolean expression and executing specific code blocks based on whether the condition is true or false.<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>if (condition) {\n" +
            "    // Execute this code if condition is true\n" +
            "} else if (anotherCondition) {\n" +
            "    // Execute this code if first condition is false but second is true\n" +
            "} else {\n" +
            "    // Execute this code if all conditions above are false\n" +
            "}</pre></div><br>" +
            "You can have any number of else-if blocks, but only one if and one optional else block.");
            
        lesson4Sections.put("Ternary Operator", 
            "The ternary operator is a shorthand for simple if-else statements, providing a concise way to make decisions.<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>variable = (condition) ? valueIfTrue : valueIfFalse;</pre></div><br>" +
            "For example: <code>String status = (age >= 18) ? \"Adult\" : \"Minor\";</code>");

        lesson4Sections.put("switch Statement", 
            "The switch statement is ideal for multi-way branching based on a single value. It compares an expression against multiple case values " +
            "and executes the matching block.<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>switch (expression) {\n" +
            "    case value1:\n" +
            "        // Code to execute if expression equals value1\n" +
            "        break;  // Exits the switch block\n" +
            "    case value2:\n" +
            "        // Code to execute if expression equals value2\n" +
            "        break;\n" +
            "    default:\n" +
            "        // Code to execute if no case matches\n" +
            "}</pre></div><br>" +
            "<strong>Important:</strong> Without the break statement, execution \"falls through\" to the next case, which is rarely desired.");

        lesson4Sections.put("Switch Expressions (Java 14+)", 
            "Modern Java introduced enhanced switch expressions that are more concise and safer:<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>String day = switch (dayNumber) {\n" +
            "    case 1 -> \"Monday\";\n" +
            "    case 2 -> \"Tuesday\";\n" +
            "    case 3 -> \"Wednesday\";\n" +
            "    default -> \"Invalid day\";\n" +
            "};</pre></div><br>" +
            "These newer switch expressions don't require break statements and can directly return values.");

        lesson4Sections.put("Nested Control Structures", 
            "Control structures can be nested inside each other to handle complex decision-making scenarios:<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>if (isWeekend) {\n" +
            "    if (isMorning) {\n" +
            "        // Weekend morning routine\n" +
            "    } else {\n" +
            "        // Weekend afternoon/evening routine\n" +
            "    }\n" +
            "} else {\n" +
            "    // Weekday routine\n" +
            "}</pre></div><br>" +
            "However, be careful with excessive nesting as it can make code harder to read and maintain.");

        String lesson4Overview = "Control structures are programming constructs that determine how your code executes, " +
                               "allowing for decision-making and different execution paths based on conditions. " +
                               "They're essential for creating dynamic and responsive applications.";
        
        String[] lesson4KeyPoints = {
            "Control structures determine the flow of program execution",
            "if-else statements handle binary and multiple condition checks",
            "switch statements are ideal for multi-way branching based on a single value",
            "The ternary operator provides a compact form of if-else for simple cases",
            "Always use braces {} even for single-line statements to prevent logic errors",
            "Be mindful of \"fall-through\" in switch statements without break"
        };
        
        String lesson4CodeExample = 
            "// Grade calculator with if-else\n" +
            "int score = 85;\n" +
            "char grade;\n\n" +
            "if (score >= 90) {\n" +
            "    grade = 'A';\n" +
            "} else if (score >= 80) {\n" +
            "    grade = 'B';\n" +
            "} else if (score >= 70) {\n" +
            "    grade = 'C';\n" +
            "} else if (score >= 60) {\n" +
            "    grade = 'D';\n" +
            "} else {\n" +
            "    grade = 'F';\n" +
            "}\n\n" +
            "// Day of week with switch\n" +
            "int day = 3;\n" +
            "String dayName;\n\n" +
            "switch (day) {\n" +
            "    case 1:\n" +
            "        dayName = \"Monday\";\n" +
            "        break;\n" +
            "    case 2:\n" +
            "        dayName = \"Tuesday\";\n" +
            "        break;\n" +
            "    case 3:\n" +
            "        dayName = \"Wednesday\";\n" +
            "        break;\n" +
            "    // other days...\n" +
            "    default:\n" +
            "        dayName = \"Invalid day\";\n" +
            "}";
            
        String lesson4Summary = "Control structures are fundamental to programming logic, allowing your code to make decisions " +
                              "and follow different execution paths. Mastering if-else statements and switch cases is essential " +
                              "for writing effective and responsive Java programs that can handle various conditions and scenarios.";
        
        String lesson4Content = buildLessonContent(
            "Control Structures (if-else, switch)",
            lesson4Overview,
            lesson4Sections,
            lesson4CodeExample,
            lesson4KeyPoints,
            lesson4Summary
        );
        
        lessons.add(new Lesson(
            "Control Structures (if-else, switch)",
            lesson4Content,
            "Control Flow",
            4,
            "Beginner",
            "int score = 85;\nif (score >= 90) {\n    System.out.println(\"Grade: A\");\n} else if (score >= 80) {\n    System.out.println(\"Grade: B\");\n} else {\n    System.out.println(\"Grade: C\");\n}\n\nint day = 3;\nswitch (day) {\n    case 1: System.out.println(\"Monday\"); break;\n    case 2: System.out.println(\"Tuesday\"); break;\n    case 3: System.out.println(\"Wednesday\"); break;\n    default: System.out.println(\"Invalid day\");\n}",
            "Control structures direct program flow using conditions and decision-making statements."
        ));

        // Lesson 5: Loops (for, while, do-while)
        Map<String, String> lesson5Sections = new HashMap<>();
        lesson5Sections.put("Introduction to Loops", 
            "Loops are control structures that allow you to repeat a block of code multiple times. They are essential " +
            "for automating repetitive tasks, processing collections of data, and implementing algorithms that require " +
            "iteration. Java provides several loop constructs to handle different scenarios.");
            
        lesson5Sections.put("for Loop", 
            "The for loop is ideal when you know exactly how many times you want to execute a block of code. " +
            "It consists of three parts: initialization, condition, and increment/decrement.<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>for (initialization; condition; increment/decrement) {\n" +
            "    // Code to repeat\n" +
            "}</pre></div><br>" +
            "For example:<br>" +
            "<code>for (int i = 0; i < 5; i++) {<br>" +
            "    System.out.println(\"Count: \" + i);<br>" +
            "}</code><br><br>" +
            "The execution flow is:<br>" +
            "1. Execute initialization once<br>" +
            "2. Check if condition is true; if false, exit loop<br>" +
            "3. Execute the loop body<br>" +
            "4. Execute the increment/decrement<br>" +
            "5. Return to step 2");
            
        lesson5Sections.put("while Loop", 
            "The while loop repeats a block of code as long as a specified condition is true. It's useful when " +
            "you don't know in advance how many iterations you'll need.<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>while (condition) {\n" +
            "    // Code to repeat\n" +
            "}</pre></div><br>" +
            "For example:<br>" +
            "<code>int count = 0;<br>" +
            "while (count < 5) {<br>" +
            "    System.out.println(\"Count: \" + count);<br>" +
            "    count++;<br>" +
            "}</code><br><br>" +
            "<strong>Important:</strong> Ensure that the condition will eventually become false, or you'll create an infinite loop.");

        lesson5Sections.put("do-while Loop", 
            "The do-while loop is similar to the while loop, but it guarantees that the code block will execute at least once, " +
            "even if the condition is initially false.<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>do {\n" +
            "    // Code to repeat\n" +
            "} while (condition);</pre></div><br>" +
            "For example:<br>" +
            "<code>int count = 0;<br>" +
            "do {<br>" +
            "    System.out.println(\"Count: \" + count);<br>" +
            "    count++;<br>" +
            "} while (count < 5);</code><br><br>" +
            "The do-while loop is particularly useful for menu systems and input validation where you want to ensure " +
            "the user is prompted at least once.");

        lesson5Sections.put("Enhanced for Loop (for-each)", 
            "The enhanced for loop (also called for-each) simplifies iterating through arrays and collections. " +
            "It's cleaner and less prone to errors than traditional indexing.<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>for (elementType element : collection) {\n" +
            "    // Process element\n" +
            "}</pre></div><br>" +
            "For example:<br>" +
            "<code>String[] fruits = {\"Apple\", \"Banana\", \"Orange\"};<br>" +
            "for (String fruit : fruits) {<br>" +
            "    System.out.println(fruit);<br>" +
            "}</code><br><br>" +
            "<strong>Note:</strong> You cannot modify the collection's structure while using the enhanced for loop.");

        lesson5Sections.put("Loop Control Statements", 
            "<strong>break</strong>: Terminates the loop immediately and transfers control to the statement following the loop.<br><br>" +
            "<code>for (int i = 0; i < 10; i++) {<br>" +
            "    if (i == 5) {<br>" +
            "        break; // Exit loop when i reaches 5<br>" +
            "    }<br>" +
            "    System.out.println(i);<br>" +
            "}</code><br><br>" +
            "<strong>continue</strong>: Skips the current iteration and proceeds to the next iteration.<br><br>" +
            "<code>for (int i = 0; i < 10; i++) {<br>" +
            "    if (i % 2 == 0) {<br>" +
            "        continue; // Skip even numbers<br>" +
            "    }<br>" +
            "    System.out.println(i); // Prints only odd numbers<br>" +
            "}</code>");
            
        lesson5Sections.put("Nested Loops", 
            "Loops can be nested inside other loops to handle multi-dimensional data structures or complex iteration patterns.<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>for (int i = 0; i < 3; i++) {       // Outer loop\n" +
            "    for (int j = 0; j < 3; j++) {   // Inner loop\n" +
            "        System.out.print(\"(\" + i + \",\" + j + \") \");\n" +
            "    }\n" +
            "    System.out.println();\n" +
            "}</pre></div><br>" +
            "This creates a grid pattern output.<br><br>" +
            "<strong>Note:</strong> Be careful with nested loops as they can significantly increase the number of operations " +
            "and affect performance (O(n²) complexity for simple nested loops).");

        String lesson5Overview = "Loops are fundamental programming constructs that allow you to execute a block of code " +
                               "multiple times. They're essential for tasks that require repetition, such as processing " +
                               "collections of data, implementing algorithms, and automating repetitive operations.";
        
        String[] lesson5KeyPoints = {
            "for loops are ideal when you know the exact number of iterations",
            "while loops execute as long as a condition remains true",
            "do-while loops always execute at least once",
            "Enhanced for loops simplify iteration over arrays and collections",
            "break exits a loop completely",
            "continue skips the current iteration and moves to the next"
        };
        
        String lesson5CodeExample = 
            "// Print the sum of numbers from 1 to 10 using different loops\n\n" +
            "// Using a for loop\n" +
            "int sum1 = 0;\n" +
            "for (int i = 1; i <= 10; i++) {\n" +
            "    sum1 += i;\n" +
            "}\n" +
            "System.out.println(\"Sum using for loop: \" + sum1);\n\n" +
            "// Using a while loop\n" +
            "int sum2 = 0;\n" +
            "int j = 1;\n" +
            "while (j <= 10) {\n" +
            "    sum2 += j;\n" +
            "    j++;\n" +
            "}\n" +
            "System.out.println(\"Sum using while loop: \" + sum2);\n\n" +
            "// Process an array with enhanced for loop\n" +
            "int[] numbers = {1, 2, 3, 4, 5};\n" +
            "int sum3 = 0;\n" +
            "for (int num : numbers) {\n" +
            "    sum3 += num;\n" +
            "}\n" +
            "System.out.println(\"Sum of array: \" + sum3);";
            
        String lesson5Summary = "Loops are powerful tools that help you automate repetitive tasks and process collections of data. " +
                              "Choosing the right loop construct for your specific scenario can make your code more efficient and readable. " +
                              "Remember to ensure that your loops have proper termination conditions to avoid infinite loops.";
        
        String lesson5Content = buildLessonContent(
            "Loops (for, while, do-while)",
            lesson5Overview,
            lesson5Sections,
            lesson5CodeExample,
            lesson5KeyPoints,
            lesson5Summary
        );
        
        lessons.add(new Lesson(
            "Loops (for, while, do-while)",
            lesson5Content,
            "Loops",
            5,
            "Beginner",
            "// for loop\nfor (int i = 1; i <= 5; i++) {\n    System.out.println(\"Count: \" + i);\n}\n\n// while loop\nint count = 1;\nwhile (count <= 3) {\n    System.out.println(\"While: \" + count);\n    count++;\n}\n\n// do-while loop\nint num = 1;\ndo {\n    System.out.println(\"Do-While: \" + num);\n    num++;\n} while (num <= 2);",
            "Loops enable repetitive execution of code blocks with different control mechanisms."
        ));

        // Lesson 6: Arrays and String Manipulation
        Map<String, String> lesson6Sections = new HashMap<>();
        lesson6Sections.put("Introduction to Arrays", 
            "An array is a container object that holds a fixed number of values of a single type. " +
            "Arrays provide an efficient way to group related data and process it as a single unit. " +
            "The elements in an array are stored in contiguous memory locations, making access efficient.");
            
        lesson6Sections.put("Array Declaration and Initialization", 
            "There are several ways to declare and initialize arrays in Java:<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>// Declaration only\n" +
            "int[] numbers;            // Preferred syntax\n" +
            "int scores[];             // Alternative syntax\n\n" +
            "// Declaration with size allocation\n" +
            "int[] counts = new int[5];  // Creates array of 5 integers with default value 0\n\n" +
            "// Declaration with initialization\n" +
            "int[] values = {10, 20, 30, 40, 50};  // Size determined by values\n\n" +
            "// Declare, then initialize later\n" +
            "String[] fruits;\n" +
            "fruits = new String[] {\"Apple\", \"Banana\", \"Orange\"};</pre></div>");
            
        lesson6Sections.put("Array Operations", 
            "<strong>Accessing Elements</strong><br>" +
            "Array elements are accessed using zero-based indices:<br>" +
            "<code>int firstValue = values[0];  // Access first element<br>" +
            "values[2] = 35;             // Modify third element</code><br><br>" +
            "<strong>Array Length</strong><br>" +
            "The length property gives the size of the array:<br>" +
            "<code>int arraySize = values.length;  // returns 5</code><br><br>" +
            "<strong>Iterating Through Arrays</strong><br>" +
            "Arrays can be traversed using loops:<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>// Using traditional for loop\n" +
            "for (int i = 0; i < values.length; i++) {\n" +
            "    System.out.println(values[i]);\n" +
            "}\n\n" +
            "// Using enhanced for loop (for-each)\n" +
            "for (int value : values) {\n" +
            "    System.out.println(value);\n" +
            "}</pre></div>");

        lesson6Sections.put("Multidimensional Arrays", 
            "Java supports multidimensional arrays, which are essentially arrays of arrays:<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>// 2D array declaration\n" +
            "int[][] matrix = new int[3][4];  // 3 rows, 4 columns\n\n" +
            "// 2D array initialization\n" +
            "int[][] grid = {\n" +
            "    {1, 2, 3},\n" +
            "    {4, 5, 6},\n" +
            "    {7, 8, 9}\n" +
            "};\n\n" +
            "// Accessing elements\n" +
            "int value = grid[1][2];  // Returns 6 (row 1, column 2)</pre></div>");

        lesson6Sections.put("Common Array Problems", 
            "<table border='1' style='border-collapse: collapse; width: 100%; margin: 10px 0;'>" +
            "<tr style='background-color: #FFF3E0;'><th>Problem</th><th>Description</th><th>Solution</th></tr>" +
            "<tr><td>ArrayIndexOutOfBoundsException</td><td>Accessing an index that doesn't exist</td><td>Always check array bounds</td></tr>" +
            "<tr><td>Fixed Size</td><td>Arrays cannot change size after creation</td><td>Use ArrayList for dynamic sizing</td></tr>" +
            "<tr><td>Primitive Limitations</td><td>Arrays are fixed-type and size</td><td>Use Java collections for more flexibility</td></tr>" +
            "</table>");

        lesson6Sections.put("String Basics", 
            "Strings in Java are immutable objects that represent sequences of characters. They're not primitive types " +
            "but are so fundamental they have special support in the language.<br><br>" +
            "String creation:<br>" +
            "<code>String name = \"John Doe\";\n" +
            "String message = new String(\"Hello\");</code>");

        lesson6Sections.put("String Methods", 
            "<table border='1' style='border-collapse: collapse; width: 100%; margin: 10px 0;'>" +
            "<tr style='background-color: #E8F5E9;'><th>Method</th><th>Description</th><th>Example</th><th>Result</th></tr>" +
            "<tr><td>length()</td><td>Returns string length</td><td>\"Java\".length()</td><td>4</td></tr>" +
            "<tr><td>charAt(index)</td><td>Returns character at position</td><td>\"Java\".charAt(2)</td><td>'v'</td></tr>" +
            "<tr><td>substring(start, end)</td><td>Extracts substring</td><td>\"JavaScript\".substring(0, 4)</td><td>\"Java\"</td></tr>" +
            "<tr><td>toUpperCase()</td><td>Converts to uppercase</td><td>\"Java\".toUpperCase()</td><td>\"JAVA\"</td></tr>" +
            "<tr><td>toLowerCase()</td><td>Converts to lowercase</td><td>\"Java\".toLowerCase()</td><td>\"java\"</td></tr>" +
            "<tr><td>equals()</td><td>Compares strings</td><td>\"Java\".equals(\"java\")</td><td>false</td></tr>" +
            "<tr><td>equalsIgnoreCase()</td><td>Case-insensitive comparison</td><td>\"Java\".equalsIgnoreCase(\"java\")</td><td>true</td></tr>" +
            "<tr><td>contains()</td><td>Checks if string contains substring</td><td>\"JavaScript\".contains(\"Java\")</td><td>true</td></tr>" +
            "<tr><td>indexOf()</td><td>Returns index of first occurrence</td><td>\"Hello\".indexOf('l')</td><td>2</td></tr>" +
            "<tr><td>split()</td><td>Splits string into array</td><td>\"a,b,c\".split(\",\")</td><td>[\"a\", \"b\", \"c\"]</td></tr>" +
            "<tr><td>replace()</td><td>Replaces characters/substrings</td><td>\"Hello\".replace('e', 'a')</td><td>\"Hallo\"</td></tr>" +
            "</table>");

        lesson6Sections.put("String Immutability", 
            "Strings in Java are immutable, meaning their values cannot be changed after creation. Every operation " +
            "that appears to modify a string actually creates a new string object.<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>String s1 = \"Hello\";\n" +
            "String s2 = s1 + \" World\";  // Creates a new string, s1 is unchanged\n" +
            "System.out.println(s1);     // Outputs \"Hello\"\n" +
            "System.out.println(s2);     // Outputs \"Hello World\"</pre></div><br>" +
            "For intensive string concatenation operations, use StringBuilder to improve performance:<br><br>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>StringBuilder sb = new StringBuilder();\n" +
            "sb.append(\"Hello\");\n" +
            "sb.append(\" World\");\n" +
            "String result = sb.toString();  // \"Hello World\"</pre></div>");

        String lesson6Overview = "Arrays and Strings are fundamental data structures in Java. Arrays allow you to store multiple values " +
                               "of the same type in a single variable, while Strings provide rich functionality for text manipulation. " +
                               "Understanding these constructs is essential for effective Java programming.";
        
        String[] lesson6KeyPoints = {
            "Arrays store multiple values of the same type with fixed size",
            "Array indices are zero-based (first element is at index 0)",
            "ArrayIndexOutOfBoundsException occurs when accessing invalid indices",
            "Strings are immutable - operations create new String objects",
            "Java provides many built-in methods for string manipulation",
            "Use StringBuilder for efficient string concatenation"
        };
        
        String lesson6CodeExample = 
            "// Array manipulation example\n" +
            "int[] numbers = {10, 20, 30, 40, 50};\n" +
            "int sum = 0;\n\n" +
            "// Calculate sum using for-each loop\n" +
            "for (int num : numbers) {\n" +
            "    sum += num;\n" +
            "}\n" +
            "System.out.println(\"Sum: \" + sum);\n\n" +
            "// String manipulation example\n" +
            "String message = \"Hello, Java Programming!\";\n" +
            "System.out.println(\"Length: \" + message.length());\n\n" +
            "// Extract substring\n" +
            "String sub = message.substring(7, 11);\n" +
            "System.out.println(\"Substring: \" + sub);  // \"Java\"\n\n" +
            "// Split string into words\n" +
            "String[] words = message.split(\" \");\n" +
            "System.out.println(\"Word count: \" + words.length);";
            
        String lesson6Summary = "Arrays and Strings are powerful tools in Java programming. Arrays provide efficient storage " +
                              "and access to collections of related data, while Strings offer extensive functionality for " +
                              "text manipulation. Mastering these concepts is crucial for building effective Java applications.";
        
        String lesson6Content = buildLessonContent(
            "Arrays and String Manipulation",
            lesson6Overview,
            lesson6Sections,
            lesson6CodeExample,
            lesson6KeyPoints,
            lesson6Summary
        );
        
        lessons.add(new Lesson(
            "Arrays and String Manipulation",
            lesson6Content,
            "Arrays",
            6,
            "Intermediate",
            "// Array example\nint[] scores = {85, 92, 78, 96, 88};\nSystem.out.println(\"First score: \" + scores[0]);\nSystem.out.println(\"Array length: \" + scores.length);\n\n// String example\nString text = \"Hello World\";\nSystem.out.println(\"Length: \" + text.length());\nSystem.out.println(\"Uppercase: \" + text.toUpperCase());\nSystem.out.println(\"Contains 'World': \" + text.contains(\"World\"));",
            "Arrays store multiple elements of the same type, while strings provide extensive text manipulation capabilities."
        ));

        // Lesson 7: Methods and Functions
        lessons.add(new Lesson(
            "Methods and Functions",
            "<h2 style='color:#3F51B5;'>Methods and Functions</h2>" +
            "<p>Methods are blocks of code that perform specific tasks and can be called multiple times. They are fundamental to Java programming and help organize code into reusable, manageable components.</p>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Method Declaration</h3>" +
            "<div style='background-color:#F5F5F5;padding:10px;border-radius:4px;font-family:monospace;'>" +
            "<pre>accessModifier returnType methodName(parameterList) {\n    // method body with statements\n    return value; // if returnType is not void\n}</pre></div>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Access Modifiers</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>public</strong>: Accessible from any class</li>" +
            "<li><strong>private</strong>: Accessible only within the same class</li>" +
            "<li><strong>protected</strong>: Accessible within package and by subclasses</li>" +
            "<li><strong>default</strong> (no modifier): Accessible only within the same package</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Return Types</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>void</strong>: Method doesn't return any value</li>" +
            "<li><strong>primitive types</strong>: int, double, boolean, etc.</li>" +
            "<li><strong>reference types</strong>: String, arrays, custom objects</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Method Types</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Static methods</strong>: Belong to the class itself" +
            "<ul><li>Called using ClassName.methodName()</li>" +
            "<li>Cannot access instance variables/methods directly</li>" +
            "<li>Used for utility functions and operations that don't require object state</li></ul></li>" +
            "<li><strong>Instance methods</strong>: Belong to objects" +
            "<ul><li>Called using objectReference.methodName()</li>" +
            "<li>Can access both instance and static variables/methods</li>" +
            "<li>Operate on object state</li></ul></li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Method Overloading</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li>Multiple methods with same name but different parameters</li>" +
            "<li>Differ in number, type, or order of parameters</li>" +
            "<li>Return type alone is not enough to overload</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Parameter Passing</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Primitives</strong>: Pass by value (a copy is passed)</li>" +
            "<li><strong>Objects</strong>: Pass by reference value (reference is passed)</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Variable Scope</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li>Method parameters and local variables exist only within the method</li>" +
            "<li>Local variables must be initialized before use</li>" +
            "</ul>",
            "Methods",
            7,
            "Intermediate",
            "public class Calculator {\n    // Class (static) variable\n    private static double PI = 3.14159;\n    \n    // Instance variable\n    private String model;\n    \n    // Constructor\n    public Calculator(String model) {\n        this.model = model;\n    }\n    \n    // Static method - called with Calculator.add(5, 3)\n    public static int add(int a, int b) {\n        return a + b;\n    }\n    \n    // Instance method - needs Calculator object to call\n    public double multiply(double x, double y) {\n        System.out.println(\"Using \" + model + \" calculator\");\n        return x * y;\n    }\n    \n    // Method overloading - same name, different parameters\n    public int add(int a, int b, int c) {\n        // Calls the other add method - reusing code\n        return add(a, b) + c;\n    }\n    \n    // Method with primitive parameters (pass by value)\n    public void incrementNumber(int number) {\n        number = number + 1;  // Only affects local copy\n    }\n    \n    // Method that returns different values based on condition\n    public String getCalculatorGrade() {\n        if (model.contains(\"Pro\")) {\n            return \"Professional\";\n        } else {\n            return \"Standard\";\n        }\n    }\n    \n    // Method using static variable\n    public double calculateCircleArea(double radius) {\n        return PI * radius * radius;\n    }\n}\n\n// Usage\nint sum = Calculator.add(5, 3);  // Static method call\nCalculator calc = new Calculator(\"TI-84 Pro\");\ndouble product = calc.multiply(4.5, 2.0);  // 9.0\nint tripleSum = calc.add(5, 3, 8);  // 16\n\nint x = 10;\ncalc.incrementNumber(x);  // x still equals 10 after this call\nString grade = calc.getCalculatorGrade();  // \"Professional\"",
            "Methods encapsulate functionality, promote code reuse, and improve program organization. They allow developers to break down complex problems into smaller, manageable pieces, making code easier to write, test, debug, and maintain."
        ));

        // Lesson 8: Object-Oriented Programming Basics
        lessons.add(new Lesson(
            "Object-Oriented Programming Basics",
            "<h2 style='color:#3F51B5;'>Object-Oriented Programming (OOP)</h2>" +
            "<p>Object-Oriented Programming (OOP) is a programming paradigm that organizes code around objects, combining data and behavior. OOP models real-world entities and provides benefits like code reusability, modularity, and easier maintenance.</p>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Four Pillars of OOP</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Encapsulation</strong>: Bundling data and methods that operate on the data, restricting direct access to some components</li>" +
            "<li><strong>Inheritance</strong>: Creating new classes (child) from existing ones (parent), inheriting their properties and behaviors</li>" +
            "<li><strong>Polymorphism</strong>: The ability of different objects to respond to the same method call in ways specific to their data type or class</li>" +
            "<li><strong>Abstraction</strong>: Hiding complex implementation details and showing only essential features</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Class vs Object</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Class</strong>: Blueprint or template that defines properties and behaviors</li>" +
            "<li><strong>Object</strong>: Instance of a class, representing a specific entity with its own state</li>" +
            "<li><strong>Example</strong>: Car (class) vs your specific Toyota Corolla (object)</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Class Components</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Fields/Attributes</strong>: Variables that store object state</li>" +
            "<li><strong>Methods</strong>: Functions that define object behavior</li>" +
            "<li><strong>Constructors</strong>: Special methods that initialize objects</li>" +
            "<li><strong>Access modifiers</strong>: Control visibility of members (public, private, protected, default)</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Constructors in Detail</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Default constructor</strong>: No parameters, provided by Java if no constructors defined</li>" +
            "<li><strong>Parameterized constructor</strong>: Takes parameters to initialize object state</li>" +
            "<li><strong>Constructor overloading</strong>: Multiple constructors with different parameters</li>" +
            "<li><strong>Constructor chaining</strong>: Calling one constructor from another using this()</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Encapsulation Implementation</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Private fields</strong>: Restrict direct access to data</li>" +
            "<li><strong>Public getter/setter methods</strong>: Control access and validation</li>" +
            "<li><strong>Benefits</strong>: Data hiding, flexibility to change implementation, data validation</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Special Keywords</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>this</strong>: References current object instance (resolves name conflicts)</li>" +
            "<li><strong>new</strong>: Creates object instances</li>" +
            "<li><strong>null</strong>: Represents absence of object reference</li>" +
            "</ul>",
            "OOP Basics",
            8,
            "Intermediate",
            "public class BankAccount {\n    // Private fields - Encapsulation\n    private String accountNumber;\n    private String owner;\n    private double balance;\n    private static double interestRate = 0.03;  // Class variable shared by all instances\n    \n    // Static initializer block - runs once when class is loaded\n    static {\n        System.out.println(\"BankAccount class loaded\");\n        // Could initialize interestRate from configuration\n    }\n    \n    // Instance initializer block - runs before every constructor\n    {\n        System.out.println(\"Creating a new account\");\n        this.balance = 0.0;\n    }\n    \n    // Default constructor\n    public BankAccount() {\n        this(\"Unknown\", \"00000000\");\n        // Constructor chaining - calls parameterized constructor\n    }\n    \n    // Parameterized constructor\n    public BankAccount(String owner, String accountNumber) {\n        this.owner = owner;\n        this.accountNumber = accountNumber;\n        // balance already initialized to 0.0 by instance initializer block\n    }\n    \n    // Overloaded constructor with initial deposit\n    public BankAccount(String owner, String accountNumber, double initialDeposit) {\n        this(owner, accountNumber);  // Call the other constructor first\n        this.deposit(initialDeposit);  // Then make the deposit\n    }\n    \n    // Instance methods - define behavior\n    public void deposit(double amount) {\n        if (amount > 0) {\n            balance += amount;\n            System.out.println(\"Deposited: $\" + amount);\n        } else {\n            System.out.println(\"Invalid deposit amount\");\n        }\n    }\n    \n    public boolean withdraw(double amount) {\n        if (amount > 0 && amount <= balance) {\n            balance -= amount;\n            System.out.println(\"Withdrawn: $\" + amount);\n            return true;\n        }\n        System.out.println(\"Invalid withdrawal or insufficient funds\");\n        return false;\n    }\n    \n    // Getter methods - part of encapsulation\n    public String getAccountNumber() { return accountNumber; }\n    public String getOwner() { return owner; }\n    public double getBalance() { return balance; }\n    \n    // Static method - operates on class level\n    public static double getInterestRate() { return interestRate; }\n    public static void setInterestRate(double newRate) {\n        if (newRate >= 0) {\n            interestRate = newRate;\n        }\n    }\n    \n    // Method demonstrating this keyword\n    public void updateOwner(String owner) {\n        this.owner = owner;  // this.owner refers to instance variable\n    }\n    \n    // Method returning string representation of object\n    @Override\n    public String toString() {\n        return \"Account [number=\" + accountNumber + \", owner=\" + owner + \", balance=$\" + balance + \"]\"; \n    }\n}\n\n// Usage example\nBankAccount account1 = new BankAccount();\nBankAccount account2 = new BankAccount(\"John Doe\", \"12345678\");\nBankAccount account3 = new BankAccount(\"Jane Smith\", \"87654321\", 1000.0);\n\naccount2.deposit(500);\nboolean success = account3.withdraw(200);\nSystem.out.println(account3);  // Using toString method\nSystem.out.println(\"Current interest rate: \" + BankAccount.getInterestRate());",
            "Object-Oriented Programming organizes code into reusable, modular structures called classes that model real-world entities as objects. This paradigm enhances code maintainability, flexibility, and organization while reducing complexity through encapsulation, inheritance, polymorphism, and abstraction."
        ));

        // Lesson 9: Inheritance and Polymorphism
        lessons.add(new Lesson(
            "Inheritance and Polymorphism",
            "<h2 style='color:#3F51B5;'>Inheritance and Polymorphism</h2>" +
            "<p>Inheritance and polymorphism are core OOP concepts that facilitate code reuse, flexibility, and extensibility in Java applications.</p>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Inheritance Fundamentals</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Definition</strong>: A mechanism where a new class (subclass) derives properties and behaviors from an existing class (superclass)</li>" +
            "<li><strong>Terminology</strong>:" +
            "<ul>" +
            "<li>Superclass (Parent/Base): The class being inherited from</li>" +
            "<li>Subclass (Child/Derived): The class that inherits</li>" +
            "<li>'extends' keyword: Used to establish inheritance relationship</li>" +
            "<li>'super' keyword: Used to reference the parent class</li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Types of Inheritance</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Single</strong>: One class inherits from one class (A → B)</li>" +
            "<li><strong>Multilevel</strong>: Chain of inheritance (A → B → C)</li>" +
            "<li><strong>Hierarchical</strong>: Multiple classes inherit from one class (A → B, A → C)</li>" +
            "<li>Java does not support multiple inheritance of classes</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Constructor Chaining</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li>Child class constructors must call parent constructors (implicitly or explicitly)</li>" +
            "<li><code>super()</code> calls parent constructor and must be the first statement</li>" +
            "<li>If not specified, Java calls <code>super()</code> automatically</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Method Overriding</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li>Redefining a parent class method in a child class with the same signature</li>" +
            "<li><strong>Annotations</strong>: <code>@Override</code> (recommended for clarity and compile-time checking)</li>" +
            "<li><strong>Requirements</strong>: Same name, parameters, return type (or covariant return)</li>" +
            "<li>Access modifier can't be more restrictive than the parent method</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Polymorphism Types</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Static (Compile-time)</strong>: Method overloading</li>" +
            "<li><strong>Dynamic (Runtime)</strong>: Method overriding</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>IS-A vs HAS-A Relationship</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>IS-A</strong>: Inheritance relationship (Car IS-A Vehicle)</li>" +
            "<li><strong>HAS-A</strong>: Composition relationship (Car HAS-A Engine)</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Object Class</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li>All classes implicitly inherit from <code>java.lang.Object</code></li>" +
            "<li>Provides common methods: <code>toString()</code>, <code>equals()</code>, <code>hashCode()</code>, etc.</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Final Keyword with Inheritance</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>final class</strong>: Cannot be subclassed</li>" +
            "<li><strong>final method</strong>: Cannot be overridden</li>" +
            "<li><strong>final variable</strong>: Cannot be reassigned</li>" +
            "</ul>",
            "Inheritance",
            9,
            "Intermediate",
            "// The base class (parent/superclass)\npublic class Animal {\n    // Protected members are accessible in subclasses\n    protected String name;\n    protected int age;\n    \n    // Constructor\n    public Animal(String name, int age) {\n        this.name = name;\n        this.age = age;\n        System.out.println(\"Animal constructor called\");\n    }\n    \n    // Methods that can be inherited and overridden\n    public void makeSound() {\n        System.out.println(\"Some generic animal sound\");\n    }\n    \n    public void eat() {\n        System.out.println(name + \" is eating\");\n    }\n    \n    // Final method that cannot be overridden\n    public final void breathe() {\n        System.out.println(name + \" is breathing\");\n    }\n    \n    // Static method - not overridden, but hidden\n    public static void taxonomyInfo() {\n        System.out.println(\"Animals are multicellular eukaryotic organisms\");\n    }\n}\n\n// Dog subclass - inherits from Animal\npublic class Dog extends Animal {\n    private String breed;\n    \n    // Constructor with constructor chaining\n    public Dog(String name, int age, String breed) {\n        super(name, age);  // Calls the parent constructor\n        this.breed = breed;\n        System.out.println(\"Dog constructor called\");\n    }\n    \n    // Method overriding - note the @Override annotation\n    @Override\n    public void makeSound() {\n        System.out.println(name + \" barks: Woof! Woof!\");\n    }\n    \n    // Additional method specific to Dog\n    public void fetch() {\n        System.out.println(name + \" is fetching the ball\");\n    }\n    \n    // Overriding toString() method from Object class\n    @Override\n    public String toString() {\n        return \"Dog [name=\" + name + \", age=\" + age + \", breed=\" + breed + \"]\"; \n    }\n    \n    // Static method hiding (not overriding)\n    public static void taxonomyInfo() {\n        System.out.println(\"Dogs belong to the family Canidae\");\n    }\n}\n\n// Example of polymorphism\npublic class InheritanceDemo {\n    public static void main(String[] args) {\n        // Dog object referenced by Dog type\n        Dog myDog = new Dog(\"Buddy\", 3, \"Golden Retriever\");\n        myDog.makeSound();  // Calls Dog's method\n        myDog.fetch();      // Dog-specific method\n        \n        // Polymorphism: Dog object referenced by Animal type\n        Animal myAnimal = new Dog(\"Rex\", 5, \"German Shepherd\");\n        myAnimal.makeSound();  // Calls Dog's overridden method\n        // myAnimal.fetch();   // Error: Animal reference doesn't know about fetch()\n        \n        // Runtime type checking with instanceof\n        if (myAnimal instanceof Dog) {\n            Dog castedDog = (Dog) myAnimal;  // Downcasting\n            castedDog.fetch();  // Now we can call Dog-specific method\n        }\n        \n        // Static method belongs to the class, not the object\n        Animal.taxonomyInfo();  // Calls Animal's static method\n        Dog.taxonomyInfo();     // Calls Dog's static method\n        \n        // Static method called through reference (not recommended)\n        myAnimal.taxonomyInfo();  // Calls Animal's method (reference type matters)\n    }\n}",
            "Inheritance enables code reuse by allowing classes to inherit properties and behaviors from other classes. Polymorphism allows objects to be treated as instances of their parent class, providing flexibility through different implementations of the same methods. Together, these concepts form the foundation for creating extensible, maintainable object hierarchies."
        ));

        // Lesson 10: Exception Handling
        lessons.add(new Lesson(
            "Exception Handling",
            "<h2 style='color:#3F51B5;'>Exception Handling</h2>" +
            "<p>Exception handling is a powerful mechanism that deals with runtime anomalies, errors, and unexpected conditions to maintain program stability and provide meaningful error information.</p>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Exception Hierarchy</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Throwable</strong>: The root of exception hierarchy" +
            "<ul>" +
            "<li><strong>Error</strong>: Serious problems, generally not recoverable (JVM issues)" +
            "<ul>" +
            "<li>OutOfMemoryError, StackOverflowError, NoClassDefFoundError</li>" +
            "</ul></li>" +
            "<li><strong>Exception</strong>: Program-level exceptions that can be handled" +
            "<ul>" +
            "<li><strong>Checked Exceptions</strong>: Must be declared or handled" +
            "<ul>" +
            "<li>IOException, SQLException, ClassNotFoundException</li>" +
            "</ul></li>" +
            "<li><strong>Unchecked Exceptions (RuntimeException)</strong>: Not required to be declared" +
            "<ul>" +
            "<li>NullPointerException, ArrayIndexOutOfBoundsException, ArithmeticException</li>" +
            "</ul></li>" +
            "</ul></li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Exception Handling Mechanisms</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>try</strong>: Contains code that might throw exceptions</li>" +
            "<li><strong>catch</strong>: Handles specific exception types</li>" +
            "<li><strong>finally</strong>: Contains cleanup code that always executes (even if exception occurs)</li>" +
            "<li><strong>throw</strong>: Explicitly throws an exception</li>" +
            "<li><strong>throws</strong>: Declares that a method might throw exceptions</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Multi-catch and Catch Order</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li>Multiple catch blocks can handle different exceptions</li>" +
            "<li>Catch blocks must be ordered from specific to general</li>" +
            "<li>Java 7+ allows multi-catch: <code>catch(ExceptionA | ExceptionB e)</code></li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Try-with-resources (Java 7+)</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li>Automatically closes resources implementing <code>AutoCloseable</code></li>" +
            "<li>Ensures proper resource cleanup even if exceptions occur</li>" +
            "<li>Replaces finally blocks for resource management</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Custom Exceptions</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li>Create by extending <code>Exception</code> or <code>RuntimeException</code></li>" +
            "<li>Provide application-specific error details</li>" +
            "<li>Follows naming convention with 'Exception' suffix</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Exception Propagation</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li>Unhandled exceptions propagate up the call stack</li>" +
            "<li>If never caught, program terminates</li>" +
            "<li>Stack trace shows method call hierarchy</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Best Practices</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li>Handle specific exceptions before general ones</li>" +
            "<li>Don't catch exceptions you can't handle properly</li>" +
            "</ul>" +
            "• Always provide meaningful error messages\n" +
            "• Clean up resources in finally or use try-with-resources\n" +
            "• Document exceptions with @throws in Javadoc\n" +
            "• Follow the principle: \"Throw early, catch late\"\n" +
            "• Preserve stack trace information when creating new exceptions\n" +
            "• Design exception hierarchies for complex applications\n" +
            "• Log exceptions appropriately for troubleshooting",
            "Exceptions",
            10,
            "Intermediate",
            "import java.io.*;\nimport java.util.Scanner;\n\n// Custom exception class\nclass InsufficientFundsException extends Exception {\n    private double amount;\n    \n    public InsufficientFundsException(double amount) {\n        super(\"Insufficient funds: Deficit of $\" + amount);\n        this.amount = amount;\n    }\n    \n    public double getAmount() {\n        return amount;\n    }\n}\n\nclass Account {\n    private String id;\n    private double balance;\n    \n    public Account(String id, double initialBalance) {\n        this.id = id;\n        this.balance = initialBalance;\n    }\n    \n    // Method with checked exception declaration\n    public void withdraw(double amount) throws InsufficientFundsException {\n        if (amount > balance) {\n            double deficit = amount - balance;\n            throw new InsufficientFundsException(deficit);  // Explicitly throw exception\n        }\n        balance -= amount;\n        System.out.println(\"Withdrawal successful. New balance: $\" + balance);\n    }\n}\n\npublic class ExceptionHandlingDemo {\n    public static void main(String[] args) {\n        Account account = new Account(\"12345\", 500.0);\n        \n        // Basic try-catch-finally\n        try {\n            // Code that might throw exceptions\n            account.withdraw(700.0);  // Will throw InsufficientFundsException\n        } catch (InsufficientFundsException e) {\n            // Exception handler\n            System.out.println(\"Could not process withdrawal: \" + e.getMessage());\n            System.out.println(\"Deficit amount: $\" + e.getAmount());\n        } finally {\n            // Always executes, whether exception occurs or not\n            System.out.println(\"Transaction completed.\\n\");\n        }\n        \n        // Multiple catch blocks\n        try {\n            int[] numbers = {1, 2, 3};\n            System.out.println(\"Array element: \" + numbers[5]);\n            int result = 10 / 0;  // ArithmeticException\n        } catch (ArrayIndexOutOfBoundsException e) {\n            System.out.println(\"Array index error: \" + e.getMessage());\n        } catch (ArithmeticException e) {\n            System.out.println(\"Arithmetic error: \" + e.getMessage());\n        } catch (Exception e) {\n            // General exception handler (must come last)\n            System.out.println(\"General error: \" + e.getMessage());\n        }\n        \n        // Multi-catch (Java 7+)\n        try {\n            String number = \"abc\";\n            int value = Integer.parseInt(number);\n        } catch (NumberFormatException | NullPointerException e) {\n            System.out.println(\"Input parsing error: \" + e.getMessage());\n        }\n        \n        // Try-with-resources (Java 7+)\n        try (Scanner scanner = new Scanner(new File(\"input.txt\"))) {\n            // Resource automatically closed after this block\n            if (scanner.hasNextLine()) {\n                System.out.println(scanner.nextLine());\n            }\n        } catch (FileNotFoundException e) {\n            System.out.println(\"File not found: \" + e.getMessage());\n        }\n        \n        // Exception propagation demonstration\n        try {\n            methodA();\n        } catch (Exception e) {\n            System.out.println(\"Caught in main: \" + e.getMessage());\n            System.out.println(\"Stack trace:\");\n            e.printStackTrace();\n        }\n    }\n    \n    // Exception propagation through method calls\n    public static void methodA() throws Exception {\n        methodB();\n    }\n    \n    public static void methodB() throws Exception {\n        methodC();\n    }\n    \n    public static void methodC() throws Exception {\n        // Unchecked exception - does not require throws declaration\n        // but including it documents the behavior\n        throw new RuntimeException(\"Exception originated in methodC\");\n    }\n}",
            "Exception handling is a crucial mechanism in Java for detecting, reporting, and resolving runtime errors. By using try-catch blocks, finally clauses, and custom exceptions, developers can create robust applications that respond gracefully to errors, protect resources, provide helpful diagnostics, and maintain stability even when unexpected conditions occur."
        ));

        // Lesson 11: Collections Framework
        lessons.add(new Lesson(
            "Collections Framework",
            "<h2 style='color:#3F51B5;'>Collections Framework</h2>" +
            "<p>The Java Collections Framework provides a unified architecture for representing and manipulating collections of objects, offering high-performance data structures and algorithms that make coding easier and more efficient.</p>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Collection Hierarchy</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Iterable</strong>: Root interface (enables for-each loop)" +
            "<ul>" +
            "<li><strong>Collection</strong>: Base interface for collections" +
            "<ul>" +
            "<li><strong>List</strong>: Ordered collection with positional access</li>" +
            "<li><strong>Set</strong>: Collection with no duplicate elements</li>" +
            "<li><strong>Queue</strong>: Collection for holding elements for processing</li>" +
            "</ul></li>" +
            "</ul></li>" +
            "<li><strong>Map</strong>: Key-value pair mappings (not a Collection)</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>List Implementations</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>ArrayList</strong>: Dynamic array implementation" +
            "<ul>" +
            "<li>Fast random access O(1)</li>" +
            "<li>Slow insertion/deletion in middle O(n)</li>" +
            "<li>Best for: Random access, iteration, adding at end</li>" +
            "<li>Default implementation for most list operations</li>" +
            "<li>Real-world use: Storing UI elements, database results</li>" +
            "</ul></li>" +
            "<li><strong>LinkedList</strong>: Doubly-linked list implementation" +
            "<ul>" +
            "<li>Fast insertion/deletion O(1) with position</li>" +
            "<li>Slow random access O(n)</li>" +
            "<li>Best for: Frequent insertions/deletions</li>" +
            "</ul></li>" +
            "<li><strong>Vector</strong>: Synchronized dynamic array (thread-safe but legacy)</li>" +
            "<li><strong>Stack</strong>: LIFO (Last-In-First-Out) operations (extends Vector)</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Set Implementations</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>HashSet</strong>: Backed by HashMap" +
            "<ul>" +
            "<li>O(1) add, remove, contains operations</li>" +
            "<li>No order guarantee</li>" +
            "<li>Best for: Membership testing, removing duplicates</li>" +
            "</ul></li>" +
            "<li><strong>LinkedHashSet</strong>: Hash table and linked list" +
            "<ul>" +
            "<li>Maintains insertion order</li>" +
            "<li>Slightly slower than HashSet</li>" +
            "</ul></li>" +
            "<li><strong>TreeSet</strong>: Red-black tree implementation" +
            "<ul>" +
            "<li>O(log n) operations</li>" +
            "<li>Elements stored in sorted order</li>" +
            "<li>Elements must be comparable</li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Map Implementations</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>HashMap</strong>: Hash table for key-value pairs" +
            "<ul>" +
            "<li>O(1) operations for put, get, containsKey</li>" +
            "<li>No order guarantee</li>" +
            "<li>Allows null keys and values</li>" +
            "<li>Not synchronized or thread-safe</li>" +
            "<li>Use case: Fast lookups by unique identifiers</li>" +
            "</ul></li>" +
            "<li><strong>LinkedHashMap</strong>: Hash table and linked list" +
            "<ul>" +
            "<li>Maintains insertion order or access order</li>" +
            "</ul></li>" +
            "<li><strong>TreeMap</strong>: Red-black tree implementation" +
            "<ul>" +
            "<li>Keys stored in sorted order</li>" +
            "<li>O(log n) operations</li>" +
            "</ul></li>" +
            "<li><strong>Hashtable</strong>: Synchronized hash table (thread-safe but legacy)</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Queue and Deque Implementations</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>PriorityQueue</strong>: Heap implementation with priority ordering</li>" +
            "<li><strong>ArrayDeque</strong>: Resizable array implementation of Deque" +
            "<ul>" +
            "<li>More efficient than Stack and LinkedList for stacks and queues</li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Iteration Mechanisms</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Iterator</strong>: Forward traversal, allows removal</li>" +
            "<li><strong>ListIterator</strong>: Bidirectional traversal for Lists</li>" +
            "<li><strong>Enhanced for-loop</strong>: Simplified iteration syntax</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Utility Methods</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Collections class</strong>: Algorithms and utility functions" +
            "<ul>" +
            "<li><code>sort()</code>, <code>binarySearch()</code>, <code>shuffle()</code>, <code>min()</code>, <code>max()</code></li>" +
            "<li><code>synchronizedCollection()</code>, <code>unmodifiableCollection()</code></li>" +
            "</ul></li>" +
            "<li><strong>Arrays class</strong>: Array utility methods" +
            "<ul>" +
            "<li><code>asList()</code>, <code>sort()</code>, <code>binarySearch()</code>, <code>equals()</code></li>" +
            "</ul></li>" +
            "</ul>",
            "Collections",
            11,
            "Advanced",
            "import java.util.*;\n\npublic class CollectionsDemo {\n    public static void main(String[] args) {\n        // ===== LIST IMPLEMENTATIONS =====\n        \n        // ArrayList - dynamic array implementation\n        List<String> arrayList = new ArrayList<>();\n        arrayList.add(\"Apple\");        // O(1) amortized\n        arrayList.add(\"Banana\");\n        arrayList.add(\"Cherry\");\n        arrayList.add(1, \"Blueberry\"); // O(n) - requires shifting elements\n        \n        System.out.println(\"ArrayList demo:\");\n        System.out.println(\"Element at index 2: \" + arrayList.get(2)); // O(1) random access\n        System.out.println(\"Contains 'Apple'? \" + arrayList.contains(\"Apple\")); // O(n)\n        \n        // LinkedList - doubly-linked list implementation\n        LinkedList<String> linkedList = new LinkedList<>(arrayList); // Initialize from another collection\n        linkedList.addFirst(\"Avocado\");   // O(1) operations at ends\n        linkedList.addLast(\"Durian\");\n        \n        System.out.println(\"\\nLinkedList demo:\");\n        System.out.println(\"First element: \" + linkedList.getFirst());\n        System.out.println(\"Last element: \" + linkedList.getLast());\n        \n        // ===== SET IMPLEMENTATIONS =====\n        \n        // HashSet - no duplicates, unordered\n        Set<String> hashSet = new HashSet<>();\n        hashSet.add(\"Dog\");    // O(1) operations\n        hashSet.add(\"Cat\");\n        hashSet.add(\"Bird\");\n        hashSet.add(\"Cat\");    // Duplicate not added\n        \n        System.out.println(\"\\nHashSet demo:\");\n        System.out.println(\"Set size: \" + hashSet.size()); // 3, not 4\n        System.out.println(\"Contains 'Dog'? \" + hashSet.contains(\"Dog\")); // O(1)\n        \n        // TreeSet - sorted set\n        TreeSet<Integer> treeSet = new TreeSet<>();\n        treeSet.add(5);\n        treeSet.add(1);\n        treeSet.add(10);\n        treeSet.add(3);\n        \n        System.out.println(\"\\nTreeSet demo (sorted):\");\n        System.out.println(\"Elements in order: \" + treeSet); // [1, 3, 5, 10]\n        System.out.println(\"First element: \" + treeSet.first());\n        System.out.println(\"Elements ≥ 3: \" + treeSet.tailSet(3)); // [3, 5, 10]\n        \n        // ===== MAP IMPLEMENTATIONS =====\n        \n        // HashMap - key-value storage\n        Map<String, Integer> studentScores = new HashMap<>();\n        studentScores.put(\"Alice\", 95);  // O(1) operations\n        studentScores.put(\"Bob\", 87);\n        studentScores.put(\"Charlie\", 92);\n        studentScores.put(\"Alice\", 98);  // Overwrites previous value\n        \n        System.out.println(\"\\nHashMap demo:\");\n        System.out.println(\"Bob's score: \" + studentScores.get(\"Bob\")); // O(1)\n        System.out.println(\"All students: \" + studentScores.keySet());\n        \n        // TreeMap - sorted by keys\n        Map<String, String> countryCapitals = new TreeMap<>(); // Sorted by keys\n        countryCapitals.put(\"USA\", \"Washington D.C.\");\n        countryCapitals.put(\"France\", \"Paris\");\n        countryCapitals.put(\"Japan\", \"Tokyo\");\n        countryCapitals.put(\"Australia\", \"Canberra\");\n        \n        System.out.println(\"\\nTreeMap demo (sorted by keys):\");\n        for (Map.Entry<String, String> entry : countryCapitals.entrySet()) {\n            System.out.println(entry.getKey() + \" - \" + entry.getValue());\n        }\n        \n        // ===== QUEUE IMPLEMENTATIONS =====\n        \n        // PriorityQueue - heap-based priority queue\n        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(); // Min-heap by default\n        priorityQueue.offer(30);\n        priorityQueue.offer(10);\n        priorityQueue.offer(20);\n        \n        System.out.println(\"\\nPriorityQueue demo:\");\n        System.out.println(\"Peek (doesn't remove): \" + priorityQueue.peek()); // Smallest element\n        System.out.println(\"Poll (removes): \" + priorityQueue.poll());\n        System.out.println(\"Next poll: \" + priorityQueue.poll());\n        \n        // ArrayDeque - efficient stack and queue implementation\n        Deque<String> stack = new ArrayDeque<>();\n        stack.push(\"First\");      // Stack operations (LIFO)\n        stack.push(\"Second\");\n        stack.push(\"Third\");\n        \n        System.out.println(\"\\nArrayDeque as Stack (LIFO):\");\n        System.out.println(\"Pop: \" + stack.pop()); // Third\n        System.out.println(\"Pop: \" + stack.pop()); // Second\n        \n        // ===== UTILITY METHODS =====\n        \n        List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9, 3);\n        System.out.println(\"\\nUtility methods demo:\");\n        System.out.println(\"Original list: \" + numbers);\n        \n        Collections.sort(numbers);\n        System.out.println(\"Sorted list: \" + numbers);\n        \n        Collections.shuffle(numbers);\n        System.out.println(\"Shuffled list: \" + numbers);\n        \n        System.out.println(\"Max value: \" + Collections.max(numbers));\n        System.out.println(\"Min value: \" + Collections.min(numbers));\n        \n        int index = Collections.binarySearch(Arrays.asList(1, 2, 3, 5, 8, 9), 5);\n        System.out.println(\"Binary search for 5: found at index \" + index);\n    }\n}",
            "The Java Collections Framework is a unified architecture for representing and manipulating collections, providing data structures and algorithms to store, process, and manipulate groups of objects efficiently. It offers pre-built implementations of common data structures, reducing programming effort while improving performance through optimized algorithms."
        ));

        // Lesson 12: File I/O and Streams
        lessons.add(new Lesson(
            "File I/O and Streams",
            "<h2 style='color:#3F51B5;'>File I/O and Streams</h2>" +
            "<p>Java Input/Output (I/O) provides comprehensive capabilities for reading from and writing to various sources including files, networks, memory, and the console. Understanding I/O is essential for data persistence, configuration, logging, and communication in Java applications.</p>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>I/O Stream Hierarchy</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Stream</strong>: Abstract concept of a sequence of data</li>" +
            "<li><strong>Byte Streams</strong>: For binary data processing" +
            "<ul>" +
            "<li><strong>InputStream</strong>: Abstract class for reading byte data" +
            "<ul>" +
            "<li><code>FileInputStream</code>: Read bytes from files</li>" +
            "<li><code>ByteArrayInputStream</code>: Read bytes from memory</li>" +
            "<li><code>DataInputStream</code>: Read primitive data types</li>" +
            "</ul></li>" +
            "<li><strong>OutputStream</strong>: Abstract class for writing byte data" +
            "<ul>" +
            "<li><code>FileOutputStream</code>: Write bytes to files</li>" +
            "<li><code>ByteArrayOutputStream</code>: Write bytes to memory</li>" +
            "<li><code>DataOutputStream</code>: Write primitive data types</li>" +
            "</ul></li>" +
            "</ul></li>" +
            "<li><strong>Character Streams</strong>: For text processing (Unicode)" +
            "<ul>" +
            "<li><strong>Reader</strong>: Abstract class for reading characters" +
            "<ul>" +
            "<li><code>FileReader</code>: Read characters from files</li>" +
            "<li><code>StringReader</code>: Read characters from strings</li>" +
            "<li><code>BufferedReader</code>: Read text efficiently with buffering</li>" +
            "</ul></li>" +
            "<li><strong>Writer</strong>: Abstract class for writing characters" +
            "<ul>" +
            "<li><code>FileWriter</code>: Write characters to files</li>" +
            "<li><code>StringWriter</code>: Write characters to strings</li>" +
            "<li><code>BufferedWriter</code>: Write text efficiently with buffering</li>" +
            "</ul></li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Decorating Streams</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li>I/O streams can be \"decorated\" to add functionality</li>" +
            "<li><code>BufferedReader</code>/<code>BufferedWriter</code>: Add buffering for performance</li>" +
            "<li><code>InputStreamReader</code>/<code>OutputStreamWriter</code>: Bridge between byte and character streams</li>" +
            "<li><code>PrintWriter</code>: Convenient methods for formatted output</li>" +
            "<li><code>ObjectInputStream</code>/<code>ObjectOutputStream</code>: For object serialization</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>File Operations (java.io)</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>File Class</strong>: Legacy way to represent files and directories" +
            "<ul>" +
            "<li>Create files/directories: <code>createNewFile()</code>, <code>mkdir()</code>, <code>mkdirs()</code></li>" +
            "<li>Check properties: <code>exists()</code>, <code>isFile()</code>, <code>isDirectory()</code>, <code>length()</code></li>" +
            "<li>List contents: <code>list()</code>, <code>listFiles()</code></li>" +
            "<li>File manipulation: <code>renameTo()</code>, <code>delete()</code></li>" +
            "</ul></li>" +
            "<li><strong>RandomAccessFile</strong>: Read/write at specific positions in a file</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Modern NIO.2 API (Java 7+)</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Path Interface</strong>: Modern representation of file paths" +
            "<ul>" +
            "<li>Created via <code>Paths.get()</code> factory method</li>" +
            "<li>Platform-independent path manipulation</li>" +
            "</ul></li>" +
            "<li><strong>Files Class</strong>: Static utility methods for file operations" +
            "<ul>" +
            "<li>Reading/Writing: <code>readAllBytes()</code>, <code>readAllLines()</code>, <code>write()</code></li>" +
            "<li>File attributes: <code>getAttribute()</code>, <code>setAttribute()</code></li>" +
            "<li>File operations: <code>copy()</code>, <code>move()</code>, <code>delete()</code></li>" +
            "<li>Directory operations: <code>createDirectory()</code>, <code>createDirectories()</code></li>" +
            "</ul></li>" +
            "<li><strong>DirectoryStream</strong>: Efficient directory iteration</li>" +
            "<li><strong>FileVisitor</strong>: For traversing directory trees</li>" +
            "<li><strong>WatchService</strong>: Monitor directory changes</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Resource Management</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>try-with-resources</strong> (Java 7+): Automatic resource closing" +
            "<ul>" +
            "<li>Resources must implement <code>AutoCloseable</code> interface</li>" +
            "<li>Ensures proper cleanup even with exceptions</li>" +
            "<li>Cleaner alternative to finally blocks</li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Character Encoding</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li>Character streams use default platform encoding unless specified</li>" +
            "<li>Specify encoding to ensure consistent behavior: <code>new FileReader(file, StandardCharsets.UTF_8)</code></li>" +
            "<li>Common encodings: UTF-8, UTF-16, ISO-8859-1</li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>Scanner and Formatting</h3>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Scanner</strong>: Parse primitive types from input sources" +
            "<ul>" +
            "<li><code>nextInt()</code>, <code>nextDouble()</code>, <code>nextLine()</code>, <code>hasNext()</code></li>" +
            "<li>Can parse from String, File, or InputStream</li>" +
            "<li>Pattern matching with regular expressions</li>" +
            "</ul></li>" +
            "<li><strong>Formatter/PrintWriter</strong>: Format output" +
            "<ul>" +
            "<li>printf()-style formatting</li>" +
            "</ul></li>" +
            "</ul>" +
            "  - System.out.printf(), String.format()",
            "File I/O",
            12,
            "Advanced",
            "import java.io.*;\nimport java.nio.charset.StandardCharsets;\nimport java.nio.file.*;\nimport java.util.*;\n\npublic class FileIODemo {\n    public static void main(String[] args) {\n        // ===== TRADITIONAL FILE I/O =====\n        \n        // 1. Working with File object\n        File file = new File(\"example.txt\");\n        System.out.println(\"File exists: \" + file.exists());\n        System.out.println(\"Is directory: \" + file.isDirectory());\n        \n        // 2. Writing text using FileWriter (character stream)\n        try (FileWriter writer = new FileWriter(file)) {\n            writer.write(\"Hello, World!\\n\");\n            writer.write(\"Java I/O is powerful\\n\");\n        } catch (IOException e) {\n            System.out.println(\"Error writing to file: \" + e.getMessage());\n        }\n        \n        // 3. Reading text using BufferedReader (for efficiency)\n        try (FileReader fileReader = new FileReader(file);\n             BufferedReader reader = new BufferedReader(fileReader)) {\n            \n            String line;\n            System.out.println(\"\\nReading with BufferedReader:\");\n            while ((line = reader.readLine()) != null) {\n                System.out.println(line);\n            }\n        } catch (IOException e) {\n            System.out.println(\"Error reading file: \" + e.getMessage());\n        }\n        \n        // 4. Writing binary data\n        try (FileOutputStream fos = new FileOutputStream(\"data.bin\");\n             DataOutputStream dos = new DataOutputStream(fos)) {\n             \n            dos.writeInt(42);\n            dos.writeDouble(3.14159);\n            dos.writeUTF(\"Binary data example\");\n        } catch (IOException e) {\n            System.out.println(\"Error writing binary data: \" + e.getMessage());\n        }\n        \n        // 5. Reading binary data\n        try (FileInputStream fis = new FileInputStream(\"data.bin\");\n             DataInputStream dis = new DataInputStream(fis)) {\n             \n            System.out.println(\"\\nReading binary data:\");\n            System.out.println(\"Integer: \" + dis.readInt());\n            System.out.println(\"Double: \" + dis.readDouble());\n            System.out.println(\"String: \" + dis.readUTF());\n        } catch (IOException e) {\n            System.out.println(\"Error reading binary data: \" + e.getMessage());\n        }\n        \n        // ===== MODERN NIO.2 FILE I/O (Java 7+) =====\n        \n        // 1. Creating and writing to a file\n        Path path = Paths.get(\"modern_example.txt\");\n        try {\n            Files.writeString(path, \"Modern Java NIO.2 API\\nMakes file operations easier!\", \n                              StandardCharsets.UTF_8);\n            System.out.println(\"\\nWrote text to \" + path);\n        } catch (IOException e) {\n            System.out.println(\"Error writing with NIO.2: \" + e.getMessage());\n        }\n        \n        // 2. Reading all lines at once\n        try {\n            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);\n            System.out.println(\"\\nReading with Files.readAllLines():\");\n            for (String line : lines) {\n                System.out.println(line);\n            }\n        } catch (IOException e) {\n            System.out.println(\"Error reading with NIO.2: \" + e.getMessage());\n        }\n        \n        // 3. Working with directory\n        Path dirPath = Paths.get(\"example_dir\");\n        try {\n            // Create directory if it doesn't exist\n            if (!Files.exists(dirPath)) {\n                Files.createDirectory(dirPath);\n                System.out.println(\"\\nCreated directory: \" + dirPath);\n            }\n            \n            // List directory contents\n            System.out.println(\"\\nListing directory contents:\");\n            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath.getParent())) {\n                for (Path entry : stream) {\n                    System.out.println(entry.getFileName() + \n                                      (Files.isDirectory(entry) ? \" (dir)\" : \" (file)\"));\n                }\n            }\n        } catch (IOException e) {\n            System.out.println(\"Error with directory operations: \" + e.getMessage());\n        }\n        \n        // ===== SCANNER FOR PARSING =====\n        \n        // 1. Scanner for console input\n        Scanner scanner = new Scanner(System.in);\n        System.out.print(\"\\nEnter your age: \");\n        \n        // Type-safe input with validation\n        int age = 0;\n        if (scanner.hasNextInt()) {\n            age = scanner.nextInt();\n            scanner.nextLine(); // consume the newline\n            System.out.println(\"You are \" + age + \" years old.\");\n        } else {\n            System.out.println(\"That's not a valid age!\");\n            scanner.nextLine(); // consume the invalid input\n        }\n        \n        // 2. Scanner for parsing a string\n        String data = \"John 25 3.14159\";\n        Scanner dataScanner = new Scanner(data);\n        \n        System.out.println(\"\\nParsing string with Scanner:\");\n        String name = dataScanner.next();\n        int number = dataScanner.nextInt();\n        double pi = dataScanner.nextDouble();\n        \n        System.out.printf(\"Name: %s, Number: %d, Pi: %.5f%n\", name, number, pi);\n        dataScanner.close();\n    }\n}",
            "Java I/O operations enable applications to interact with external resources like files, networks, and other data sources. The comprehensive I/O framework provides both traditional stream-based approaches and modern NIO.2 APIs, offering powerful tools for efficient data reading, writing, and manipulation across different media with proper resource management. When working with I/O, follow these best practices: 1) Always close resources using try-with-resources to prevent leaks, 2) Handle exceptions appropriately to provide meaningful feedback, 3) Use buffered streams for better performance with larger data, 4) Consider character encoding when working with text files, 5) Prefer NIO.2 API for modern applications that need better performance and more features, and 6) Use appropriate stream types based on your data (character vs. binary data)."
        ));

        // Lesson 13: Interfaces and Abstract Classes
        lessons.add(new Lesson(
            "Interfaces and Abstract Classes",
            "<h2 style='color:#3F51B5;'>Interfaces and Abstract Classes</h2>" +
            "<p>Interfaces and abstract classes are powerful tools for defining contracts, ensuring consistency across classes, and providing reusable partial implementations in Java. They are foundational elements of object-oriented design that enable polymorphism, code reuse, and architectural flexibility.</p>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>INTERFACES</h3>\n\n" +
            "<h4 style='color:#3F51B5;margin-top:14px;'>Core Interface Concepts</h4>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Definition</strong>: A contract that specifies what a class must do, but not how</li>" +
            "<li><strong>Purpose</strong>: Defines a common behavior across unrelated classes</li>" +
            "<li><strong>Implementation</strong>: Classes use the 'implements' keyword</li>" +
            "<li><strong>Inheritance</strong>: Classes can implement multiple interfaces (solving the multiple inheritance problem)</li>" +
            "</ul>\n\n" +
            "<h4 style='color:#3F51B5;margin-top:14px;'>Traditional Interface Features (Pre-Java 8)</h4>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Abstract Methods</strong>: Only method declarations with no implementation" +
            "<ul>" +
            "<li>All methods implicitly public and abstract</li>" +
            "</ul></li>" +
            "<li><strong>Constants</strong>: Can contain variables, but they must be public, static, and final" +
            "<ul>" +
            "<li>Implicitly defined as such (constants)</li>" +
            "</ul></li>" +
            "<li><strong>No Constructors</strong>: Interfaces cannot be instantiated</li>" +
            "<li><strong>No State</strong>: Cannot contain instance fields</li>" +
            "</ul>\n\n" +
            "<h4 style='color:#3F51B5;margin-top:14px;'>Modern Interface Features (Java 8+)</h4>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Default Methods</strong>: Provide implementation within interfaces" +
            "<ul>" +
            "<li>Enable interface evolution without breaking implementation classes</li>" +
            "<li>Allow adding new methods to interfaces with backward compatibility</li>" +
            "<li>Use 'default' keyword followed by method implementation</li>" +
            "</ul></li>" +
            "<li><strong>Static Methods</strong>: Methods that belong to the interface itself" +
            "<ul>" +
            "<li>Cannot be overridden by implementing classes</li>" +
            "<li>Accessed using the interface name: <code>InterfaceName.staticMethod()</code></li>" +
            "</ul></li>" +
            "<li><strong>Functional Interfaces</strong>: Interfaces with exactly one abstract method" +
            "<ul>" +
            "<li>Can be annotated with <code>@FunctionalInterface</code></li>" +
            "<li>Can be implemented using lambda expressions or method references</li>" +
            "<li>Examples: <code>Runnable</code>, <code>Comparable</code>, <code>Consumer&lt;T&gt;</code>, <code>Supplier&lt;T&gt;</code></li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h4 style='color:#3F51B5;margin-top:14px;'>Java 9+ Interface Features</h4>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Private Methods</strong>: Allow code reuse within the interface" +
            "<ul>" +
            "<li>Can be called only by default and static methods in the same interface</li>" +
            "<li>Helps avoid code duplication in default methods</li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>ABSTRACT CLASSES</h3>\n\n" +
            "<h4 style='color:#3F51B5;margin-top:14px;'>Core Abstract Class Concepts</h4>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Definition</strong>: A class that cannot be instantiated and may contain abstract methods</li>" +
            "<li><strong>Purpose</strong>: Share common code among related classes</li>" +
            "<li><strong>Inheritance</strong>: Classes use the 'extends' keyword</li>" +
            "<li><strong>Limitation</strong>: Java allows single inheritance only for classes</li>" +
            "</ul>\n\n" +
            "<h4 style='color:#3F51B5;margin-top:14px;'>Abstract Class Features</h4>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Abstract Methods</strong>: Methods declared without implementation" +
            "<ul>" +
            "<li>Must be implemented by concrete subclasses</li>" +
            "<li>Use 'abstract' keyword in method declaration</li>" +
            "</ul></li>" +
            "<li><strong>Concrete Methods</strong>: Can contain fully implemented methods" +
            "<ul>" +
            "<li>Provide common functionality to all subclasses</li>" +
            "</ul></li>" +
            "<li><strong>State</strong>: Can have instance variables (fields) to store state</li>" +
            "<li><strong>Constructors</strong>: Can have constructors (though they cannot be directly instantiated)" +
            "<ul>" +
            "<li>Used during subclass instantiation</li>" +
            "</ul></li>" +
            "<li><strong>Access Modifiers</strong>: Can have methods with any access modifier" +
            "<ul>" +
            "<li>public, protected, private, package-private</li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>CHOOSING BETWEEN INTERFACES AND ABSTRACT CLASSES</h3>\n\n" +
            "<h4 style='color:#3F51B5;margin-top:14px;'>Use Interfaces When</h4>" +
            "<ul style='margin-top:8px;'>" +
            "<li>Multiple classes will implement the same behavior but aren't related</li>" +
            "<li>You need to define a contract for API/library consumers</li>" +
            "<li>A class needs to implement multiple behaviors (multiple interfaces)</li>" +
            "<li>You want to specify what classes can do, not how they do it</li>" +
            "<li>You need to decouple implementation from specification</li>" +
            "<li>You want to enable testability through mock implementations</li>" +
            "<li>Example use cases: <code>Comparable</code>, <code>Serializable</code>, <code>EventListener</code>, Repository pattern</li>" +
            "</ul>\n\n" +
            "<h4 style='color:#3F51B5;margin-top:14px;'>Use Abstract Classes When</h4>" +
            "<ul style='margin-top:8px;'>" +
            "<li>You need to share code among closely related classes</li>" +
            "<li>You want to provide common fields and methods with implementation details</li>" +
            "<li>You need non-public members (protected/private methods or fields)</li>" +
            "<li>You need to define a template method pattern</li>" +
            "<li>Example use cases: <code>AbstractList</code>, <code>AbstractMap</code>, <code>AbstractCollection</code></li>" +
            "</ul>\n\n" +
            "<h4 style='color:#3F51B5;margin-top:14px;'>Design Patterns Utilizing These Concepts</h4>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Strategy Pattern</strong>: Using interfaces to define interchangeable algorithms</li>" +
            "<li><strong>Template Method Pattern</strong>: Using abstract classes to define process skeletons</li>" +
            "<li><strong>Adapter Pattern</strong>: Implementing interfaces to adapt between classes</li>" +
            "<li><strong>Bridge Pattern</strong>: Using interfaces to separate abstraction from implementation</li>" +
            "</ul>",
            "Interfaces",
            13,
            "Advanced",
            "// Interface definition with different features\ninterface Payable {\n    // Abstract method (implicitly public and abstract)\n    double calculatePayment();\n    \n    // Constants (implicitly public, static, final)\n    double MINIMUM_WAGE = 15.0;\n    \n    // Default method (Java 8+)\n    default String getPaymentDetails() {\n        return \"Payment amount: $\" + calculatePayment();\n    }\n    \n    // Static method (Java 8+)\n    static boolean isValidPayment(double amount) {\n        return amount >= 0;\n    }\n    \n    // Private method for internal use (Java 9+)\n    private double calculateTax(double amount) {\n        return amount * 0.2; // 20% tax\n    }\n}\n\n// Another interface to demonstrate multiple inheritance\ninterface Identified {\n    String getId();\n    \n    default void printId() {\n        System.out.println(\"ID: \" + getId());\n    }\n}\n\n// Abstract class example\nabstract class Person {\n    // Instance variables (state)\n    protected String name;\n    protected int age;\n    \n    // Constructor\n    public Person(String name, int age) {\n        this.name = name;\n        this.age = age;\n    }\n    \n    // Concrete method (implemented)\n    public void introduce() {\n        System.out.println(\"Hello, my name is \" + name + \".\");\n    }\n    \n    // Abstract method (must be implemented by subclasses)\n    public abstract String getOccupation();\n    \n    // Protected method (accessible to subclasses)\n    protected boolean isAdult() {\n        return age >= 18;\n    }\n}\n\n// Concrete implementation of both an abstract class and multiple interfaces\nclass Employee extends Person implements Payable, Identified {\n    private String employeeId;\n    private double hourlyRate;\n    private int hoursWorked;\n    \n    public Employee(String name, int age, String employeeId, double hourlyRate) {\n        super(name, age);\n        this.employeeId = employeeId;\n        this.hourlyRate = hourlyRate;\n        this.hoursWorked = 0;\n    }\n    \n    public void addHours(int hours) {\n        this.hoursWorked += hours;\n    }\n    \n    // Implementing abstract method from Person\n    @Override\n    public String getOccupation() {\n        return \"Employee\";\n    }\n    \n    // Implementing abstract method from Payable\n    @Override\n    public double calculatePayment() {\n        return Math.max(hourlyRate * hoursWorked, MINIMUM_WAGE * hoursWorked);\n    }\n    \n    // Implementing abstract method from Identified\n    @Override\n    public String getId() {\n        return employeeId;\n    }\n    \n    // Overriding default method from Payable\n    @Override\n    public String getPaymentDetails() {\n        return \"Employee: \" + name + \", Payment: $\" + calculatePayment();\n    }\n}\n\n// Demo code showing the use of interfaces and abstract class\nclass InterfaceAbstractDemo {\n    public static void main(String[] args) {\n        // Cannot instantiate interfaces or abstract classes\n        // Person p = new Person(\"John\", 25); // Error\n        \n        // Create employee instance\n        Employee emp = new Employee(\"Alice Smith\", 30, \"E12345\", 25.0);\n        emp.addHours(40);\n        \n        // Using methods from Person (abstract class)\n        emp.introduce();  // Concrete method from abstract class\n        System.out.println(\"Occupation: \" + emp.getOccupation());  // Implemented abstract method\n        \n        // Using methods from Payable interface\n        System.out.println(emp.getPaymentDetails());  // Default method (overridden)\n        System.out.println(\"Is payment valid: \" + Payable.isValidPayment(emp.calculatePayment()));  // Static method\n        \n        // Using methods from Identified interface\n        emp.printId();  // Default method\n        \n        // Polymorphism with interfaces\n        Payable payableEntity = emp;\n        System.out.println(\"Payment through interface: $\" + payableEntity.calculatePayment());\n        \n        // Polymorphism with abstract class\n        Person person = emp;\n        person.introduce();\n    }\n}",
            "Interfaces and abstract classes are foundational elements of Java's object-oriented design, enabling code organization through contracts and partial implementations. Interfaces define what classes must do without specifying how, supporting multiple inheritance of behavior, while abstract classes provide a common base for related subclasses with shared implementation details. Together, they enable polymorphism, enhance code reusability, and create flexible, maintainable software architectures."
        ));

        // Lesson 14: Lambda Expressions and Functional Programming
        lessons.add(new Lesson(
            "Lambda Expressions and Functional Programming",
            "<h2 style='color:#3F51B5;'>Lambda Expressions and Functional Programming</h2>" +
            "<p>Lambda expressions, introduced in Java 8, represent a paradigm shift toward more functional programming in Java. They enable treating functionality as a method argument, creating more concise code, and facilitating parallel processing through the Stream API.</p>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>LAMBDA EXPRESSIONS</h3>\n\n" +
            "<h4 style='color:#3F51B5;margin-top:14px;'>Core Concepts</h4>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Definition</strong>: Anonymous functions that can be passed around as values</li>" +
            "<li><strong>Purpose</strong>: Enable treating functionality as data</li>" +
            "<li><strong>Benefits</strong>: Concise code, easier-to-read code, support for functional programming</li>" +
            "</ul>\n\n" +
            "<h4 style='color:#3F51B5;margin-top:14px;'>Lambda Syntax Patterns</h4>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Basic</strong>: <code>(parameters) -> expression</code>" +
            "<ul>" +
            "<li>Returns the result of the expression</li>" +
            "<li>No need for return statement</li>" +
            "<li>Example: <code>(a, b) -> a + b</code></li>" +
            "</ul></li>" +
            "<li><strong>Block</strong>: <code>(parameters) -> { statements; }</code>" +
            "<ul>" +
            "<li>For multiple statements</li>" +
            "<li>Requires return statement if returning a value</li>" +
            "<li>Example: <code>(x, y) -> { int sum = x + y; return sum; }</code></li>" +
            "</ul></li>" +
            "<li><strong>Parameter variations</strong>:" +
            "<ul>" +
            "<li>Zero parameters: <code>() -> expression</code></li>" +
            "<li>One parameter: <code>x -> expression</code> (parentheses optional)</li>" +
            "<li>Multiple parameters: <code>(x, y) -> expression</code></li>" +
            "<li>With explicit types: <code>(String s) -> s.length()</code></li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h3>FUNCTIONAL INTERFACES:</h3>\n\n" +
            "<h4>Core Concepts:</h4>\n" +
            "<ul>" +
            "<li>Definition: Interface with exactly one abstract method</li>" +
            "<li>Purpose: Serves as target type for lambda expressions</li>" +
            "<li>Annotation: @FunctionalInterface (optional but recommended)</li>" +
            "</ul>\n\n" +
            "<h4>Key Built-in Functional Interfaces (java.util.function):</h4>\n\n" +
            "<ul>" +
            "<li><strong>Function&lt;T, R&gt;</strong>: Transforms a T to an R" +
            "<ul>" +
            "<li>abstract method: <code>R apply(T t)</code></li>" +
            "<li>Example: <code>Function&lt;String, Integer&gt; length = s -> s.length();</code></li>" +
            "<li>Specialized variants: IntFunction, LongToDoubleFunction, etc.</li>" +
            "</ul></li>" +
            "<li><strong>Predicate&lt;T&gt;</strong>: Tests a condition, returns boolean" +
            "<ul>" +
            "<li>abstract method: <code>boolean test(T t)</code></li>" +
            "<li>Example: <code>Predicate&lt;String&gt; isEmpty = s -> s.isEmpty();</code></li>" +
            "<li>Specialized variants: IntPredicate, DoublePredicate, etc.</li>" +
            "</ul></li>" +
            "<li><strong>Consumer&lt;T&gt;</strong>: Performs an action on T, returns nothing" +
            "<ul>" +
            "<li>abstract method: <code>void accept(T t)</code></li>" +
            "<li>Example: <code>Consumer&lt;String&gt; print = s -> System.out.println(s);</code></li>" +
            "<li>Specialized variants: IntConsumer, ObjDoubleConsumer, etc.</li>" +
            "</ul></li>" +
            "<li><strong>Supplier&lt;T&gt;</strong>: Provides a T, takes no input" +
            "<ul>" +
            "<li>abstract method: <code>T get()</code></li>" +
            "<li>Example: <code>Supplier&lt;Double&gt; random = () -> Math.random();</code></li>" +
            "<li>Specialized variants: BooleanSupplier, IntSupplier, etc.</li>" +
            "</ul></li>" +
            "<li><strong>BinaryOperator&lt;T&gt;</strong>: Takes two Ts, returns a T" +
            "<ul>" +
            "<li>abstract method: <code>T apply(T t1, T t2)</code></li>" +
            "<li>Example: <code>BinaryOperator&lt;Integer&gt; add = (a, b) -> a + b;</code></li>" +
            "<li>Specialized variants: IntBinaryOperator, etc.</li>" +
            "</ul></li>" +
            "<li><strong>UnaryOperator&lt;T&gt;</strong>: Takes a T, returns a T" +
            "<ul>" +
            "<li>abstract method: <code>T apply(T t)</code></li>" +
            "<li>Example: <code>UnaryOperator&lt;String&gt; toUpperCase = s -> s.toUpperCase();</code></li>" +
            "<li>Specialized variants: IntUnaryOperator, etc.</li>" +
            "</ul></li>" +
            "<li><strong>Other common interfaces</strong>: Comparator&lt;T&gt;, Runnable, Callable&lt;V&gt;</li>" +
            "</ul>\n\n" +
            "<h3>METHOD REFERENCES:</h3>\n\n" +
            "<ul>" +
            "<li>Shorthand for lambdas that call a single method</li>" +
            "<li>Syntax: <code>ClassName::methodName</code></li>" +
            "<li>Types:" +
            "<ul>" +
            "<li>Static method reference: <code>Math::abs</code></li>" +
            "<li>Instance method of specific object: <code>myObj::instanceMethod</code></li>" +
            "<li>Instance method of arbitrary object of specific type: <code>String::toUpperCase</code></li>" +
            "<li>Constructor reference: <code>ArrayList::new</code></li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h3>STREAM API:</h3>\n\n" +
            "<h4>Core Concepts:</h4>\n" +
            "<ul>" +
            "<li>Definition: A sequence of elements supporting sequential and parallel operations</li>" +
            "<li>Purpose: Functional-style operations on collections</li>" +
            "<li>Characteristics: Not a data structure, designed for lambdas, lazily evaluated</li>" +
            "</ul>\n\n" +
            "<h4>Creating Streams:</h4>\n" +
            "<ul>" +
            "<li>From collections: <code>collection.stream()</code></li>" +
            "<li>From arrays: <code>Arrays.stream(array)</code></li>" +
            "<li>From values: <code>Stream.of(\"a\", \"b\", \"c\")</code></li>" +
            "<li>Infinite streams: <code>Stream.generate(() -> Math.random())</code></li>" +
            "</ul>\n\n" +
            "<h4>Stream Operations:</h4>\n" +
            "<ul>" +
            "<li><strong>Intermediate operations</strong>: Return another stream" +
            "<ul>" +
            "<li><code>filter(Predicate)</code>: Filter elements matching predicate</li>" +
            "<li><code>map(Function)</code>: Transform each element</li>" +
            "<li><code>flatMap(Function)</code>: Transform and flatten</li>" +
            "<li><code>distinct()</code>: Remove duplicates</li>" +
            "<li><code>sorted()</code>: Sort elements</li>" +
            "<li><code>limit(n)/skip(n)</code>: Limit/skip elements</li>" +
            "<li><code>peek(Consumer)</code>: Perform action on each element (for debugging)</li>" +
            "</ul></li>" +
            "<li><strong>Terminal operations</strong>: Produce a result or side-effect" +
            "<ul>" +
            "<li><code>forEach(Consumer)</code>: Perform action on each element</li>" +
            "<li><code>collect(Collector)</code>: Accumulate elements into a container</li>" +
            "<li><code>reduce(identity, BinaryOperator)</code>: Reduce to single value</li>" +
            "<li><code>count()</code>: Count elements</li>" +
            "<li><code>anyMatch()/allMatch()/noneMatch()</code>: Test predicates</li>" +
            "<li><code>findFirst()/findAny()</code>: Find elements</li>" +
            "<li><code>min()/max()</code>: Find extreme values</li>" +
            "<li><code>toArray()</code>: Convert to array</li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h4>Common Collectors:</h4>\n" +
            "<ul>" +
            "<li><code>toList(), toSet(), toMap()</code>: Convert to collections</li>" +
            "<li><code>joining()</code>: Concatenate strings</li>" +
            "<li><code>summarizingInt/Long/Double()</code>: Statistical summaries</li>" +
            "<li><code>groupingBy()</code>: Group by classifier</li>" +
            "<li><code>partitioningBy()</code>: Partition by predicate</li>" +
            "</ul>\n\n" +
            "<h4>Parallel Streams:</h4>\n" +
            "<ul>" +
            "<li><code>parallel()</code>: Convert to parallel stream</li>" +
            "<li><code>parallelStream()</code>: Create parallel stream from collection</li>" +
            "<li>Best for large data sets with independent operations</li>" +
            "</ul>",
            "Lambda",
            14,
            "Advanced",
            "import java.util.*;\nimport java.util.function.*;\nimport java.util.stream.*;\n\npublic class LambdaAndFunctionalDemo {\n    public static void main(String[] args) {\n        // ===== LAMBDA EXPRESSIONS =====\n        \n        // Basic lambda expression examples\n        Runnable noArgLambda = () -> System.out.println(\"No arguments lambda\");\n        noArgLambda.run();\n        \n        // Single parameter (type inferred)\n        Function<String, Integer> lengthFunction = s -> s.length();\n        System.out.println(\"Length of 'Hello': \" + lengthFunction.apply(\"Hello\"));  // 5\n        \n        // Multiple parameters with explicit types\n        BinaryOperator<Integer> multiply = (Integer a, Integer b) -> a * b;\n        System.out.println(\"5 × 7 = \" + multiply.apply(5, 7));  // 35\n        \n        // Block lambda with multiple statements\n        Function<Integer, Integer> factorial = n -> {\n            int result = 1;\n            for (int i = 1; i <= n; i++) {\n                result *= i;\n            }\n            return result;\n        };\n        System.out.println(\"Factorial of 5: \" + factorial.apply(5));  // 120\n        \n        // ===== FUNCTIONAL INTERFACES =====\n        \n        List<String> names = Arrays.asList(\"Alice\", \"Bob\", \"Charlie\", \"Dave\", \"Eve\");\n        \n        // Predicate - test a condition\n        Predicate<String> startsWithA = name -> name.startsWith(\"A\");\n        System.out.println(\"\\nNames starting with 'A': \" + \n                        names.stream().filter(startsWithA).collect(Collectors.toList()));\n        \n        // Consumer - perform operation without return\n        System.out.println(\"\\nPrinting names with Consumer:\");\n        Consumer<String> printUpperCase = name -> System.out.println(name.toUpperCase());\n        names.forEach(printUpperCase);\n        \n        // Function - transform input to output\n        Function<String, String> addGreeting = name -> \"Hello, \" + name + \"!\";\n        List<String> greetings = names.stream().map(addGreeting).collect(Collectors.toList());\n        System.out.println(\"\\nGreetings: \" + greetings);\n        \n        // Supplier - generate values\n        Supplier<Double> randomValue = () -> Math.random() * 100;\n        System.out.println(\"\\nRandom values from supplier:\");\n        for (int i = 0; i < 3; i++) {\n            System.out.println(randomValue.get());\n        }\n        \n        // BiFunction - takes two inputs, returns one output\n        BiFunction<String, Integer, String> repeatString = \n            (str, times) -> str.repeat(times);  // Java 11+ feature\n        System.out.println(\"\\nRepeated string: \" + repeatString.apply(\"Java\", 3));\n        \n        // ===== METHOD REFERENCES =====\n        \n        // Static method reference\n        Function<Double, Double> sqrt = Math::sqrt;\n        System.out.println(\"\\nSquare root of 16: \" + sqrt.apply(16.0));  // 4.0\n        \n        // Instance method reference of specific object\n        String prefix = \"Mr. \";\n        Function<String, String> addPrefix = prefix::concat;\n        System.out.println(addPrefix.apply(\"Smith\"));  // Mr. Smith\n        \n        // Instance method reference of arbitrary object of specific type\n        Function<String, String> toUpper = String::toUpperCase;\n        System.out.println(toUpper.apply(\"hello\"));  // HELLO\n        \n        // Constructor reference\n        Supplier<List<String>> listFactory = ArrayList::new;\n        List<String> newList = listFactory.get();  // Creates new ArrayList\n        \n        // ===== STREAM API =====\n        \n        List<Person> people = Arrays.asList(\n            new Person(\"Alice\", 28, \"Engineer\"),\n            new Person(\"Bob\", 35, \"Manager\"),\n            new Person(\"Charlie\", 22, \"Engineer\"),\n            new Person(\"Dave\", 42, \"Director\"),\n            new Person(\"Eve\", 29, \"Designer\")\n        );\n        \n        // Filtering\n        List<Person> engineers = people.stream()\n            .filter(p -> p.getJob().equals(\"Engineer\"))\n            .collect(Collectors.toList());\n        System.out.println(\"\\nEngineers: \" + engineers);\n        \n        // Mapping\n        List<String> upperCaseNames = people.stream()\n            .map(Person::getName)\n            .map(String::toUpperCase)\n            .collect(Collectors.toList());\n        System.out.println(\"\\nUpper case names: \" + upperCaseNames);\n        \n        // Sorting\n        List<Person> sortedByAge = people.stream()\n            .sorted(Comparator.comparing(Person::getAge))\n            .collect(Collectors.toList());\n        System.out.println(\"\\nPeople sorted by age: \" + sortedByAge);\n        \n        // Reducing\n        int totalAge = people.stream()\n            .mapToInt(Person::getAge)\n            .sum();\n        System.out.println(\"\\nTotal age: \" + totalAge);\n        \n        double averageAge = people.stream()\n            .mapToInt(Person::getAge)\n            .average()\n            .orElse(0);\n        System.out.println(\"Average age: \" + averageAge);\n        \n        // Collectors - groupingBy\n        Map<String, List<Person>> peopleByJob = people.stream()\n            .collect(Collectors.groupingBy(Person::getJob));\n        System.out.println(\"\\nPeople grouped by job: \" + peopleByJob);\n        \n        // Collectors - joining\n        String allNames = people.stream()\n            .map(Person::getName)\n            .collect(Collectors.joining(\", \", \"Names: \", \".\"));\n        System.out.println(\"\\n\" + allNames);\n        \n        // Finding elements\n        Optional<Person> youngest = people.stream()\n            .min(Comparator.comparing(Person::getAge));\n        System.out.println(\"\\nYoungest person: \" + youngest.orElse(null));\n        \n        // Parallel stream for potentially better performance\n        long count = people.parallelStream()\n            .filter(p -> p.getAge() > 30)\n            .count();\n        System.out.println(\"\\nNumber of people over 30: \" + count);\n    }\n    \n    // Helper class for demonstrations\n    static class Person {\n        private String name;\n        private int age;\n        private String job;\n        \n        public Person(String name, int age, String job) {\n            this.name = name;\n            this.age = age;\n            this.job = job;\n        }\n        \n        public String getName() { return name; }\n        public int getAge() { return age; }\n        public String getJob() { return job; }\n        \n        @Override\n        public String toString() {\n            return name + \"(\" + age + \", \" + job + \")\";\n        }\n    }\n}",
            "Lambda expressions and functional programming in Java 8+ enable writing more concise and expressive code. Lambda expressions provide a way to represent anonymous functions that can be passed as arguments, supporting a more declarative coding style. The Stream API facilitates functional-style operations on collections, combining powerful data processing capabilities with potential performance benefits through parallelism. Real-world applications include event handling in UI frameworks, callback mechanisms in asynchronous programming, data transformation pipelines, and concurrent processing of large datasets. This functional approach leads to code that is more maintainable, less error-prone, and often better aligned with business requirements."
        ));

        // Lesson 15: Multithreading and Concurrency
        lessons.add(new Lesson(
            "Multithreading and Concurrency",
            "<h2 style='color:#3F51B5;'>Multithreading and Concurrency</h2>" +
            "<p>Multithreading and concurrency enable programs to execute multiple operations simultaneously, improving responsiveness and resource utilization. Understanding these concepts is essential for developing efficient, scalable applications in modern multi-core environments.</p>\n\n" +
            "<h3 style='color:#3F51B5;margin-top:16px;'>THREAD FUNDAMENTALS</h3>\n\n" +
            "<h4 style='color:#3F51B5;margin-top:14px;'>Core Concepts</h4>" +
            "<ul style='margin-top:8px;'>" +
            "<li><strong>Process</strong>: An independent program with its own memory space</li>" +
            "<li><strong>Thread</strong>: Lightweight unit of execution within a process</li>" +
            "<li><strong>Benefits</strong>: Improved responsiveness, resource utilization, performance</li>" +
            "<li><strong>Challenges</strong>: Thread safety, deadlocks, race conditions</li>" +
            "</ul>\n\n" +
            "<h4 style='color:#3F51B5;margin-top:14px;'>Creating and Starting Threads</h4>" +
            "<ol style='margin-top:8px;'>" +
            "<li><strong>Extending Thread class</strong>:" +
            "<ul>" +
            "<li>Create a subclass of Thread</li>" +
            "<li>Override the <code>run()</code> method</li>" +
            "<li>Call start() to execute in new thread</li>" +
            "</ul></li>" +
            "<li><strong>Implementing Runnable interface (preferred)</strong>:" +
            "<ul>" +
            "<li>Implement the run() method</li>" +
            "<li>Pass to Thread constructor</li>" +
            "<li>Call start() on Thread instance</li>" +
            "</ul></li>" +
            "<li><strong>Using lambda expressions (Java 8+)</strong>:" +
            "<ul>" +
            "<li>Thread t = new Thread(() -> { /* code */ });</li>" +
            "<li>Concise syntax for simple thread operations</li>" +
            "</ul></li>" +
            "</ol>\n\n" +
            "<h3>Thread Lifecycle States:</h3>\n" +
            "<ul>" +
            "<li><strong>NEW</strong>: Thread created but not started</li>" +
            "<li><strong>RUNNABLE</strong>: Thread is executing or ready for execution</li>" +
            "<li><strong>BLOCKED</strong>: Thread waiting for monitor lock</li>" +
            "<li><strong>WAITING</strong>: Thread waiting indefinitely for another thread</li>" +
            "<li><strong>TIMED_WAITING</strong>: Thread waiting for specified period</li>" +
            "<li><strong>TERMINATED</strong>: Thread completed execution</li>" +
            "</ul>\n\n" +
            "<h3>Thread Operations:</h3>\n" +
            "<ul>" +
            "<li><code>start()</code>: Begin thread execution</li>" +
            "<li><code>join()</code>: Wait for thread completion</li>" +
            "<li><code>sleep(ms)</code>: Pause thread execution</li>" +
            "<li><code>interrupt()</code>: Request thread termination</li>" +
            "<li><code>isAlive()</code>: Check if thread is running</li>" +
            "<li><code>setPriority(1-10)</code>: Set thread priority</li>" +
            "<li><code>setDaemon(true)</code>: Set as daemon thread</li>" +
            "</ul>\n\n" +
            "<h3>THREAD SYNCHRONIZATION:</h3>\n\n" +
            "<h4>The Need for Synchronization:</h4>\n" +
            "<ul>" +
            "<li>Race conditions: Unpredictable results when threads access shared data</li>" +
            "<li>Thread interference: Threads interfering with each other's operations</li>" +
            "<li>Memory consistency errors: Thread seeing stale values</li>" +
            "<li>Data corruption: Inconsistent state due to partial updates</li>" +
            "<li>Lost updates: One thread's changes overwritten by another thread</li>" +
            "</ul>\n\n" +
            "<h4>Key Synchronization Concepts:</h4>\n" +
            "<ul>" +
            "<li>Mutual exclusion: Only one thread can access a critical section at a time</li>" +
            "<li>Visibility: Changes made by one thread are visible to other threads</li>" +
            "<li>Ordering: Operations happen in a predictable sequence</li>" +
            "</ul>\n\n" +
            "<h4>Synchronization Mechanisms:</h4>\n" +
            "<ol>" +
            "<li><strong>synchronized keyword</strong>:" +
            "<ul>" +
            "<li>Method level: synchronized void method() { }</li>" +
            "<li>Block level: synchronized(object) { /* code */ }</li>" +
            "<li>Provides mutual exclusion using object's intrinsic lock</li>" +
            "</ul></li>" +
            "<li><strong>Lock interface (java.util.concurrent.locks)</strong>:" +
            "<ul>" +
            "<li>More flexible than synchronized</li>" +
            "<li>ReentrantLock: Basic mutual exclusion</li>" +
            "<li>ReadWriteLock: Separate locks for reads/writes</li>" +
            "<li>StampedLock: Optimized for reader-writer scenarios</li>" +
            "</ul></li>" +
            "<li><strong>Atomic classes (java.util.concurrent.atomic)</strong>:" +
            "<ul>" +
            "<li>Thread-safe without explicit locking</li>" +
            "<li>AtomicInteger, AtomicBoolean, AtomicReference, etc.</li>" +
            "<li>Compare-and-swap (CAS) operations</li>" +
            "</ul></li>" +
            "<li><strong>volatile keyword</strong>:" +
            "<ul>" +
            "<li>Ensures visibility of changes to variables across threads</li>" +
            "<li>Does NOT provide atomicity for compound actions</li>" +
            "</ul></li>" +
            "</ol>\n\n" +
            "<h4>Inter-thread Communication:</h4>\n" +
            "<ul>" +
            "<li><code>wait()</code>: Release lock and wait until notification</li>" +
            "<li><code>notify()</code>: Wake up a single waiting thread</li>" +
            "<li><code>notifyAll()</code>: Wake up all waiting threads</li>" +
            "</ul>\n\n" +
            "<h4>Common Concurrency Issues:</h4>\n" +
            "<ul>" +
            "<li>Deadlock: Threads waiting for each other's locks</li>" +
            "<li>Livelock: Threads responding to each other without progress</li>" +
            "<li>Starvation: Thread unable to gain access to shared resource</li>" +
            "<li>Thread leakage: Threads not properly terminated</li>" +
            "</ul>\n\n" +
            "<h3>CONCURRENCY UTILITIES (java.util.concurrent):</h3>\n\n" +
            "<h4>Executor Framework:</h4>\n" +
            "<ul>" +
            "<li>Thread pooling and management</li>" +
            "<li>ExecutorService: Manages thread lifecycle" +
            "<ul>" +
            "<li>submit() tasks return Future objects</li>" +
            "<li>shutdown() graceful termination</li>" +
            "</ul></li>" +
            "<li>ThreadPoolExecutor: Configurable thread pool</li>" +
            "<li>Types of thread pools:" +
            "<ul>" +
            "<li>Fixed: Fixed number of threads</li>" +
            "<li>Cached: Creates new threads as needed</li>" +
            "<li>Scheduled: Scheduled task execution</li>" +
            "<li>SingleThreaded: Single-threaded executor</li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h4>Future and CompletableFuture:</h4>\n" +
            "<ul>" +
            "<li>Future: Represents result of asynchronous computation" +
            "<ul>" +
            "<li><code>get()</code>: Retrieve result (blocking)</li>" +
            "<li><code>isDone()</code>: Check completion status</li>" +
            "<li><code>cancel()</code>: Attempt to cancel execution</li>" +
            "</ul></li>" +
            "<li>CompletableFuture (Java 8+): Enhanced asynchronous programming" +
            "<ul>" +
            "<li>Chaining operations: <code>thenApply()</code>, <code>thenCompose()</code></li>" +
            "<li>Combining results: <code>thenCombine()</code>, <code>allOf()</code>, <code>anyOf()</code></li>" +
            "<li>Exception handling: <code>exceptionally()</code>, <code>handle()</code></li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h4>Concurrent Collections:</h4>\n" +
            "<ul>" +
            "<li>ConcurrentHashMap: Thread-safe hash map</li>" +
            "<li>CopyOnWriteArrayList: Thread-safe list for frequent reads</li>" +
            "<li>ConcurrentLinkedQueue: Non-blocking queue</li>" +
            "<li>BlockingQueue: Supports blocking operations" +
            "<ul>" +
            "<li>ArrayBlockingQueue: Bounded blocking queue</li>" +
            "<li>LinkedBlockingQueue: Optionally bounded</li>" +
            "</ul></li>" +
            "</ul>\n\n" +
            "<h4>Synchronizers:</h4>\n" +
            "<ul>" +
            "<li>CountDownLatch: Wait until count reaches zero</li>" +
            "<li>CyclicBarrier: Wait for group of threads</li>" +
            "<li>Semaphore: Control access to resources</li>" +
            "<li>Phaser: More flexible barrier</li>" +
            "<li>Exchanger: Exchange data between two threads</li>" +
            "</ul>\n\n" +
            "<h3>THREAD-SAFE PROGRAMMING:</h3>\n\n" +
            "<h4>Best Practices:</h4>\n" +
            "<ul>" +
            "<li>Minimize shared mutable state</li>" +
            "<li>Use immutable objects when possible</li>" +
            "<li>Prefer concurrent collections</li>" +
            "<li>Keep synchronized blocks small</li>" +
            "<li>Avoid complex synchronization logic</li>" +
            "<li>Use higher-level concurrency utilities</li>" +
            "<li>Document thread-safety guarantees</li>" +
            "</ul>",
            "Multithreading",
            15,
            "Advanced",
            "import java.util.concurrent.*;\nimport java.util.concurrent.atomic.*;\nimport java.util.concurrent.locks.*;\nimport java.util.*;\n\npublic class MultithreadingAndConcurrencyDemo {\n    public static void main(String[] args) throws Exception {\n        System.out.println(\"=== Thread Creation and Basic Operations ===\\n\");\n        threadCreationDemo();\n        \n        System.out.println(\"\\n=== Thread Synchronization ===\\n\");\n        synchronizationDemo();\n        \n        System.out.println(\"\\n=== Executor Framework ===\\n\");\n        executorDemo();\n        \n        System.out.println(\"\\n=== CompletableFuture ===\\n\");\n        completableFutureDemo();\n        \n        System.out.println(\"\\n=== Concurrent Collections ===\\n\");\n        concurrentCollectionsDemo();\n        \n        System.out.println(\"\\n=== Synchronizers ===\\n\");\n        synchronizersDemo();\n    }\n    \n    // Demonstrates different ways to create and use threads\n    private static void threadCreationDemo() throws InterruptedException {\n        // 1. Extending Thread class\n        class MyThread extends Thread {\n            @Override\n            public void run() {\n                System.out.println(\"Thread created by extending Thread: \" + Thread.currentThread().getName());\n            }\n        }\n        \n        // 2. Implementing Runnable interface\n        class MyRunnable implements Runnable {\n            @Override\n            public void run() {\n                System.out.println(\"Thread created with Runnable: \" + Thread.currentThread().getName());\n            }\n        }\n        \n        // Create and start threads\n        Thread t1 = new MyThread();\n        t1.start();\n        \n        Thread t2 = new Thread(new MyRunnable());\n        t2.start();\n        \n        // 3. Using lambda expression (Java 8+)\n        Thread t3 = new Thread(() -> {\n            System.out.println(\"Thread created with lambda: \" + Thread.currentThread().getName());\n        });\n        t3.start();\n        \n        // Wait for all threads to complete\n        t1.join();\n        t2.join();\n        t3.join();\n        \n        // Thread state demonstration\n        Thread stateDemo = new Thread(() -> {\n            try {\n                Thread.sleep(100); // TIMED_WAITING state\n            } catch (InterruptedException e) {\n                System.out.println(\"Thread interrupted\");\n            }\n        });\n        \n        System.out.println(\"\\nThread State Demo:\");\n        System.out.println(\"Initial state: \" + stateDemo.getState()); // NEW\n        stateDemo.start();\n        System.out.println(\"After start(): \" + stateDemo.getState()); // RUNNABLE\n        Thread.sleep(50);\n        System.out.println(\"While sleeping: \" + stateDemo.getState()); // TIMED_WAITING\n        stateDemo.join();\n        System.out.println(\"After completion: \" + stateDemo.getState()); // TERMINATED\n    }\n    \n    // Demonstrates different synchronization mechanisms\n    private static void synchronizationDemo() throws InterruptedException {\n        // 1. Using synchronized blocks\n        Counter synchronizedCounter = new Counter();\n        \n        // 2. Using Lock interface\n        LockCounter lockCounter = new LockCounter();\n        \n        // 3. Using Atomic classes\n        AtomicCounter atomicCounter = new AtomicCounter();\n        \n        // Create threads to increment each counter\n        Runnable incrementTask = () -> {\n            for (int i = 0; i < 10000; i++) {\n                synchronizedCounter.increment();\n                lockCounter.increment();\n                atomicCounter.increment();\n            }\n        };\n        \n        Thread t1 = new Thread(incrementTask);\n        Thread t2 = new Thread(incrementTask);\n        \n        t1.start();\n        t2.start();\n        \n        t1.join();\n        t2.join();\n        \n        System.out.println(\"Synchronized Counter: \" + synchronizedCounter.getValue());\n        System.out.println(\"Lock-based Counter: \" + lockCounter.getValue());\n        System.out.println(\"Atomic Counter: \" + atomicCounter.getValue());\n        \n        // 4. Demonstrating volatile keyword\n        VolatileDemo volatileDemo = new VolatileDemo();\n        Thread volatileThread = new Thread(volatileDemo);\n        volatileThread.start();\n        \n        // Give some time for the thread to start\n        Thread.sleep(100);\n        \n        // Signal the thread to stop\n        volatileDemo.stop();\n        volatileThread.join();\n        System.out.println(\"Volatile demo completed after \" + volatileDemo.getCount() + \" iterations\");\n    }\n    \n    // Demonstrates the Executor framework\n    private static void executorDemo() throws Exception {\n        // Create fixed thread pool\n        ExecutorService executor = Executors.newFixedThreadPool(2);\n        \n        System.out.println(\"Submitting tasks to executor...\");\n        \n        // Submit Runnable task (no result)\n        executor.execute(() -> {\n            System.out.println(\"Task executed in thread: \" + Thread.currentThread().getName());\n        });\n        \n        // Submit Callable task (with result)\n        Future<String> future = executor.submit(() -> {\n            Thread.sleep(500); // Simulate work\n            return \"Task completed at \" + new Date();\n        });\n        \n        // Get result (blocking call)\n        System.out.println(\"Future result: \" + future.get());\n        \n        // Shutdown executor properly\n        executor.shutdown();\n        boolean terminated = executor.awaitTermination(1, TimeUnit.SECONDS);\n        System.out.println(\"Executor terminated: \" + terminated);\n    }\n    \n    // Demonstrates CompletableFuture for async operations\n    private static void completableFutureDemo() throws Exception {\n        // Create a CompletableFuture\n        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {\n            try {\n                Thread.sleep(200);\n                return \"Step 1\";\n            } catch (InterruptedException e) {\n                return \"Error in step 1\";\n            }\n        });\n        \n        // Chain operations\n        CompletableFuture<String> finalFuture = future\n            .thenApply(result -> result + \" -> Step 2\") // Transform result\n            .thenCompose(result -> CompletableFuture.supplyAsync(() -> {\n                // Perform another async operation\n                return result + \" -> Step 3\";\n            }))\n            .exceptionally(ex -> \"Error occurred: \" + ex.getMessage()); // Handle exceptions\n        \n        // Get final result\n        System.out.println(\"CompletableFuture result: \" + finalFuture.get());\n        \n        // Combine two CompletableFutures\n        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 10);\n        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 20);\n        \n        CompletableFuture<Integer> combinedFuture = future1.thenCombine(future2, (x, y) -> x + y);\n        System.out.println(\"Combined future result: \" + combinedFuture.get());\n    }\n    \n    // Demonstrates concurrent collections\n    private static void concurrentCollectionsDemo() throws InterruptedException {\n        // 1. ConcurrentHashMap\n        Map<String, Integer> concurrentMap = new ConcurrentHashMap<>();\n        \n        // 2. CopyOnWriteArrayList\n        List<String> concurrentList = new CopyOnWriteArrayList<>();\n        \n        // Populate collections\n        Runnable populateTask = () -> {\n            for (int i = 0; i < 100; i++) {\n                concurrentMap.put(\"Key-\" + i, i);\n                concurrentList.add(\"Item-\" + i);\n            }\n        };\n        \n        Thread t1 = new Thread(populateTask);\n        Thread t2 = new Thread(populateTask);\n        \n        t1.start();\n        t2.start();\n        t1.join();\n        t2.join();\n        \n        System.out.println(\"ConcurrentHashMap size: \" + concurrentMap.size());\n        System.out.println(\"CopyOnWriteArrayList size: \" + concurrentList.size());\n        \n        // 3. BlockingQueue example\n        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(10);\n        \n        // Producer thread\n        Thread producer = new Thread(() -> {\n            try {\n                for (int i = 0; i < 5; i++) {\n                    String item = \"Item-\" + i;\n                    blockingQueue.put(item); // Blocks if queue is full\n                    System.out.println(\"Produced: \" + item);\n                }\n            } catch (InterruptedException e) {\n                Thread.currentThread().interrupt();\n            }\n        });\n        \n        // Consumer thread\n        Thread consumer = new Thread(() -> {\n            try {\n                for (int i = 0; i < 5; i++) {\n                    String item = blockingQueue.take(); // Blocks if queue is empty\n                    System.out.println(\"Consumed: \" + item);\n                    Thread.sleep(100); // Consume slower than production\n                }\n            } catch (InterruptedException e) {\n                Thread.currentThread().interrupt();\n            }\n        });\n        \n        producer.start();\n        consumer.start();\n        producer.join();\n        consumer.join();\n    }\n    \n    // Demonstrates synchronizer utilities\n    private static void synchronizersDemo() throws InterruptedException {\n        // 1. CountDownLatch\n        CountDownLatch latch = new CountDownLatch(3);\n        \n        Runnable latchTask = () -> {\n            try {\n                Thread.sleep((long)(Math.random() * 200));\n                System.out.println(Thread.currentThread().getName() + \" completed task\");\n                latch.countDown(); // Decrements the count\n            } catch (InterruptedException e) {\n                Thread.currentThread().interrupt();\n            }\n        };\n        \n        System.out.println(\"Starting CountDownLatch demo with 3 tasks...\");\n        for (int i = 0; i < 3; i++) {\n            new Thread(latchTask, \"LatchThread-\" + i).start();\n        }\n        \n        // Wait for all tasks to complete\n        latch.await();\n        System.out.println(\"All latch tasks completed\\n\");\n        \n        // 2. CyclicBarrier\n        CyclicBarrier barrier = new CyclicBarrier(3, () -> {\n            // This runs when all threads reach the barrier\n            System.out.println(\"All threads reached the barrier!\\n\");\n        });\n        \n        Runnable barrierTask = () -> {\n            try {\n                System.out.println(Thread.currentThread().getName() + \" waiting at barrier\");\n                barrier.await(); // Wait for all threads to reach this point\n                System.out.println(Thread.currentThread().getName() + \" continued after barrier\");\n            } catch (InterruptedException | BrokenBarrierException e) {\n                Thread.currentThread().interrupt();\n            }\n        };\n        \n        System.out.println(\"Starting CyclicBarrier demo...\");\n        for (int i = 0; i < 3; i++) {\n            new Thread(barrierTask, \"BarrierThread-\" + i).start();\n        }\n        \n        // Give time for barrier demo to complete\n        Thread.sleep(500);\n        \n        // 3. Semaphore (limiting concurrent access)\n        Semaphore semaphore = new Semaphore(2); // Allow only 2 concurrent accesses\n        \n        Runnable semaphoreTask = () -> {\n            try {\n                System.out.println(Thread.currentThread().getName() + \" waiting for permit\");\n                semaphore.acquire();\n                System.out.println(Thread.currentThread().getName() + \" acquired permit\");\n                Thread.sleep(200); // Simulate work\n                System.out.println(Thread.currentThread().getName() + \" releasing permit\");\n                semaphore.release();\n            } catch (InterruptedException e) {\n                Thread.currentThread().interrupt();\n            }\n        };\n        \n        System.out.println(\"\\nStarting Semaphore demo (2 permits, 4 threads)...\");\n        for (int i = 0; i < 4; i++) {\n            new Thread(semaphoreTask, \"SemaphoreThread-\" + i).start();\n        }\n    }\n    \n    // Class demonstrating synchronized methods\n    static class Counter {\n        private int count = 0;\n        \n        public synchronized void increment() {\n            count++;\n        }\n        \n        public synchronized int getValue() {\n            return count;\n        }\n    }\n    \n    // Class demonstrating Lock interface\n    static class LockCounter {\n        private int count = 0;\n        private final Lock lock = new ReentrantLock();\n        \n        public void increment() {\n            lock.lock();\n            try {\n                count++;\n            } finally {\n                lock.unlock(); // Always release lock in finally block\n            }\n        }\n        \n        public int getValue() {\n            lock.lock();\n            try {\n                return count;\n            } finally {\n                lock.unlock();\n            }\n        }\n    }\n    \n    // Class demonstrating Atomic variables\n    static class AtomicCounter {\n        private AtomicInteger count = new AtomicInteger(0);\n        \n        public void increment() {\n            count.incrementAndGet();\n        }\n        \n        public int getValue() {\n            return count.get();\n        }\n    }\n    \n    // Class demonstrating volatile keyword\n    static class VolatileDemo implements Runnable {\n        private volatile boolean running = true;\n        private int count = 0;\n        \n        @Override\n        public void run() {\n            while (running) {\n                count++;\n            }\n        }\n        \n        public void stop() {\n            running = false;\n        }\n        \n        public int getCount() {\n            return count;\n        }\n    }\n}",
            "Multithreading and concurrency enable Java applications to execute multiple tasks simultaneously, improving performance and resource utilization on modern multi-core processors. Through thread creation, synchronization mechanisms, and high-level concurrency utilities, developers can build responsive applications while managing the challenges of shared state and race conditions. Proper thread management is essential for creating scalable, efficient applications in environments where parallel processing is increasingly important."
        ));

        Log.d(TAG, "Attempting to insert " + lessons.size() + " lessons");
        try {
            database.lessonDao().insertAllLessons(lessons);
            int insertedCount = database.lessonDao().getLessonCount();
            Log.d(TAG, "Successfully inserted lessons. Total count now: " + insertedCount);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting lessons: " + e.getMessage(), e);
        }
    }

    private static void remapQuizLessonIds(JavaBuddyDatabase database, List<Quiz> quizzes) {
        if (quizzes.isEmpty()) {
            Log.d(TAG, "No quizzes to remap to lesson IDs");
            return;
        }

        try {
            List<Lesson> lessons = database.lessonDao().getAllLessons();
            if (lessons == null || lessons.isEmpty()) {
                Log.w(TAG, "Cannot remap quizzes because no lessons were found in the database");
                return;
            }

            Map<Integer, Integer> orderToId = new HashMap<>();
            for (Lesson lesson : lessons) {
                orderToId.put(lesson.getOrderIndex(), lesson.getId());
            }

            for (Quiz quiz : quizzes) {
                Integer actualLessonId = orderToId.get(quiz.getLessonId());
                if (actualLessonId != null) {
                    quiz.setLessonId(actualLessonId);
                } else {
                    Log.w(TAG, "No lesson ID found for order index " + quiz.getLessonId() +
                            " while preparing quiz: " + quiz.getQuestion());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error remapping quiz lesson IDs: " + e.getMessage(), e);
        }
    }

    private static void populateQuizzes(JavaBuddyDatabase database) {
        List<Quiz> quizzes = new ArrayList<>();

        // ===== LESSON 1 QUIZZES =====
        quizzes.add(new Quiz(1, "What does WORA stand for in Java?", 
                           "Write Once, Run Anywhere", "Write Once, Read Anywhere", 
                           "Write Only, Run Anywhere", "Write Once, Repeat Anywhere",
                           1, "multiple_choice", null, 
                           "WORA means Write Once, Run Anywhere - Java's key feature of platform independence."));

        quizzes.add(new Quiz(1, "Java code is compiled into bytecode.", 
                           "True", "False", null, null,
                           1, "true_false", null,
                           "True. Java source code is compiled into bytecode, which runs on the JVM."));

        quizzes.add(new Quiz(1, "Which company originally developed Java?", 
                           "Oracle", "Sun Microsystems", "Microsoft", "IBM",
                           2, "multiple_choice", null,
                           "Sun Microsystems originally developed Java, later acquired by Oracle."));

        quizzes.add(new Quiz(1, "Java is a platform-independent language.", 
                           "True", "False", null, null,
                           1, "true_false", null,
                           "True. Java's bytecode can run on any platform with a JVM."));

        quizzes.add(new Quiz(1, "Which of the following is NOT a feature of Java?", 
                           "Object-Oriented", "Platform Independent", "Memory Management", "Pointer Arithmetic",
                           4, "multiple_choice", null,
                           "Java does not support pointer arithmetic, which enhances security and simplicity."));
                           
        // New additional quizzes for Lesson 1
        quizzes.add(new Quiz(1, "What is the entry point of a Java application?", 
                           "start() method", "main() method", "run() method", "init() method",
                           2, "multiple_choice", null, 
                           "The main() method is the entry point for Java applications. The JVM looks for this method to begin program execution."));
        
        quizzes.add(new Quiz(1, "Complete the code: public static void main(String[] ____ ) {", 
                           "args", "parameters", "argv", "options",
                           1, "code_completion", "public static void main(String[] ____ ) {",
                           "The correct parameter name is 'args', which stands for arguments passed to the program."));
                           
        quizzes.add(new Quiz(1, "What software component executes Java bytecode?", 
                           "Java Compiler", "Java Virtual Machine (JVM)", "Java Runtime Environment (JRE)", "Java Development Kit (JDK)",
                           2, "multiple_choice", null, 
                           "The JVM (Java Virtual Machine) executes the compiled bytecode. This is what allows Java to be platform-independent."));
                           
        quizzes.add(new Quiz(1, "What is the correct file extension for Java source code files?", 
                           ".class", ".java", ".jar", ".jvm",
                           2, "multiple_choice", null, 
                           "Java source code files use the .java extension. After compilation, they become .class files containing bytecode."));
                           
        quizzes.add(new Quiz(1, "Java is a purely object-oriented language.", 
                           "True", "False", null, null,
                           2, "true_false", null,
                           "False. Java uses primitive data types like int and boolean that aren't objects, so it's not purely object-oriented."));

        // ===== LESSON 2 QUIZZES =====
        quizzes.add(new Quiz(2, "Which data type would you use to store a person's age?", 
                           "String", "int", "char", "boolean",
                           2, "multiple_choice", null,
                           "int is appropriate for storing whole numbers like age."));

        quizzes.add(new Quiz(2, "Complete the variable declaration: _____ isComplete = true;", 
                           "boolean", "int", "String", "char",
                           1, "code_completion", "_____ isComplete = true;",
                           "boolean is the data type for true/false values."));

        quizzes.add(new Quiz(2, "What is the size of an int in Java?", 
                           "16 bits", "32 bits", "64 bits", "8 bits",
                           2, "multiple_choice", null,
                           "An int in Java is 32 bits, ranging from -2^31 to 2^31-1."));

        quizzes.add(new Quiz(2, "Which of these is a valid variable name in Java?", 
                           "2name", "class", "_age", "public",
                           3, "multiple_choice", null,
                           "_age is valid. Variable names can start with underscore, but not numbers or reserved words."));

        quizzes.add(new Quiz(2, "String is a primitive data type in Java.", 
                           "True", "False", null, null,
                           2, "true_false", null,
                           "False. String is a reference type (object), not a primitive data type."));

        quizzes.add(new Quiz(2, "What is the default value of a boolean variable?", 
                           "true", "false", "null", "0",
                           2, "multiple_choice", null,
                           "The default value of a boolean variable is false."));
                           
        // New additional quizzes for Lesson 2
        quizzes.add(new Quiz(2, "Which data type would you use to store a decimal number with high precision?", 
                           "int", "float", "double", "long",
                           3, "multiple_choice", null,
                           "double has higher precision than float and is suitable for precise decimal calculations."));
                           
        quizzes.add(new Quiz(2, "What is the result of this code? int x = 5; double y = x;", 
                           "Compilation error", "Runtime error", "x is converted to 5.0", "x remains as 5",
                           3, "multiple_choice", null,
                           "This is an example of implicit type conversion (widening). The int value 5 is automatically converted to double 5.0."));
                           
        quizzes.add(new Quiz(2, "Complete the code to declare a constant: ____ final PI = 3.14159;", 
                           "double", "int", "var", "const",
                           1, "code_completion", "____ final PI = 3.14159;",
                           "double is needed because PI is a decimal value, and final makes it a constant."));
                           
        quizzes.add(new Quiz(2, "What happens when you try to assign a value that's too large for a byte variable?", 
                           "The value wraps around", "Compilation error", "Runtime exception", "The value is truncated",
                           2, "multiple_choice", null, 
                           "A compilation error occurs when trying to assign a value outside the range of a data type without explicit casting."));
                           
        quizzes.add(new Quiz(2, "In Java, char values are stored using Unicode encoding.", 
                           "True", "False", null, null,
                           1, "true_false", null,
                           "True. Java uses Unicode to represent characters, which allows for international character sets."));
                           
        quizzes.add(new Quiz(2, "Which statement correctly creates a long variable?", 
                           "long value = 10;", "long value = 10l;", "long value = 10L;", "Both B and C",
                           4, "multiple_choice", null, 
                           "Both 'long value = 10l;' and 'long value = 10L;' are correct. The L suffix (upper or lowercase) explicitly marks a literal as long."));

        // ===== LESSON 3 QUIZZES =====
        quizzes.add(new Quiz(3, "What is the result of 10 % 3 in Java?", 
                           "3", "1", "0", "10",
                           2, "multiple_choice", null,
                           "The modulus operator (%) returns the remainder of division: 10 ÷ 3 = 3 remainder 1."));

        quizzes.add(new Quiz(3, "The && operator performs logical AND operation.", 
                           "True", "False", null, null,
                           1, "true_false", null,
                           "True. The && operator returns true only when both conditions are true."));

        quizzes.add(new Quiz(3, "Which operator is used for string concatenation?", 
                           "*", "+", "&", ".",
                           2, "multiple_choice", null,
                           "The + operator is used for both arithmetic addition and string concatenation."));

        quizzes.add(new Quiz(3, "What does the != operator do?", 
                           "Assigns values", "Checks equality", "Checks inequality", "Adds values",
                           3, "multiple_choice", null,
                           "The != operator checks if two values are not equal."));

        quizzes.add(new Quiz(3, "Complete: int x = 5; x ___= 3; // Result: x = 15", 
                           "*", "/", "+", "-",
                           1, "code_completion", "int x = 5; x ___= 3; // Result: x = 15",
                           "*= multiplies x by 3: x = x * 3 = 5 * 3 = 15."));

        quizzes.add(new Quiz(3, "The || operator requires both conditions to be true.", 
                           "True", "False", null, null,
                           2, "true_false", null,
                           "False. The || operator (logical OR) returns true if at least one condition is true."));
                           
        // New additional quizzes for Lesson 3
        quizzes.add(new Quiz(3, "What is the output of the following code? int x = 5; System.out.println(x++ + ++x);", 
                           "11", "12", "13", "10",
                           2, "multiple_choice", null,
                           "x++ returns 5 then increments to 6. ++x increments to 7 then returns 7. So result is 5 + 7 = 12."));
                           
        quizzes.add(new Quiz(3, "Which of the following has the highest operator precedence?", 
                           "Addition (+)", "Assignment (=)", "Multiplication (*)", "Logical AND (&&)",
                           3, "multiple_choice", null, 
                           "Multiplication (*) has higher precedence than addition, logical operators, and assignment."));
                           
        quizzes.add(new Quiz(3, "What is the result of the expression 5 & 3 in Java?", 
                           "8", "2", "1", "15",
                           2, "multiple_choice", null, 
                           "5 in binary is 101, 3 in binary is 011. Bitwise AND (&) compares each bit: 101 & 011 = 001, which is 1 in decimal."));
                           
        quizzes.add(new Quiz(3, "Short-circuit evaluation means:", 
                           "Operators are evaluated from left to right", "The program ends if an operator throws an error", 
                           "The second condition isn't evaluated if the first determines the result", "Variables are automatically initialized",
                           3, "multiple_choice", null, 
                           "Short-circuit evaluation means that in logical expressions, the second condition isn't evaluated if the first condition determines the result."));
                           
        quizzes.add(new Quiz(3, "Complete: boolean result = (10 > 5) ___ (7 < 3); // result should be false", 
                           "&&", "||", "!=", "==",
                           1, "code_completion", "boolean result = (10 > 5) ___ (7 < 3); // result should be false",
                           "The && operator requires both conditions to be true. Since (10 > 5) is true but (7 < 3) is false, the result is false."));
                           
        quizzes.add(new Quiz(3, "What is the difference between = and == in Java?", 
                           "= assigns values, == compares values", "= compares values, == assigns values", 
                           "= works with numbers, == works with strings", "They are interchangeable",
                           1, "multiple_choice", null, 
                           "= is the assignment operator that assigns values to variables, while == is the equality operator that compares values."));

        // ===== LESSON 4 QUIZZES =====
        quizzes.add(new Quiz(4, "Which statement is used for multi-way branching?", 
                           "if", "switch", "for", "while",
                           2, "multiple_choice", null,
                           "The switch statement allows multi-way branching based on a variable's value."));

        quizzes.add(new Quiz(4, "What happens if you forget 'break' in a switch case?", 
                           "Compilation error", "Runtime error", "Fall-through occurs", "Nothing happens",
                           3, "multiple_choice", null,
                           "Without break, execution continues to the next case (fall-through behavior)."));

        quizzes.add(new Quiz(4, "Complete the if statement: if (score ___ 90) { grade = 'A'; }", 
                           "=", "==", ">=", "=>",
                           3, "code_completion", "if (score ___ 90) { grade = 'A'; }",
                           ">= checks if score is greater than or equal to 90."));

        quizzes.add(new Quiz(4, "The else clause is mandatory in an if statement.", 
                           "True", "False", null, null,
                           2, "true_false", null,
                           "False. The else clause is optional in if statements."));
                           
        // New additional quizzes for Lesson 4
        quizzes.add(new Quiz(4, "What is the correct syntax for the ternary operator?", 
                           "condition ? value1 : value2", "condition ? value1 | value2", "condition : value1 ? value2", "condition || value1 && value2",
                           1, "multiple_choice", null,
                           "The ternary operator has the syntax: condition ? expressionIfTrue : expressionIfFalse"));
                           
        quizzes.add(new Quiz(4, "Which data types can be used in a switch statement in Java before Java 12?", 
                           "int, double, String", "byte, short, int, char, String, enum", "All primitive types", "Any object type",
                           2, "multiple_choice", null, 
                           "Before Java 12, switch statements could only use byte, short, int, char, String and enum types."));
                           
        quizzes.add(new Quiz(4, "Complete the code: ___ (dayOfWeek == 1 || dayOfWeek == 7) {\n    System.out.println(\"Weekend\");\n} else {\n    System.out.println(\"Weekday\");\n}", 
                           "if", "switch", "while", "for",
                           1, "code_completion", "___ (dayOfWeek == 1 || dayOfWeek == 7) {\n    System.out.println(\"Weekend\");\n} else {\n    System.out.println(\"Weekday\");\n}",
                           "if is the correct keyword for conditional execution based on a boolean expression."));
                           
        quizzes.add(new Quiz(4, "What's the output of this code? int x = 10; if(x > 5) { System.out.print(\"A\"); } if(x < 15) { System.out.print(\"B\"); } else { System.out.print(\"C\"); }", 
                           "A", "B", "AB", "C",
                           3, "multiple_choice", null, 
                           "Both conditions (x > 5) and (x < 15) are true for x = 10, so both A and B are printed."));
                           
        quizzes.add(new Quiz(4, "In a switch statement, the default case is mandatory.", 
                           "True", "False", null, null,
                           2, "true_false", null,
                           "False. The default case is optional in switch statements. It's executed when no other case matches."));
                           
        quizzes.add(new Quiz(4, "What is a nested if statement?", 
                           "An if statement inside another if statement", "Multiple if-else statements in sequence", 
                           "An if statement with multiple conditions", "An if statement that uses logical operators",
                           1, "multiple_choice", null, 
                           "A nested if statement is an if statement that is placed inside another if or else block."));

        quizzes.add(new Quiz(4, "Which data types can be used in switch statements?", 
                           "int, char, String", "double, float", "boolean", "All primitive types",
                           1, "multiple_choice", null,
                           "Switch supports int, char, String, and enum types, but not floating-point or boolean."));

        // ===== LESSON 5 QUIZZES =====
        quizzes.add(new Quiz(5, "Which loop executes at least once?", 
                           "for loop", "while loop", "do-while loop", "enhanced for loop",
                           3, "multiple_choice", null,
                           "The do-while loop executes the body at least once before checking the condition."));

        quizzes.add(new Quiz(5, "What does the 'break' statement do in a loop?", 
                           "Skips current iteration", "Exits the loop", "Restarts the loop", "Does nothing",
                           2, "multiple_choice", null,
                           "The break statement terminates the loop and transfers control to the next statement."));

        quizzes.add(new Quiz(5, "Complete the for loop: for (int i = 0; i < 10; ____) { }", 
                           "i++", "i--", "++i", "Both i++ and ++i",
                           4, "code_completion", "for (int i = 0; i < 10; ____) { }",
                           "Both i++ and ++i increment i, though i++ is more commonly used in for loops."));

        quizzes.add(new Quiz(5, "The enhanced for loop can modify array elements.", 
                           "True", "False", null, null,
                           2, "true_false", null,
                           "False. Enhanced for loops provide read-only access to collection elements."));

        quizzes.add(new Quiz(5, "What does 'continue' do in a loop?", 
                           "Exits the loop", "Skips to next iteration", "Restarts from beginning", "Pauses execution",
                           2, "multiple_choice", null,
                           "The continue statement skips the current iteration and proceeds to the next iteration of the loop."));
                           
        // New additional quizzes for Lesson 5
        quizzes.add(new Quiz(5, "What is the output of this code? int i = 0; while(i < 5) { i++; if(i == 3) continue; System.out.print(i); }", 
                           "12345", "1245", "12345 (with a space after 2)", "01234",
                           2, "multiple_choice", null,
                           "The loop prints i after incrementing it, but skips printing when i equals 3 due to the continue statement."));
                           
        quizzes.add(new Quiz(5, "Which of the following is NOT a valid loop in Java?", 
                           "for loop", "while loop", "do-while loop", "repeat-until loop",
                           4, "multiple_choice", null, 
                           "Java does not have a built-in repeat-until loop. It has for, while, do-while, and enhanced for loops."));
                           
        quizzes.add(new Quiz(5, "Complete the code: int sum = 0; ____ (int i = 1; i <= 10; i++) { sum += i; }", 
                           "for", "while", "do", "loop",
                           1, "code_completion", "int sum = 0; ____ (int i = 1; i <= 10; i++) { sum += i; }",
                           "for is the correct keyword to start a standard for loop with initialization, condition, and increment."));
                           
        quizzes.add(new Quiz(5, "What would be the output of this nested loop? for(int i=1; i<=3; i++) { for(int j=1; j<=2; j++) { System.out.print(i*j + \" \"); } }", 
                           "1 2 2 4 3 6", "1 2 3 4 5 6", "1 1 2 2 3 3", "1 2 3 4 6 9",
                           1, "multiple_choice", null, 
                           "The outer loop runs 3 times and the inner loop runs 2 times for each outer loop. i*j values: 1*1=1, 1*2=2, 2*1=2, 2*2=4, 3*1=3, 3*2=6"));
                           
        quizzes.add(new Quiz(5, "In a for-each loop, you can directly modify the collection being iterated.", 
                           "True", "False", null, null,
                           2, "true_false", null,
                           "False. Modifying the collection (adding/removing elements) while iterating with a for-each loop will throw ConcurrentModificationException."));
                           
        quizzes.add(new Quiz(5, "Which loop is most appropriate when you need to iterate through all elements of an array?", 
                           "while loop", "do-while loop", "for loop", "enhanced for loop (for-each)",
                           4, "multiple_choice", null, 
                           "The enhanced for loop (for-each) is designed specifically for iterating through collections and arrays when you need to process each element."));

        // ===== LESSON 6 QUIZZES =====
        quizzes.add(new Quiz(6, "How do you declare an array of 10 integers?", 
                           "int array = new int[10];", "int[] array = new int[10];", "int array[10];", "array int[10];",
                           2, "multiple_choice", null,
                           "int[] array = new int[10]; is the correct syntax for declaring an integer array."));

        quizzes.add(new Quiz(6, "Array indices in Java start from 0.", 
                           "True", "False", null, null,
                           1, "true_false", null,
                           "True. Java arrays are zero-indexed, meaning the first element is at index 0."));

        quizzes.add(new Quiz(6, "Which method returns the length of a string?", 
                           "size()", "length()", "count()", "len()",
                           2, "multiple_choice", null,
                           "The length() method returns the number of characters in a string."));

        quizzes.add(new Quiz(6, "What does String.charAt(2) return for \"Hello\"?", 
                           "'H'", "'e'", "'l'", "'o'",
                           3, "multiple_choice", null,
                           "charAt(2) returns the character at index 2, which is 'l' in \"Hello\"."));

        quizzes.add(new Quiz(6, "Complete: String text = \"Java\"; text.___() returns \"JAVA\"", 
                           "toUpperCase", "toUpper", "upperCase", "capitalize",
                           1, "code_completion", "String text = \"Java\"; text.___() returns \"JAVA\"",
                           "toUpperCase() converts all characters in the string to uppercase."));

        quizzes.add(new Quiz(6, "Arrays in Java can change size after creation.", 
                           "True", "False", null, null,
                           2, "true_false", null,
                           "False. Arrays have fixed size once created. Use collections for dynamic sizing."));

        // ===== LESSON 7 QUIZZES =====
        quizzes.add(new Quiz(7, "Which access modifier makes a method accessible from anywhere?", 
                           "private", "protected", "public", "default",
                           3, "multiple_choice", null,
                           "Public methods can be accessed from any class in any package, providing the widest accessibility."));

        quizzes.add(new Quiz(7, "What is the correct way to call a static method?", 
                           "object.methodName()", "this.methodName()", "ClassName.methodName()", "super.methodName()",
                           3, "multiple_choice", null,
                           "Static methods belong to the class itself, not to instances, so they should be called using the class name."));

        quizzes.add(new Quiz(7, "Complete the method signature: public _____ double calculateArea(double radius) { return Math.PI * radius * radius; }", 
                           "static", "void", "return", "final",
                           1, "code_completion", "public _____ double calculateArea(double radius) { return Math.PI * radius * radius; }",
                           "The static keyword makes this a class method that can be called without instantiating an object."));

        quizzes.add(new Quiz(7, "What happens when a void method contains a return statement with no value?", 
                           "Compilation error", "Runtime exception", "The method exits immediately", "The method returns null",
                           3, "multiple_choice", null,
                           "A void method can contain a 'return;' statement without a value, which causes the method to exit immediately."));

        quizzes.add(new Quiz(7, "When passing a primitive data type to a method, what gets passed?", 
                           "A reference to the variable", "A copy of the variable's value", "The memory address", "The variable itself",
                           2, "multiple_choice", null,
                           "Java uses pass-by-value for primitives, meaning a copy of the value is passed, and modifications inside the method don't affect the original."));

        quizzes.add(new Quiz(7, "Method overloading can be achieved by changing only the return type.", 
                           "True", "False", null, null,
                           2, "true_false", null,
                           "False. Method overloading requires different parameter lists (different number, type, or order). Changing only the return type is not sufficient."));

        quizzes.add(new Quiz(7, "What is the purpose of the 'this' keyword in a method?", 
                           "To call another method", "To refer to the current object", "To call a parent class method", "To create a new instance",
                           2, "multiple_choice", null,
                           "The 'this' keyword refers to the current object instance, allowing access to instance variables and methods."));

        quizzes.add(new Quiz(7, "Complete: int result = Math.___(5, 8); // Returns the larger number", 
                           "max", "larger", "greater", "highest",
                           1, "code_completion", "int result = Math.___(5, 8); // Returns the larger number",
                           "Math.max() is a built-in static method that returns the larger of two numbers."));

        quizzes.add(new Quiz(7, "Which statement about instance methods is true?", 
                           "They can access static variables", "They can be called without creating an object", 
                           "They are declared with the static keyword", "They cannot access instance variables",
                           1, "multiple_choice", null,
                           "Instance methods can access both static and instance variables, but require an object instance to be called."));

        quizzes.add(new Quiz(7, "Local variables defined in a method are accessible from outside the method.", 
                           "True", "False", null, null,
                           2, "true_false", null,
                           "False. Local variables have method scope, meaning they are only accessible within the method where they are defined."));

        quizzes.add(new Quiz(7, "What happens when an object is passed to a method and modified inside the method?", 
                           "The original object is not affected", "The original object is affected by the modifications", 
                           "A compilation error occurs", "A new object is created",
                           2, "multiple_choice", null,
                           "When objects are passed to methods, the reference is passed by value. Changes to the object's state affect the original object."));

        quizzes.add(new Quiz(7, "Which type of method can directly access instance variables without using an object reference?", 
                           "Static methods", "Public methods", "Private methods", "Instance methods",
                           4, "multiple_choice", null,
                           "Instance methods operate in the context of an object instance and can directly access its instance variables."));

        // ===== LESSON 8 QUIZZES =====
        quizzes.add(new Quiz(8, "Which of the following is NOT one of the four pillars of OOP?", 
                           "Encapsulation", "Inheritance", "Compilation", "Polymorphism",
                           3, "multiple_choice", null,
                           "The four pillars of OOP are Encapsulation, Inheritance, Polymorphism, and Abstraction. Compilation is not one of them."));

        quizzes.add(new Quiz(8, "What is the relationship between a class and an object?", 
                           "A class is an instance of an object", "An object is an instance of a class", 
                           "They are different terms for the same concept", "Objects contain classes",
                           2, "multiple_choice", null,
                           "An object is an instance of a class. The class is the blueprint or template, while objects are specific instances created from that template."));

        quizzes.add(new Quiz(8, "Which statement about constructors is true?", 
                           "Constructors must have the same name as the class", "Constructors have return types", 
                           "Every class must have at least two constructors", "Constructors are called when objects are destroyed",
                           1, "multiple_choice", null,
                           "Constructors must have exactly the same name as the class and do not specify a return type, not even void."));

        quizzes.add(new Quiz(8, "What is the purpose of the 'this' keyword in a constructor?", 
                           "To call a method in the same class", "To call another constructor in the same class", 
                           "To refer to the current object instance", "Both B and C are correct",
                           4, "multiple_choice", null,
                           "'this' refers to the current object instance, and 'this()' calls another constructor in the same class (constructor chaining)."));

        quizzes.add(new Quiz(8, "Complete: class Student { private int id; public void _____(int id) { this.id = id; } }", 
                           "setId", "getId", "updateId", "changeId",
                           1, "code_completion", "class Student { private int id; public void _____(int id) { this.id = id; } }",
                           "setId follows the Java bean naming convention for setter methods that modify private fields."));

        quizzes.add(new Quiz(8, "Encapsulation can be implemented by:", 
                           "Making fields public and methods private", "Using only static methods", 
                           "Making fields private and providing getter/setter methods", "Avoiding the use of constructors",
                           3, "multiple_choice", null,
                           "Encapsulation is typically implemented by making fields private and providing controlled access through public getter and setter methods."));

        quizzes.add(new Quiz(8, "A static initializer block in a class runs:", 
                           "Every time an object is created", "Only once when the class is first loaded", 
                           "When a specific method is called", "When the program ends",
                           2, "multiple_choice", null,
                           "A static initializer block (static {}) runs only once when the class is first loaded by the JVM, not each time an object is created."));

        quizzes.add(new Quiz(8, "What is the difference between instance variables and class (static) variables?", 
                           "Instance variables belong to objects, static variables belong to the class", 
                           "Instance variables are always public, static variables are always private", 
                           "Static variables can only be numbers, instance variables can be any type", 
                           "There is no difference",
                           1, "multiple_choice", null,
                           "Instance variables belong to each object instance with separate copies, while static variables belong to the class itself with only one copy shared by all instances."));

        quizzes.add(new Quiz(8, "In Java, can a class have multiple constructors?", 
                           "True", "False", null, null,
                           1, "true_false", null,
                           "True. A class can have multiple constructors with different parameter lists, known as constructor overloading."));

        quizzes.add(new Quiz(8, "Complete: BankAccount account = ___ BankAccount(\"John\", 1000.0);", 
                           "new", "create", "instance", "init",
                           1, "code_completion", "BankAccount account = ___ BankAccount(\"John\", 1000.0);",
                           "The 'new' keyword is used to create new object instances in Java."));

        quizzes.add(new Quiz(8, "What happens if a class doesn't explicitly define any constructor?", 
                           "Compilation error", "The class cannot be instantiated", 
                           "Java provides a default no-argument constructor", "Objects are created without initialization",
                           3, "multiple_choice", null,
                           "If no constructor is defined, Java automatically provides a default no-argument constructor that initializes fields to their default values."));

        quizzes.add(new Quiz(8, "Instance initializer blocks run:", 
                           "Before the constructor", "After the constructor", 
                           "Only when explicitly called", "Only for static variables",
                           1, "multiple_choice", null,
                           "Instance initializer blocks (enclosed in {}) run before the constructor code, each time an object is created."));
                           
        quizzes.add(new Quiz(8, "Which of these is NOT a valid access modifier in Java?", 
                           "public", "private", "protected", "friend",
                           4, "multiple_choice", null,
                           "friend is not a Java access modifier. Valid Java access modifiers are public, private, protected, and default (no modifier)."));

        // ===== LESSON 9 QUIZZES =====
        quizzes.add(new Quiz(9, "Which keyword is used for class inheritance in Java?", 
                           "inherit", "extends", "implements", "super",
                           2, "multiple_choice", null,
                           "The 'extends' keyword is used to establish inheritance between classes, where the subclass extends the superclass."));

        quizzes.add(new Quiz(9, "What is the primary purpose of the 'super' keyword in inheritance?", 
                           "To create a new parent object", "To access parent class members and constructor", 
                           "To prevent method overriding", "To implement multiple inheritance",
                           2, "multiple_choice", null,
                           "The 'super' keyword is used to call the parent class constructor, methods, or access its variables."));

        quizzes.add(new Quiz(9, "Which of the following is an example of runtime polymorphism in Java?", 
                           "Method overloading", "Method overriding", "Constructor overloading", "Method hiding",
                           2, "multiple_choice", null,
                           "Method overriding is an example of runtime (dynamic) polymorphism, where the method to be executed is determined at runtime based on the object's type."));

        quizzes.add(new Quiz(9, "Complete the code: class Dog _____ Animal { }", 
                           "implements", "inherits", "extends", "import",
                           3, "code_completion", "class Dog _____ Animal { }",
                           "The correct keyword is 'extends'. A class extends another class to inherit its properties and behaviors."));

        quizzes.add(new Quiz(9, "What happens if a subclass constructor doesn't explicitly call the superclass constructor?", 
                           "Compilation error", "Runtime error", "Java calls the default superclass constructor automatically", 
                           "The superclass constructor is never executed",
                           3, "multiple_choice", null,
                           "If not explicitly called, Java automatically inserts a call to the superclass's no-argument constructor (super()). This will cause a compilation error if no such constructor exists."));

        quizzes.add(new Quiz(9, "Which statement about method overriding is false?", 
                           "Overridden methods must have the same name and parameters", 
                           "Return type must be the same or a subtype of the parent's return type", 
                           "Access modifier can be more restrictive than the parent method", 
                           "Static methods cannot be overridden, only hidden",
                           3, "multiple_choice", null,
                           "Access modifier in the overriding method cannot be more restrictive than the overridden method. It can be the same or less restrictive."));

        quizzes.add(new Quiz(9, "Which is the correct position to place a super() constructor call?", 
                           "Anywhere in the constructor", "As the first statement of constructor", 
                           "As the last statement of constructor", "Outside the constructor",
                           2, "multiple_choice", null,
                           "The super() call must be the first statement in a constructor. This ensures the parent is initialized before the child."));

        quizzes.add(new Quiz(9, "A final class can be extended by another class.", 
                           "True", "False", null, null,
                           2, "true_false", null,
                           "False. The 'final' modifier on a class prevents it from being subclassed/extended."));

        quizzes.add(new Quiz(9, "Which statement describes the 'IS-A' relationship?", 
                           "It represents inheritance", "It represents composition", 
                           "It represents method overloading", "It represents interfaces only",
                           1, "multiple_choice", null,
                           "The 'IS-A' relationship represents inheritance. For example, 'Dog IS-A Animal' means Dog inherits from Animal."));

        quizzes.add(new Quiz(9, "What is the output of this code? Animal a = new Dog(); a.makeSound();", 
                           "Calls Animal's makeSound method", "Calls Dog's makeSound method", 
                           "Compilation error", "Runtime error",
                           2, "multiple_choice", null,
                           "This demonstrates runtime polymorphism. Although the reference is of type Animal, the actual object is Dog, so Dog's makeSound() is called."));

        quizzes.add(new Quiz(9, "Which class is implicitly extended by all Java classes?", 
                           "Main", "System", "Object", "String",
                           3, "multiple_choice", null,
                           "java.lang.Object is the root class of Java's class hierarchy. All classes implicitly extend Object if they don't explicitly extend another class."));

        quizzes.add(new Quiz(9, "How can you prevent a method from being overridden?", 
                           "Mark it as private", "Mark it as static", "Mark it as final", "All of the above",
                           3, "multiple_choice", null,
                           "A 'final' method cannot be overridden in subclasses. Private methods are not inherited, and static methods are hidden, not overridden.")); 

        quizzes.add(new Quiz(9, "Polymorphism allows objects of different types to respond to the same:", 
                           "Variable", "Interface", "Constructor", "Package",
                           2, "multiple_choice", null,
                           "Polymorphism allows different objects to respond to the same interface or method call."));

        // ===== LESSON 10 QUIZZES =====
        quizzes.add(new Quiz(10, "What is the difference between 'throw' and 'throws' keywords?", 
                           "'throw' creates an exception, 'throws' declares it", 
                           "'throws' creates an exception, 'throw' declares it", 
                           "They are interchangeable", 
                           "'throw' is for checked exceptions, 'throws' is for unchecked",
                           1, "multiple_choice", null,
                           "'throw' is used to explicitly create and throw an exception object, while 'throws' is used in method signatures to declare that a method might throw certain exceptions."));

        quizzes.add(new Quiz(10, "When is the finally block NOT executed?", 
                           "When an exception occurs in the try block", 
                           "When System.exit() is called", 
                           "When the catch block handles the exception", 
                           "When no exceptions are thrown",
                           2, "multiple_choice", null,
                           "The finally block always executes regardless of whether an exception occurs or is caught, except when the JVM exits (e.g., System.exit() is called) or a fatal error occurs."));

        quizzes.add(new Quiz(10, "Which of the following is the correct order for catch blocks when handling multiple exceptions?", 
                           "Most specific to most general", 
                           "Most general to most specific", 
                           "Order doesn't matter", 
                           "Alphabetical order",
                           1, "multiple_choice", null,
                           "Catch blocks must be ordered from most specific to most general exception types. Placing a more general exception before a specific one will result in a compilation error as the specific catch becomes unreachable."));

        quizzes.add(new Quiz(10, "Complete: try (FileReader fr = new FileReader(\"file.txt\")) { // read file } // Java 7+ feature", 
                           "with resources", "finally", "auto-close", "catch-free",
                           1, "code_completion", "try (FileReader fr = new FileReader(\"file.txt\")) { // read file } // Java 7+ feature",
                           "This is the try-with-resources statement introduced in Java 7, which automatically closes resources that implement AutoCloseable, eliminating the need for explicit finally blocks for resource cleanup."));

        quizzes.add(new Quiz(10, "What happens if a checked exception is not caught or declared in a method?", 
                           "Runtime error", 
                           "The exception is ignored", 
                           "Compilation error", 
                           "The exception becomes unchecked",
                           3, "multiple_choice", null,
                           "Checked exceptions must either be caught in a try-catch block or declared in the method's throws clause. Not doing either results in a compilation error."));

        quizzes.add(new Quiz(10, "Which is the parent class of all exceptions in Java?", 
                           "Error", "Exception", "RuntimeException", "Throwable",
                           4, "multiple_choice", null,
                           "Throwable is the root class of the exception hierarchy in Java. Both Error and Exception are direct subclasses of Throwable."));

        quizzes.add(new Quiz(10, "What is the correct way to create a custom exception?", 
                           "Create a class and throw it", 
                           "Extend the Exception or RuntimeException class", 
                           "Implement the Throwable interface", 
                           "Override the throw method",
                           2, "multiple_choice", null,
                           "To create a custom exception, you should extend the Exception class (for checked exceptions) or RuntimeException (for unchecked exceptions)."));

        quizzes.add(new Quiz(10, "In a try-catch-finally structure, if both the catch and finally blocks have return statements, which one takes precedence?", 
                           "The catch block return", 
                           "The finally block return", 
                           "Both returns are combined", 
                           "Compilation error occurs",
                           2, "multiple_choice", null,
                           "If both catch and finally blocks have return statements, the finally block's return value will override the catch block's return value, as the finally block always executes last."));

        quizzes.add(new Quiz(10, "Which statement about unchecked exceptions is true?", 
                           "They must be declared in method signatures", 
                           "They are subclasses of Error", 
                           "They are subclasses of RuntimeException", 
                           "They can only occur during compilation",
                           3, "multiple_choice", null,
                           "Unchecked exceptions are subclasses of RuntimeException. They don't need to be declared or explicitly caught, though they can be."));

        quizzes.add(new Quiz(10, "A method can declare multiple exception types in its throws clause.", 
                           "True", "False", null, null,
                           1, "true_false", null,
                           "True. A method can declare multiple exception types in its throws clause, separated by commas."));

        quizzes.add(new Quiz(10, "Complete: try { riskyOperation(); } catch (Exception e) { System.out.println(e._____); }", 
                           "getMessage", "printStackTrace", "getErrorCode", "toString",
                           1, "code_completion", "try { riskyOperation(); } catch (Exception e) { System.out.println(e._____); }",
                           "getMessage() returns the detail message (string) of the exception, which is often helpful for debugging and user feedback."));

        quizzes.add(new Quiz(10, "Which of these is a good practice for exception handling?", 
                           "Catch and ignore all exceptions", 
                           "Create only checked exceptions", 
                           "Handle only RuntimeExceptions", 
                           "Catch specific exceptions before general ones",
                           4, "multiple_choice", null,
                           "It's a best practice to catch specific exceptions before more general ones. This allows for more precise error handling and prevents catching exceptions you don't know how to handle properly."));

        // ===== LESSON 11 QUIZZES =====
        quizzes.add(new Quiz(11, "Which collection maintains insertion order?", 
                           "HashSet", "ArrayList", "TreeSet", "HashMap",
                           2, "multiple_choice", null,
                           "ArrayList maintains the order of elements as they were inserted."));

        quizzes.add(new Quiz(11, "Set collections allow duplicate elements.", 
                           "True", "False", null, null,
                           2, "true_false", null,
                           "False. Set collections do not allow duplicate elements. This is a fundamental characteristic of all Set implementations (HashSet, TreeSet, LinkedHashSet)."));

        quizzes.add(new Quiz(11, "Which interface is implemented by HashMap?", 
                           "List", "Set", "Map", "Queue",
                           3, "multiple_choice", null,
                           "HashMap implements the Map interface for key-value pair storage. Note that Map is not a subinterface of Collection."));

        quizzes.add(new Quiz(11, "Which collection implementation would be most efficient for frequent insertions and deletions in the middle of the collection?", 
                           "ArrayList", "LinkedList", "HashSet", "TreeMap",
                           2, "multiple_choice", null,
                           "LinkedList provides O(1) insertions and deletions when you have a position (like an iterator), making it ideal for frequent modifications in the middle of the collection."));

        quizzes.add(new Quiz(11, "Complete: List<String> list = new ____();", 
                           "ArrayList<String>", "LinkedList<String>", "Vector<String>", "All of the above",
                           4, "code_completion", "List<String> list = new ____();",
                           "All these classes implement the List interface and can be used. This demonstrates polymorphism and the benefit of programming to interfaces."));

        quizzes.add(new Quiz(11, "Which method checks if a collection is empty?", 
                           "empty()", "isEmpty()", "size() == 0", "Both isEmpty() and size() == 0",
                           4, "multiple_choice", null,
                           "Both isEmpty() method and size() == 0 check can determine if a collection is empty. However, isEmpty() is often preferred as it can be more efficient for some implementations."));

        quizzes.add(new Quiz(11, "TreeSet automatically sorts its elements.", 
                           "True", "False", null, null,
                           1, "true_false", null,
                           "True. TreeSet maintains elements in sorted order automatically using a Red-Black tree implementation."));
                           
        quizzes.add(new Quiz(11, "Which collection implementation offers O(1) time complexity for add, remove, and contains operations?", 
                           "ArrayList", "LinkedList", "HashSet", "TreeSet",
                           3, "multiple_choice", null,
                           "HashSet offers O(1) constant time complexity for basic operations like add, remove, and contains, making it very efficient for membership testing."));
                           
        quizzes.add(new Quiz(11, "Complete the code to iterate through a Map: for (___<String, Integer> entry : map.entrySet()) { }", 
                           "Pair", "KeyValue", "Map.Entry", "MapEntry",
                           3, "code_completion", "for (___<String, Integer> entry : map.entrySet()) { }",
                           "Map.Entry is the correct interface type for entries in a Map, accessed through the entrySet() method."));
                           
        quizzes.add(new Quiz(11, "Which collection would you use to implement a FIFO (First-In-First-Out) data structure?", 
                           "Stack", "TreeSet", "HashMap", "Queue",
                           4, "multiple_choice", null,
                           "Queue interface and its implementations (like LinkedList or ArrayDeque) are designed for FIFO operations."));
                           
        quizzes.add(new Quiz(11, "The Collections class provides static utility methods for collection operations.", 
                           "True", "False", null, null,
                           1, "true_false", null,
                           "True. The Collections class provides static utility methods like sort(), binarySearch(), shuffle(), and min/max operations for collections."));

        // ===== LESSON 12 QUIZZES =====
        quizzes.add(new Quiz(12, "Which class hierarchy is used for reading character data from files?", 
                           "InputStream/FileInputStream", "Reader/FileReader", "OutputStream/FileOutputStream", "Writer/FileWriter",
                           2, "multiple_choice", null,
                           "The Reader hierarchy, specifically FileReader, is designed for reading character-based (text) data, while InputStream is for byte-based data."));

        quizzes.add(new Quiz(12, "Try-with-resources automatically closes resources, even if an exception occurs.", 
                           "True", "False", null, null,
                           1, "true_false", null,
                           "True. Try-with-resources automatically closes resources that implement AutoCloseable interface, ensuring proper cleanup even when exceptions are thrown."));

        quizzes.add(new Quiz(12, "Which of the following is NOT a feature of Java NIO.2 (introduced in Java 7)?", 
                           "Path interface", "Files utility class", "WatchService API", "FileInputStream class",
                           4, "multiple_choice", null,
                           "FileInputStream is part of the traditional java.io package, not the modern NIO.2 API. The other options (Path, Files, and WatchService) are all key features introduced in Java 7's NIO.2."));

        quizzes.add(new Quiz(12, "Which method reads a line from console input using Scanner?", 
                           "nextLine()", "readLine()", "getLine()", "inputLine()",
                           1, "multiple_choice", null,
                           "The nextLine() method reads an entire line including spaces. BufferedReader uses readLine(), but Scanner uses nextLine()."));

        quizzes.add(new Quiz(12, "Complete: try (BufferedReader reader = new BufferedReader(new ___(new File(\"data.txt\")))) { ... }", 
                           "FileReader", "FileInputStream", "InputStreamReader", "Scanner",
                           1, "code_completion", "try (BufferedReader reader = new BufferedReader(new ___(new File(\"data.txt\")))) { ... }",
                           "FileReader is the appropriate character stream class to wrap with a BufferedReader for efficient reading of text files."));

        quizzes.add(new Quiz(12, "BufferedReader is faster than FileReader for large files.", 
                           "True", "False", null, null,
                           1, "true_false", null,
                           "True. BufferedReader reduces system calls by reading chunks of data into a memory buffer, making it much more efficient for reading large files than using FileReader directly."));

        quizzes.add(new Quiz(12, "Which method can be used to read an entire file as a String in one operation using the modern Files class?", 
                           "Files.readFile()", "Files.readAllText()", "Files.readString()", "Files.getString()",
                           3, "multiple_choice", null,
                           "Files.readString() (introduced in Java 11) reads all content from a file as a String in one operation. Files.readAllLines() is similar but returns a List<String>."));
                           
        quizzes.add(new Quiz(12, "Which of the following represents the correct way to specify character encoding when reading a text file?", 
                           "new FileReader(file, \"UTF-8\")", "new FileReader(file, StandardCharsets.UTF_8)", "new FileReader(file, Charset.forName(\"UTF-8\"))", "Both B and C",
                           4, "multiple_choice", null,
                           "Both StandardCharsets.UTF_8 (preferred) and Charset.forName(\"UTF-8\") are valid ways to specify the UTF-8 encoding when reading text files."));
                           
        quizzes.add(new Quiz(12, "Complete: DirectoryStream<Path> stream = Files.___(Paths.get(\"mydir\"));", 
                           "list", "newDirectoryStream", "listFiles", "getFiles",
                           2, "code_completion", "DirectoryStream<Path> stream = Files.___(Paths.get(\"mydir\"));",
                           "Files.newDirectoryStream() is the correct method for creating a DirectoryStream to efficiently iterate over directory entries."));
                           
        quizzes.add(new Quiz(12, "DataInputStream and DataOutputStream allow you to read and write Java primitive data types directly.", 
                           "True", "False", null, null,
                           1, "true_false", null,
                           "True. These classes provide methods like readInt()/writeInt(), readDouble()/writeDouble() that allow reading and writing primitive data types in a machine-independent way."));

        // ===== LESSON 13 QUIZZES =====
        quizzes.add(new Quiz(13, "Which keyword is used to implement an interface?", 
                           "extends", "implements", "inherit", "interface",
                           2, "multiple_choice", null,
                           "The 'implements' keyword is used to implement interfaces in Java, while 'extends' is used for class inheritance."));

        quizzes.add(new Quiz(13, "Abstract classes can be instantiated directly.", 
                           "True", "False", null, null,
                           2, "true_false", null,
                           "False. Abstract classes cannot be instantiated directly. They are meant to be extended by concrete subclasses."));

        quizzes.add(new Quiz(13, "Which of the following is a valid feature of interfaces in Java?", 
                           "Can contain private instance variables", "Can have constructors", "Can be implemented by multiple classes", "Can extend multiple interfaces",
                           3, "multiple_choice", null,
                           "Interfaces can be implemented by any number of classes, enabling a form of multiple inheritance. Interfaces cannot contain private instance variables or constructors, though interfaces can extend multiple interfaces."));

        quizzes.add(new Quiz(13, "Complete: public class Rectangle extends Shape implements ___ { ... }", 
                           "Object", "Drawable, Printable", "Comparable", "Nothing (remove this part)",
                           2, "code_completion", "public class Rectangle extends Shape implements ___ { ... }",
                           "A class can implement multiple interfaces using a comma-separated list. This demonstrates how Java enables multiple inheritance of behavior through interfaces."));

        quizzes.add(new Quiz(13, "Java 8 introduced default methods in interfaces.", 
                           "True", "False", null, null,
                           1, "true_false", null,
                           "True. Java 8 added default methods to interfaces, allowing interface methods to have implementations while maintaining backward compatibility."));

        quizzes.add(new Quiz(13, "Which statement about abstract classes and interfaces is FALSE?", 
                           "Abstract classes can have constructors", "Interfaces can contain static methods", "Abstract classes support multiple inheritance", "Interfaces can have default methods",
                           3, "multiple_choice", null,
                           "Abstract classes do NOT support multiple inheritance; a class can extend only one abstract class. The other statements are true: abstract classes can have constructors, interfaces can have static methods (Java 8+), and interfaces can have default methods (Java 8+)."));
                           
        quizzes.add(new Quiz(13, "Which access modifier can be used for methods in an interface prior to Java 9?", 
                           "private", "protected", "package-private", "All methods are implicitly public",
                           4, "multiple_choice", null,
                           "Prior to Java 9, all methods in an interface were implicitly public. Java 9 introduced private methods in interfaces."));
                           
        quizzes.add(new Quiz(13, "Complete the declaration: @___ interface Runnable { void run(); }", 
                           "Abstract", "Interface", "FunctionalInterface", "DefaultMethod",
                           3, "code_completion", "@___ interface Runnable { void run(); }",
                           "@FunctionalInterface is used to indicate that an interface has exactly one abstract method, making it eligible for lambda expressions."));
                           
        quizzes.add(new Quiz(13, "When should you use an abstract class instead of an interface?", 
                           "When you need to define a contract for unrelated classes", "When you want multiple classes to implement the same behavior", "When you need to share code and state among related classes", "When you need to support multiple inheritance of behavior",
                           3, "multiple_choice", null,
                           "Abstract classes are ideal when you need to share code and state (fields) among related classes. Interfaces are better for defining contracts or enabling multiple inheritance of behavior."));
                           
        quizzes.add(new Quiz(13, "An interface can contain instance fields that are not constants.", 
                           "True", "False", null, null,
                           2, "true_false", null,
                           "False. Interface fields are implicitly public, static, and final (constants). Interfaces cannot contain instance fields or maintain state."));

        // ===== LESSON 14 QUIZZES =====
        quizzes.add(new Quiz(14, "Lambda expressions were introduced in which Java version?", 
                           "Java 7", "Java 8", "Java 9", "Java 10",
                           2, "multiple_choice", null,
                           "Lambda expressions were introduced in Java 8 along with the Stream API and default methods in interfaces, marking a significant shift toward functional programming in Java."));

        quizzes.add(new Quiz(14, "Which statement about functional interfaces is TRUE?", 
                           "They can have multiple abstract methods", "They cannot have default methods", "They must have exactly one abstract method", "They must use the @Lambda annotation",
                           3, "multiple_choice", null,
                           "Functional interfaces must have exactly one abstract method (SAM - Single Abstract Method), though they can also contain default and static methods. The @FunctionalInterface annotation is optional but recommended."));

        quizzes.add(new Quiz(14, "Which of the following is NOT a built-in functional interface in the java.util.function package?", 
                           "Predicate<T>", "Function<T,R>", "Converter<T,R>", "Consumer<T>",
                           3, "multiple_choice", null,
                           "Converter<T,R> is not a standard functional interface in java.util.function. The standard interfaces include Predicate, Function, Consumer, Supplier, and others like BiFunction and UnaryOperator."));

        quizzes.add(new Quiz(14, "Complete the lambda expression for a Comparator that compares strings by length: Comparator<String> byLength = (s1, s2) -> ____;", 
                           "s1.length().compareTo(s2.length())", "s1.length() - s2.length()", "Integer.compare(s1.length(), s2.length())", "Any of these would work",
                           4, "code_completion", "Comparator<String> byLength = (s1, s2) -> ____;",
                           "All of these implementations would correctly compare strings by length. The third option (Integer.compare) is often preferred as it avoids potential integer overflow issues."));

        quizzes.add(new Quiz(14, "Stream operations like filter() and map() are known as _____ operations because they return another stream.", 
                           "terminal", "intermediate", "transformative", "processing",
                           2, "multiple_choice", null,
                           "filter(), map(), sorted(), etc. are intermediate operations that return another stream for further processing. They are executed only when a terminal operation is encountered."));

        quizzes.add(new Quiz(14, "Which of the following is a terminal operation in the Stream API?", 
                           "filter()", "map()", "flatMap()", "collect()",
                           4, "multiple_choice", null,
                           "collect() is a terminal operation that triggers stream processing and produces a result (typically a collection). Other terminal operations include forEach(), reduce(), and count()."));
                           
        quizzes.add(new Quiz(14, "What is the correct way to create a method reference to a static method?", 
                           "Class::staticMethod", "Class.staticMethod()", "staticMethod::Class", "::Class.staticMethod",
                           1, "multiple_choice", null,
                           "The correct syntax for a static method reference is ClassName::staticMethodName, such as Math::abs or Integer::parseInt."));
                           
        quizzes.add(new Quiz(14, "Complete the code to convert a List<String> to uppercase using streams: List<String> upperCaseNames = names.stream()._____(String::toUpperCase).collect(Collectors.toList());", 
                           "filter", "map", "forEach", "reduce",
                           2, "code_completion", "List<String> upperCaseNames = names.stream()._____(String::toUpperCase).collect(Collectors.toList());",
                           "The map() operation applies a function (String::toUpperCase in this case) to each element, transforming it from one type to another, or from one value to another of the same type."));
                           
        quizzes.add(new Quiz(14, "A lambda expression can access effectively final variables from its enclosing scope.", 
                           "True", "False", null, null,
                           1, "true_false", null,
                           "True. Lambda expressions can access local variables from their enclosing scope if they are effectively final (either explicitly declared as final or not modified after initialization)."));
                           
        quizzes.add(new Quiz(14, "Which collector would you use to group a list of Person objects by their age?", 
                           "Collectors.counting()", "Collectors.mapping()", "Collectors.groupingBy()", "Collectors.toMap()",
                           3, "multiple_choice", null,
                           "Collectors.groupingBy() is used to group elements by a classification function, such as groupingBy(Person::getAge) to group persons by their age into a Map<Integer, List<Person>>."));

        // ===== LESSON 15 QUIZZES =====
        quizzes.add(new Quiz(15, "Which are the two primary ways to define a thread in Java?", 
                           "Implementing Runnable and extending Thread", "Implementing Callable and extending Future", "Extending Executor and implementing Task", "Implementing ThreadWorker and extending Processor",
                           1, "multiple_choice", null,
                           "The two primary ways to create a thread in Java are by implementing the Runnable interface (preferred) or extending the Thread class. Both approaches require implementing the run() method that contains the thread's execution code."));

        quizzes.add(new Quiz(15, "What happens if you call the run() method directly instead of start() on a Thread object?", 
                           "The code executes faster", "A new thread is created", "The code executes in the current thread, not a new one", "The Thread object throws an IllegalStateException",
                           3, "multiple_choice", null,
                           "Calling run() directly executes the code in the current thread, not in a new thread. Only the start() method actually creates and starts a new thread of execution."));

        quizzes.add(new Quiz(15, "Which of the following is NOT a valid thread state in Java?", 
                           "RUNNABLE", "BLOCKED", "ACTIVE", "WAITING",
                           3, "multiple_choice", null,
                           "ACTIVE is not a valid thread state in Java. The valid states are NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, and TERMINATED as defined in the Thread.State enum."));

        quizzes.add(new Quiz(15, "Complete: ReentrantLock lock = new ReentrantLock(); lock._____(); try { // critical section } finally { lock._____(); }", 
                           "lock, unlock", "acquire, release", "take, free", "get, put",
                           1, "code_completion", "ReentrantLock lock = new ReentrantLock(); lock._____(); try { // critical section } finally { lock._____(); }",
                           "The ReentrantLock methods are lock() to acquire the lock and unlock() to release it. It's crucial to call unlock() in a finally block to ensure the lock is released even if an exception occurs."));

        quizzes.add(new Quiz(15, "What does the volatile keyword guarantee in Java?", 
                           "Thread safety for all operations", "Atomicity of compound operations", "Visibility of variable changes across threads", "Prevention of deadlocks",
                           3, "multiple_choice", null,
                           "The volatile keyword guarantees that any thread reading a volatile variable will see the most recently written value. It ensures visibility of changes across threads, but does not provide atomicity for compound operations like i++ (read-modify-write)."));

        quizzes.add(new Quiz(15, "Which of these classes from java.util.concurrent.atomic provides atomic operations on integers?", 
                           "AtomicInt", "ConcurrentInteger", "ThreadSafeInteger", "AtomicInteger",
                           4, "multiple_choice", null,
                           "AtomicInteger provides atomic operations on int values, like getAndIncrement(), without the need for explicit synchronization. Other atomic classes include AtomicBoolean, AtomicLong, and AtomicReference."));
                           
        quizzes.add(new Quiz(15, "What is a deadlock in multithreading?", 
                           "When a thread is terminated unexpectedly", "When two or more threads are blocked forever, each waiting for resources held by the other", "When a thread consumes all available CPU time", "When the thread pool has no available threads",
                           2, "multiple_choice", null,
                           "A deadlock occurs when two or more threads are blocked forever, each waiting for a lock or resource held by another thread in the deadlock. Deadlocks typically result from circular wait conditions and can freeze the application."));
                           
        quizzes.add(new Quiz(15, "Complete: ExecutorService executor = Executors._____(3); // Creates a thread pool with 3 threads", 
                           "createThreadPool", "newFixedThreadPool", "createExecutor", "newThreadPool",
                           2, "code_completion", "ExecutorService executor = Executors._____(3); // Creates a thread pool with 3 threads",
                           "Executors.newFixedThreadPool(n) creates an executor with a fixed number of threads. Other factory methods include newCachedThreadPool(), newSingleThreadExecutor(), and newScheduledThreadPool()."));
                           
        quizzes.add(new Quiz(15, "Which mechanism can be used to make one thread wait until several other threads complete their tasks?", 
                           "wait() and notify()", "Thread.sleep()", "CountDownLatch", "Semaphore",
                           3, "multiple_choice", null,
                           "CountDownLatch is designed for scenarios where one thread waits for multiple other threads to complete. The waiting thread calls await() while other threads call countDown() when they finish their tasks."));
                           
        quizzes.add(new Quiz(15, "Which of the following is a thread-safe implementation of Map in Java?", 
                           "HashMap", "TreeMap", "LinkedHashMap", "ConcurrentHashMap",
                           4, "multiple_choice", null,
                           "ConcurrentHashMap is a thread-safe implementation of Map designed for concurrent access. It offers better performance than Collections.synchronizedMap()"));

        Log.d(TAG, "Attempting to insert " + quizzes.size() + " quizzes");
        remapQuizLessonIds(database, quizzes);
        try {
            database.quizDao().insertAllQuizzes(quizzes);
            int insertedCount = database.quizDao().getQuizCount();
            Log.d(TAG, "Successfully inserted quizzes. Total count now: " + insertedCount);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting quizzes: " + e.getMessage(), e);
        }
    }

    private static void populatePracticeProblems(JavaBuddyDatabase database) {
        List<PracticeProblem> problems = new ArrayList<>();

        // ===== BEGINNER PROBLEMS =====
        problems.add(new PracticeProblem(
            "Hello World Program",
            "Beginner",
            "Write a Java program that prints 'Hello, World!' to the console.",
            "No input required",
            "Hello, World!",
            "public class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}",
            "Use System.out.println() to print text to the console.",
            "Basic I/O",
            10
        ));

        problems.add(new PracticeProblem(
            "Sum of Two Numbers",
            "Beginner",
            "Write a program that takes two integers as input and prints their sum.",
            "5 3",
            "8",
            "import java.util.Scanner;\n\npublic class Sum {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n        scanner.close();\n    }\n}",
            "Use Scanner to read input and the + operator to add numbers.",
            "Arithmetic",
            15
        ));

        problems.add(new PracticeProblem(
            "Even or Odd",
            "Beginner",
            "Write a program that checks if a given number is even or odd.",
            "7",
            "Odd",
            "import java.util.Scanner;\n\npublic class EvenOdd {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int num = scanner.nextInt();\n        if (num % 2 == 0) {\n            System.out.println(\"Even\");\n        } else {\n            System.out.println(\"Odd\");\n        }\n        scanner.close();\n    }\n}",
            "Use the modulus operator (%) to check if a number is divisible by 2.",
            "Conditional",
            20
        ));

        problems.add(new PracticeProblem(
            "Maximum of Three Numbers",
            "Beginner",
            "Write a program to find the maximum of three given numbers.",
            "15 25 10",
            "25",
            "import java.util.Scanner;\n\npublic class MaxThree {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        int c = scanner.nextInt();\n        \n        int max = a;\n        if (b > max) max = b;\n        if (c > max) max = c;\n        \n        System.out.println(max);\n        scanner.close();\n    }\n}",
            "Use if statements to compare three numbers and find the largest.",
            "Conditional",
            25
        ));

        problems.add(new PracticeProblem(
            "Multiplication Table",
            "Beginner",
            "Write a program that prints the multiplication table of a given number up to 10.",
            "3",
            "3 x 1 = 3\n3 x 2 = 6\n3 x 3 = 9\n3 x 4 = 12\n3 x 5 = 15\n3 x 6 = 18\n3 x 7 = 21\n3 x 8 = 24\n3 x 9 = 27\n3 x 10 = 30",
            "import java.util.Scanner;\n\npublic class MultiplicationTable {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int num = scanner.nextInt();\n        \n        for (int i = 1; i <= 10; i++) {\n            System.out.println(num + \" x \" + i + \" = \" + (num * i));\n        }\n        scanner.close();\n    }\n}",
            "Use a for loop to iterate from 1 to 10 and multiply by the input number.",
            "Loops",
            30
        ));

        problems.add(new PracticeProblem(
            "Factorial Calculator",
            "Beginner",
            "Write a program to calculate the factorial of a given number.",
            "5",
            "120",
            "import java.util.Scanner;\n\npublic class Factorial {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int num = scanner.nextInt();\n        \n        long factorial = 1;\n        for (int i = 1; i <= num; i++) {\n            factorial *= i;\n        }\n        \n        System.out.println(factorial);\n        scanner.close();\n    }\n}",
            "Use a loop to multiply all numbers from 1 to n.",
            "Loops",
            35
        ));

        // ===== INTERMEDIATE PROBLEMS =====
        problems.add(new PracticeProblem(
            "Palindrome Checker",
            "Intermediate",
            "Write a program that checks if a given string is a palindrome (reads the same forwards and backwards).",
            "racecar",
            "true",
            "import java.util.Scanner;\n\npublic class Palindrome {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        String str = scanner.nextLine().toLowerCase();\n        \n        int left = 0;\n        int right = str.length() - 1;\n        boolean isPalindrome = true;\n        \n        while (left < right) {\n            if (str.charAt(left) != str.charAt(right)) {\n                isPalindrome = false;\n                break;\n            }\n            left++;\n            right--;\n        }\n        \n        System.out.println(isPalindrome);\n        scanner.close();\n    }\n}",
            "Compare characters from both ends of the string moving towards the center.",
            "String Manipulation",
            40
        ));

        problems.add(new PracticeProblem(
            "Prime Number Checker",
            "Intermediate",
            "Write a program to check if a given number is prime.",
            "17",
            "true",
            "import java.util.Scanner;\n\npublic class PrimeChecker {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int num = scanner.nextInt();\n        \n        boolean isPrime = true;\n        if (num <= 1) {\n            isPrime = false;\n        } else {\n            for (int i = 2; i <= Math.sqrt(num); i++) {\n                if (num % i == 0) {\n                    isPrime = false;\n                    break;\n                }\n            }\n        }\n        \n        System.out.println(isPrime);\n        scanner.close();\n    }\n}",
            "Check divisibility from 2 to square root of the number.",
            "Mathematical",
            45
        ));

        problems.add(new PracticeProblem(
            "Array Sum Calculator",
            "Intermediate",
            "Write a program that calculates the sum of all elements in an array.",
            "5\n1 2 3 4 5",
            "15",
            "import java.util.Scanner;\n\npublic class ArraySum {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int n = scanner.nextInt();\n        int[] array = new int[n];\n        \n        for (int i = 0; i < n; i++) {\n            array[i] = scanner.nextInt();\n        }\n        \n        int sum = 0;\n        for (int num : array) {\n            sum += num;\n        }\n        \n        System.out.println(sum);\n        scanner.close();\n    }\n}",
            "Use an enhanced for loop to iterate through the array elements.",
            "Arrays",
            30
        ));

        problems.add(new PracticeProblem(
            "Fibonacci Sequence",
            "Intermediate",
            "Write a program to print the first n numbers of the Fibonacci sequence.",
            "8",
            "0 1 1 2 3 5 8 13",
            "import java.util.Scanner;\n\npublic class Fibonacci {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int n = scanner.nextInt();\n        \n        if (n >= 1) System.out.print(\"0\");\n        if (n >= 2) System.out.print(\" 1\");\n        \n        int prev1 = 0, prev2 = 1;\n        for (int i = 3; i <= n; i++) {\n            int current = prev1 + prev2;\n            System.out.print(\" \" + current);\n            prev1 = prev2;\n            prev2 = current;\n        }\n        \n        scanner.close();\n    }\n}",
            "Each number is the sum of the two preceding numbers.",
            "Mathematical",
            50
        ));

        problems.add(new PracticeProblem(
            "String Reversal",
            "Intermediate",
            "Write a program that reverses a given string without using built-in reverse methods.",
            "Hello World",
            "dlroW olleH",
            "import java.util.Scanner;\n\npublic class StringReverse {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        String input = scanner.nextLine();\n        \n        String reversed = \"\";\n        for (int i = input.length() - 1; i >= 0; i--) {\n            reversed += input.charAt(i);\n        }\n        \n        System.out.println(reversed);\n        scanner.close();\n    }\n}",
            "Iterate through the string from the last character to the first.",
            "String Manipulation",
            35
        ));

        problems.add(new PracticeProblem(
            "Grade Calculator",
            "Intermediate",
            "Write a program that calculates the average grade and assigns a letter grade.",
            "3\n85 92 78",
            "Average: 85.0\nGrade: B",
            "import java.util.Scanner;\n\npublic class GradeCalculator {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int n = scanner.nextInt();\n        \n        double sum = 0;\n        for (int i = 0; i < n; i++) {\n            sum += scanner.nextInt();\n        }\n        \n        double average = sum / n;\n        char grade;\n        \n        if (average >= 90) grade = 'A';\n        else if (average >= 80) grade = 'B';\n        else if (average >= 70) grade = 'C';\n        else if (average >= 60) grade = 'D';\n        else grade = 'F';\n        \n        System.out.println(\"Average: \" + average);\n        System.out.println(\"Grade: \" + grade);\n        scanner.close();\n    }\n}",
            "Calculate average and use if-else statements for grade assignment.",
            "Conditional",
            40
        ));

        // ===== ADVANCED PROBLEMS =====
        problems.add(new PracticeProblem(
            "Binary Search Implementation",
            "Advanced",
            "Implement binary search algorithm to find an element in a sorted array.",
            "5\n1 3 5 7 9\n5",
            "Index: 2",
            "import java.util.Scanner;\n\npublic class BinarySearch {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int n = scanner.nextInt();\n        int[] array = new int[n];\n        \n        for (int i = 0; i < n; i++) {\n            array[i] = scanner.nextInt();\n        }\n        \n        int target = scanner.nextInt();\n        int result = binarySearch(array, target);\n        \n        if (result != -1) {\n            System.out.println(\"Index: \" + result);\n        } else {\n            System.out.println(\"Not found\");\n        }\n        scanner.close();\n    }\n    \n    static int binarySearch(int[] arr, int target) {\n        int left = 0, right = arr.length - 1;\n        \n        while (left <= right) {\n            int mid = left + (right - left) / 2;\n            \n            if (arr[mid] == target) return mid;\n            if (arr[mid] < target) left = mid + 1;\n            else right = mid - 1;\n        }\n        \n        return -1;\n    }\n}",
            "Divide the search space in half with each comparison.",
            "Algorithms",
            60
        ));

        problems.add(new PracticeProblem(
            "Matrix Multiplication",
            "Advanced",
            "Write a program to multiply two matrices.",
            "2 2\n1 2\n3 4\n2 2\n5 6\n7 8",
            "19 22\n43 50",
            "import java.util.Scanner;\n\npublic class MatrixMultiplication {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        \n        int r1 = scanner.nextInt();\n        int c1 = scanner.nextInt();\n        int[][] matrix1 = new int[r1][c1];\n        \n        for (int i = 0; i < r1; i++) {\n            for (int j = 0; j < c1; j++) {\n                matrix1[i][j] = scanner.nextInt();\n            }\n        }\n        \n        int r2 = scanner.nextInt();\n        int c2 = scanner.nextInt();\n        int[][] matrix2 = new int[r2][c2];\n        \n        for (int i = 0; i < r2; i++) {\n            for (int j = 0; j < c2; j++) {\n                matrix2[i][j] = scanner.nextInt();\n            }\n        }\n        \n        int[][] result = new int[r1][c2];\n        for (int i = 0; i < r1; i++) {\n            for (int j = 0; j < c2; j++) {\n                for (int k = 0; k < c1; k++) {\n                    result[i][j] += matrix1[i][k] * matrix2[k][j];\n                }\n            }\n        }\n        \n        for (int i = 0; i < r1; i++) {\n            for (int j = 0; j < c2; j++) {\n                System.out.print(result[i][j]);\n                if (j < c2 - 1) System.out.print(\" \");\n            }\n            System.out.println();\n        }\n        scanner.close();\n    }\n}",
            "Use three nested loops: rows of first matrix, columns of second matrix, and sum calculation.",
            "Arrays",
            70
        ));

        problems.add(new PracticeProblem(
            "Simple Calculator with Methods",
            "Advanced",
            "Create a calculator class with methods for basic arithmetic operations.",
            "10 + 5",
            "15.0",
            "import java.util.Scanner;\n\nclass Calculator {\n    public double add(double a, double b) {\n        return a + b;\n    }\n    \n    public double subtract(double a, double b) {\n        return a - b;\n    }\n    \n    public double multiply(double a, double b) {\n        return a * b;\n    }\n    \n    public double divide(double a, double b) {\n        if (b != 0) {\n            return a / b;\n        } else {\n            throw new ArithmeticException(\"Division by zero!\");\n        }\n    }\n}\n\npublic class SimpleCalculator {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        Calculator calc = new Calculator();\n        \n        double a = scanner.nextDouble();\n        String operator = scanner.next();\n        double b = scanner.nextDouble();\n        \n        double result = 0;\n        switch (operator) {\n            case \"+\": result = calc.add(a, b); break;\n            case \"-\": result = calc.subtract(a, b); break;\n            case \"*\": result = calc.multiply(a, b); break;\n            case \"/\": result = calc.divide(a, b); break;\n            default: System.out.println(\"Invalid operator\");\n        }\n        \n        System.out.println(result);\n        scanner.close();\n    }\n}",
            "Create separate methods for each operation and use switch statement for operation selection.",
            "OOP",
            55
        ));

        problems.add(new PracticeProblem(
            "Word Counter",
            "Advanced",
            "Write a program that counts the frequency of each word in a given text.",
            "hello world hello java world",
            "hello: 2\nworld: 2\njava: 1",
            "import java.util.*;\n\npublic class WordCounter {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        String text = scanner.nextLine().toLowerCase();\n        \n        String[] words = text.split(\" \");\n        Map<String, Integer> wordCount = new HashMap<>();\n        \n        for (String word : words) {\n            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);\n        }\n        \n        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {\n            System.out.println(entry.getKey() + \": \" + entry.getValue());\n        }\n        \n        scanner.close();\n    }\n}",
            "Use HashMap to store word frequencies and split the input by spaces.",
            "Collections",
            65
        ));

        problems.add(new PracticeProblem(
            "Student Management System",
            "Advanced",
            "Create a simple student management system with add, search, and display functionality.",
            "3\nJohn 85\nAlice 92\nBob 78\nAlice",
            "Student: Alice, Grade: 92",
            "import java.util.*;\n\nclass Student {\n    private String name;\n    private int grade;\n    \n    public Student(String name, int grade) {\n        this.name = name;\n        this.grade = grade;\n    }\n    \n    public String getName() { return name; }\n    public int getGrade() { return grade; }\n    \n    @Override\n    public String toString() {\n        return \"Student: \" + name + \", Grade: \" + grade;\n    }\n}\n\npublic class StudentManagement {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        List<Student> students = new ArrayList<>();\n        \n        int n = scanner.nextInt();\n        scanner.nextLine(); // consume newline\n        \n        for (int i = 0; i < n; i++) {\n            String line = scanner.nextLine();\n            String[] parts = line.split(\" \");\n            String name = parts[0];\n            int grade = Integer.parseInt(parts[1]);\n            students.add(new Student(name, grade));\n        }\n        \n        String searchName = scanner.nextLine();\n        \n        for (Student student : students) {\n            if (student.getName().equals(searchName)) {\n                System.out.println(student);\n                break;\n            }\n        }\n        \n        scanner.close();\n    }\n}",
            "Create a Student class with encapsulated fields and use ArrayList to manage multiple students.",
            "OOP",
            75
        ));

        Log.d(TAG, "Attempting to insert " + problems.size() + " practice problems");
        try {
            database.practiceProblemDao().insertAllProblems(problems);
            int insertedCount = database.practiceProblemDao().getProblemCount();
            Log.d(TAG, "Successfully inserted practice problems. Total count now: " + insertedCount);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting practice problems: " + e.getMessage(), e);
        }
    }
}