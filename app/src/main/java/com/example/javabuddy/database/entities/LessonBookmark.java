package com.example.javabuddy.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "lesson_bookmarks",
    indices = {@Index(value = "lessonId", unique = true)},
    foreignKeys = @ForeignKey(
        entity = Lesson.class,
        parentColumns = "id",
        childColumns = "lessonId",
        onDelete = ForeignKey.CASCADE
    )
)
public class LessonBookmark {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int lessonId;
    private long createdAt;

    public LessonBookmark(int lessonId, long createdAt) {
        this.lessonId = lessonId;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
