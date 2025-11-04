package com.example.javabuddy.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(tableName = "user_progress",
        indices = {@Index("lessonId")},
        foreignKeys = @ForeignKey(entity = Lesson.class,
                                parentColumns = "id",
                                childColumns = "lessonId",
                                onDelete = ForeignKey.CASCADE))
public class UserProgress {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int lessonId;
    private boolean completed;
    private int score;
    private long timestamp;
    private int timeSpent; // in minutes
    private int attempts;

    public UserProgress() {}

    @Ignore
    public UserProgress(int lessonId, boolean completed, int score, long timestamp, int timeSpent, int attempts) {
        this.lessonId = lessonId;
        this.completed = completed;
        this.score = score;
        this.timestamp = timestamp;
        this.timeSpent = timeSpent;
        this.attempts = attempts;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLessonId() { return lessonId; }
    public void setLessonId(int lessonId) { this.lessonId = lessonId; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getTimeSpent() { return timeSpent; }
    public void setTimeSpent(int timeSpent) { this.timeSpent = timeSpent; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    // Additional utility methods
    public int getBestScore() { return score; }
    public int getTimeSpentMinutes() { return timeSpent; }
}