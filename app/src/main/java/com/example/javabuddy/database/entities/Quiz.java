package com.example.javabuddy.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(tableName = "quizzes",
        indices = {@Index("lessonId")},
        foreignKeys = @ForeignKey(entity = Lesson.class,
                                parentColumns = "id",
                                childColumns = "lessonId",
                                onDelete = ForeignKey.CASCADE))
public class Quiz {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int lessonId;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private int correctAnswer; // 1-4 for multiple choice, 0/1 for true/false
    private String questionType; // "multiple_choice", "true_false", "code_completion"
    private String codeSnippet; // for code completion questions
    private String explanation;

    public Quiz() {}

    @Ignore
    public Quiz(int lessonId, String question, String option1, String option2, 
                String option3, String option4, int correctAnswer, String questionType,
                String codeSnippet, String explanation) {
        this.lessonId = lessonId;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswer = correctAnswer;
        this.questionType = questionType;
        this.codeSnippet = codeSnippet;
        this.explanation = explanation;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLessonId() { return lessonId; }
    public void setLessonId(int lessonId) { this.lessonId = lessonId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getOption1() { return option1; }
    public void setOption1(String option1) { this.option1 = option1; }

    public String getOption2() { return option2; }
    public void setOption2(String option2) { this.option2 = option2; }

    public String getOption3() { return option3; }
    public void setOption3(String option3) { this.option3 = option3; }

    public String getOption4() { return option4; }
    public void setOption4(String option4) { this.option4 = option4; }

    public int getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(int correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}