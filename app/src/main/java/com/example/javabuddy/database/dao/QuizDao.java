package com.example.javabuddy.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.javabuddy.database.entities.Quiz;

import java.util.List;

@Dao
public interface QuizDao {
    
    @Query("SELECT * FROM quizzes WHERE lessonId = :lessonId")
    List<Quiz> getQuizzesByLessonId(int lessonId);

    @Query("SELECT * FROM quizzes")
    List<Quiz> getAllQuizzes();

    @Query("SELECT * FROM quizzes ORDER BY RANDOM() LIMIT :limit")
    List<Quiz> getRandomQuizzes(int limit);

    @Query("SELECT * FROM quizzes WHERE id = :id")
    Quiz getQuizById(int id);

    @Query("SELECT * FROM quizzes WHERE questionType = :questionType")
    List<Quiz> getQuizzesByType(String questionType);

    @Insert
    void insertQuiz(Quiz quiz);

    @Insert
    void insertAllQuizzes(List<Quiz> quizzes);

    @Update
    void updateQuiz(Quiz quiz);

    @Delete
    void deleteQuiz(Quiz quiz);

    @Query("DELETE FROM quizzes WHERE lessonId = :lessonId")
    void deleteQuizzesByLessonId(int lessonId);

    @Query("DELETE FROM quizzes")
    void deleteAllQuizzes();

    @Query("SELECT COUNT(*) FROM quizzes WHERE lessonId = :lessonId")
    int getQuizCountByLessonId(int lessonId);

    @Query("SELECT COUNT(*) FROM quizzes")
    int getQuizCount();
}