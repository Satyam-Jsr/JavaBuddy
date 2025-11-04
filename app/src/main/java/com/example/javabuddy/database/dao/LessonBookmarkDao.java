package com.example.javabuddy.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.javabuddy.database.entities.Lesson;
import com.example.javabuddy.database.entities.LessonBookmark;

import java.util.List;

@Dao
public interface LessonBookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookmark(LessonBookmark bookmark);

    @Query("DELETE FROM lesson_bookmarks WHERE lessonId = :lessonId")
    void deleteBookmark(int lessonId);

    @Query("SELECT COUNT(*) FROM lesson_bookmarks WHERE lessonId = :lessonId")
    int isBookmarked(int lessonId);

    @Query("SELECT lessons.* FROM lessons INNER JOIN lesson_bookmarks ON lessons.id = lesson_bookmarks.lessonId ORDER BY lesson_bookmarks.createdAt DESC")
    List<Lesson> getBookmarkedLessons();

    @Query("DELETE FROM lesson_bookmarks")
    void deleteAll();
}
