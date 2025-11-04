package com.example.javabuddy.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.javabuddy.database.entities.Lesson;

import java.util.List;

@Dao
public interface LessonDao {
    
    @Query("SELECT * FROM lessons ORDER BY orderIndex ASC")
    List<Lesson> getAllLessons();

    @Query("SELECT * FROM lessons WHERE conceptGroup = :conceptGroup ORDER BY orderIndex ASC")
    List<Lesson> getLessonsByConceptGroup(String conceptGroup);

    @Query("SELECT * FROM lessons WHERE id = :id")
    Lesson getLessonById(int id);

    @Query("SELECT * FROM lessons WHERE orderIndex > (SELECT orderIndex FROM lessons WHERE id = :lessonId) ORDER BY orderIndex ASC LIMIT 1")
    Lesson getNextLesson(int lessonId);

    @Query("SELECT DISTINCT conceptGroup FROM lessons ORDER BY conceptGroup")
    List<String> getAllConceptGroups();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLesson(Lesson lesson);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllLessons(List<Lesson> lessons);

    @Update
    void updateLesson(Lesson lesson);

    @Delete
    void deleteLesson(Lesson lesson);

    @Query("DELETE FROM lessons")
    void deleteAllLessons();

    @Query("SELECT COUNT(*) FROM lessons")
    int getLessonCount();
}