package com.example.javabuddy.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.javabuddy.database.dao.LessonDao;
import com.example.javabuddy.database.dao.LessonBookmarkDao;
import com.example.javabuddy.database.dao.PracticeProblemDao;
import com.example.javabuddy.database.dao.QuizDao;
import com.example.javabuddy.database.dao.UserProgressDao;
import com.example.javabuddy.database.entities.Lesson;
import com.example.javabuddy.database.entities.LessonBookmark;
import com.example.javabuddy.database.entities.PracticeProblem;
import com.example.javabuddy.database.entities.Quiz;
import com.example.javabuddy.database.entities.UserProgress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
    entities = {Lesson.class, Quiz.class, UserProgress.class, PracticeProblem.class, LessonBookmark.class},
    version = 6,
    exportSchema = false
)
public abstract class JavaBuddyDatabase extends RoomDatabase {
    
    public abstract LessonDao lessonDao();
    public abstract QuizDao quizDao();
    public abstract UserProgressDao userProgressDao();
    public abstract PracticeProblemDao practiceProblemDao();
    public abstract LessonBookmarkDao lessonBookmarkDao();

    private static volatile JavaBuddyDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static JavaBuddyDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (JavaBuddyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            JavaBuddyDatabase.class, "javabuddy_database")
                            .fallbackToDestructiveMigration()  // This will recreate DB with new content
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(androidx.sqlite.db.SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    android.util.Log.d("JavaBuddyDatabase", "Database created - version 5");
                                    // Populate database on creation only
                                    databaseWriteExecutor.execute(() -> {
                                        try {
                                            populateDatabase(INSTANCE);
                                            android.util.Log.d("JavaBuddyDatabase", "Database population completed");
                                        } catch (Exception e) {
                                            android.util.Log.e("JavaBuddyDatabase", "Error populating database", e);
                                        }
                                    });
                                }
                                
                                @Override
                                public void onOpen(androidx.sqlite.db.SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    android.util.Log.d("JavaBuddyDatabase", "Database opened - version: " + db.getVersion());
                                    // Check if database needs population
                                    databaseWriteExecutor.execute(() -> {
                                        try {
                                            // Only populate if database is empty
                                            if (INSTANCE.lessonDao().getLessonCount() == 0) {
                                                android.util.Log.d("JavaBuddyDatabase", "Database empty, populating...");
                                                populateDatabase(INSTANCE);
                                            } else {
                                                android.util.Log.d("JavaBuddyDatabase", "Database already populated");
                                            }
                                        } catch (Exception e) {
                                            android.util.Log.e("JavaBuddyDatabase", "Error checking/populating database", e);
                                        }
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void populateDatabase(JavaBuddyDatabase db) {
        try {
            // Populate with initial data
            DatabasePopulator.populateInitialData(db);
            android.util.Log.d("JavaBuddyDatabase", "Database populated successfully");
        } catch (Exception e) {
            android.util.Log.e("JavaBuddyDatabase", "Failed to populate database", e);
        }
    }
    
    public void clearAllTables() {
        try {
            // Clear all tables
            lessonDao().deleteAllLessons();
            quizDao().deleteAllQuizzes();
            userProgressDao().deleteAllProgress();
            practiceProblemDao().deleteAllProblems();
            lessonBookmarkDao().deleteAll();
            android.util.Log.d("JavaBuddyDatabase", "All tables cleared successfully");
        } catch (Exception e) {
            android.util.Log.e("JavaBuddyDatabase", "Error clearing tables", e);
        }
    }
    
    /**
     * Force recreate database - useful for development and schema changes
     */
    public static void recreateDatabase(Context context) {
        try {
            if (INSTANCE != null) {
                INSTANCE.close();
                INSTANCE = null;
            }
            
            // Delete the database file
            context.deleteDatabase("javabuddy_database");
            android.util.Log.d("JavaBuddyDatabase", "Database file deleted, will be recreated on next access");
            
            // Get fresh instance
            getDatabase(context);
        } catch (Exception e) {
            android.util.Log.e("JavaBuddyDatabase", "Error recreating database", e);
        }
    }
}