package com.example.javabuddy.database.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "lessons")
public class Lesson {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private String conceptGroup;
    private int orderIndex;
    private String difficulty;
    private String codeExample;
    private String summary;

    public Lesson() {}

    @Ignore
    public Lesson(String title, String content, String conceptGroup, int orderIndex, 
                  String difficulty, String codeExample, String summary) {
        this.title = title;
        this.content = content;
        this.conceptGroup = conceptGroup;
        this.orderIndex = orderIndex;
        this.difficulty = difficulty;
        this.codeExample = codeExample;
        this.summary = summary;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getConceptGroup() { return conceptGroup; }
    public void setConceptGroup(String conceptGroup) { this.conceptGroup = conceptGroup; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getCodeExample() { return codeExample; }
    public void setCodeExample(String codeExample) { this.codeExample = codeExample; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}