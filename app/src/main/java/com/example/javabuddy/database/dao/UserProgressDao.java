package com.example.javabuddy.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.javabuddy.database.entities.UserProgress;

import java.util.List;

@Dao
public interface UserProgressDao {
    
    @Query("SELECT * FROM user_progress WHERE lessonId = :lessonId LIMIT 1")
    UserProgress getProgressByLessonId(int lessonId);

    @Query("SELECT * FROM user_progress WHERE completed = 1")
    List<UserProgress> getCompletedLessons();

    @Query("SELECT * FROM user_progress ORDER BY timestamp DESC")
    List<UserProgress> getAllProgress();

    @Query("SELECT COUNT(*) FROM user_progress WHERE completed = 1")
    int getCompletedLessonsCount();

    @Query("SELECT AVG(score) FROM user_progress WHERE completed = 1")
    double getAverageScore();

    @Query("SELECT SUM(timeSpent) FROM user_progress")
    int getTotalTimeSpent();

    @Insert
    void insertProgress(UserProgress progress);

    @Update
    void updateProgress(UserProgress progress);

    @Delete
    void deleteProgress(UserProgress progress);

    @Query("DELETE FROM user_progress WHERE lessonId = :lessonId")
    void deleteProgressByLessonId(int lessonId);
    
    @Query("DELETE FROM user_progress")
    void deleteAllProgress();
}