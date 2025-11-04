package com.example.javabuddy.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.javabuddy.database.entities.PracticeProblem;

import java.util.List;

@Dao
public interface PracticeProblemDao {
    
    @Query("SELECT * FROM practice_problems ORDER BY CASE difficulty WHEN 'Beginner' THEN 1 WHEN 'Intermediate' THEN 2 WHEN 'Advanced' THEN 3 ELSE 4 END, title")
    List<PracticeProblem> getAllProblems();

    @Query("SELECT * FROM practice_problems WHERE difficulty = :difficulty ORDER BY title")
    List<PracticeProblem> getProblemsByDifficulty(String difficulty);

    @Query("SELECT * FROM practice_problems WHERE category = :category ORDER BY CASE difficulty WHEN 'Beginner' THEN 1 WHEN 'Intermediate' THEN 2 WHEN 'Advanced' THEN 3 ELSE 4 END, title")
    List<PracticeProblem> getProblemsByCategory(String category);

    @Query("SELECT * FROM practice_problems WHERE id = :id")
    PracticeProblem getProblemById(int id);

    @Query("SELECT DISTINCT category FROM practice_problems ORDER BY category")
    List<String> getAllCategories();

    @Query("SELECT DISTINCT difficulty FROM practice_problems ORDER BY difficulty")
    List<String> getAllDifficulties();

    @Insert
    void insertProblem(PracticeProblem problem);

    @Insert
    void insertAllProblems(List<PracticeProblem> problems);

    @Update
    void updateProblem(PracticeProblem problem);

    @Delete
    void deleteProblem(PracticeProblem problem);

    @Query("DELETE FROM practice_problems")
    void deleteAllProblems();

    @Query("SELECT COUNT(*) FROM practice_problems")
    int getProblemCount();
}