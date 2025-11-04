package com.example.javabuddy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.javabuddy.R;
import com.example.javabuddy.activities.AIProgrammingChallengeActivity;
import com.example.javabuddy.activities.AIQuizGeneratorActivity;
import com.example.javabuddy.activities.IDEActivity;
import com.example.javabuddy.activities.LessonActivity;
import com.example.javabuddy.activities.PracticeActivity;
import com.example.javabuddy.activities.ProgressActivity;
import com.example.javabuddy.activities.QuizSelectionActivity;
import com.example.javabuddy.activities.TimedTestActivity;
import com.example.javabuddy.database.JavaBuddyDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.preference.PreferenceManager;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private CardView lessonsCard, quizCard, ideCard, practiceCard, progressCard, timedTestCard;
    private CardView aiQuizCard, aiChallengeCard;
    private TextView welcomeText, progressSummaryText, motivationText;
    private LottieAnimationView aiAnimationView;
    private JavaBuddyDatabase database;
    private ExecutorService executor;
    private SharedPreferences settingsPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        initializeViews(view);
        setupClickListeners();
        loadProgressSummary();
        
        return view;
    }

    private void initializeViews(View view) {
        lessonsCard = view.findViewById(R.id.lessons_card);
        quizCard = view.findViewById(R.id.quiz_card);
        ideCard = view.findViewById(R.id.ide_card);
        practiceCard = view.findViewById(R.id.practice_card);
        progressCard = view.findViewById(R.id.progress_card);
    timedTestCard = view.findViewById(R.id.timed_test_card);
        aiQuizCard = view.findViewById(R.id.ai_quiz_card);
        aiChallengeCard = view.findViewById(R.id.ai_challenge_card);
        aiAnimationView = view.findViewById(R.id.ai_pulse_animation);
        
        welcomeText = view.findViewById(R.id.welcome_text);
        progressSummaryText = view.findViewById(R.id.progress_summary_text);
        motivationText = view.findViewById(R.id.motivation_text);
        
        database = JavaBuddyDatabase.getDatabase(requireContext());
        executor = Executors.newSingleThreadExecutor();
    settingsPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        configureLottieAnimation();
        
        // Set welcome message
        welcomeText.setText("Welcome to JavaBuddy! 👋");
        setMotivationalMessage();
    }

    private void configureLottieAnimation() {
        if (aiAnimationView == null) {
            return;
        }

        boolean animationsEnabled = settingsPreferences.getBoolean("pref_enable_card_animations", true);
        if (!animationsEnabled) {
            aiAnimationView.cancelAnimation();
            aiAnimationView.setVisibility(View.GONE);
            return;
        }

        aiAnimationView.setVisibility(View.VISIBLE);

        aiAnimationView.setAnimation("bird_hi.lottie");
        aiAnimationView.setFailureListener(result -> {
            Log.e(TAG, "Failed to load bird_hi.lottie, falling back", result);
            aiAnimationView.setAnimation(R.raw.ai_pulse);
            aiAnimationView.playAnimation();
        });
        aiAnimationView.playAnimation();
    }

    private void setupClickListeners() {
        lessonsCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LessonActivity.class);
            startActivity(intent);
        });

        quizCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuizSelectionActivity.class);
            startActivity(intent);
        });

        ideCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), IDEActivity.class);
            startActivity(intent);
        });

        practiceCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PracticeActivity.class);
            startActivity(intent);
        });

        progressCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProgressActivity.class);
            startActivity(intent);
        });

        timedTestCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TimedTestActivity.class);
            startActivity(intent);
        });

        aiQuizCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AIQuizGeneratorActivity.class);
            startActivity(intent);
        });

        aiChallengeCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AIProgrammingChallengeActivity.class);
            startActivity(intent);
        });

    }

    private void loadProgressSummary() {
        executor.execute(() -> {
            int completedLessons = database.userProgressDao().getCompletedLessonsCount();
            int totalLessons = database.lessonDao().getLessonCount();
            double averageScore = database.userProgressDao().getAverageScore();
            
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    String progressText = String.format(
                        "Progress: %d/%d lessons completed", 
                        completedLessons, totalLessons);
                    
                    if (averageScore > 0) {
                        progressText += String.format(" • Average Score: %.1f%%", averageScore);
                    }
                    
                    progressSummaryText.setText(progressText);
                });
            }
        });
    }

    private void setMotivationalMessage() {
        if (!settingsPreferences.getBoolean("pref_show_motivation", true)) {
            motivationText.setVisibility(View.GONE);
            return;
        }

        motivationText.setVisibility(View.VISIBLE);
        String[] messages = {
            "Every expert was once a beginner. Keep learning! 🚀",
            "Code is like humor. When you have to explain it, it's bad. 😄",
            "The best way to learn to program is by writing programs! 💻",
            "Programming isn't about what you know; it's about what you can figure out. 🧠",
            "Java is to JavaScript what car is to Carpet! 🚗",
            "Talk is cheap. Show me the code! 💪",
            "First, solve the problem. Then, write the code. ⭐",
            "Experience is the name everyone gives to their mistakes. 🎯"
        };
        
        int randomIndex = (int) (Math.random() * messages.length);
        motivationText.setText(messages[randomIndex]);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh progress summary when returning to home
        loadProgressSummary();
        if (settingsPreferences != null) {
            configureLottieAnimation();
            setMotivationalMessage();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}