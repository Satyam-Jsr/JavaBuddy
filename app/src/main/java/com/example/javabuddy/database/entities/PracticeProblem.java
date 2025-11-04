package com.example.javabuddy.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "practice_problems")
public class PracticeProblem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String difficulty; // "Beginner", "Intermediate", "Advanced"
    private String problemStatement;
    private String sampleInput;
    private String expectedOutput;
    private String solution;
    private String hints;
    private String category;
    private int points;

    public PracticeProblem() {}

    @Ignore
    public PracticeProblem(String title, String difficulty, String problemStatement, 
                          String sampleInput, String expectedOutput, String solution,
                          String hints, String category, int points) {
        this.title = title;
        this.difficulty = difficulty;
        this.problemStatement = problemStatement;
        this.sampleInput = sampleInput;
        this.expectedOutput = expectedOutput;
        this.solution = solution;
        this.hints = hints;
        this.category = category;
        this.points = points;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getProblemStatement() { return problemStatement; }
    public void setProblemStatement(String problemStatement) { this.problemStatement = problemStatement; }

    public String getSampleInput() { return sampleInput; }
    public void setSampleInput(String sampleInput) { this.sampleInput = sampleInput; }

    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }

    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }

    public String getHints() { return hints; }
    public void setHints(String hints) { this.hints = hints; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}